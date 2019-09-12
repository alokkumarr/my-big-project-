package mapr.streaming

import java.io.IOException
import java.util
import java.util.Map.Entry
import java.util.Properties
import java.util.concurrent.{ExecutionException, TimeoutException}

import com.mapr.db.exceptions.TableExistsException
import com.mapr.streams.Streams
import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import exceptions.{ErrorCodes, RTException}
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.hadoop.conf.Configuration
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsObject, Json}
import sncr.bda.core.file.HFileOperations

import scala.collection.{Seq, mutable}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.io.File
import scala.util.Random

object EventHandler {
  val KEY_STREAM_TOPIC_SELECTOR_METHOD = "stream.topic.selectormethod"
  val TOPIC_SELECTOR_ROUND_ROBIN = "roundrobin"
  val TOPIC_SELECTOR_RANDOM = "random"

  private var streamFile: String = null
  var streamWaitTime: Int = 0
  val retrySeconds = 5

  val debugMode = System.getProperty("processing.mode", "normal").equalsIgnoreCase("debug")

  /**
    * Topic selector function which selects between random or round-robin or any other selector mechanism if available
    */
  var topicSelectorFunction: Array[mutable.HashMap[String, String]] => Int = null

  def getStreamFile: String = streamFile


  def getAppKeys = eventHandlerStreams.keySet


  private val m_log: Logger = LoggerFactory.getLogger(classOf[EventHandler].getName)
  private[streaming] val eventHandlerProperties = new scala.collection.mutable.HashMap[String, Properties]
  private[streaming] val eventHandlerStreams = new scala.collection.mutable.HashMap
    [String,
      (Array[mutable.HashMap[String, String]],
        Array[mutable.HashMap[String, String]])
      ]
  protected[streaming] val eventSenders = new scala.collection.mutable.HashMap[String, EventSender]
  private[streaming] var streamPointer: mutable.Map[String, String] = new mutable.HashMap[String, String]

  private val rand = new Random()

  /**
    * This function is used to select a random topic with-in a given <code>stream</code>.
    * This function will be sent as a parameter to <code>selectEventSender</code> function.
    *
    * @param stream - A MapR Stream
    * @return Int - Randomly selected topic number
    */
  def randomSelector(stream: Array[mutable.HashMap[String, String]]): Int = {
    if (stream.length > 1) rand.nextInt(stream.length) else 0
  }


  private var streamNumber = -1 //Persists the previously selected topic

  /**
    * This function is used to select between the topics of a <code>stream</code> in round-robin fashion.
    * This function will be sent as a parameter to {@link mapr.streaming.EventHandler#selectEventSender} function
    *
    * @param stream - A MapR Stream
    * @return Int - Topic number selected in round-robin fashion
    */
  def roundRobinSelector(stream: Array[mutable.HashMap[String, String]]): Int = {
    val topicNumber = {
      if (stream.length != 0)
        (streamNumber + 1) % stream.length
      else 0
    }

    streamNumber += 1

    topicNumber
  }

  def buildEventHandlerList {
    import scala.collection.JavaConversions._
    if (eventHandlerProperties.nonEmpty) return

    val conf: Config = ConfigFactory.load
    val mapr_conf: java.util.List[_ <: Config] = conf.getConfigList("mapping")
    m_log.debug("Read properties:")

    streamFile = conf.getString("stream.file.location")
    streamWaitTime = conf.getInt("stream.send.timeout")

    val topicSelectorMethod = conf.getString(EventHandler.KEY_STREAM_TOPIC_SELECTOR_METHOD)

    topicSelectorFunction = topicSelectorMethod match {
      case EventHandler.TOPIC_SELECTOR_ROUND_ROBIN => roundRobinSelector
      case EventHandler.TOPIC_SELECTOR_RANDOM => randomSelector
      case _ => randomSelector
    }

    if (streamFile == null || streamFile.isEmpty)
      streamFile = "/tmp/frontend-server.stream.checkpoint"

    for (c <- mapr_conf) {
      var key: String = null
      val properties: Properties = new Properties
      var p_list, s_list: Array[mutable.HashMap[String, String]] = null
      for (e <- c.entrySet) {
        e.getKey match {
          case StreamHelper.primaryStreamsKey => {
            val v = generateStreamList(e, StreamHelper.primaryStreamsKey)
            p_list = v.get
            if (v == None || p_list.size == 0) throw new RTException(ErrorCodes ConfigurationIsNotCorrect, "Could not find primary streams!")
          }
          case StreamHelper.secondaryStreamsKey => {
            val v = generateStreamList(e, StreamHelper.secondaryStreamsKey)
            s_list = v.get
            if (v == None || s_list.size == 0) throw new RTException(ErrorCodes ConfigurationIsNotCorrect, "Could not find secondary (hot swap) streams!")
          }
          case _ => {
            val value: String = if ((e.getValue == null || e.getValue.render.isEmpty)) ""
            else (e.getValue.render.replaceAll("\"", ""))
            m_log.debug(e.getKey + ": " + value)
            properties.put(e.getKey, value)
          }
        }
      }

      key = properties.remove("app_key").asInstanceOf[String]
      val clazz = properties.get("class").asInstanceOf[String]
      if (clazz == null || clazz.isEmpty) {
        m_log.error(String.format(ErrorCodes.getDescription(ErrorCodes.InvalidAppConf), "class"))
        throw new RTException(ErrorCodes.ConfigurationIsNotCorrect, clazz)
      }
      m_log.debug(s"Create property for class ${clazz}, and Application key: ${key}, # of entries: " + properties.size)

      p_list.foreach(primary_queue => {
        val pCompositeKey = key +
          StreamHelper.keyConcat +
          primary_queue(StreamHelper.streamKey) +
          StreamHelper.keyConcat +
          primary_queue(StreamHelper.topicKey)

        m_log debug ("Add sender for primary stream: " + pCompositeKey)
        eventSenders(pCompositeKey) = new EventSender(primary_queue(StreamHelper.streamKey), primary_queue(StreamHelper.topicKey), properties)
      })

      s_list.foreach(secondary_queue => {
        val pCompositeKey = key +
          StreamHelper.keyConcat +
          secondary_queue(StreamHelper.streamKey) +
          StreamHelper.keyConcat + secondary_queue(StreamHelper.topicKey)

        m_log debug ("Add sender for secondary stream: " + pCompositeKey)
        eventSenders(pCompositeKey) = new EventSender(secondary_queue(StreamHelper.streamKey), secondary_queue(StreamHelper.topicKey), properties)
      })
      eventHandlerStreams(key) = (p_list, s_list)
      eventHandlerProperties(key) = properties
      StreamHelper.loadActiveStreams
    }
  }

  import scala.collection.JavaConversions._

  private def generateStreamList(e: Entry[String, ConfigValue], key: String): Option[Array[mutable.HashMap[String, String]]] = {
    val ps: java.util.List[_ <: Config] = e.getValue.atKey(key).getConfigList(key)
    val res: Array[mutable.HashMap[String, String]] = ps.map(topic_queue_pair =>
      topic_queue_pair.entrySet().foldLeft(new mutable.HashMap[String, String])((m, qt_entry)
      => m += (qt_entry.getKey ->
          (if ((qt_entry.getValue == null || qt_entry.getValue.render.isEmpty)) ""
          else (qt_entry.getValue.render.replaceAll("\"", "")))))
    ).toArray
    m_log.debug(key + ": " + res.mkString("[", ",", "]"))
    Option(res)
  }


  def getEventHandler[T <: EventHandler](key: String): T = {
    if (eventHandlerProperties == null) {
      m_log.error(String.format(ErrorCodes.getDescription(ErrorCodes.InvalidAppConf), "mapping - missing section " + key))
      return null.asInstanceOf[T]
    }

    val properties = eventHandlerProperties.getOrDefault(key, null)

    if (properties == null) {
      m_log.error(String.format(ErrorCodes.getDescription(ErrorCodes.ConfigurationIsNotCorrect), key))

      return null.asInstanceOf[T]
    }

    var eh: EventHandler = null
    val clazz = properties.get("class").asInstanceOf[String]
    m_log.debug("Key Handler = " + clazz)

    try {
      eh = Class.forName(clazz).newInstance.asInstanceOf[T]
    }
    catch {
      case e: InstantiationException => {
        m_log.error(s"Class: ${clazz}: ", e)
        throw new RTException(ErrorCodes.NoEventHandler, clazz)
      }
      case e: IllegalAccessException => {
        m_log.error(s"Class: ${clazz}: ", e)
        throw new RTException(ErrorCodes.IllegalAccess, clazz)
      }
      case e: ClassNotFoundException => {
        m_log.error(s"Class: ${clazz}: ", e)
        throw new RTException(ErrorCodes.ClassNotFound, clazz)
      }
    }
    eh.setEventHandlerProps(properties, key)
    eh.setStreams(eventHandlerStreams(key))
    eh.asInstanceOf[T]
  }

  def buildOnDemandEventHandlerList(key: String): Boolean = {
    import scala.collection.JavaConversions._

    var validKey: Boolean = false
    val conf: Config = ConfigFactory.load
    streamFile = conf.getString("stream.file.location")
    streamWaitTime = conf.getInt("stream.send.timeout")

    val topicSelectorMethod = conf.getString(EventHandler.KEY_STREAM_TOPIC_SELECTOR_METHOD)

    topicSelectorFunction = topicSelectorMethod match {
      case EventHandler.TOPIC_SELECTOR_ROUND_ROBIN => roundRobinSelector
      case EventHandler.TOPIC_SELECTOR_RANDOM => randomSelector
      case _ => randomSelector
    }

    if (streamFile == null || streamFile.isEmpty)
      streamFile = "/tmp/frontend-server.stream.checkpoint"

    val host = conf.getString("rtis.config.host")
    val connector = if (conf.hasPath("rtis.config.connector")) conf.getString("rtis.config.connector") else "http"
    val configURL = connector + "://localhost:" + host + "/internal/rtisconfig/config/" + key;

    val mainPath = if (conf.hasPath("stream.queue.location"))
      conf.getString("stream.queue.location") + "/stream" else "/main/stream"

    val result = scala.io.Source.fromURL(configURL).mkString
    val config: List[mutable.HashMap[String, Any]] = RTISConfiguration.getConfig(result)

    var streamList: List[mutable.HashMap[String, Any]] = null;
    // break the event handling processing if key mismatch
    config.foreach(m => {
      m.entrySet().foreach(c => {
        if (c.getValue.equals(key)) {
          validKey = c.getValue.equals(key)
        }

        if (c.getKey.equals(StreamHelper.primaryStreamsKey)) {
          streamList = c.getValue.asInstanceOf[List[mutable.HashMap[String, Any]]]
        } else if (streamList != null && !streamList.isEmpty && c.getKey.equals(StreamHelper.secondaryStreamsKey)) {
          streamList = c.getValue.asInstanceOf[List[mutable.HashMap[String, Any]]]
        }
      })
    })
    if (!validKey) return validKey

    var queue: Any = null
    for(p <- streamList) {
      p.get("queue") != null
      queue = p.get("queue").get
    }
    val RtisStream: String = mainPath + File.separator + queue.toString

    // create stream if not exist
    createIfNotExists(12, RtisStream, mainPath)

    for (c <- config) {
      var key: String = null
      val properties: Properties = new Properties
      var p_list, s_list: Array[mutable.HashMap[String, String]] = null

      for (e <- c.entrySet) {
        e.getKey match {
          case StreamHelper.primaryStreamsKey => {
            if (e.getValue.isInstanceOf[List[mutable.HashMap[String, String]]]) {
              val v = generateCacheStreamList(e, StreamHelper.primaryStreamsKey)
              p_list = v.get
              if (v == None || p_list.size == 0) throw new RTException(ErrorCodes ConfigurationIsNotCorrect, "Could not find primary streams!")
            }
          }
          case StreamHelper.secondaryStreamsKey => {
            if (e.getValue.isInstanceOf[List[mutable.HashMap[String, String]]]) {
              val v = generateCacheStreamList(e, StreamHelper.secondaryStreamsKey)
              s_list = v.get
              if (v == None || s_list.size == 0) throw new RTException(ErrorCodes ConfigurationIsNotCorrect, "Could not find secondary (hot swap) streams!")
            }
          }
          case _ => {
            if (e.getValue.isInstanceOf[String]) {
              val keyValue = e.getValue.asInstanceOf[String]
              val value: String = if (keyValue == null || keyValue.isEmpty) ""
              else (keyValue.replaceAll("\"", ""))
              m_log.debug(e.getKey + ": " + value)
              properties.put(e.getKey, value)
            }
          }
        }
      }

      key = properties.remove("app_key").asInstanceOf[String]
      val clazz = properties.get("class").asInstanceOf[String]
      if (clazz == null || clazz.isEmpty) {
        m_log.error(String.format(ErrorCodes.getDescription(ErrorCodes.InvalidAppConf), "class"))
        throw new RTException(ErrorCodes.ConfigurationIsNotCorrect, clazz)
      }
      m_log.debug(s"Create property for class ${clazz}, and Application key: ${key}, # of entries: " + properties.size)

      p_list.foreach(primary_queue => {
        val pCompositeKey = key +
          StreamHelper.keyConcat +
          primary_queue(StreamHelper.streamKey) +
          StreamHelper.keyConcat +
          primary_queue(StreamHelper.topicKey)

        m_log debug ("Add sender for primary stream: " + pCompositeKey)
        eventSenders(pCompositeKey) = new EventSender(primary_queue(StreamHelper.streamKey), primary_queue(StreamHelper.topicKey), properties)
      })

      s_list.foreach(secondary_queue => {
        val pCompositeKey = key +
          StreamHelper.keyConcat +
          secondary_queue(StreamHelper.streamKey) +
          StreamHelper.keyConcat + secondary_queue(StreamHelper.topicKey)

        m_log debug ("Add sender for secondary stream: " + pCompositeKey)
        eventSenders(pCompositeKey) = new EventSender(secondary_queue(StreamHelper.streamKey), secondary_queue(StreamHelper.topicKey), properties)
      })

      eventHandlerStreams(key) = (p_list, s_list)
      eventHandlerProperties(key) = properties
      StreamHelper.loadActiveStreams
    }
    validKey
  }

  import scala.collection.JavaConversions._

  private def generateCacheStreamList(e: util.Map.Entry[String, Any], key: String): Option[Array[mutable.HashMap[String, String]]] = {
    val ps: List[_ <: mutable.HashMap[String, String]] = e.getValue.asInstanceOf[List[mutable.HashMap[String, String]]]
    val res: Array[mutable.HashMap[String, String]] = ps.map(topic_queue_pair =>
      topic_queue_pair.entrySet().foldLeft(new mutable.HashMap[String, String])((m, qt_entry)
      => m += (qt_entry.getKey ->
          (if ((qt_entry.getValue == null || qt_entry.getValue.isEmpty)) ""
          else (qt_entry.getValue.replaceAll("\"", "")))))
    ).toArray
    m_log.debug(key + ": " + res.mkString("[", ",", "]"))
    Option(res)
  }

  /**
    * Create required MapR streams if they do not exist
    */
  def createIfNotExists(retries: Int, RTISStream: String, mainPath: String) {
    /* Create the parent directory of the stream if it does not exist */
    try
        if (!HFileOperations.exists(mainPath)) {
          HFileOperations.createDir(mainPath)
        }
    catch {
      case e: IOException => {
        m_log.debug("Failed creating main directory: {}", mainPath)
        /* Retry creating directory for some time, as the MapR-FS connection
         * might be intermittently unavailable at system startup */
        retryCreateIfNotExists(e, retries, RTISStream, mainPath)
      }
    }
    /* Create the queue stream */
    m_log.debug("Creating stream for rtis queue: {}", RTISStream)
    val conf = new Configuration()
    val streamAdmin = Streams.newAdmin(conf)
    val desc = Streams.newStreamDescriptor()
    try {
      streamAdmin.createStream(RTISStream, desc)
      m_log.info("Stream created: {}", RTISStream)
    } catch {
      case e: TableExistsException =>
        m_log.debug("Stream already exists, so not creating: {}", RTISStream)
      case e: Exception => {
        m_log.debug("Failed creating stream: {}", RTISStream)
        /* Retry creating stream for some time, as the MapR-FS connection
         * might be intermittently unavailable at system startup */
        retryCreateIfNotExists(e, retries, RTISStream, mainPath)
      }
    } finally {
      streamAdmin.close()
    }
  }

  private def retryCreateIfNotExists(exception: Exception, retries: Int, RTISStream: String, mainPath: String) {
    if (retries == 0) {
      throw exception
    }
    m_log.debug("Waiting for {} seconds and retrying", retrySeconds)
    Thread.sleep(retrySeconds * 1000)
    createIfNotExists(retries - 1, RTISStream, mainPath)
  }
}


abstract class EventHandler {

  var myKey: String = null

  def setStreams(eventHandlerStreams:
                 (Array[mutable.HashMap[String, String]], Array[mutable.HashMap[String, String]])) = {
    maprStreams(StreamHelper.primaryStreamsKey) = eventHandlerStreams._1
    maprStreams(StreamHelper.secondaryStreamsKey) = eventHandlerStreams._2
  }


  val m_log: Logger = LoggerFactory.getLogger(classOf[EventHandler].getName)


  /**
    * Selects the topic to which event has to be sent
    *
    * @param topicSelector - Function which is used to select a topic from the stream
    */
  def selectEventSender(topicSelector: Array[mutable.HashMap[String, String]] => Int) = {
    current_stream = EventHandler.streamPointer(myKey)

    val stream_topic_map = maprStreams(current_stream)(topicSelector(maprStreams(current_stream)))

    val compositeKey: String = myKey +
      StreamHelper.keyConcat +
      stream_topic_map(StreamHelper.streamKey) +
      StreamHelper.keyConcat +
      stream_topic_map(StreamHelper.topicKey)

    m_log trace ("Select event sender: " + compositeKey)
    eventSender = EventHandler.eventSenders(compositeKey)

  }

  def sendMessage(msg: Any, messageID: String): Unit = {
    if (eventSender == null) selectEventSender(EventHandler.topicSelectorFunction)
    m_log debug "Send message to queue: " + eventSender.queue

    val f: Future[Unit] =
      Future {
        try {
          msg match {
            case a: Array[Byte] => eventSender.send(a)
            case s: String => eventSender.send(s, messageID)
            case _ => throw new RTException(ErrorCodes.UnsupportedMsgType, msg.getClass.getName)
          }
        }
        catch {
          case e: RTException => Future.failed(e)
          case e: InterruptedException => Future(new RTException(ErrorCodes.StreamStale, e))
          case e: ExecutionException => Future(new RTException(ErrorCodes.StreamStale, e))
          case e: TimeoutException => Future(new RTException(ErrorCodes.StreamStale, e))
        }
      }

    f onSuccess { case _ => m_log trace "Sent message successfully to queue: " + eventSender.queue }
    f onFailure {
      case ex => m_log error "Could not send message to queue: " + eventSender.queue +
        " " + ex.getMessage;
        m_log.debug(ExceptionUtils.getStackTrace(ex))
    }

  }


  def getEventHandlerProps: Properties = {
    return event_handler_props
  }

  def setEventHandlerProps(event_handler_props: Properties, key: String) {
    this.event_handler_props = event_handler_props
    this.myKey = key
  }

  protected var eventSender: EventSender = null
  private var event_handler_props: Properties = null
  private var maprStreams: scala.collection.mutable.HashMap[String, Array[mutable.HashMap[String, String]]]
  = new scala.collection.mutable.HashMap[String, Array[mutable.HashMap[String, String]]]
  private var current_stream: String = StreamHelper.primaryStreamsKey


  def createMessage(q: scala.collection.immutable.Map[String, Seq[String]]): Unit = {
    m_log trace "create message "
    raw_data = q.foldLeft[JsObject](JsObject(Nil))((acc, kv) => acc ++ (Json obj (kv._1 -> (Json.toJson(kv._2)))))
    m_log trace "Print result: " + (Json prettyPrint (raw_data))
  }

  def createFlattenMessage(q: scala.collection.immutable.Map[String, String]): Unit = {
    m_log trace "create message "
    raw_data = q.foldLeft[JsObject](JsObject(Nil))((acc, kv) => acc ++ (Json obj (kv._1 -> (Json.toJson(kv._2)))))
    m_log trace "Print result: " + (Json prettyPrint (raw_data))
  }

  def processRequest(): (Boolean, String)

  var raw_data: JsObject = null

}

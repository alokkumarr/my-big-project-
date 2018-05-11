package mapr.streaming

import java.util.Properties
import java.util.concurrent.{ExecutionException, TimeoutException}

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}
import exceptions.{ErrorCodes, RTException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by srya0001 on 12/5/2017.
  */
class LogMessageHandler(val cid: String, logType: String ) {

  val m_log: Logger = LoggerFactory.getLogger(classOf[LogMessageHandler].getName)
  val myLogMessageSender = LogMessageHandler.logmessageSender(  cid + LogMessageHandler.delimiter + logType )

  def sendMessage(msg:Any, messageID : String) : Unit =
  {
    m_log debug "Send message to queue: " + myLogMessageSender.queue

    val f: Future[Unit] =
      Future {
        try {
          msg match {
            case a: Array[Byte] => myLogMessageSender.send(a)
            case s: String => myLogMessageSender.send(s, messageID)
            case _ => throw new RTException(ErrorCodes.UnsupportedMsgType, msg.getClass.getName)
          }
        }
        catch{
          case e: RTException => Future.failed(e)
          case e: InterruptedException => Future( new RTException(ErrorCodes.StreamStale, e))
          case e: ExecutionException => Future( new RTException(ErrorCodes.StreamStale, e))
          case e: TimeoutException => Future( new RTException(ErrorCodes.StreamStale, e))
        }
      }

    f onSuccess{ case _ => m_log trace ("Sent message successfully to queue: {}", myLogMessageSender.queue) }
    f onFailure{ case _ => m_log error ("Could not sent message to queue: {}", myLogMessageSender.queue) }

  }



}

object LogMessageHandler{

  protected[streaming] val eventHandlerProperties = new scala.collection.mutable.HashMap[String, Properties]
  protected[streaming] val logmessageSender = new scala.collection.mutable.HashMap[String, EventSender]
  val delimiter = "::"

  private val m_log: Logger = LoggerFactory.getLogger(classOf[LogMessageHandler].getName)

  def loadLogHandlerConf : Unit = {
    import scala.collection.JavaConversions._

    if (eventHandlerProperties.nonEmpty) return

    val conf: Config = ConfigFactory.load
    val mapr_conf: java.util.List[_ <: Config] = conf.getConfigList("genericlog.mapping")
    m_log.debug("Read properties for genericlog.mapping:")

    for (c <- mapr_conf) {
      val properties: Properties = new Properties
      for (e <- c.entrySet() ) {
        val value: String = if (e.getValue == null || e.getValue.render.isEmpty) ""
        else e.getValue.render.replaceAll("\"", "")
        m_log.debug(e.getKey + ": " + value)
        properties.put(e.getKey, value)
      }

      val cust = if ( properties.containsKey("customer") ) Option(properties.remove("customer").asInstanceOf[String]) else None
      val queue = if ( properties.containsKey("queue") ) Option(properties.remove("queue").asInstanceOf[String]) else None
      val topic = if ( properties.containsKey("topic") ) Option(properties.remove("topic").asInstanceOf[String]) else None
      val ltype = if ( properties.containsKey("logtype") ) Option(properties.remove("logtype").asInstanceOf[String]) else None

      m_log debug s"Customer: $cust, Log type: $ltype, Queue: $queue, Topic: $topic"

      if ( cust.isEmpty || ltype.isEmpty || queue.isEmpty || topic.isEmpty )
          throw new RTException(ErrorCodes.ConfigurationIsNotCorrect, " one of these: customer, logtype, queue, topic ")

      val clazz =  if ( properties.containsKey("class") ) Option(properties.remove("class").asInstanceOf[String]) else None
      if (clazz.isEmpty) {
        m_log.error(String.format(ErrorCodes.getDescription(ErrorCodes.InvalidAppConf), "class"))
        throw new RTException(ErrorCodes.ConfigurationIsNotCorrect, clazz)
      }
      m_log.debug(s"Create property for class ${clazz}, and Application key: ${cust+"::"+ltype}, # of entries: " + properties.size)
       logmessageSender(cust.get + delimiter + ltype.get) = new EventSender(queue.get, topic.get, properties)
    }

  }



}

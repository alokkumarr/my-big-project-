package synchronoss.handlers.countly

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone, UUID}

import mapr.streaming.EventHandler
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsArray, JsObject, JsValue, _}

/**
  * Created by srya0001 on 10/17/2016.
  */
class CountlyGenericBridge extends CountlyHandler{

  override val m_log: Logger = LoggerFactory.getLogger(classOf[CountlyGenericBridge].getName)
  private var mEvents : List[JsObject] = null

  def getEvents : List[JsObject] = mEvents

  protected val tr_events = (__ \ 'events)
  protected val tr_metrics = (__ \ 'metrics)
  protected val tr_ud_custom = (__ \ 'user_details \  'custom)
  protected val tr_ud = (__ \ 'user_details )

  private val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  private val tz = TimeZone.getTimeZone("UTC")
  protected val now = Calendar.getInstance(tz)

  // All values should be de-sequenced

  //flatten
  protected var metrics: JsValue = null

  //flatten
  protected var user_details: JsValue = null

  //List, non-flatten
  protected var user_details_custom: JsValue  = null

  //List, non-flatten
  protected var events: JsValue = null

  override def processRequest(): (Boolean, String) =
  {
    if (validateKey(raw_data, "app_key", (__ \ 'app_key)) &&
      validateKey(raw_data, "device_id", (__ \ 'device_id))) {
      val (t_events, workingJSON) = _root_.util.JsProcessor.splitter(raw_data, "events", tr_events)
      val (t_user_details_custom, workingJSON1) = _root_.util.JsProcessor.splitter(workingJSON, "user details/custom", tr_ud_custom)
      val (t_user_details, workingJSON2) = _root_.util.JsProcessor.splitter(workingJSON1, "user details", tr_ud)
      val (t_metrics, workingJSON3) = _root_.util.JsProcessor.splitter(workingJSON2, "metric", tr_metrics)
      mHeader = _root_.util.JsProcessor deSequencer workingJSON3
      events = _root_.util.JsProcessor reparseValue t_events
      metrics = _root_.util.JsProcessor reparseValue t_metrics
      user_details = _root_.util.JsProcessor reparseValue t_user_details
      user_details_custom = _root_.util.JsProcessor reparseValue t_user_details_custom
      (true, "n/a")
    }
    else
    {
      m_log debug "Header is not valid"
      (false, "Mandatory keys not found")
    }
  }

  def processCountlyEvents (): Unit = {
    if (events != null) {
       mEvents =  events.as[JsArray].value.map( e => createEvent(e.as[JsObject])).toList
      m_log debug "Create multiple events from coming request, total number of events: " + mEvents.size
    }
    else {
      mEvents = List(createEvent(null))
      m_log debug "Create events from header only, total number of events: " + mEvents.size
    }
  }


  def createEvent(a: JsObject ): JsObject = {
    var event = createAndGetHeader()
    m_log debug "Header: " + event.toString()

    val eventData = a match {
      case x: JsObject => x.fieldSet.seq.foldLeft[JsObject]( JsObject(Nil) )( ( z, ed) => z + getEventData( ed ) )
      case _ => null
    }
    if ( eventData != null )
      event = event + ( "event" -> eventData )
//        event = event ++ eventData   -- includes flattening

    if (user_details != null)
      event = event + ( "user_details" -> user_details )
//      event = event ++ getUserData   -- includes flattening

    if (user_details_custom != null)
      event = event + ( "user_details_custom" -> user_details_custom )

//    if (getMetrics != null)
    if (metrics != null )
      event = event + ( "metrics" -> metrics )

    if (EventHandler.debugMode)
      m_log info "Print ready to be sent event: " + event.toString
    else
      m_log trace "Print ready to be sent event: " + event.toString

    event
  }

  def getEventData(pair:(String, JsValue) ): (String, JsValue) = {

    val seg_data: JsObject = pair._1 match {
      case "segmentation" => pair._2.as[JsObject]
      case _ => null
    }
    if (seg_data != null) {
      m_log debug "Created segmentation map, values: " + seg_data.values.mkString(",")
      ( "segmentation" -> seg_data )
    }
    pair
  }


  def getUserData: JsObject =
  {
    user_details match {
      case ud:JsValue => { m_log trace "Get User details: " + user_details.toString(); ud.as[JsObject] }
      case _ => null
    }
  }

  def getMetrics: JsObject =
  {
    metrics match {
      case m: JsValue => { m_log trace "Get Metrics: " + metrics.toString();  m.as[JsObject] }
      case _ => null
    }
  }

  def createAndGetHeader(): JsObject =
  {
    val uuid = UUID.randomUUID().toString
    val rts = df.format(now.getTimeInMillis)

    val event = mHeader.foldLeft[JsObject](JsObject( Seq( ("UID", JsString(uuid)),
                    ("received_ts", JsString(rts)) ) ))( (z, d) => z + (d._1 -> JsString(d._2.replace("\"", ""))) )
    m_log trace s"UID: ${uuid},  header: ${play.api.libs.json.Json.stringify(event)}"
    event
  }

  def sendMessages():Unit = {

    if (!EventHandler.debugMode) {
      mEvents.foreach(v => {
        val uid = (v \ "UID").get.as[JsString].value
        val strValue = play.api.libs.json.Json.stringify(v)
        m_log trace s"Print event [ UID: ${uid} ] before sending, size = " + strValue.length + ", Data: " + strValue
        sendMessage(strValue, uid)
      })
      m_log debug "Messages have been sent, # of msgs: " + mEvents.size
    }
  }


}
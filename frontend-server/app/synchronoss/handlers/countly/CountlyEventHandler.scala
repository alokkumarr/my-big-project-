package synchronoss.handlers.countly

import util._
import play.api.libs.json._
import synchronoss.data.countly.model._

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer




class CountlyEventHandler extends CountlyHandler{

  private var mEvents : ListBuffer[Event] = new ListBuffer[Event]

  def getEvents :ListBuffer[Event] = mEvents

  protected val tr_events = (__ \ 'events)
  protected val tr_metrics = (__ \ 'metrics)
  protected val tr_ud_custom = (__ \ 'user_details \  'custom)
  protected val tr_ud = (__ \ 'user_details )

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
      val (t_events, workingJSON) = JsProcessor.splitter(raw_data, "events", tr_events)
      val (t_user_details_custom, workingJSON1) = JsProcessor.splitter(workingJSON, "user details/custom", tr_ud_custom)
      val (t_user_details, workingJSON2) = JsProcessor.splitter(workingJSON1, "user details", tr_ud)
      val (t_metrics, workingJSON3) = JsProcessor.splitter(workingJSON2, "metric", tr_metrics)
      mHeader = JsProcessor deSequencer workingJSON3
      events = JsProcessor reparseValue t_events
      metrics = JsProcessor reparseValue t_metrics
      user_details = JsProcessor reparseValue t_user_details
      user_details_custom = JsProcessor reparseValue t_user_details_custom
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
      mEvents = events.as[JsArray].value.foldLeft[ListBuffer[Event]](ListBuffer[Event]()) {
        (z, e) => z  += createEvent(e.as[JsObject])
      }
      m_log debug "Create multiple events from coming request, size: " +  mEvents.size
    }
    else {
      m_log debug "Before create events from header only, size " + mEvents.size
      mEvents += createEvent(null)
      m_log debug "Create events from header only, size " + mEvents.size
    }
  }

  def getMetrics: Option[Metrics] =
  {
    Option( metrics match {
       case m: JsValue => m.as[JsObject].fieldSet.seq.foldLeft[Metrics](new Metrics())(mapMetricData(_, _))
       case _ => null
       }
     )
  }

  def createEvent(a: JsObject ): Event = {
    val event = createAndGetHeader()
    event.eventData = a match {
      case x: JsObject => x.fieldSet.seq.foldLeft[EventData](new EventData())(getEventData(_, _))
      case _ => null
    }
    event.userDetails = getUSerData match { case Some(x) => x; case None => null }
    event.metrics = getMetrics match { case Some(x) => x; case None => null }
//    m_log debug "Print entire event" + event.toString
    event
  }

    def getEventData(ed: EventData, pair:(String, JsValue) ): EventData = {

      val seg_data: scala.collection.mutable.HashMap[String, String] = pair._1 match {
        case "segmentation" => (pair._2.as[JsObject].fieldSet.seq.
          foldLeft[scala.collection.mutable.HashMap[String, String]]
          (new scala.collection.mutable.HashMap[String, String])(mapSegmentationData(_, _)))
        case _ => null
      }
    if (seg_data != null) {
      ed.segmentation = seg_data.asJava
      m_log debug "Created segmentation map, size: " + seg_data.size + " values: " + seg_data.values.mkString(",")
    }
    else
      mapEventData(ed, pair)
    ed
  }


  def getUSerData: Option[UserDetails] =
  {
      Option(user_details match {
        case ud:JsValue => ud.as[JsObject].fieldSet.seq.foldLeft[UserDetails](new UserDetails())(mapUserData(_, _))
        case _ => null
      } )
  }

  def createAndGetHeader(): Event =
  {
    val ev = mHeader.foldLeft[Event]( new Event() )(mapBaseEventData(_,_))
    ev
  }

  def sendMessages(eventID: String):Unit =
  {
    for (elem <- mEvents) {
      val buf:Array[Byte] = elem.getBytes()
      m_log trace "Print event before sending, size = " + buf.length + ", Data: " + elem.toString
      sendMessage(buf, eventID)
    }
    m_log debug "Messages have been sent, # of msgs: " + mEvents.size
  }

}
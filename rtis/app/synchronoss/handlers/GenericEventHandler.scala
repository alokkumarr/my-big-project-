package synchronoss.handlers

import java.util.{Base64, UUID}

import mapr.streaming.EventHandler
import play.api.libs.json._
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 7/11/2016.
  */
class GenericEventHandler extends EventHandler {

  val m_log_rejected: Logger = LoggerFactory.getLogger("RejectedRequests")
  val mandatoryKeys = Array("APP_KEY", "APP_VERSION", "APP_MODULE", "EVENT_ID", "EVENT_DATE")
  var jResult : JsObject = null

  def validateKey(data: JsObject, k: String, path: JsPath): Boolean =
  {
    //    m_log debug "Validate header"
    val lk = path.json.pick
    val pickResult = data.transform( lk )
    val result: Boolean = pickResult match {
      case JsError(_) =>  m_log debug "Mandatory key " + k + " not found "; false
      case _ => true
    }
    result
  }

  override def processRequest(): (Boolean, String) = {
    val result = mandatoryKeys.exists(mk => validateKey(raw_data, mk, (JsPath \ mk)))
    if (result) {
      val uuid = UUID.randomUUID().toString
      val v = Json.toJson(uuid)
      jResult = raw_data
      jResult += ("UID" -> v)
      (true, uuid)
    }
    else {
      (false, "None")
    }
  }

  def addPayload(id: String, body: Map[String, Array[String]]): JsObject =
  {
    if (body == null || body.isEmpty){
      m_log debug s"Payload of request ${id} is null"
      JsObject(Nil)
    }
    val newEvent :JsObject = body.foldLeft[JsObject](JsObject(Nil))((acc,kv)=>
    acc++(Json obj (kv._1-> ( Json.toJson(kv._2)) )))
    m_log trace Json.prettyPrint(newEvent)
    newEvent
  }

  def addPayload(id: String, body: Array[Byte]): Unit =
  {
      val encoder = Base64.getEncoder
      val encBody = Json.toJson(encoder.encodeToString(body))
      if (encBody != null)
        jResult += ("payload" -> encBody)
      else
        m_log debug s"Payload of request ${id} is null"

      m_log trace Json.prettyPrint(jResult)
  }

  def sendMessage(eventID : String): Unit =
  {
    val serValue : Array[Byte] = Json.stringify(jResult).getBytes("UTF-8")
    m_log trace Json.prettyPrint(jResult)
    super.sendMessage(serValue, eventID)
  }

}

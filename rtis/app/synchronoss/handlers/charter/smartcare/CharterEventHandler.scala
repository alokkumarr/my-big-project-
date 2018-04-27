package synchronoss.handlers.charter.smartcare

import java.util.NoSuchElementException

import com.fasterxml.jackson.databind.node.ObjectNode
import controllers.Stat
import exceptions.{ErrorCodes, RTException}
import mapr.streaming.EventHandler
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsObject, JsValue}
import play.libs.Json
import play.mvc.Result
import synchronoss.handlers.GenericEventHandler


object CharterEventHandler
{
  val CHARTER_APP_KEY = "Charter.SmartCare"
  val m_log: Logger = LoggerFactory.getLogger(classOf[CharterEventHandler].getName)
  val m_log_rejected: Logger = LoggerFactory.getLogger("RejectedRequests")


  def process(query: Map[String, String], payload: Array[Byte]): Result =
  {
    val res: ObjectNode = Json.newObject
    var eventHandler: CharterEventHandler = null
    try {
      eventHandler = EventHandler.getEventHandler[CharterEventHandler](CHARTER_APP_KEY)
      if ( eventHandler == null) {
        Stat.getRejectedRequestStat(CHARTER_APP_KEY, 1)
        throw new RTException(ErrorCodes.NoCharterHandler)
      }
    }
    catch
    {
      case e: NoSuchElementException => {
                                          Stat.getRejectedRequestStat(CHARTER_APP_KEY, 1)
                                          throw new RTException(ErrorCodes.NoGenericHandler)
                                        }
    }

//    eventHandler.createMessage(query)
    eventHandler.createFlattenMessage(query)
    val (validationResult, id) = eventHandler.processRequest()
    if (validationResult){
      if (payload != null && payload.length > 0) {
        if (eventHandler.parseAndValidatePayload(payload)) {
          try {
            eventHandler.sendMessage(query("EVENT_ID"))
            Stat.getRequestStat(CharterEventHandler.CHARTER_APP_KEY, 1)
            res.put("result", "success")
            res.put("registeredID", id)
          }
          catch {
            case e: RTException =>
              res.put("result", "failure")
              val reason = "Could not send message to RTPS: " + e.getMessage
              res.put("reason", reason)
              m_log_rejected error s"Request ${id} " + reason
              Stat.getRejectedRequestStat(CHARTER_APP_KEY, 1)
              play.mvc.Results.internalServerError(res)

            case e: Exception =>
              res.put("result", "failure")
              val reason = "Could not merge payload. Perhaps JSON document is malformed"
              res.put("reason", reason)
              m_log_rejected error s"Request ${id} " + reason
              Stat.getRejectedRequestStat(CHARTER_APP_KEY, 1)
              play.mvc.Results.badRequest(res)
          }
        }
        else {
          res.put("result", "failure")
          val reason = "Payload is null or invalid: malformed, not parsable"
          res.put("reason", reason)
          m_log_rejected error s"Request ${id} " + reason
          Stat.getRejectedRequestStat(CHARTER_APP_KEY, 1)
          play.mvc.Results.badRequest(res)
        }
      }
      else{
        res.put("result", "failure")
        val reason =  "Payload is null/empty"
        res.put("reason", reason)
        m_log_rejected error s"Request ${id} " + reason
        Stat.getRejectedRequestStat(CHARTER_APP_KEY, 1)
        play.mvc.Results.badRequest(res)
      }
    }
    else {
      res.put("result", "failure")
      val reason = "Mandatory keys are not provided"
      res.put("reason", reason)

      Stat.getRejectedRequestStat(CHARTER_APP_KEY, 1)
      m_log_rejected error s"Request ${id} was rejected: ${reason}"

      return play.mvc.Results.badRequest(res)
    }
    play.mvc.Results.ok(res)
  }

}

/**
  * Created by srya0001 on 10/6/2016.
  */



class CharterEventHandler extends GenericEventHandler {

  var payloadAsJSON : JsValue  = null

  // The method merges payload and initial JSON into one document
  private def mergePayload()  =
  {
    try {
      //feature: field values from body (including named by parameters: APP_KEY, APP_MODULE...) [payloadAsJSON]
      // will prevail over values from parameters [jResult]
      jResult ++ payloadAsJSON.as[JsObject]
    }
    catch {
      case  e: Exception => throw new Exception("could not merge parameter and body")
    }
  }



  //Parse and validate result.
  // Put here any additional processing for JSON
  // from
  private def parseAndValidatePayload(payload: Array[Byte]): Boolean =
  {
    try {
      payloadAsJSON = play.api.libs.json.Json.parse(payload)
      m_log trace "Payload: " + play.api.libs.json.Json.prettyPrint(payloadAsJSON)
      if (payloadAsJSON == null)  {
        m_log.error("Could not parse payload")
        false
      }
      else
      {
        import play.api.libs.json.Reads.JsObjectReads
        val valRes = payloadAsJSON.validate
        valRes.isSuccess
      }
    }catch
    {
      case e: Exception => false
    }
  }


  override def sendMessage(eventID: String): Unit =
  {
    val serValue : String = play.api.libs.json.Json.stringify(mergePayload)
    m_log trace "Merged JSON: " + serValue
    super.sendMessage(serValue, eventID)
  }


}

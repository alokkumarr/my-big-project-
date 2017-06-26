package controllers

import java.util.NoSuchElementException

import com.fasterxml.jackson.databind.node.ObjectNode
import exceptions.{ErrorCodes, RTException}
import io.swagger.annotations.{ApiResponses, _}
import mapr.streaming.EventHandler
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.{Controller, Http, Result}
import synchronoss.handlers.GenericEventHandler
import synchronoss.handlers.charter.smartcare.CharterEventHandler

/**
  * Created by srya0001 on 6/28/2016.
  */

@Api(value = "SynchronossGenericEvent",
  produces = "application/json",
  consumes = "application/octet-stream")
class GenericHandler extends Controller {

  EventHandler.buildEventHandlerList
  val m_log: Logger = LoggerFactory.getLogger(classOf[GenericHandler].getName)


  @ApiOperation(
    nickname = "SyncronossEventWithPayload",
    value = "Method registers a Synchronoss generic event with payload",
    notes = "The interface is designed to accepts events in JSON format and sends them to streams (PI BDA platform)",
    httpMethod = "POST",
    tags = Array("publishevent")
  )
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "RTI internal exceptions"),
    new ApiResponse(code = 400, message = "Bad request: interface accepts only event related data"),
    new ApiResponse(code = 200, message = "Success")))
  def event(APP_KEY:String, APP_VERSION:String, APP_MODULE:String,
            EVENT_ID:String, EVENT_DATE:String,EVENT_TYPE:Option[String],
            payload:Option[String]): Result = {

    m_log debug s"Start event processing  [ Event ID: ${EVENT_ID} ]"

//    if (EventHandler.isStreamMalfunctioning)
//        return failure500("Service is not available. Please call for support")

    val ctx: Http.Context = Http.Context.current.get
    m_log.debug("Raw request: " + ctx._requestHeader.rawQueryString)
    if (APP_KEY == null || APP_KEY.isEmpty)
      return failure400("Application key is null or empty", "UNKNOWN")
    else if (!validateAppKey(APP_KEY))
      failure400("Application key does not exists", "UNKNOWN")
    val genericEventHandlerAppKey = APP_KEY.replaceAll("\"", "")

    if (APP_VERSION == null || APP_VERSION.isEmpty)
      return failure400("Application version is null or empty", genericEventHandlerAppKey)
    if (APP_MODULE == null || APP_MODULE.isEmpty)
      return failure400("Application module is null or empty", genericEventHandlerAppKey)
    if (EVENT_ID == null || EVENT_ID.isEmpty)
      return failure400("Event ID is null or empty", genericEventHandlerAppKey)
    if (EVENT_DATE == null || EVENT_DATE.isEmpty)
      return failure400("Event timestamp is null or empty", genericEventHandlerAppKey)

    val body = ctx.request.body.asBytes().toArray

    m_log.debug("BODY = " + new String(body))


    val query: scala.collection.immutable.Map[String, String] =
      Array(GenericHandler.KEY_APP_KEY, GenericHandler.KEY_APP_VERSION, GenericHandler.KEY_APP_MODULE,
          GenericHandler.KEY_EVENT_ID, GenericHandler.KEY_EVENT_DATE, GenericHandler.KEY_EVENT_TYPE, GenericHandler.KEY_RECEIVED_TS).map(
        _ match {
          case GenericHandler.KEY_APP_KEY => GenericHandler.KEY_APP_KEY -> APP_KEY
          case GenericHandler.KEY_APP_VERSION => GenericHandler.KEY_APP_VERSION -> APP_VERSION
          case GenericHandler.KEY_APP_MODULE => GenericHandler.KEY_APP_MODULE -> APP_MODULE
          case GenericHandler.KEY_EVENT_ID => GenericHandler.KEY_EVENT_ID -> EVENT_ID
          case GenericHandler.KEY_EVENT_DATE => GenericHandler.KEY_EVENT_DATE -> EVENT_DATE
          case GenericHandler.KEY_EVENT_TYPE => GenericHandler.KEY_EVENT_TYPE -> EVENT_TYPE.get
          case GenericHandler.KEY_RECEIVED_TS =>
            GenericHandler.KEY_RECEIVED_TS -> new DateTime().toString(GenericHandler.timestampFormat)
        }
      ).toMap

    m_log.debug("QUERY = " + query)

    if (genericEventHandlerAppKey.equals(CharterEventHandler.CHARTER_APP_KEY)) {
      CharterEventHandler.process(query, body)
    }
    else {
      process(genericEventHandlerAppKey, query, body)
    }
  }

  private def process(genericEventHandlerAppKey: String, query: Map[String, String], payload:Array[Byte] ) : Result =
  {
    val res: ObjectNode = Json.newObject

    var eventHandler: GenericEventHandler = null
    try {
      eventHandler = EventHandler.getEventHandler[GenericEventHandler](genericEventHandlerAppKey)
      if (eventHandler == null) eventHandler = EventHandler.getEventHandler[GenericEventHandler]("generic")
    }
    catch {
      case e: NoSuchElementException => {
            Stat.getRejectedRequestStat(genericEventHandlerAppKey, 1)
            throw new RTException(ErrorCodes.NoGenericHandler)
          }
    }

//    eventHandler.createMessage(query)
    eventHandler.createFlattenMessage(query)
    val (validationResult, id) = eventHandler.processRequest()
    if (validationResult) {
      if (payload != null) eventHandler.addPayload(id, payload)
      eventHandler.sendMessage(query("EVENT_ID"))
      Stat.getRequestStat(genericEventHandlerAppKey, 1)
      res.put("result", "success")
      res.put("registeredID", id)
      return play.mvc.Results.ok(res)

    }
    else {
      failure400("Mandatory keys are not provided", "UNKNOWN")
    }
  }

  val m_log_rejected: Logger = LoggerFactory.getLogger("RejectedRequests")

  def failure400(msg: String, appKey : String): Result = {
    val res: ObjectNode = Json.newObject
    Stat.getRejectedRequestStat(appKey, 1)
    m_log_rejected.error(s"APP_KEY = ${appKey}, Rejected reason: ${msg}")
    res.put("result", "failure")
    res.put("reason", msg)
    return play.mvc.Results.badRequest(res)
  }

  def failure500(msg: String): Result = {
    val res: ObjectNode = Json.newObject
    m_log.error("Stream is stale, the server should be restarted.")
    res.put("result", "failure")
    res.put("reason", msg)
    return play.mvc.Results.internalServerError(res)
  }

  private def validateAppKey(appKey: String) : Boolean =
  {
    val appKeys = EventHandler.getAppKeys
    appKeys.contains(appKey)
  }


}

object GenericHandler {
  val KEY_APP_KEY     = "APP_KEY"
  val KEY_APP_VERSION = "APP_VERSION"
  val KEY_APP_MODULE  = "APP_MODULE"
  val KEY_EVENT_ID    = "EVENT_ID"
  val KEY_EVENT_DATE  = "EVENT_DATE"
  val KEY_EVENT_TYPE  = "EVENT_TYPE"
  val KEY_RECEIVED_TS = "received_ts"


  val timestampFormat = "yyyy-MM-dd HH:mm:ss"
}


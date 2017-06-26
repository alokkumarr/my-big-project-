package controllers

import java.util
import java.util.UUID

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import exceptions.{ErrorCodes, RTException}
import io.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses, _}
import mapr.streaming.EventHandler
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._
import play.libs.Json
import play.mvc.{Controller, Http, Result}
import synchronoss.handlers.countly.{CountlyCrashBridge, CountlyGenericBridge}

import scala.collection.Seq


@Api(value = "SynchronossClientEvents",
  produces = "application/json",
  consumes = "application/json")
class Application extends Controller {

  val countlyEventHandler = "countly_event"
  val countlyCrashHandler = "countly_crash"


  EventHandler.buildEventHandlerList
  val m_log: Logger = LoggerFactory.getLogger(classOf[Application].getName)

  private def success(): Result = {
    val res: ObjectNode = Json.newObject
    res.put("result", "success")
//    Http.Context.Implicit.response().setContentType("application/json; charset=utf-8")
    return play.mvc.Results.ok(res)
  }

  private def failure(res: JsonNode): Result = {
    return play.mvc.Results.internalServerError(res)
  }

  private def failure400(appKey: String, res: JsonNode): Result = {
    Stat.getRejectedRequestStat(appKey, 1)
    return play.mvc.Results.badRequest(res)
  }

  private def failure500(msg: String): Result = {
    val res: ObjectNode = Json.newObject
    m_log.error("Stream is stale, the server should be restarted.")
    res.put("result", "failure")
    res.put("reason", msg)
    return play.mvc.Results.internalServerError(res)
  }

  def index: Result = {
    val jsonResponse: JsonNode = Json.toJson("Your new application is ready.")
    val st = play.mvc.Results.ok(jsonResponse) ;
    st
  }

  private def queryIsValid(query: Map[String, Seq[String]]): Boolean =
  {
    query.contains("events") || query.contains("metrics")
  }

  private def isWindowsPhoneQuery(query: Map[String, Seq[String]]): Boolean =
  {
    query.contains("request")
  }



  @ApiOperation(
    nickname = "eventSyncronoss",
    value = "Method registers an event",
    notes = "The interface is designed to accepts events in JSON format and sends them to streams (PI BDA platform)",
    httpMethod = "GET",
    tags = Array("i")
  )
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "RTIS internal exceptions"),
    new ApiResponse(code = 400, message = "Bad request: interface accepts only event related data"),
    new ApiResponse(code = 200, message = "Success")))
  def i : Result = {

    m_log debug "Inside genericI method"

    val result: ObjectNode = Json.newObject
    val ctx: Http.Context = Http.Context.current.get
    val query: Map[String, Seq[String]] = ctx._requestHeader().queryString

    m_log.debug("QUERY STRING = " + query.toString)

    if (EventHandler.debugMode)
      m_log.info("Raw request: " + ctx._requestHeader.rawQueryString)
    else
      m_log.debug("Raw request: " + ctx._requestHeader.rawQueryString)

    if ( isWindowsPhoneQuery(query))
    {
      val msg = "Unsupported event format"
      m_log warn msg
      result.put("result", msg)
      failure400(countlyEventHandler, result)
    }

    if (queryIsValid(query))
    {
      m_log debug "Send events"

      val appKey = query("app_key").mkString

      m_log.debug("APP KEY = " + appKey)

      m_log.debug("Getting event handler")

      val countly_event_handler:CountlyGenericBridge = EventHandler.getEventHandler[CountlyGenericBridge](appKey)
      if (countly_event_handler == null) throw new RTException(ErrorCodes.NoEventHandler)

      countly_event_handler.createMessage(query)

      val (validationResult, msg) = countly_event_handler.processRequest

      m_log.debug("VALIDATION RESULT = " + validationResult.toString)
      m_log.debug("Message = " + msg)

      if (validationResult) {
        countly_event_handler.processCountlyEvents
        countly_event_handler.sendMessages()
      }
      else{
        m_log warn "Incorrect event request received"
        result.put("result", msg)
        failure400(countlyEventHandler, result)
      }
    }
    success
  }


  @ApiOperation(
    nickname = "crashReportSyncronoss",
    value = "Method registers crash reports",
    notes = "The interface is designed to accept crash report in JSON format and sends them to streams (PI BDA platform)",
    httpMethod = "POST",
    tags = Array("i")
  )
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "RTI internal exceptions"),
    new ApiResponse(code = 400, message = "Bad request: interface accepts only crash related data"),
    new ApiResponse(code = 200, message = "Success")))
  def iPost: Result = {

//    if (EventHandler.isStreamMalfunctioning)
//      return failure500("Service is not available. Please call for support")
    handleCrash
  }

  private def handleCrash() : Result =
  {

    val uuid = UUID.randomUUID().toString
    m_log debug s"Start crash processing  [ Crash ID: ${uuid} ]"

    val countly_crash_handler:CountlyCrashBridge = EventHandler.getEventHandler[CountlyCrashBridge](countlyCrashHandler)
    if (countly_crash_handler  == null) throw new RTException(ErrorCodes.NoCrashReportSender)
    val result: ObjectNode = Json.newObject
    val ctx: Http.Context = Http.Context.current.get
    import scala.collection.JavaConversions._
    val req = ctx.request()
    val url_enc: util.Map[String, Array[String]] =  req.body().asFormUrlEncoded()

    if (EventHandler.debugMode) {
      m_log info "Print request body: "
      url_enc.keySet().foreach((x: String) => {
        m_log info "Key: " + x + " values: " + url_enc(x).mkString("[", ",", "]")
      })
    }
    val query: scala.collection.immutable.Map[String, Seq[String]] =
    url_enc.filter( !_._1.equalsIgnoreCase("crash")).map( kv => (kv._1 -> kv._2.toSeq)).toMap
    m_log debug "Send crash reports: "
    countly_crash_handler.createMessage(query)
    if (url_enc.contains("crash"))
    {
      countly_crash_handler.addCrashData(play.api.libs.json.Json.parse(url_enc("crash").mkString))
    }
    val ( validationResult, msg) = countly_crash_handler.processRequest
    if (validationResult) {
      countly_crash_handler.createCrashReport(uuid)
      countly_crash_handler.sendMessages(uuid)
      result.put("result", "Success")
      Stat.getRequestStat(countlyCrashHandler, 1)
    }
    else
    {
      result.put("result", msg)
      failure400(countlyCrashHandler, result)
    }
    success
  }


}
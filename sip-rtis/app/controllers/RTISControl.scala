package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import control.RTISServerCMD
import io.swagger.annotations.ApiOperation
import mapr.streaming.EventHandler
import org.json4s.native.JsonMethods.{parse => _}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsObject, JsString}
import play.libs.Json
import play.libs.Json._
import play.mvc.{Controller, Http, Result}

/**
  * Created by srya0001 on 10/6/2016.
  */
class RTISControl extends Controller
{

  EventHandler.buildEventHandlerList
  val m_log: Logger = LoggerFactory.getLogger(classOf[RTISControl].getName)


  def handleEmptyRequest( msg: String ) : Result = {
    val ctx: Http.Context = Http.Context.current.get
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", "empty request")
    m_log.debug(s"Empty request with $msg content type came from: ${ctx.request().host()}/${ctx.request().username()}")
    play.mvc.Results.badRequest(res)
  }


  @ApiOperation( hidden = true,
    nickname = "Control basic interface",
    value = "ControlBaseInterface"
  )
  def executeCmd(CMD:String, PARAMETERS:Option[String]) : Result =
  {
    val result: ObjectNode = newObject
    if (CMD.isEmpty)
    {
      result.put("error", "CMD is empty")
      play.mvc.Results.badRequest(result)
    }

    if(RTISServerCMD.CMDs.contains(CMD)){

      val parametersAsJson = if (PARAMETERS != None) play.api.libs.json.Json.parse(PARAMETERS.get) else null
      val rtiscmd = new RTISServerCMD(CMD, parametersAsJson )

      m_log.debug("CMD: " + CMD + ", Validate and Execute: \n" + (if(parametersAsJson != null) parametersAsJson else "n/a"))
      if (rtiscmd.valid) {
        val res = rtiscmd.execute
        m_log.debug("CMD: " + CMD + ", Response: \n" +  play.api.libs.json.Json.asciiStringify(res))
//        return play.mvc.Results.ok(play.api.libs.json.Json.prettyPrint(res))
        return play.mvc.Results.ok(play.api.libs.json.Json.asciiStringify(res))
      }
      else{
        result.put("error", "Command is not valid")
        play.mvc.Results.badRequest(result)
      }
    }
    else{
      result.put("error", "Unsupported command")
      play.mvc.Results.badRequest(result)
    }
  }

  @ApiOperation( hidden = true,
    nickname = "Control Extended Interface",
    value = "ControlExtendedInterface"
  )
  def executeExtendedCmd(CMD:String) : Result =
  {
    val result: ObjectNode = newObject
    val ctx: Http.Context = Http.Context.current.get
    val header = ctx._requestHeader()
    var PARAMETERS : String = null
    header.contentType match {
      case None => if (ctx.request.body.asText == null) handleEmptyRequest("text/plain")
      else PARAMETERS = ctx.request.body.asText()
      case _ => header.contentType.get match {
        case "text/plain" => if (ctx.request.body.asText == null) handleEmptyRequest("text/plain")
        else PARAMETERS = ctx.request.body.asText()
        case "application/x-www-form-urlencoded" => if (ctx.request.body.asFormUrlEncoded == null) handleEmptyRequest("application/x-www-form-urlencoded")
        else {
          result.put("result", "failure")
          result.put("reason", s"Unsupported content type: ${header.contentType}")
          m_log.debug(s"Unprocessed request: ${ctx._requestHeader.rawQueryString}")
          return play.mvc.Results.badRequest(result)
        }
        case "application/json" => if (ctx.request.body.asJson == null) handleEmptyRequest("application/json")
        else {
          PARAMETERS = ctx.request.body.asJson.toString
        }
        case _ =>
          result.put("result", "failure")
          result.put("reason", s"Unsupported content type: ${header.contentType}")
          m_log.debug(s"Unprocessed request: ${ctx._requestHeader.rawQueryString}")
          return play.mvc.Results.badRequest(result)
      }
    }
    if(RTISServerCMD.CMDs.contains(CMD)){

      val parametersAsJson = if (PARAMETERS != None) play.api.libs.json.Json.parse(PARAMETERS) else null
      val rtiscmd = new RTISServerCMD(CMD, parametersAsJson )

      m_log.debug("CMD: " + CMD + ", Validate and Execute: \n" + (if(parametersAsJson != null) parametersAsJson else "n/a"))
      if (rtiscmd.valid) {
        val res = rtiscmd.execute
        m_log.debug("CMD: " + CMD + ", Response: \n" +  play.api.libs.json.Json.asciiStringify(res))
        //        return play.mvc.Results.ok(play.api.libs.json.Json.prettyPrint(res))
        play.mvc.Results.ok(play.api.libs.json.Json.asciiStringify(res))
      }
      else{
        result.put("error", "Command is not valid")
        play.mvc.Results.badRequest(result)
      }
    }
    else{
      result.put("error", "Unsupported command")
      play.mvc.Results.badRequest(result)
    }
  }

  @ApiOperation( hidden = true,
    nickname = "status report",
    value = "sr"
  )
  def sr(parameters:Option[String]): Result = {

/*
    if (EventHandler.isStreamMalfunctioning){
      val result : JsObject = new JsObject(List(
        "service" -> JsString("FrontEnd-Server"),
        "status" -> JsString("Stale"))
        .toMap)
      return play.mvc.Results.ok(result.toString())
    }
*/

    val result : JsObject = new JsObject(List(
      "service" -> JsString("FrontEnd-Server"),
      "status" -> JsString("Alive"))
    .toMap)

    val rtiscmd = new RTISServerCMD("sr", if (parameters != None) play.api.libs.json.Json.parse(parameters.get) else null)
    if (rtiscmd.valid) {
      val res : JsObject =  rtiscmd.execute
      val final_res = result ++ res
      m_log.debug("Result: " + final_res.toString())
      play.mvc.Results.ok(final_res.toString())
    }
    else
    {
      val res: ObjectNode = newObject
      res.put("error", "Command syntax not valid")
      play.mvc.Results.badRequest(res)
    }
  }

}

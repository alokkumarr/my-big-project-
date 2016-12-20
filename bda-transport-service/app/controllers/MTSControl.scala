package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import org.json4s.native.JsonMethods.{parse => _}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsObject, JsString}
import play.libs.Json._
import play.mvc.{Controller, Result}

/**
  * Created by srya0001 on 10/6/2016.
  */
class MTSControl extends Controller
{

  val m_log: Logger = LoggerFactory.getLogger(classOf[MTSControl].getName)

  def executeCmd(CMD:String, PARAMETERS:Option[String]) : Result =
  {
    val result: ObjectNode = newObject
    if (CMD.isEmpty)
    {
      result.put("error", "CMD is empty")
      play.mvc.Results.badRequest(result)
    }
    result.put("error", "CMD is empty")
    return play.mvc.Results.ok(result)

  }

  def executeExtendedCmd(CMD:String) : Result =
  {
    val res: ObjectNode = newObject
    res.put("result", "success")
    return play.mvc.Results.ok(res)
  }

  def sr(parameters:Option[String]): Result = {
    val result : JsObject = new JsObject(List(
      "service" -> JsString("Middle-Tier-Server"),
      "status" -> JsString("Alive"))
    .toMap)
    play.mvc.Results.ok(result.toString())
  }


  def index : Result =
  {
    val res: ObjectNode = newObject
    res.put("result", "Middle-Tier-Sever auto-response")
    return play.mvc.Results.ok(res)
  }


}

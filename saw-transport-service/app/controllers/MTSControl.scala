package controllers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.json4s.native.JsonMethods.{parse => _}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsObject, JsString}
import play.libs.Json._
import play.mvc.{Controller, Http, Result}
import sncr.metadata.objectstore.NodeStore

/**
  * Created by srya0001 on 10/6/2016.
  */
class MTSControl extends Controller
{

  val m_log: Logger = LoggerFactory.getLogger(classOf[MTSControl].getName)

  def executeCmd(CMD:String, PARAMETERS:Option[String]) : Result =
  {
    val res: ObjectNode = newObject
    res.put("result", "success")
    return play.mvc.Results.ok(res)
  }

  def executeExtendedCmd(CMD:String) : Result =
  {
    val result: ObjectNode = newObject
    m_log debug "Create NodeStore"
    val ns = new NodeStore("UI")
    if (CMD.isEmpty)
    {
      result.put("error", "CMD is empty")
      play.mvc.Results.badRequest(result)
    }

    val cmd_data = CMD.split("-")
    m_log debug s"CMD = ${cmd_data(0)} and ${cmd_data(1)}"

    val ctx: Http.Context = Http.Context.current.get
    val header = ctx._requestHeader()
    var cnt : String = null
    var jn : JsonNode = null
    header.contentType match {
      case None => if (ctx.request.body.asText == null) m_log debug "No content at all"
      else cnt = ctx.request.body.asText()
      case _ => header.contentType.get match {
        case "text/plain" => if (ctx.request.body.asText == null) m_log debug "No content - text/plain"
                            else {
                                cnt = ctx.request.body.asText()
                                jn =  play.libs.Json.parse(cnt)
                            }
        case "application/x-www-form-urlencoded" => if (ctx.request.body.asFormUrlEncoded == null) m_log debug "No content - application/x-www-form-urlencoded"
        else {
          result.put("result", "failure")
          result.put("reason", s"Unsupported content type: ${header.contentType}")
          m_log.debug(s"Unprocessed request: ${ctx._requestHeader.rawQueryString}")
          return play.mvc.Results.badRequest(result)
        }
        case "application/json" => if (ctx.request.body.asJson == null) m_log debug "No content - application/json"
        else {
          jn = ctx.request.body.asJson
          cnt = ctx.request.body.asJson.toString
        }
        case _ =>
      }
    }


    cmd_data(0) match {
      case "md_create" => { val md_res = ns.createMDNode(cmd_data(1), cnt ); result.put("MDOperationResult", md_res) }
      case "md_update" => { val md_res = ns.updateMDNode(cmd_data(1), cnt); result.put("MDOperationResult", md_res)}
      case "md_retrieve" => {
        val resMap = ns.readMDNode(cmd_data(1), true)
        .getOrElse("Node does not exists: " + cmd_data(1)).asInstanceOf[Map[String, Any]]
        result.put("MDOperationResult", resMap.mkString("{", ",", "}"))}
      case "md_delete" => { val md_res = ns.deleteMDNode(cmd_data(1)); result.put("MDOperationResult", md_res)}
      case "md_search"  => {
              if ( jn == null)
              {
                result.put("result", "failure")
                result.put("reason", s"Incorrect request")
                return play.mvc.Results.badRequest(result)
              }
              import scala.collection.JavaConversions._
              var c = 0
              val sc : Map[String, String] = (for( e <- jn.fields()) yield (e.getKey -> e.getValue.asText())).toMap
              val rowKeyList = ns.searchMetadata( sc, "and" )
              rowKeyList.foreach( n => {result.put(s"Node ID-${c}", new String(n)); c +=1})
              val nodes = ns.loadMDNodes(rowKeyList, true)
              result.put("MDOperationResult", if (nodes != null && !nodes.isEmpty) nodes.mkString else "No data found/incorrect request")
      }
      case "md_scan"  => {
        val rowKeyList = ns.scanMDNodes
        val nodes = ns.loadMDNodes(rowKeyList, true)
        result.put("MDOperationResult", if (nodes != null && !nodes.isEmpty) nodes.mkString else "No data found/incorrect request")
      }
    }
    ns.close
    play.mvc.Results.ok(result)
  }

  def sr(parameters:Option[String]): Result = {
    val result : JsObject = new JsObject(List(
      "service" -> JsString("SAW-Service"),
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

package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.{Controller, Http, Result}

/**
  * Created by srya0001 on 2/17/2017.
  */
trait BaseServiceProvider extends Controller {

  val m_log: Logger = LoggerFactory.getLogger(classOf[BaseServiceProvider].getName)

  def handleEmptyRequest( msg: String ) : Result = {
    val ctx: Http.Context = Http.Context.current.get
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", "empty request")
    m_log.debug(s"Empty request with $msg content type came from: ${ctx.request().host()}/${ctx.request().username()}")
    play.mvc.Results.badRequest(res)
  }

  def handleRequest: Result = {

    val ctx: Http.Context = Http.Context.current.get

    val header = ctx._requestHeader()

    val res: ObjectNode = Json.newObject

    m_log.trace("Request: {} {}", ctx.request, ctx.request.body.asJson: Any)
    val response = header.contentType match {
      case None  => if (ctx.request.body.asText == null) handleEmptyRequest("text/plain")
      else process(ctx.request.body.asText())
      case _ =>  header.contentType.get match {
        case "text/plain" => if (ctx.request.body.asText == null) handleEmptyRequest("text/plain")
        else process(ctx.request.body.asText())
        case "application/x-www-form-urlencoded" => if (ctx.request.body.asFormUrlEncoded == null) handleEmptyRequest("application/x-www-form-urlencoded")
        else {
          val requestBody =  ctx.request.body.asFormUrlEncoded().toString
          m_log debug s"URL encoded: $requestBody"
          process(requestBody)
        }
        case "application/json" => if (ctx.request.body.asJson == null) handleEmptyRequest("application/json")
        else { val body = ctx.request.body.asJson
          process(body.toString)
        }
        case "octet/stream" =>  if (ctx.request.body.asBytes.toArray == null) handleEmptyRequest("octet/stream")
        else process(ctx.request.body.asBytes.toArray)
        case _ =>
          res.put("result", "failure")
          res.put("reason", s"Unsupported content type: ${header.contentType}")
          m_log.debug(s"Unprocessed request: ${ctx._requestHeader.rawQueryString}")
          play.mvc.Results.badRequest(res)

      }
    }
    m_log.trace("Response: {} {}", response.status, response.headers)
    response
  }

  def process(arr: Array[Byte]): Result = ???

  def process(txt: String): Result = ???


}

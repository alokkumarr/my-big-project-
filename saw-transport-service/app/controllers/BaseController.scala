package controllers

import java.text.SimpleDateFormat

import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{compact, parse, pretty, render}
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.{Controller, Http, Result, Results}

import model.ClientException

class BaseController extends Controller {
  val log: Logger = LoggerFactory.getLogger(classOf[BaseController].getName)
  /* Keep old logger name for backward compatibility with existing code */
  val m_log = log

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  protected def handle(process: JValue => JValue): Result = {
    val response = handleResponse(process)
    log.trace("Response: {} {}", response.status, response.headers)
    response
  }

  private def handleResponse(process: JValue => JValue): Result = {
    val ctx: Http.Context = Http.Context.current.get
    val header = ctx._requestHeader()
    log.trace("Request: {} {}", ctx.request,
      Json.prettyPrint(ctx.request.body.asJson): Any)
    val body = header.contentType match {
      case None => throw new ClientException(
        "Content-type header is missing")
      case _ => header.contentType.get match {
        case "application/json" => {
          val body = ctx.request.body.asJson
          if (body == null) {
            throw new ClientException("Request body is empty")
          }
          parse(body.toString)
        }
        case "text/plain" => ("body", ctx.request.body.asText) : JObject
        case contentType => throw new ClientException(
          "Unhandled content type: " + contentType)
      }
    }
    try {
      Results.ok(playJson(process(body)))
    } catch {
      case ClientException(message) => userErrorResponse(message)
      case e: Exception => {
        log.error("Internal server error", e)
        serverErrorResponse(e.getMessage())
      }
    }
  }

  protected def playJson(json: JValue) = {
    log.trace("Response body: {}", pretty(render(json)))
    Json.parse(compact(render(json)))
  }

  protected def userErrorResponse(message: String): Result = {
    val response: JObject = ("error", ("message", message))
    Results.badRequest(playJson(response))
  }

  protected def serverErrorResponse(message: String): Result = {
    val response: JObject = ("error", ("message", message))
    Results.internalServerError(playJson(response))
  }

  protected def shortMessage(message: String) = {
    message.substring(0, Math.min(message.length(), 1500))
  }

  protected def unexpectedElement(expected: String, obj: JValue): Nothing = {
    val name = obj.getClass.getSimpleName
    throw new RuntimeException(
      "Expected %s but got: %s".format(expected, name))
  }
}

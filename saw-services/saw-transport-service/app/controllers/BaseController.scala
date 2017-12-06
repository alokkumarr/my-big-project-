package controllers

import java.text.SimpleDateFormat
import java.security.Key

import io.jsonwebtoken.Jwts

import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{compact, parse, pretty, render}
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.{Controller, Http, Result, Results}
import model.{ClientException, TransportUtils}

class BaseController extends Controller {
  val log: Logger = LoggerFactory.getLogger(classOf[BaseController].getName)
  /* Keep old logger name for backward compatibility with existing code */
  val m_log = log

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  protected def handle(
    process: (JValue, Option[Ticket]) => JValue): Result = {
    val response = handleResponse(process)
    log.trace("Response: {} {}", response.status, response.headers)
    response
  }

  private def handleResponse(
    process: (JValue, Option[Ticket]) => JValue): Result = {
    val ctx: Http.Context = Http.Context.current.get
    val header = ctx._requestHeader()
    log.trace("Request: {} {}", ctx.request.method, ctx.request.uri : Any)
    log.trace("Request headers: {}", ctx.request.headers)
    log.trace("Request body: {}", Json.prettyPrint(ctx.request.body.asJson))
    val ticket = getTicket(ctx.request.getHeader("Authorization"))
    val body = ctx.request.method match {
      case "GET" => JObject()
      case "POST" =>
        header.contentType match {
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
      case method => throw new ClientException(
        "Unhandled method: " + method)
    }
    try {
      Results.ok(playJson(process(body, ticket)))
    } catch {
      case e: ClientException => {
        log.debug("Client error", e)
        clientErrorResponse(e.getMessage)
      }
      case e: Exception => {
        log.error("Internal server error", e)
        serverErrorResponse(e.getMessage)
      }
    }
  }

  private def getTicket(header: String): Option[Ticket] = {
    header match {
      case null => None
      case value => {
        val head = "Bearer "
        if (value.startsWith(head)) {
          val key = "sncrsaw2"
          val token = value.substring(head.length)
          val body = Jwts.parser().setSigningKey(key)
            .parseClaimsJws(token).getBody()
          val ticket = body.get("ticket")
            .asInstanceOf[java.util.Map[String, Object]]
          Some(Ticket(
            ticket.get("userId").asInstanceOf[Integer],
            ticket.get("userFullName").asInstanceOf[String],
            ticket.get("dataSecurityKey").asInstanceOf[java.util.List[Object]],
            ticket.get("roleType").asInstanceOf[String],
            ticket.get("products").asInstanceOf[java.util.List[Object]]))
        } else {
          log.info("Unrecognized Authorization header: " + value)
          None
        }
      }
    }
  }

  protected def playJson(json: JValue) = {
    log.trace("Response body: {}", pretty(render(json)))
    Json.parse(compact(render(json)))
  }

  protected def clientErrorResponse(message: String): Result = {
    val response: JObject = ("error",
      ("type", "client-error") ~
        ("message", message))
    Results.badRequest(playJson(response))
  }

  protected def serverErrorResponse(message: String): Result = {
    val response: JObject = ("error",
      ("type", "internal-server-error") ~
        ("message", message))
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

case class Ticket(userId: Integer, userFullName: String, dataSecurityKey: java.util.List[Object], roleType:String , product :java.util.List[Object])



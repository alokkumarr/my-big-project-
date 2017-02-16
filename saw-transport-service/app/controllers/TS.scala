package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsValue
import play.libs.Json
import play.mvc.{Controller, Http, Result}
import sncr.es.ESQueryHandler
import sncr.metadata.MetadataDictionary
import sncr.request.Extractor
import sncr.saw.common.config.SAWServiceConfig
import sncr.security.TokenValidator

/**
  * Created by srya0001 on 6/28/2016.
  */
class TS extends Controller {

  val m_log: Logger = LoggerFactory.getLogger(classOf[TS].getName)

  def handleEmptyRequest( msg: String ) : Result = {
    val ctx: Http.Context = Http.Context.current.get
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", "empty request")
    m_log.debug(s"Empty request with $msg content type came from: ${ctx.request().host()}/${ctx.request().username()}")
    play.mvc.Results.badRequest(res)
  }

  def query: Result = {

    val ctx: Http.Context = Http.Context.current.get

    val header = ctx._requestHeader()

    val res: ObjectNode = Json.newObject

    header.contentType match
    {
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
//                                                      m_log debug ( "App/JSON: " + body.toString)
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
  }

  def process(arr: Array[Byte]): Result =
  {
     process(play.api.libs.json.Json.parse(arr))
  }

  def process(txt: String): Result =
  {
    process(play.api.libs.json.Json.parse(txt))
  }

  def process(json: JsValue): Result = {
    val res: ObjectNode = Json.newObject
    m_log.debug("Validate and process request:  " + play.api.libs.json.Json.prettyPrint(json))

    val extractor = new Extractor

    val (isValid, msg) = extractor.validateRequest(json)
    if (!isValid) {
      m_log debug msg
      res.put("result", msg)
      return play.mvc.Results.unauthorized(res)
    }

    val secBypass = SAWServiceConfig.security_settings.getBoolean("bypass")
    if ( secBypass == null || !secBypass){
      val tokenValidator  = new TokenValidator(extractor.security.get(MetadataDictionary.Token.toString).get.asInstanceOf[String])
      if (!tokenValidator.validate)
      {
        res.put("result", "Provided token is invalid")
        return play.mvc.Results.unauthorized(res)
      }
    }

    val (isHeaderValid, msg2) = extractor.getDataDescriptorHeader(json)
    if (!isHeaderValid) {
      m_log debug msg2
      res.put("result", msg2)
      return play.mvc.Results.badRequest(res)
    }

    val stv : String = extractor.values.get(MetadataDictionary.storage_type.toString).get.asInstanceOf [String]
    m_log debug s"Storage Type: ${stv}"

    val req = new ESQueryHandler(extractor)
    stv match
    {
      case "ES" =>  req.esRequest(json)
      case "DL" =>  res.put("result", "success"); return play.mvc.Results.ok(res)
      case _ =>
            res.put("result", "failure")
            res.put("result", "Unsupported storage type")
            play.mvc.Results.badRequest(res)
    }

  }




}


package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import play.api.libs.json.JsValue
import play.libs.Json
import play.mvc.Result
import sncr.es.ESQueryHandler
import sncr.metadata.engine.MetadataDictionary
import sncr.request.Extractor
import sncr.saw.common.config.SAWServiceConfig
import sncr.security.TokenValidator

/**
  * Created by srya0001 on 6/28/2016.
  */
class TS extends BaseServiceProvider {

  override def process(arr: Array[Byte]): Result =
  {
    process(play.api.libs.json.Json.parse(arr))
  }

  override def process(txt: String): Result =
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

    val secBypass : Boolean = SAWServiceConfig.security_settings.getBoolean("bypass")
    if ( !secBypass){
      try {
        val tokenValidator = new TokenValidator(extractor.security.get(MetadataDictionary.Token.toString).get.asInstanceOf[String])
        if (!tokenValidator.validate) {
          res.put("result", "Provided token is invalid")
          return play.mvc.Results.unauthorized(res)
        }
      }
      catch {
        case x: Exception => {
            m_log error("Token validation could not be executed: ", x)
            res.put("result", "Service unavailable")
            return play.mvc.Results.unauthorized(res)
        }
        case tout: java.util.concurrent.TimeoutException => {
            m_log error ("Token validation could not be executed: ", tout)
            res.put("result", "Service unavailable")
            return play.mvc.Results.unauthorized(res)
        }
        case _ : Throwable => {
            m_log error "Token validation could not be executed: Reason unknown"
            res.put("result", "Unknown reason")
            return play.mvc.Results.unauthorized(res)
        }
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
            res.put("reason", "Unsupported storage type")
            play.mvc.Results.badRequest(res)
    }

  }

}


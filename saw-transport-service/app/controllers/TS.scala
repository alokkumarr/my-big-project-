package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import play.api.libs.json.JsValue
import play.libs.Json
import play.mvc.Result
import sncr.es.ESQueryHandler
import sncr.metadata.engine.MetadataDictionary
import sncr.request.Extractor

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
      case "ES" =>  req.handleRequest(json)
      case "DL" =>  res.put("result", "success"); return play.mvc.Results.ok(res)
      case _ =>
            res.put("result", "failure")
            res.put("reason", "Unsupported storage type")
            play.mvc.Results.badRequest(res)
    }

  }

}


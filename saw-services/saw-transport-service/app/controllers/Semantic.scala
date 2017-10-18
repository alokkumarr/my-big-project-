package controllers

import java.text.SimpleDateFormat

import com.fasterxml.jackson.databind.node.ObjectNode
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import play.libs.Json
import play.mvc.Result
import sncr.metadata.engine.ihandlers.RequestProcessor
import sncr.metadata.engine.{Contexts, ProcessingResult}
import sncr.metadata.semantix.SemanticRequestProcessor
import sncr.metadata.ui_components.UIMDRequestProcessor

class Semantic extends BaseServiceProvider {

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  override def process(arr: Array[Byte]): Result =
  {
    process(new String(arr))
  }

  override def process(txt: String): Result =
  {
    process( parse(txt, false, false))
  }

  def process(json: JValue): Result = {
    m_log trace("Validate and process request:  " + compact(render(json)))
    val res: ObjectNode = Json.newObject
    try {

      val context = (json \ "contents" \ "context").extractOpt[String].getOrElse(Contexts.UndefContext.toString)
      var handler : RequestProcessor = null
      context match{
        case "UI" => handler = new UIMDRequestProcessor(json)
        case "Semantic" =>  handler = new SemanticRequestProcessor(json)
        case _ => res.put("reason", "Request context is undefined"); res.put("result", "failure"); return play.mvc.Results.ok(res)
      }
      handler.validate match {
        case (0, _) =>
          return play.mvc.Results.ok(handler.execute)
        case (res_id: Int, r: String) => res.put("reason", r); res.put("result", ProcessingResult(res_id).toString)
      }
    }
    catch{
      case e:Exception => val msg = e.getMessage
        res.put("reason", msg); res.put("result", ProcessingResult.Rejected.toString)
    }
    play.mvc.Results.ok(res)
  }




}

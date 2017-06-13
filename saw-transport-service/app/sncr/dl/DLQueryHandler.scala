package sncr.dl

import java.io.IOException
import java.util.concurrent.ExecutionException

import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException
import org.apache.http.client.HttpResponseException
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsValue
import play.libs.Json
import play.mvc.Result
import sncr.metadata.engine.MetadataDictionary
import sncr.request.{Extractor, TSResponse}

/**
  * Created by srya0001 on 6/9/2017.
  */
class DLQueryHandler (val ext: Extractor) extends TSResponse{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[DLQueryHandler].getName)
  def handleRequest(source : JsValue) : Result =
  {
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")

    val analysisId : String = if (ext.values.contains(MetadataDictionary.analysisId.toString))
                                  ext.values.get(MetadataDictionary.analysisId.toString).get.asInstanceOf[String]
                              else null

    val semanticId : String = if (ext.values.contains(MetadataDictionary.semanticId.toString))
                                 ext.values.get(MetadataDictionary.semanticId.toString).get.asInstanceOf[String]
                              else null

    val objectId : String = if (ext.values.contains(MetadataDictionary.semanticId.toString))
                              ext.values.get(MetadataDictionary.semanticId.toString).get.asInstanceOf[String]
                            else null

    val query : JsValue = ext.values.get(MetadataDictionary.query.toString).get.asInstanceOf[JsValue]
    val verb : String = ext.values.get(MetadataDictionary.verb.toString).get.asInstanceOf [String]

    try {
      res.put("reason", s"Response: ")
      play.mvc.Results.ok(res)
    }
    catch{
      case e:HttpResponseException => return handleFailure("Could not process HTTP response",  e)
      case e:IOException => return handleFailure("Network exception",  e)
      case e:TimeoutException => return handleFailure("Request to target service timed out",  e)
      case e:InterruptedException => return handleFailure("Request execution interrupted",  e)
      case e:ExecutionException => return handleFailure("Request execution failed",  e)
      case e:Exception => return handleFailure("Internal error",  e)
    }
    play.mvc.Results.internalServerError(res)
  }


}

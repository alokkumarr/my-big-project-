package sncr.dl

import java.io.IOException
import java.util
import java.util.concurrent.ExecutionException

import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException
import org.apache.http.client.HttpResponseException
import org.json4s.JsonAST.{JBool, JLong, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsValue
import play.libs.Json
import play.mvc.Result
import sncr.datalake.DLSession
import sncr.datalake.engine.{Analysis, ExecutionType}
import sncr.datalake.handlers.{AnalysisNodeExecutionHelper, HasDataObject, SemanticNodeExecutionHelper}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.MetadataDictionary
import sncr.metadata.engine.context.SelectModels
import sncr.metadata.semantix.SemanticNode
import sncr.request.{Extractor, TSResponse}
/**
  * Created by srya0001 on 6/9/2017.
  */
class DLQueryHandler (val ext: Extractor) extends TSResponse{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[DLQueryHandler].getName)

  def handleRequest(source : JsValue) : Result =
  {
    val res: ObjectNode = Json.newObject

    val analysisId : String = if (ext.values.contains(MetadataDictionary.analysisId.toString))
                                  ext.values.get(MetadataDictionary.analysisId.toString).get.asInstanceOf[String]
                              else null

    val semanticId : String = if (ext.values.contains(MetadataDictionary.semanticId.toString))
                                 ext.values.get(MetadataDictionary.semanticId.toString).get.asInstanceOf[String]
                              else null

    val objectId : String = if (ext.values.contains(MetadataDictionary.dataObjectId.toString))
                              ext.values.get(MetadataDictionary.dataObjectId.toString).get.asInstanceOf[String]
                              else null

    val analysisResult : String = if (ext.values.contains(MetadataDictionary.analysisResult.toString))
      ext.values.get(MetadataDictionary.analysisResult.toString).get.asInstanceOf[String]
    else null


    val verb : String = ext.values.getOrElse(MetadataDictionary.verb.toString, "none").asInstanceOf[String]

    val query : JsValue = if (ext.values.contains(MetadataDictionary.query.toString))
                                ext.values.get(MetadataDictionary.query.toString).get.asInstanceOf[JsValue]
                              else null


    try {
      verb match {
        case "execute" => {

          if (analysisId != null) {

            val analysis = new Analysis(analysisId)
            val execution = analysis.execute(ExecutionType.preview)
            res.put("result", s"Created execution id: ${execution.id}")


            //TODO:: Should be uncommented when Spark 2.2.0 will be available, See https://issues.apache.org/jira/browse/SPARK-13747
            /*
            m_log debug s"Execution has been started, get data via future"
            val f = execution.getData
            Await.result(f,  Duration(30, "seconds")); m_log debug "Execution result: " + f.value
            f onSuccess { case _ => m_log info "Request was successfully processed"; if (f.value.get.get != null ) res.put( "data", processResult(f.value.get.get))}
            f onFailure { case _ => m_log error "Could not process request: " + f.value }
    */

            val resultData = execution.fetchData
            if (resultData != null)
              res.put("data", processResult(resultData))
            else {
              m_log debug s"Exec code: ${execution.executionCode}, message: ${execution.executionMessage}"
              res.put("result", execution.executionCode)
              res.put("reason", execution.executionMessage)
            }

          }
          else if (semanticId != null) {

            if (query == null || query.toString().isEmpty) {
              res.put("result", "failed")
              res.put("reason", "Query is not provided. Semantic layer execution requires query")
              return play.mvc.Results.badRequest(res)
            }

            val q = query.toString().slice(1, query.toString().length - 1)
            m_log debug s"Execute query: $q"
            val sm = SemanticNode(semanticId, SelectModels.everything.id)
            val snh = new SemanticNodeExecutionHelper(sm, true)
            snh.executeSQL(q)
            val data = snh.getData
            if (data != null) res.put("data", processResult(data))
            else {
              m_log debug s"Exec-code: ${snh.lastSQLExecRes}, message: ${snh.lastSQLExecMessage}"
              res.put("result", snh.lastSQLExecRes)
              res.put("reason", snh.lastSQLExecMessage)
            }
          }
        }
        case "show" => {
          if (objectId != null && objectId.nonEmpty) {
              val dobj = DataObject(objectId)
              val dl = dobj.getDLLocations(0)
              val dlsession = new DLSession("SAW-TS-Show-DataObject")
              val (nameDO, formatDO) = HasDataObject.loadDODescriptor(dobj)
              if (nameDO.isEmpty || formatDO.isEmpty ){
                res.put("result", "failed")
                res.put("reason", "Object descriptor is not correct")
                return play.mvc.Results.badRequest(res)
              }
              dlsession.loadObject(nameDO.get, dl, formatDO.get)
              val result = processResult(dlsession.getData(nameDO.get))
              res.put("result", "success")
              res.put("reason", result)

          }
          else if ( analysisResult != null && analysisResult.nonEmpty ){
            val result = processResult(AnalysisNodeExecutionHelper.loadAnalysisResult(analysisResult))
            res.put("result", "success")
            res.put("reason", result)
          }
        }
        case "save-exec" => {

          if (analysisId == null) {
            res.put("result", "failed")
            res.put("reason", "save-exec is supported only for Analysis, set analysis ID")
            return play.mvc.Results.badRequest(res)
          }

          val analysis = new Analysis(analysisId)
          val execution = analysis.execute(ExecutionType.onetime)
          res.put("result", s"Created execution id: ${execution.id}")


          //TODO:: Should be uncommented when Spark 2.2.0 will be available, See https://issues.apache.org/jira/browse/SPARK-13747
          /*
            m_log debug s"Execution has been started, get data via future"
            val f = execution.getData
            Await.result(f,  Duration(30, "seconds")); m_log debug "Execution result: " + f.value
            f onSuccess { case _ => m_log info "Request was successfully processed"; if (f.value.get.get != null ) res.put( "data", processResult(f.value.get.get))}
            f onFailure { case _ => m_log error "Could not process request: " + f.value }
          */

          val resultData = execution.fetchData
          if (resultData != null)
            res.put("data", processResult(resultData))
          else {
            m_log debug s"Exec code: ${execution.executionCode}, message: ${execution.executionMessage}"
            res.put("result", execution.executionCode)
            res.put("reason", execution.executionMessage)
          }
        }
        case "none" =>
        {
          res.put("result", "failed")
          res.put("reason", "A verb must be provided: execute, show or save-exec")
          return play.mvc.Results.badRequest(res)
        }
      }
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

  import scala.collection.JavaConversions._
  def processResult(data: util.List[util.Map[String, (String, Object)]]) : String = {

    if ( data == null || data.isEmpty) return "empty result"

    var i = 0
    pretty ( render (JArray(data.map(m => {
      i += 1
      JObject(
        List(JField("rowid", JInt(i)),
          JField("data",
            JObject(m.keySet().map(k =>
              JField(k, m.get(k)._1 match {
                case "StringType" => JString(m.get(k)._2.asInstanceOf[String])
                case "IntegerType" => JInt(m.get(k)._2.asInstanceOf[Int])
                case "BooleanType" => JBool(m.get(k)._2.asInstanceOf[Boolean])
                case "LongType" => JLong(m.get(k)._2.asInstanceOf[Long])
              })).toList
            )
          )
        )
      )
    }
    ).toList)))

  }

}

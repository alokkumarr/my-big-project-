package sncr.dl

import java.io.IOException
import java.util
import java.util.concurrent.ExecutionException

import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException
import org.apache.http.client.HttpResponseException
import org.apache.spark.sql.catalyst
import org.apache.spark.sql.catalyst.analysis
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

    val numOfRec : Int = Integer.parseInt(if (ext.values.contains(MetadataDictionary.numberOfRecords.toString))
                        ext.values.get(MetadataDictionary.numberOfRecords.toString).get.asInstanceOf[String]
                      else "100")

    try {
      verb match {
        case "preview" => {

          if (analysisId != null) {

            val analysis = new Analysis(analysisId)
            val execution = analysis.executeAndWait(ExecutionType.preview)
            res.put("result", s"Created execution id: ${execution.getId}")


            //TODO:: Should be uncommented when Spark 2.2.0 will be available, See https://issues.apache.org/jira/browse/SPARK-13747
            /*
            m_log debug s"Execution has been started, get data via future"
            val f = execution.getData
            Await.result(f,  Duration(30, "seconds")); m_log debug "Execution result: " + f.value
            f onSuccess { case _ => m_log info "Request was successfully processed"; if (f.value.get.get != null ) res.put( "data", processResult(f.value.get.get))}
            f onFailure { case _ => m_log error "Could not process request: " + f.value }
    */

            val resultData = execution.getAllData
            if (resultData != null)
              res.put("data", processResult(resultData))
            else
              res.put("data", "no data found")

            m_log debug s"Exec code: ${execution.getExecCode}, message: ${execution.getExecMessage}"
            res.put("result", execution.getExecCode)
            res.put("reason", execution.getExecMessage)
            res.put("start" , execution.getStartedTimestamp)
            res.put("finished" , execution.getFinishedTimestamp)
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
            snh.executeSQL(q, numOfRec)
            val data = snh.getData

            if (data != null)
              res.put("data", processResult(data))
            else
              res.put("data", "no data found")

            m_log debug s"Exec-code: ${snh.lastSQLExecRes}, message: ${snh.lastSQLExecMessage}"
            res.put("result", snh.lastSQLExecRes)
            res.put("reason", snh.lastSQLExecMessage)
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
        case "execute" => {
          if (analysisId == null) {
            res.put("result", "failed")
            res.put("reason", "save-exec is supported only for Analysis, set analysis ID")
            return play.mvc.Results.badRequest(res)
          }
          val analysis = new Analysis(analysisId)
          val execution = analysis.executeAndWait(ExecutionType.onetime)
          val resultData = execution.getAllData
          if (resultData != null){
            res.put("data", processResult(resultData))
          }
          else{
            res.put("data", "no data found")
          }
          m_log debug s"Exec code: ${execution.getExecCode}, message: ${execution.getExecMessage}"
          res.put("result", execution.getExecCode)
          res.put("reason", execution.getExecMessage)
          res.put("start" , analysis.getStartTS )
          res.put("finished" , analysis.getFinishedTS )
        }
        case "exec-save" => {
          if (analysisId == null) {
            res.put("result", "failed")
            res.put("reason", "save-exec is supported only for Analysis, set analysis ID")
            return play.mvc.Results.badRequest(res)
          }
          val analysis = new Analysis(analysisId)
          val execution = analysis.executeAndWait(ExecutionType.scheduled)
          res.put("execution_id", s"${execution.getId}")
          m_log debug s"Created execution ID: ${execution.getId}"
          m_log debug s"Exec code: ${execution.getExecCode}, message: ${execution.getExecMessage}"
          res.put("result", execution.getExecCode)
          res.put("reason", execution.getExecMessage)
          res.put("start" , analysis.getStartTS )
          res.put("finished" , analysis.getFinishedTS )
        }
        case "exec-asynch" => {
          if (analysisId != null) {
            if (DLQueryHandler.storedExecutors.contains(analysisId))
            {
              var msg = s"Request for Analysis ID: $analysisId is still being executed"
               if(DLQueryHandler.storedResults(analysisId).isDone) {
                 msg = s"Request for Analysis ID: $analysisId has been completed"
                 res.put("result", "COMPLETED")
               }
               else if (DLQueryHandler.storedResults(analysisId).isCancelled) {
                 res.put("result", "CANCELED")
               }
              else{
                 res.put("result", "IN_PROGRESS")
               }
              m_log debug msg
              res.put("reason", msg)
              return play.mvc.Results.ok(res)
            }
            val analysis = new Analysis(analysisId)
            synchronized{  DLQueryHandler.storedExecutors += (analysisId -> analysis) }
            val future = analysis.execute(ExecutionType.onetime)
            synchronized{ DLQueryHandler.storedResults += ( analysisId -> future ) }
            res.put("result", "STARTED")
            res.put("reason", analysis.getStatus)
            res.put("start" , analysis.getStartTS )
            res.put("finished" , analysis.getFinishedTS )
          }
          //TODO:: Implement functionality for Semantic layer execution
          else if (semanticId != null) {
            res.put("result", "Not implemented yet")
          }
        }
        case "exec-complete" =>
          {
            if (analysisId != null) {
              if (DLQueryHandler.storedExecutors.contains(analysisId))
              {
                val data = DLQueryHandler.storedResults(analysisId).get
                val analysis = DLQueryHandler.storedExecutors(analysisId)
                synchronized {DLQueryHandler.storedExecutors -= analysisId}
                synchronized {DLQueryHandler.storedResults -= analysisId}
                val execution = analysis.exec
                if (data != null)
                  res.put("data", processResult(data))
                else
                  res.put("data", "no data found")

                m_log debug s"Exec code: ${execution.getExecCode}, message: ${execution.getExecMessage}"
                res.put("result", execution.getExecCode)
                res.put("reason", execution.getExecMessage)
              }
              else{
                res.put("result", "analysis does not exist")
                res.put("reason", s"Cannot complete request, analysis [ID = $analysisId] was not executed.")
              }
            }
            //TODO:: Implement functionality for Semantic layer execution
            else if (semanticId != null)
            {
              res.put("result", "Not implemented yet")
            }
          }
        case "exec-cancel" =>
        {
          if (analysisId != null) {
            if (DLQueryHandler.storedExecutors.contains(analysisId)){
              DLQueryHandler.storedResults(analysisId).cancel(true)
              synchronized{DLQueryHandler.storedExecutors -= analysisId}
              synchronized{DLQueryHandler.storedResults -= analysisId}
              res.put("result", "request canceled")
              res.put("reason", "")
            }
            else{
              res.put("result", "analysis does not exist")
              res.put("reason", s"Cannot cancel request, analysis [ID = $analysisId] was not executed.")
            }
          }
          //TODO:: Implement functionality for Semantic layer execution
          else if (semanticId != null)
          {
             res.put("result", "Not implemented yet")
          }
        }
        case "delete" =>
        {
          if (analysisId != null ) {
            val analysis = new Analysis(analysisId)
            analysis.delete
            res.put("result", "success")
            res.put("reason", s"All results related to analysis ID [ $analysisId] have been removed.")
          }
          else {
            res.put("result", "failed")
            res.put("reason", "A verb requires Analysis ID")
            return play.mvc.Results.badRequest(res)
          }
        }

        case "none" =>
        {
          res.put("result", "failed")
          res.put("reason", "A verb must be provided: preview, execute, show, exec-asynch, exec-complete, exec-cancel, exec-save")
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
                case "DoubleType" => JDouble(m.get(k)._2.asInstanceOf[Double])
              })).toList
            )
          )
        )
      )
    }
    ).toList)))

  }

}

object DLQueryHandler{

  var storedResults : Map[String, java.util.concurrent.Future[java.util.List[java.util.Map[String, (String, Object)]]]] = Map.empty
  var storedExecutors : Map[String, Analysis] = Map.empty


}

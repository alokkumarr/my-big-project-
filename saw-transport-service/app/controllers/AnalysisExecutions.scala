package controllers

import java.{util, _}
import java.text.SimpleDateFormat
import java.util._

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import model.PaginateDataSet
import org.json4s.JsonAST.{JArray, JObject, JString, JValue}
import org.json4s.JsonDSL._
import play.libs.Json
import play.mvc.{Http, Result, Results}
import sncr.datalake.{DLConfiguration}
import sncr.metadata.analysis.AnalysisResult
import sncr.metadata.engine.MDObjectStruct

class AnalysisExecutions extends BaseController {
  val analysisController = new Analysis

  def list(analysisId: String): Result = {
    handle((json, ticket) => {
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val executions = analysis.listExecutions.map(result => {
        val content = result.getCachedData("content") match {
          case obj: JObject => obj
          case obj: JValue => unexpectedElement("object", obj)
        }
        val id = Bytes.toString(result.getRowKey)
        ("id", id) ~
        ("finished", (content \ "execution_finish_ts").extractOpt[Long]) ~
        ("status", (content \ "exec-msg").extractOpt[String])
      })
      /* Note: Keep "results" property for API backwards compatibility */
      ("executions", executions) ~ ("results", executions) : JValue
    })
  }


  def getExecutionData(analysisId: String, executionId: String, start: Int,limit: Int): Result = {
    handle(process = (json, ticket) => {
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val execution = analysis.getExecution(executionId)
      val data = execution.loadExecution(executionId)
      var totalRows : Int =0
      var pagingData: JValue = null
      var limitSize: Int =0;
      if (limit==0) {limitSize = DLConfiguration.rowLimit} else limitSize = limit;
      if (data == null) {
        val anares = AnalysisResult(analysisId, executionId)
        val desc = anares.getCachedData(MDObjectStruct.key_Definition.toString)
        val d_type = (desc.asInstanceOf[JValue] \ "type").extractOpt[String];
        if (d_type.isDefined) {

          if (d_type.get == "chart" || d_type.get == "pivot") {
            ("data", execution.loadESExecutionData(anares))
          }
          else throw new Exception("Unsupported data format")
        }
        else null
      }
      else {
        if (PaginateDataSet.INSTANCE.getCache(analysisId.toString.concat(executionId)) != null)
        {
          pagingData = analysisController.processReportResult(PaginateDataSet.INSTANCE.paginate(limitSize, start, analysisId.toString.concat(executionId)));
          totalRows = PaginateDataSet.INSTANCE.sizeOfData();
        }
        else {
          pagingData = analysisController.processReportResult(data)
          PaginateDataSet.INSTANCE.putCache(analysisId.toString.concat(executionId),data);
          pagingData = analysisController.processReportResult(PaginateDataSet.INSTANCE.paginate(limitSize, start, analysisId.toString.concat(executionId)));
          totalRows = PaginateDataSet.INSTANCE.sizeOfData();
        }
        ("data", pagingData) ~ ("totalRows",totalRows)
      }
    })
  }
  def execute(analysisId: String): Result = {
    handle((json, ticket) => {
      analysisController.executeAnalysis(analysisId, null, null)
      JObject()
    })
  }
}

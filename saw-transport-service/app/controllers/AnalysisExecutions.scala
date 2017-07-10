package controllers

import java.text.SimpleDateFormat

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.{JArray, JObject, JString, JValue}
import org.json4s.JsonDSL._
import play.libs.Json
import play.mvc.{Http, Result, Results}

import sncr.metadata.analysis.AnalysisResult

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
        ("status", (content \ "exec-code").extractOpt[Int])
      })
      /* Note: Keep "results" property for API backwards compatibility */
      ("executions", executions) ~ ("results", executions) : JValue
    })
  }

  def getExecutionData(analysisId: String, executionId: String): Result = {
    handle((json, ticket) => {
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val execution = analysis.getExecution(executionId)
      val data = execution.loadExecution(executionId)
      ("data", analysisController.processReportResult(data))
    })
  }

  def execute(analysisId: String): Result = {
    handle((json, ticket) => {
      analysisController.executeAnalysis(analysisId, null, null)
      JObject()
    })
  }
}

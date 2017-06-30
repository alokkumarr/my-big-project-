package controllers

import java.text.SimpleDateFormat

import org.json4s.JsonAST.{JArray, JObject, JString, JValue}
import org.json4s.JsonDSL._
import play.libs.Json
import play.mvc.{Http, Result, Results}

import sncr.metadata.analysis.AnalysisResult

class AnalysisExecutions extends BaseController {
  def list(analysisId: String): Result = {
    handle((json, ticket) => {
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val executions = analysis.listExecutions.map(result => {
        val content = result.getCachedData("content") match {
          case obj: JObject => obj
          case obj: JValue => unexpectedElement("object", obj)
        }
        ("id", (content \ "id").extract[String]) ~
        ("finished", (content \ "execution_finish_ts").extract[Long]) ~
        ("status", (content \ "exec-code").extract[Int])
      })
      /* Note: Keep "results" property for API backwards compatibility */
      ("executions", executions) ~ ("results", executions) : JValue
    })
  }

  def getExecutionData(analysisId: String, executionId: String): Result = {
    handle((json, ticket) => {
      val result = AnalysisResult(analysisId, executionId)
      val content = result.getCachedData("content") match {
        case obj: JObject => obj
      }
      val data = result.getCachedData("data") match {
        case obj: JArray => obj
        case obj: JObject => obj
      }
      ("data", data) : JValue
    })
  }

  def execute(analysisId: String): Result = {
    handle((json, ticket) => {
      val analysisController = new Analysis
      analysisController.executeAnalysis(analysisId)
      JObject()
    })
  }
}

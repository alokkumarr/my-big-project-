package controllers

import java.text.SimpleDateFormat

import org.json4s.JsonAST.{JArray, JObject, JString, JValue}
import org.json4s.JsonDSL._
import play.libs.Json
import play.mvc.{Http, Result, Results}

import sncr.metadata.analysis.AnalysisResult

class AnalysisExecutions extends BaseController {
  def list(analysisId: String): Result = {
    val execution = searchAnalysisExecution(analysisId, (
      id: String, obj: JObject) => {
      val status = (obj \ "execution_result").extract[String] match {
        case "Return Code 0" => "success"
        case _ => "unknown"
      }
      ("id", id) ~
      ("finished", obj \ "execution_timestamp") ~
      ("status", status)
    })
    handle((json, ticket) => {
      /* Note: Keep "results" property for API backwards compatibility */
      ("execution", execution) ~ ("results", execution) : JValue
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
      }
      ("data", data) : JValue
    })
  }

  private def searchAnalysisExecution(
    analysisId: String, handle: (String, JObject) => JObject): List[JObject] = {
    val analysisResults = new AnalysisResult("")
    val search = Map("analysisId" -> analysisId)
    analysisResults.find(search).map(result => {
      val id = result("id").toString
      result("content") match {
        case obj: JObject => handle(id, obj)
        case obj: JValue => unexpectedElement("object", obj)
      }
    })
  }
}

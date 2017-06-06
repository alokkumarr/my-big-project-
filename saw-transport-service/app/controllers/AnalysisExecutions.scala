package controllers

import java.text.SimpleDateFormat

import org.json4s.JsonAST.{JObject, JValue, JString}
import org.json4s.JsonDSL._
import play.libs.Json
import play.mvc.{Http, Result, Results}

import sncr.metadata.analysis.AnalysisResult

class AnalysisExecutions extends BaseController {
  def list(analysisId: String): Result = {
    handle(json => {
      val analysisResults = new AnalysisResult("")
      val search = Map("analysisId" -> analysisId)
      val execution = analysisResults.find(search).map(result => {
        result("content") match {
          case obj: JObject => {
            val status = (obj \ "execution_result").extract[String] match {
              case "Return Code 0" => "success"
              case _ => "unknown"
            }
            ("id", result("id").toString) ~
            ("finished", obj \ "execution_timestamp") ~
            ("status", status)
          }
          case obj: JValue => unexpectedElement("object", obj)
        }
      })
      /* Note: Keep "results" property for API backwards compatibility */
      ("execution", execution) ~ ("results", execution) : JValue
    })
  }
}

package controllers

import java.text.SimpleDateFormat

import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import play.libs.Json
import play.mvc.{Http, Result, Results}

import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.analysis.AnalysisResult
import sncr.metadata.engine.MDNodeUtil
import sncr.metadata.engine.ProcessingResult._
import sncr.analysis.execution.ExecutorRunner
import sncr.analysis.execution.ProcessExecutionResult

import model.QueryBuilder
import model.QueryException

class ANA extends BaseServiceProvider {
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  val executorRunner = new ExecutorRunner(1)

  override def process(txt: String): Result = {
    val json = parse(txt)
    val action = (json \ "contents" \ "action").extract[String].toLowerCase
    val response = action match {
      case "create" => {
        val analysisNode = new AnalysisNode(analysisJson(json))
        val (result, message) = analysisNode.write
        if (result != NodeCreated.id) {
          throw new RuntimeException("Writing failed: " + message)
        }
        json
      }
      case "update" => {
        val analysisId = extractAnalysisId(json)
        val analysisNode = new AnalysisNode(analysisJson(json))
        val (result, message) = analysisNode.update(
          Map("analysisId" -> analysisId))
        if (result != Success.id) {
          throw new RuntimeException("Updating failed: " + message)
        }
        json
      }
      case "read" => {
        val analysisId = extractAnalysisId(json)
        val analysisNode = new AnalysisNode
        val result = analysisNode.read(Map("analysisId" -> analysisId))
        if (result == Map.empty) {
          throw new RuntimeException("Reading failed")
        }
        result("content") match {
          case content: JValue => {
            json merge(
              ("contents", ("analysis", JArray(List(content)))) ~ (".", "."))
          }
          case _ => throw new RuntimeException("no match")
        }
      }
      case "execute" => {
        val analysisId = extractAnalysisId(json)
        executeAnalysis(analysisId)
        json
      }
      case "delete" => {
        val analysisId = extractAnalysisId(json)
        val analysisNode = new AnalysisNode
        val result = analysisNode.deleteAll(Map("analysisId" -> analysisId))
        if (result == Map.empty) {
          throw new RuntimeException("Deleting failed")
        }
        json
      }
      case _ => {
        throw new RuntimeException("Unknown action: " + action)
      }
    }
    val playJson = Json.parse(compact(render(response)))
    Results.ok(playJson)
  }

  def extractAnalysisId(json: JValue) = {
    val JString(analysisId) = (json \ "contents" \ "keys")(0)
    analysisId
  }

  def analysisJson(json: JValue) = {
    val analysisListJson = json \ "contents" \ "analysis"
    val analysis = analysisListJson match {
      case array: JArray => {
        if (array.arr.length > 1) {
          throw new RuntimeException("Only one element supported")
        }
        if (array.arr.length == 0) {
          throw new RuntimeException("No element to write found")
        }
        array.arr(0)
      }
      case _ => throw new RuntimeException(
        "Expected array: " + analysisListJson)
    }
    val query: JValue = ("query", JString(QueryBuilder.build(analysis)))
    analysis merge(query)
  }

  def executeAnalysis(analysisId: String) = {
    /* Placeholder for Spark SQL execution library until available */
    executorRunner.startSQLExecutor(analysisId)
    val status = executorRunner.waitForCompletion(analysisId, 2000)
    if (status != ProcessExecutionResult.Success.toString) {
      throw new RuntimeException("Process execution failed: " + status);
    }
    val resultId = executorRunner.getLastResult(analysisId)
    // To be implemented when executor results are available:
    // AnalysisResult(resultId).getData
  }
}

package controllers

import java.text.SimpleDateFormat
import java.util.UUID

import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import play.libs.Json
import play.mvc.{Http, Result, Results}
import sncr.metadata.analysis.{AnalysisExecutionHandler, AnalysisNode, AnalysisResult}
import sncr.metadata.engine.context.SelectModels
import sncr.metadata.engine.{MDNodeUtil, tables}
import sncr.metadata.engine.ProcessingResult._
import sncr.analysis.execution.{AnalysisExecutionRunner, ExecutionTaskHandler, ProcessExecutionResult}
import sncr.metadata.semantix.SemanticNode
import model.QueryBuilder
import model.ClientException
import org.apache.hadoop.hbase.util.Bytes
import sncr.metadata.engine.SearchMetadata._

class ANA extends BaseServiceProvider {
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  val executorRunner = new ExecutionTaskHandler(1)

  override def process(txt: String): Result = {
    try {
      doProcess(txt)
    } catch {
      case ClientException(message) => userErrorResponse(message)
      case e: Exception => {
        m_log.error("Internal server error", e)
        serverErrorResponse(e.getMessage())
      }
    }
  }

  private def doProcess(txt: String): Result = {
    val json = parse(txt)
    val action = (json \ "contents" \ "action").extract[String].toLowerCase
    val response = action match {
      case "create" => {
        val semanticId = extractAnalysisId(json)
        val semanticIdJson: JObject = ("semanticId", semanticId)
        val idJson: JObject = ("id", UUID.randomUUID.toString)
        val analysisType = extractKey(json, "analysisType")
        val typeJson: JObject = ("type", analysisType)
        val semanticJson = readSemanticJson(semanticId)
        val mergeJson = contentsAnalyze(
          semanticJson.merge(idJson).merge(semanticIdJson).merge(typeJson))
        val responseJson = json merge mergeJson
        val analysisJson = (responseJson \ "contents" \ "analyze")(0)
        val analysisNode = new AnalysisNode(analysisJson)
        val semanticNode = readSemanticNode(semanticId)
        for ((category, id) <- semanticNode.getRelatedNodes) {
          if (category == "DataObject") {
            analysisNode.addNodeToRelation(id, category)
          }
        }
        val (result, message) = analysisNode.write
        if (result != NodeCreated.id) {
          throw new ClientException("Writing failed: " + message)
        }
        responseJson
      }
      case "update" => {
        val analysisId = extractAnalysisId(json)
        val analysisNode = new AnalysisNode(analysisJson(json))
        val (result, message) = analysisNode.update(
          Map("id" -> analysisId))
        if (result != Success.id) {
          throw new ClientException("Updating failed: " + message)
        }
        json
      }
      case "read" => {
        val analysisId = extractAnalysisId(json)
        json merge contentsAnalyze(readAnalysisJson(analysisId))
      }
      case "execute" => {
        val analysisId = extractAnalysisId(json)
        val data = executeAnalysis(analysisId)
        json merge contentsAnalyze(("data", data))
      }
      case "delete" => {
        val analysisId = extractAnalysisId(json)
        val analysisNode = new AnalysisNode
        val result = analysisNode.deleteAll(Map("id" -> analysisId))
        if (result == Map.empty) {
          throw new ClientException("Deleting failed")
        }
        json
      }
      case _ => {
        throw new ClientException("Unknown action: " + action)
      }
    }
    Results.ok(playJson(response))
  }

  private def userErrorResponse(message: String): Result = {
    val response: JObject = ("error", ("message", message))
    Results.badRequest(playJson(response))
  }

  private def serverErrorResponse(message: String): Result = {
    val response: JObject = ("error", ("message", message))
    Results.internalServerError(playJson(response))
  }

  private def playJson(json: JValue) = {
    Json.parse(compact(render(json)))
  }

  def extractAnalysisId(json: JValue) = {
    extractKey(json, "id")
  }

  private def extractKey(json: JValue, property: String) = {
    val JString(value) = (json \ "contents" \ "keys")(0) \ property
    value
  }

  def analysisJson(json: JValue) = {
    val analysisListJson = json \ "contents" \ "analyze"
    val analysis = analysisListJson match {
      case array: JArray => {
        if (array.arr.length > 1) {
          throw new ClientException("Only one element supported")
        }
        if (array.arr.length == 0) {
          throw new ClientException("No element to write found")
        }
        array.arr(0)
      }
      case _ => throw new ClientException(
        "Expected array: " + analysisListJson)
    }
    val query: JValue = ("query", JString(QueryBuilder.build(analysis)))
    analysis merge(query)
  }

  private def readAnalysisJson
    (analysisId: String, semantic: Boolean = false): JObject = {
    /* If the analysis ID is given as "_static" return a static analysis
     * template that is used to bootstrap tests when the semantic
     * database is empty */
    if (analysisId == "_static") {
      return ("module", "analyze") ~
      ("customerCode", "static") ~
      ("name", "static")
    }
    val analysisNode = if (semantic) {
      readSemanticNode(analysisId)
    } else {
      AnalysisNode(analysisId)
    }
    analysisNode.getCachedData("content") match {
      case content: JObject => content
      case _ => throw new ClientException("no match")
    }
  }

  private def readSemanticJson(semanticId: String): JObject = {
    readAnalysisJson(semanticId, true)
  }

  private def readSemanticNode(semanticId: String): SemanticNode = {
    SemanticNode(semanticId, SelectModels.relation.id)
  }

  private def contentsAnalyze(analysis: JObject): JObject = {
    ("contents", ("analyze", JArray(List(analysis))))
  }

  def executeAnalysis(analysisId: String): JValue = {
    /* TODO: Until analysis and executor changes are finished, return a
     * static mock of execution results */
    if (true) {
      return List(("foo", 1))
    }

    val er: ExecutionTaskHandler = new ExecutionTaskHandler(1)
    try {
      val analysisNode = AnalysisNode(analysisId)
      if ( analysisNode.getCachedData == null || analysisNode.getCachedData.isEmpty)
        throw new Exception("Could not find analysis node with provided analysis ID.")
      val aeh: AnalysisExecutionHandler = new AnalysisExecutionHandler(analysisId)

      //Start executing Spark SQL
      er.startSQLExecutor(aeh)
      val analysisResultId: String = er.getPredefResultRowID(analysisId)
      er.waitForCompletion(analysisId, aeh.getWaitTime)
      val msg = "Execution: AnalysisID = " + analysisId + ", Result Row ID: " + analysisResultId
      // Handle result
      aeh.handleResult()
      m_log debug msg
    }
    catch {
      case e: Exception => val msg = s"Execution exception: ${e.getMessage}"; m_log error (msg, e)
    }
    /* TODO: Return empty result until integrated */
    List()
  }
}

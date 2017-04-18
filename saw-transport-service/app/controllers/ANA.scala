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
import sncr.metadata.engine.{MDNodeUtil, tables}
import sncr.metadata.engine.ProcessingResult._
import sncr.analysis.execution.{AnalysisExecutionRunner, ExecutionTaskHandler, ProcessExecutionResult}
import model.QueryBuilder
import model.QueryException
import org.apache.hadoop.hbase.util.Bytes
import sncr.metadata.engine.SearchMetadata._

class ANA extends BaseServiceProvider {
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  val executorRunner = new ExecutionTaskHandler(1)

  override def process(txt: String): Result = {
    val json = parse(txt)
    val action = (json \ "contents" \ "action").extract[String].toLowerCase
    val response = action match {
      case "create" => {
        val analysisId: JValue = ("analysisId", UUID.randomUUID.toString)
        val analysis = analysisJson(json).merge(analysisId)
        val analysisNode = new AnalysisNode(analysis)
        val (result, message) = analysisNode.write
        if (result != NodeCreated.id) {
          throw new RuntimeException("Writing failed: " + message)
        }
        val withId: JObject = ("contents", ("analyze", JArray(List(analysis))))
        json match {
          case obj: JObject => obj ~ withId
          case _ => throw new RuntimeException("Not object: " + json)
        }
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
              ("contents", ("analyze", JArray(List(content)))) ~ (".", "."))
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
    val JString(analysisId) = (json \ "contents" \ "keys")(0) \ "_id"
    analysisId
  }

  def analysisJson(json: JValue) = {
    val analysisListJson = json \ "contents" \ "analyze"
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

    //Create keys to filter records.
    val keys2 : Map[String, Any] = Map ("analysisId" -> analysisId)
    val systemKeys : Map[String, Any] = Map.empty   //( syskey_NodeCategory.toString -> classOf[AnalysisNode].getName )
    // Create executor service
    val er: ExecutionTaskHandler = new ExecutionTaskHandler(1)
    try {
      val search = simpleSearch(tables.AnalysisMetadata.toString, keys2, systemKeys, "and")
      val rowKey = Bytes.toString(search.head)
      val analysisNode = AnalysisNode(rowKey)
      if ( analysisNode.getCachedData == null || analysisNode.getCachedData.isEmpty)
        throw new Exception("Could not find analysis node with provided analysis ID.")
      val aeh: AnalysisExecutionHandler = new AnalysisExecutionHandler(rowKey, analysisId)

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

  }
}

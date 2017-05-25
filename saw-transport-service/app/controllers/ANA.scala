package controllers

import java.io.ByteArrayOutputStream
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
import com.synchronoss.querybuilder.SAWElasticSearchQueryExecutor
import com.synchronoss.querybuilder.EntityType
import com.synchronoss.querybuilder.SAWElasticSearchQueryBuilder

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
        val analysisId = UUID.randomUUID.toString
        val idJson: JObject = ("id", analysisId)
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
        val analysisNode = AnalysisNode(analysisId)
        val responseJson = analysisJson(json)
        analysisNode.setDefinition(responseJson)
        val (result, message) = analysisNode.update(
          Map("id" -> analysisId))
        if (result != Success.id) {
          throw new ClientException("Updating failed: " + message)
        }
        json.removeField({
          case JField("analyze", JArray(_)) => true
          case _ => false
        }).merge(contentsAnalyze(responseJson))
      }
      case "read" => {
        val analysisId = extractAnalysisId(json)
        json merge contentsAnalyze(readAnalysisJson(analysisId))
      }
      case "search" => {
        val keys = (json \ "contents" \ "keys")(0) match {
          case keys: JObject => keys
          case obj => throw new ClientException("Expected object, got: " + obj)
        }
        json merge contentsAnalyze(searchAnalysisJson(keys))
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
    m_log.trace("Response body: {}", pretty(render(json)))
    Json.parse(compact(render(json)))
  }

  def extractAnalysisId(json: JValue) = {
    extractKey(json, "id")
  }

  private def extractKey(json: JValue, property: String) = {
    val JString(value) = (json \ "contents" \ "keys")(0) \ property
    value
  }

  def analysisJson(json: JValue): JObject = {
    val analysisListJson = json \ "contents" \ "analyze"
    val analysis = analysisListJson match {
      case array: JArray => {
        if (array.arr.length > 1) {
          throw new ClientException("Only one element supported")
        }
        if (array.arr.length == 0) {
          throw new ClientException("No element to write found")
        }
        array.arr(0) match {
          case obj: JObject => obj
          case obj: JValue => throw new ClientException(
            "Expected object: " + obj)
        }
      }
      case _ => throw new ClientException(
        "Expected array: " + analysisListJson)
    }
    val query = (analysis \ "queryManual") match {
      case JNothing => QueryBuilder.build(analysis)
      case obj: JString => ""
      case obj => unexpectedElement("string", obj)
    }
    val queryJson: JObject = ("query", JString(query)) ~
    ("outputFile",
      ("outputFormat", "json") ~ ("outputFileName", "test.json"))
    analysis merge(queryJson)
  }

  private def readAnalysisJson
    (analysisId: String, semantic: Boolean = false): JObject = {
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

  private def searchAnalysisJson
    (keys: JObject, semantic: Boolean = false): List[JObject] = {
    val analysisNode = new AnalysisNode
    val search = keys.extract[Map[String, Any]]
    analysisNode.find(search).map {
      _("content") match {
        case obj: JObject => obj
        case obj: JValue => unexpectedElement("object", obj)
      }
    }
  }

  private def contentsAnalyze(analysis: JObject): JObject = {
    contentsAnalyze(List(analysis))
  }

  private def contentsAnalyze(analyses: List[JObject]): JObject = {
    ("contents", ("analyze", JArray(analyses)))
  }

  def executeAnalysis(analysisId: String): JValue = {
 
    // reading the JSON extract type
    val analysisJSON = readAnalysisJson(analysisId);
    val analysisType = (analysisJSON \ "type");
    val analysisNode = AnalysisNode(analysisId)
    if (analysisNode.getCachedData == null || analysisNode.getCachedData.isEmpty)
      throw new Exception("Could not find analysis node with provided analysis ID")
    // check the type
    val typeInfo = analysisType.extract[String];
    println("TypeInfo :" + typeInfo);
    val json = render(analysisJSON).toString();
    println("Json for ElasticSearch:" + json);
    if ( typeInfo.equals("pivot") ){
      val data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.PIVOT, json), json);
      val myArray = data.asInstanceOf[JArray]
      m_log.trace("pivot dataset: {}", myArray)
      return myArray.arr
    }
    if ( typeInfo.equals("chart") ){
      val data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.CHART, json), json);
      val myArray = data.asInstanceOf[JArray]
      m_log.trace("chart dataset: {}", myArray)
      return myArray.arr
    }
    else {
    // This is the part of report type starts here
    val er: ExecutionTaskHandler = new ExecutionTaskHandler(1)
    val aeh: AnalysisExecutionHandler = new AnalysisExecutionHandler(analysisId)
    er.startSQLExecutor(aeh)
    val analysisResultId: String = er.getPredefResultRowID(analysisId)
    er.waitForCompletion(analysisId, aeh.getWaitTime)
    val out = new ByteArrayOutputStream()
    aeh.handleResult(out)
    val resultJson = parse(new String(out.toByteArray()))
    val resultLog = shortMessage(pretty(render(resultJson)))
    
    m_log.trace("Spark SQL executor result: {}", resultLog)
    (resultJson match {
      case obj: JObject => {
        m_log.error("Execution failed: {}", pretty(render(obj)))
        throw new RuntimeException(
          "Spark SQL execution failed: " +
            (obj \ "error_message").extractOrElse[String]("none"))
      }
      case JArray(result) => result.arr
      case value: JValue => throw new RuntimeException(
        "Expected array: " + value)
    }).drop(1)
    // This is the end of report type ends here
    }
  }

  private def shortMessage(message: String) = {
    message.substring(0, Math.min(message.length(), 1500))
  }

  private def unexpectedElement(expected: String, obj: JValue): Nothing = {
    val name = obj.getClass.getSimpleName
    throw new RuntimeException("Expected %s but got: %s".format(expected, name))
  }

  


}

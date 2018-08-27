package controllers

import java.time.Instant
import java.util
import java.util.UUID

import com.synchronoss.querybuilder.{EntityType, SAWElasticSearchQueryBuilder, SAWElasticSearchQueryExecutor}
import model.{ClientException, PaginateDataSet, QueryBuilder, TransportUtils}
import org.json4s.JsonAST.{JArray, JLong, JObject, JString, JValue, JBool => _, JField => _, JInt => _, JNothing => _}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import play.mvc.Result
import sncr.analysis.execution.ExecutionTaskHandler
import sncr.datalake.DLConfiguration
import sncr.datalake.TimeLogger._
import sncr.datalake.engine.ExecutionType
import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.analysis.AnalysisResult
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.context.SelectModels
import sncr.metadata.semantix.SemanticNode
import com.synchronoss.querybuilder.SAWElasticSearchQueryExecutor
import com.synchronoss.querybuilder.EntityType
import com.synchronoss.querybuilder.SAWElasticSearchQueryBuilder
import com.synchronoss.BuilderUtil
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

import scala.collection.JavaConverters._
import executor.ReportExecutorQueue
import org.json4s
import sncr.datalake.handlers.AnalysisNodeExecutionHelper
import sncr.metadata.engine.{Fields, MDObjectStruct, MetadataDictionary}
import sncr.saw.common.config.SAWServiceConfig

import scala.reflect.io.File

class Analysis extends BaseController {
  val executorRunner = new ExecutionTaskHandler(1);
  val executorRegularQueue = new ReportExecutorQueue("regular")
  val executorFastQueue = new ReportExecutorQueue("fast")
  var totalRows: Int = 0;

  /**
    * List analyses.  At the moment only used by scheduler to list
    * analyses that are scheduled.
    */
  @Deprecated
  def list(view: String): Result = {
    handle((json, ticket) => {
      if (view == "schedule") {
        listSchedules
      } else {
        throw new ClientException("Unknown view: " + view)
      }
    })
  }

  @Deprecated
  private def listSchedules: JObject = {
    val analysisNode = new AnalysisNode
    val search = Map[String, Any]("isScheduled" -> "true")
    /* Get all analyses */
    val analyses = analysisNode.find(search).map {
      _ ("content") match {
        case obj: JObject => obj
        case obj: JValue => unexpectedElement("object", obj)
      }
    }.filter(analysis => {
      /* Filter out those that have an schedule set */
      (analysis \ "schedule") match {
        case obj: JObject => true
        case _ => false
      }
    }).map(analysis => {
      /* Return only schedule view */
      ("id", (analysis \ "id")) ~
        ("name", (analysis \ "name")) ~
        ("description", (analysis \ "description")) ~
        ("metricName", (analysis \ "metricName")) ~
        ("userFullName", (analysis \ "userFullName")) ~
        ("schedule", (analysis \ "schedule")) ~
        ("type", (analysis \ "type"))
    })
    ("analyses", analyses)
  }

  def getMetadataByID(analysisId: String): Result = {
    handle((json, ticket) => {
      if (analysisId != null) {
        getAnalysisMetadataByID(analysisId)
      } else {
        throw new ClientException("Unknown request")
      }
    })
  }

  /** Return the analysis metadata by id field,
    * Currently this is only used in back-end pivot table creation. */
  private def getAnalysisMetadataByID(analysisId: String): JObject = {
    val analysisNode = new AnalysisNode
    val search = Map[String, Any]("id" -> analysisId)
    /* Get analyses by ID */
    val analysis = analysisNode.find(search).map {
      _ ("content") match {
        case obj: JObject => obj
        case obj: JValue => unexpectedElement("object", obj)
      }
    }
    ("analysis", analysis)
  }

  def process: Result = {
    handle(doProcess)
  }

  private def doProcess(json: JValue, ticket: Option[Ticket]): JValue = {
    val action = (json \ "contents" \ "action").extract[String].toLowerCase
    val (dataSecurityKey: java.util.List[Object]) = ticket match {
      case None => throw new ClientException(
        "Valid JWT not found in Authorization header")
      case Some(ticket) =>
        (ticket.dataSecurityKey)
    }

    action match {
      case "create" => {
        val semanticId = extractAnalysisId(json)
        val (userId: Integer, userFullName: String) = ticket match {
          case None => throw new ClientException(
            "Valid JWT not found in Authorization header")
          case Some(ticket) =>
            (ticket.userId, ticket.userFullName)
        }
        val instanceJson: JObject = ("semanticId", semanticId) ~
          ("createdTimestamp", Instant.now().toEpochMilli()) ~
          ("userId", userId.asInstanceOf[Number].longValue) ~
          ("userFullName", userFullName)
        val analysisId = UUID.randomUUID.toString
        val idJson: JObject = ("id", analysisId)
        val analysisType = extractKey(json, "analysisType")
        val typeJson: JObject = ("type", analysisType)
        val semanticJson = readSemanticJson(semanticId)
        val mergeJson = contentsAnalyze(
          semanticJson.merge(idJson).merge(instanceJson).merge(typeJson))
        val responseJson = json merge mergeJson
        val analysisJson = (responseJson \ "contents" \ "analyze") (0)
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
        m_log.trace("dataSecurityKey before processing in update: {}", dataSecurityKey);
        var dskStr: String = ""
        if (dataSecurityKey.size() > 0) {
          //dskStr = dataSecurityKey.asScala.mkString(",") ;
          dskStr = BuilderUtil.constructDSKCompatibleString(BuilderUtil.listToJSONString(dataSecurityKey));
          m_log.trace("dskStr after processing in update: {}", dskStr);
        }

        val analysisId = extractAnalysisId(json)
        val analysisNode = AnalysisNode(analysisId)
        m_log.trace("dskStr after processing inside update block before analysisJson(json, dskStr) : {}", dskStr);
        val responseJson = analysisJson(json, dskStr)
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
        val keys = (json \ "contents" \ "keys") (0) match {
          case keys: JObject => keys
          case obj => throw new ClientException("Expected object, got: " + obj)
        }
        m_log.debug("search key" + keys);
        val categoryId = extractKey(json, "categoryId")
        if (TransportUtils.checkIfPrivateAnalysis(ticket.get.product, categoryId) && !ticket.get.roleType.equalsIgnoreCase("Admin"))
          json merge contentsAnalyze(searchAnalysisJson(keys), ticket.get.userId.toString)
        else
          json merge contentsAnalyze(searchAnalysisJson(keys))
      }
      case "export" => {
        val keys = (json \ "contents" \ "keys") (0) match {
          case keys: JObject => keys
          case obj => throw new ClientException("Expected object, got: " + obj)
        }
        m_log.debug("search key" + keys);
        var allAnalysisList: List[JObject] = List()
        for {
              JArray(objList) <- (json \ "contents" \ "keys")
                 JObject(obj) <- objList
              } {
                 val analysisList = searchAnalysisJson(obj)
                 allAnalysisList = allAnalysisList ++ analysisList
              }
        json merge contentsAnalyze(allAnalysisList)
      }
      case "execute" => {

        logWithTime(m_log, "Execute analysis from controller", {
          m_log.trace("dataSecurityKey before processing in execute: {}", dataSecurityKey);
          var dskStr: String = ""
          if (dataSecurityKey.size() > 0) {
            //dskStr = dataSecurityKey.asScala.mkString(",") ;
            dskStr = BuilderUtil.constructDSKCompatibleString(BuilderUtil.listToJSONString(dataSecurityKey));
            m_log.trace("dskStr after processing in execute: {}", dskStr);
          }

          val analysisId = extractAnalysisId(json)
          var executionType: String = null
          var queryRuntime: String = null
          (json \ "contents" \ "analyze") match {
            case obj: JArray => {
              val analysis = analysisJson(json, dskStr)
              val analysisType = (analysis \ "type")
              val typeInfo = analysisType.extract[String]
              executionType = (analysis \ "executionType").extractOrElse[String]("onetime")
              if (typeInfo.equals("report")) {
                /* Build query based on analysis supplied in request body */
                val runtime = (executionType == ExecutionType.onetime.toString
                  || executionType == ExecutionType.regularExecution.toString
                  || executionType == ExecutionType.publish.toString)
                m_log.debug("Execution type: {}", executionType)
                m_log.trace("dskStr after processing inside execute block before runtime: {}", dskStr);
                m_log.trace("runtime execute block before queryRuntime: {}", runtime);
                queryRuntime = (analysis \ "queryManual") match {
                  case JNothing => QueryBuilder.build(analysis, runtime, dskStr)
                  case obj: JString => obj.extract[String]
                  case obj => unexpectedElement("string", obj)
                }
                m_log.info("RUNTIME_QUERY_RUNTIME" + queryRuntime)
              }
            }
            case _ => {}
          }
          if (executionType==null || executionType.isEmpty){
           // Consider the default Execution type as publish for the backward compatibility.
            executionType = "publish"
          }

          m_log.trace("dskStr after processing inside execute block before Execute analysis and return result data : {}", dskStr);
          val data = executeAnalysis(analysisId, executionType, queryRuntime, json, dskStr)
          contentsAnalyze(("data", data._1) ~ ("totalRows", totalRows) ~ ("executionId", data._2))
        })

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
  }

  def extractAnalysisId(json: JValue) = {
    extractKey(json, "id")
  }

  private def extractKey(json: JValue, property: String) = {
    try {
      val JString(value) = (json \ "contents" \ "keys") (0) \ property
      value
    } catch {
      case e: Exception =>
        throw new ClientException("Analysis ID not found in keys property")
    }
  }

  def analysisJson(json: JValue, DSK: String): JObject = {
    val analysisListJson = json \ "contents" \ "analyze"
    val analysisJson = analysisListJson match {
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
    val analysis = (analysisJson \ "schedule") match {
      case obj: JObject => updateField(analysisJson, "isScheduled", "true")
      case _ => updateField(analysisJson, "isScheduled", "false")
    }
    val analysisType = (analysisListJson \ "type");
    val typeInfo = analysisType.extract[String];
    if (typeInfo.equals("report")) {
      val query = (analysis \ "queryManual") match {
        case JNothing => QueryBuilder.build(analysis, false, DSK)
        case obj: JString => obj.extract[String]
        case obj => unexpectedElement("string", obj)
      }
      m_log.info("REPORT_QUERY:" + query)
      val queryJson: JObject = ("query", JString(query)) ~
        ("outputFile",
          ("outputFormat", "json") ~ ("outputFileName", "test.json"))
      return analysis merge (queryJson)
    }
    else {
      return analysis
    }
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
      _ ("content") match {
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

  def unexpectedElement(
                         json: JValue, expected: String, location: String): Nothing = {
    val name = json.getClass.getSimpleName
    throw new ClientException(
      "Unexpected element: %s, expected %s, at %s".format(
        name, expected, location))
  }

  /**
    * Return the list of analysis created in my analysis category by requested user.
    *
    * @param analyses
    * @param userId
    * @return
    */
  private def contentsAnalyze(analyses: List[JObject], userId: String): JObject = {
    ("contents", ("analyze", JArray(analyses.filter(_.values.get("userId").get == userId.toInt))))
  }

  var result: String = null

  def setResult(r: String): Unit = result = r

  def executeAnalysis(analysisId: String, executionType: String, queryRuntime: String = null, reqJSON: JValue = null, dataSecurityKeyStr: String): (json4s.JValue, String) = {
    var json: String = "";
    var typeInfo: String = "";
    var analysisJSON: JObject = null;
    m_log.trace("dataSecurityKeyStr dataset: {}", dataSecurityKeyStr);
    m_log.trace("json dataset: {}", reqJSON);
    val start = (reqJSON \ "contents" \ "page").extractOrElse(1)
    val limit = (reqJSON \ "contents" \ "pageSize").extractOrElse(10)
    val analysis = (reqJSON \ "contents" \ "analyze") match {
      case obj: JArray => analysisJson(reqJSON, dataSecurityKeyStr); // reading from request body
      case _ => null
    }
    var analysisDefinition :JObject = null

    if (analysis == null) {
      analysisJSON = readAnalysisJson(analysisId); // reading from the store
      analysisDefinition = analysisJson(analysisJSON,dataSecurityKeyStr)
      val analysisType = (analysisJSON \ "type");
      typeInfo = analysisType.extract[String];
      json = compact(render(analysisJSON));
    }
    else {
      val analysisType = (analysis \ "type");
      typeInfo = analysisType.extract[String];
      analysisDefinition = analysisJson(reqJSON,dataSecurityKeyStr)
      json = compact(render(analysis));
    }
    val queryBuilder : JObject = (analysisDefinition \ "sqlBuilder") match {
      case obj: JObject => obj
      case JNothing => JObject()
      case obj: JValue => unexpectedElement(obj, "object", "sqlBuilder")
    }
    val analysisNode = AnalysisNode(analysisId)
    val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val analysisName = (analysisJSON \ "metricName").extractOpt[String]
    var descriptor: JObject = null
    val ldt: LocalDateTime = LocalDateTime.now()
    val timestamp: String = ldt.format(dfrm)
    var schema: JValue = JNothing
    var resultNode: AnalysisResult = null

    if (analysisNode.getCachedData == null || analysisNode.getCachedData.isEmpty)
      throw new Exception("Could not find analysis node with provided analysis ID")
    m_log.trace("json dataset: {}", json);
    m_log.trace("type: {}", typeInfo);
    val timeOut: java.lang.Integer = if (SAWServiceConfig.es_conf.hasPath("timeout"))
      new Integer(SAWServiceConfig.es_conf.getInt("timeout")) else new java.lang.Integer(3)
    if (typeInfo.equals("pivot")) {
      var data: String = null
      if (dataSecurityKeyStr != null) {
        m_log.trace("dataSecurityKeyStr dataset inside pivot block: {}", dataSecurityKeyStr);
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.PIVOT, json, dataSecurityKeyStr, timeOut), json, timeOut);
      }
      else {
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.PIVOT, json, timeOut), json, timeOut);

      }

      val finishedTS = System.currentTimeMillis;
      val myArray = parse(data);
      var analysisResultNodeID: String = analysisId + "::" + System.nanoTime();
      m_log.trace("pivot dataset: {}", myArray)
      /* skip the resultNode creation for preview/onetime execution result node */

      if (!(executionType.equalsIgnoreCase(ExecutionType.onetime.toString)
        || executionType.equalsIgnoreCase(ExecutionType.preview.toString)
        || executionType.equalsIgnoreCase(ExecutionType.regularExecution.toString))) {

        // The below block is for execution result to store
        if (data != null) {
          var nodeExists = false
          try {
            m_log debug s"Remove result: " + analysisResultNodeID
            resultNode = AnalysisResult(analysisId, analysisResultNodeID)
            nodeExists = true
          }
          catch {
            case e: Exception => m_log debug("Tried to load node: {}", e.toString)
          }
          if (nodeExists) resultNode.delete

          schema = JObject(JField("schema", JString("Does not need int the case of the Chart")))
          descriptor = new JObject(List(
            JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("id", JString(analysisId)),
            JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("execution_result", JString("success")),
            JField("type", JString("pivot")),
            JField("executionType", JString(executionType)),
            JField("execution_result", JString("success")),
            JField("execution_finish_ts", JLong(finishedTS)),
            JField("exec-code", JInt(0)),
            JField("execution_start_ts", JString(timestamp)),
            JField("queryBuilder", queryBuilder)
          ))
          m_log debug s"Create result: with content: ${compact(render(descriptor))}"
        }
        else {
          val errorMsg = "There is no result for query criteria";
          descriptor = new JObject(List(
            JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("id", JString(analysisId)),
            JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("execution_result", JString("failed")),
            JField("execution_finish_ts", JLong(-1L)),
            JField("type", JString("pivot")),
            JField("executionType", JString(executionType)),
            JField("exec-code", JInt(1)),
            JField("execution_start_ts", JString(timestamp)),
            JField("error_message", JString(errorMsg)),
            JField("queryBuilder", queryBuilder)
          ))
        }

        var descriptorPrintable: JValue = null
        resultNode = new AnalysisResult(analysisId, descriptor, analysisResultNodeID)
        if (data != null) {
          resultNode.addObject("data", myArray, schema);
          val (res, msg) = resultNode.create;
          m_log debug s"Analysis result creation: $res ==> $msg"
        }
        else {
          descriptorPrintable = descriptor
        }
      }

      return (myArray ,analysisResultNodeID)
    }

    if (typeInfo.equals("esReport")) {
      var data: String = null
      val rowLimit: java.lang.Integer = if (SAWServiceConfig.es_conf.hasPath("inline-es-report-data-store-limit-rows"))
        new Integer(SAWServiceConfig.es_conf.getInt("inline-es-report-data-store-limit-rows")) else new java.lang.Integer(10000)
      if (dataSecurityKeyStr != null) {
        m_log.trace("dataSecurityKeyStr dataset inside esReport block: {}", dataSecurityKeyStr)
        data = SAWElasticSearchQueryExecutor.executeReturnDataAsString(
          new SAWElasticSearchQueryBuilder(rowLimit).getSearchSourceBuilder(EntityType.ESREPORT, json, dataSecurityKeyStr, timeOut), json, timeOut);
      }
      else {
        data = SAWElasticSearchQueryExecutor.executeReturnDataAsString(
          new SAWElasticSearchQueryBuilder(rowLimit).getSearchSourceBuilder(EntityType.ESREPORT, json, timeOut), json, timeOut);
      }

      val finishedTS = System.currentTimeMillis;
      val myArray = parse(data);
      m_log.trace("esReport dataset: {}", myArray)
      var analysisResultNodeID: String = analysisId + "::" + System.nanoTime();
      /* skip the resultNode creation for preview/onetime execution result node */

      /* To support the pagination for es-report, store all the result as history
        to avoid re-execution for same report in case of pagination request. */
        // The below block is for execution result to store
        if (data != null) {
          var nodeExists = false
          try {
            m_log debug s"Remove result: " + analysisResultNodeID
            resultNode = AnalysisResult(analysisId, analysisResultNodeID)
            nodeExists = true
          }
          catch {
            case e: Exception => m_log debug("Tried to load node: {}", e.toString)
          }
          if (nodeExists) resultNode.delete

          schema = JObject(JField("schema", JString("Does not need int")))
          descriptor = new JObject(List(
            JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("id", JString(analysisId)),
            JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("execution_result", JString("success")),
            JField("type", JString("esReport")),
            JField("executionType", JString(executionType)),
            JField("execution_result", JString("success")),
            JField("execution_finish_ts", JLong(finishedTS)),
            JField("exec-code", JInt(0)),
            JField("execution_start_ts", JString(timestamp)),
            JField("queryBuilder", queryBuilder)
          ))
          m_log debug s"Create result: with content: ${compact(render(descriptor))}"
        }
        else {
          val errorMsg = "There is no result for query criteria";
          descriptor = new JObject(List(
            JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("id", JString(analysisId)),
            JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("execution_result", JString("failed")),
            JField("execution_finish_ts", JLong(-1L)),
            JField("type", JString("esReport")),
            JField("executionType", JString(executionType)),
            JField("exec-code", JInt(1)),
            JField("execution_start_ts", JString(timestamp)),
            JField("error_message", JString(errorMsg)),
            JField("queryBuilder", queryBuilder)
          ))
        }

        var descriptorPrintable: JValue = null
        resultNode = new AnalysisResult(analysisId, descriptor, analysisResultNodeID)
        if (data != null) {
          resultNode.addObject("data", myArray, schema);
          val (res, msg) = resultNode.create;
          m_log debug s"Analysis result creation: $res ==> $msg"
        }
        else {
          descriptorPrintable = descriptor
        }

      return (getESReportData(analysisResultNodeID, start, limit, typeInfo, myArray),analysisResultNodeID)
    }
    if (typeInfo.equals("chart")) {
      var data: String = null
      if (dataSecurityKeyStr != null) {
        m_log.trace("dataSecurityKeyStr dataset inside chart block: {}", dataSecurityKeyStr);
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.CHART, json, dataSecurityKeyStr, timeOut), json, timeOut);
      }
      else {
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.CHART, json, timeOut), json, timeOut);
      }
      val finishedTS = System.currentTimeMillis;
      val myArray = parse(data);
      var analysisResultNodeID: String = analysisId + "::" + System.nanoTime();
      /* skip the resultNode creation for preview/onetime execution result node */

    if (!(executionType.equalsIgnoreCase(ExecutionType.onetime.toString)
        || executionType.equalsIgnoreCase(ExecutionType.preview.toString)
        || executionType.equalsIgnoreCase(ExecutionType.regularExecution.toString))) {

        // The below block is for execution result to store
        if (data != null) {
          var nodeExists = false
          try {
            m_log debug s"Remove result: " + analysisResultNodeID
            resultNode = AnalysisResult(analysisId, analysisResultNodeID)
            nodeExists = true
          }
          catch {
            case e: Exception => m_log debug("Tried to load node: {}", e.toString)
          }
          if (nodeExists) resultNode.delete

          schema = JObject(JField("schema", JString("Does not need int the case of the Chart")))
          descriptor = new JObject(List(
            JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("id", JString(analysisId)),
            JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("execution_finish_ts", JLong(finishedTS)),
            JField("type", JString("chart")),
            JField("executionType", JString(executionType)),
            JField("execution_result", JString("success")),
            JField("exec-code", JInt(0)),
            JField("execution_start_ts", JString(timestamp)),
            JField("queryBuilder", queryBuilder)

          ))
          m_log debug s"Create result: with content: ${compact(render(descriptor))}"
        }
        else {
          val errorMsg = "There is no result for query criteria";
          descriptor = new JObject(List(
            JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("id", JString(analysisId)),
            JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
            JField("execution_result", JString("failed")),
            JField("execution_finish_ts", JLong(-1L)),
            JField("type", JString("chart")),
            JField("executionType", JString(executionType)),
            JField("exec-code", JInt(1)),
            JField("execution_start_ts", JString(timestamp)),
            JField("error_message", JString(errorMsg)),
            JField("queryBuilder", queryBuilder)
          ))
        }

        var descriptorPrintable: JValue = null
        resultNode = new AnalysisResult(analysisId, descriptor, analysisResultNodeID)
        if (data != null) {
          resultNode.addObject("data", myArray, schema);
          val (res, msg) = resultNode.create;
          m_log debug s"Analysis result creation: $res ==> $msg"
        }
        else {
          descriptorPrintable = descriptor
        }
      }
      m_log.trace("chart dataset: {}", myArray)
      return (myArray,analysisResultNodeID)
    }
    else {
      // This is the part of report type starts here
      m_log.trace("dataSecurityKeyStr dataset inside report block: {}", dataSecurityKeyStr);
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      m_log.trace("queryRuntime inside report block before executeAndWait: {}", queryRuntime);
      val query = if (queryRuntime != null) queryRuntime
      // In case of scheduled execution type if manual query exists take the precedence.
      else if (executionType ==
        ExecutionType.scheduled.toString) {
        (analysisJSON \ "queryManual") match {
          case JNothing => QueryBuilder.build(analysisJSON, false, dataSecurityKeyStr)
          case obj: JString => obj.extract[String]
          case obj => unexpectedElement("string", obj)
        }
      }
      else
        QueryBuilder.build(analysisJSON, false, dataSecurityKeyStr)
      m_log.trace("query inside report block before executeAndWait : {}", query);
      /* Execute analysis report query through queue for concurrency */
      val executionTypeEnum = executionType match {
        case "preview" => ExecutionType.preview
        case "onetime" => ExecutionType.onetime
        case "scheduled" => ExecutionType.scheduled
        case "regularExecution" => ExecutionType.regularExecution
        case "publish" => ExecutionType.publish
        case obj => throw new RuntimeException("Unknown execution type: " + obj)
      }
      val execution = analysis.executeAndWaitQueue(
        executionTypeEnum, query, (analysisId, resultId, query) => {
          val executorQueue = executionTypeEnum match {
            case ExecutionType.preview => executorFastQueue
            case ExecutionType.onetime => executorFastQueue
            case ExecutionType.scheduled => executorRegularQueue
            case ExecutionType.regularExecution => executorRegularQueue
            case ExecutionType.publish => executorRegularQueue
            case obj => throw new RuntimeException("Unknown execution type: " + obj)
          }
          executorQueue.send(executionTypeEnum, analysisId, resultId, query, limit)
        })
      val analysisResultId: String = execution.getId
      m_log.trace("analysisResultId inside report block after executeAndWait : {}", analysisResultId);
      //TODO:: Subject to change: to get ALL data use:  val resultData = execution.getAllData
      //TODO:: DLConfiguration.rowLimit can be replace with some Int value

      logWithTime(m_log, "Load execution result", {
        var data: JValue = null
        val resultData: java.util.List[java.util.Map[String, (String, Object)]] =
          new util.ArrayList[util.Map[String, (String, Object)]]()

        if (PaginateDataSet.INSTANCE.getCache(analysisResultId) != null &&
          PaginateDataSet.INSTANCE.getCache(analysisResultId).get(0).size() > 0) {
          m_log.trace("when data is available in cache analysisResultId: {}", analysisResultId);
          m_log.trace("when data is available in cache size of limit {}", limit);
          m_log.trace("when data is available in cache size of start {}", start);
          data = processReportResult(PaginateDataSet.INSTANCE.paginate(limit, start, analysisResultId));
          totalRows = PaginateDataSet.INSTANCE.sizeOfData();
          m_log.trace("totalRows {}", totalRows);
        }
        else {
          /* Load execution results from data lake (instead of from Spark driver) */
          /* Performance consideration: For preview and one time analysis execution,no need to
            create resultNode, transport service will directly read data from data lake for */

        if (executionType.equalsIgnoreCase(ExecutionType.onetime.toString)
            || executionType.equalsIgnoreCase(ExecutionType.preview.toString)
            || executionType.equalsIgnoreCase(ExecutionType.regularExecution.toString)) {
            val outputLocation = AnalysisNodeExecutionHelper.getUserSpecificPath(DLConfiguration.commonLocation) +
              File.separator + "preview-" + execution.getId
            val resultStream = execution.loadOneTimeExecution(outputLocation, DLConfiguration.rowLimit)
            prepareResultDataFromStream(resultStream, resultData)
          }
          else {
            val resultStream = execution.loadExecution(execution.getId, DLConfiguration.rowLimit)
            prepareResultDataFromStream(resultStream, resultData)
          }
          m_log.trace("when data is not available in cache analysisResultId: {}", analysisResultId);
          m_log.trace("when data is not available in cache size of limit {}", limit);
          m_log.trace("when data is not available in cache size of start {}", start);
          if (resultData != null) {
            PaginateDataSet.INSTANCE.putCache(analysisResultId, resultData);
            data = processReportResult(PaginateDataSet.INSTANCE.paginate(limit, start, analysisResultId))
            totalRows = PaginateDataSet.INSTANCE.sizeOfData();
            m_log.info("totalRows {}", totalRows);
          }
          else {
            data = JArray(List())
          }
        }
        m_log debug s"Exec code: ${execution.getExecCode}, message: ${execution.getExecMessage}, created execution id: $analysisResultId"
        m_log debug s"start:  ${analysis.getStartTS} , finished  ${analysis.getFinishedTS} "
        m_log.trace("Spark SQL executor result: {}", pretty(render(data)))
        (data,analysisResultId)
      })
    }
  }

  import scala.collection.JavaConversions._

  def processReportResult(data: util.List[util.Map[String, (String, Object)]]): JValue = {
    if (data == null || data.isEmpty) return JArray(List())
    JArray(data.map(m => {
      JObject(m.keySet().map(k =>
        JField(k, m.get(k)._1 match {
          case "StringType" => JString(m.get(k)._2.asInstanceOf[String])
          case "IntegerType" => JInt(m.get(k)._2.asInstanceOf[Int])
          case "BooleanType" => JBool(m.get(k)._2.asInstanceOf[Boolean])
          case "LongType" => JLong(m.get(k)._2.asInstanceOf[Long])
          case "DoubleType" => JDouble(m.get(k)._2.asInstanceOf[Double])
          /* It is possible that the data type information returned from the
           * Spark SQL Executor might not always come through
           * reliably.  So as a fallback also perform conversions
           * based on the Java object classes.  */
          case dataType => m.get(k)._2 match {
            case obj: String => JString(obj)
            case obj: java.lang.Integer => JInt(obj.intValue())
            case obj: java.lang.Long => JLong(obj.longValue())
            case obj: java.lang.Float => JDouble(obj.floatValue())
            case obj: java.lang.Double => JDouble(obj.doubleValue())
            case obj: java.lang.Boolean => JBool(obj.booleanValue())
            case obj: java.sql.Date => JLong(obj.getTime())
            case obj: scala.math.BigInt => JLong(obj.toLong)
            case obj =>
              throw new RuntimeException(
                "Unsupported data type in result: " + dataType
                  + ", object class: " + obj.getClass.getName)
          }
        })
      ).toList
      )
    }
    ).toList)
  }

  private def updateField(json: JObject, name: String, value: JValue) = {
    ((json removeField {
      case JField(fieldName, _) => fieldName == name
      case _ => false
    }) match {
      case obj: JObject => obj
      case obj => throw new RuntimeException("Unsupported type: " + obj)
    }) ~ (name, value)
  }

  private def getESReportData(executionId: String, page: Int, pageSize: Int, analysisType: String, data: JValue): JValue = {
    if (analysisType.equalsIgnoreCase("esReport")) {
      var pagingData: JValue = null
      val results = new java.util.ArrayList[java.util.Map[String, (String, Object)]]
      data.extract[scala.List[Map[String, Any]]]
        .foreach(row => {
          val resultsRow = new java.util.HashMap[String, (String, Object)]
          row.keys.foreach(key => {
            row.get(key).foreach(value => resultsRow.put(key, ("unknown", value.asInstanceOf[AnyRef])))
          })
          results.add(resultsRow)
        })
      pagingData = processReportResult(results)
      PaginateDataSet.INSTANCE.putCache(executionId, results)
      pagingData = processReportResult(PaginateDataSet.INSTANCE.paginate(pageSize, page, executionId))
      totalRows = PaginateDataSet.INSTANCE.sizeOfData()
      m_log.trace("totalRows {}", totalRows)
      pagingData
    }
    else throw new Exception("Unsupported data format")
  }

  private def prepareResultDataFromStream(resultStream: java.util.stream.Stream[String],
                                          resultData: java.util.List[java.util.Map[String, (String, Object)]]): Unit = {
    resultStream.limit(DLConfiguration.rowLimit)
      .iterator.asScala
      .foreach(line => {
        val resultsRow = new java.util.HashMap[String, (String, Object)]
        parse(line) match {
          case obj: JObject => {
            /* Convert the parsed JSON to the data type expected by the
            * loadExecution method signature */
            val rowMap = obj.extract[Map[String, Any]]
            rowMap.keys.foreach(key => {
              rowMap.get(key).foreach(value => resultsRow.put(key, ("unknown", value.asInstanceOf[AnyRef])))
            })
            resultData.add(resultsRow)
          }
          case obj => throw new RuntimeException("Unknown result row type from JSON: " + obj.getClass.getName)
        }
      })
  }
}

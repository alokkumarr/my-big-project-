package controllers

import java.time.Instant
import java.util
import java.util.UUID
import com.synchronoss.querybuilder.{EntityType, SAWElasticSearchQueryBuilder, SAWElasticSearchQueryExecutor}
import model.{ClientException, PaginateDataSet, QueryBuilder}
import org.json4s.JsonAST.{JArray, JLong, JObject, JString, JValue, JBool => _, JField => _, JInt => _, JNothing => _}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import play.mvc.Result
import sncr.analysis.execution.ExecutionTaskHandler
import sncr.datalake.DLConfiguration
import sncr.datalake.engine.ExecutionType
import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.analysis.AnalysisResult
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.context.SelectModels
import sncr.metadata.semantix.SemanticNode
import com.synchronoss.querybuilder.SAWElasticSearchQueryExecutor
import com.synchronoss.querybuilder.EntityType
import com.synchronoss.querybuilder.SAWElasticSearchQueryBuilder
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

import sncr.metadata.engine.{Fields, MetadataDictionary}

class Analysis extends BaseController {
  val executorRunner = new ExecutionTaskHandler(1);
  var totalRows: Int = 0;
  
  /**
    * List analyses.  At the moment only used by scheduler to list
    * analyses that are scheduled.
    */
  def list(view: String): Result = {
    handle((json, ticket) => {
      if (view == "schedule") {
        listSchedules
      } else {
        throw new ClientException("Unknown view: " + view)
      }
    })
  }

  private def listSchedules: JObject = {
    val analysisNode = new AnalysisNode
    val search = Map[String, Any]("isScheduled" -> "true")
    /* Get all analyses */
    val analyses = analysisNode.find(search).map {
      _("content") match {
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
      ("schedule", (analysis \ "schedule"))
    })
    ("analyses", analyses)
  }

  def process: Result = {
    handle(doProcess)
  }

  private def doProcess(json: JValue, ticket: Option[Ticket]): JValue = {
    val action = (json \ "contents" \ "action").extract[String].toLowerCase
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
        val (dataSecurityKey: String) = ticket match {
          case None => throw new ClientException(
            "Valid JWT not found in Authorization header")
          case Some(ticket) =>
            (ticket.dataSecurityKey)
        }
        val dskString = dataSecurityKey.asInstanceOf[String].toString;
        val dskStr : String =null
        var parsedDSK : JValue=null
        if((dskString!=null) && (!dskString.equals("") && !dskStr.equals("NA"))) {
        val dskStr = "{ \"dataSecurityKey\" :" +dataSecurityKey.asInstanceOf[String].toString + "}";
        m_log.trace("dskStr: {}", dskStr);
        parsedDSK = parse(dskStr);
          m_log.trace("parsedDskStr dataset: {}", parsedDSK);
        }

        var queryRuntime: String = null
        (json \ "contents" \ "analyze") match {
          case obj: JArray => {
            val analysis = analysisJson(json)
            val analysisType = (analysis \ "type")
    	    val typeInfo = analysisType.extract[String]
    	    if (typeInfo.equals("report"))
    	    {
	      /* Build query based on analysis supplied in request body */
	      val executionType = (analysis \ "executionType")
	        .extractOrElse[String]("interactive")
	      val runtime = (executionType == "interactive")
              queryRuntime = (analysis \ "queryManual") match {
                case JNothing => QueryBuilder.build(analysis, runtime)
                case obj: JString => obj.extract[String]
                case obj => unexpectedElement("string", obj)
              }
          }}
          case _ => {}
        }
        /* Execute analysis and return result data */
        val data = executeAnalysis(analysisId, queryRuntime, json, dskStr)
        contentsAnalyze(("data", data)~ ("totalRows",totalRows))

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
      val JString(value) = (json \ "contents" \ "keys")(0) \ property
      value
    } catch {
      case e: Exception =>
        throw new ClientException("Analysis ID not found in keys property")
    }
  }

  def analysisJson(json: JValue): JObject = {
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
    if ( typeInfo.equals("report") ){
    val query = (analysis \ "queryManual") match {
      case JNothing => QueryBuilder.build(analysis)
      case obj: JString => ""
      case obj => unexpectedElement("string", obj)
    }
    val queryJson: JObject = ("query", JString(query)) ~
    ("outputFile",
      ("outputFormat", "json") ~ ("outputFileName", "test.json"))
     return analysis merge(queryJson)
    }
    else 
    {
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

  var result: String = null
  def setResult(r: String): Unit = result = r
 
  def executeAnalysis(analysisId: String, queryRuntime: String = null, reqJSON: JValue =null, dataSecurityKeyStr : String): JValue = {
 	var json: String = "";
 	var typeInfo : String = "";
 	var analysisJSON : JObject = null;
 	
 	m_log.trace("json dataset: {}", reqJSON);
    val start = (reqJSON \ "contents" \ "page").extractOrElse(0)
    val limit = (reqJSON \ "contents" \ "pageSize").extractOrElse(10)
    val analysis = (reqJSON \ "contents" \ "analyze") match {
      case obj: JArray => analysisJson(reqJSON); // reading from request body
      case _ => null
    }
    if (analysis == null) {
		analysisJSON = readAnalysisJson(analysisId); // reading from the store
		val analysisType = (analysisJSON \ "type");
   		typeInfo = analysisType.extract[String];
   		json = compact(render(analysisJSON));
    }
    else {
        val analysisType = (analysis \ "type");
        typeInfo = analysisType.extract[String];
   		json = compact(render(analysis));
    } 
    val analysisNode = AnalysisNode(analysisId)
    val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val analysisName = (analysisJSON \ "metricName").extractOpt[String]
    var descriptor: JObject = null
    val ldt: LocalDateTime = LocalDateTime.now()
    val timestamp: String = ldt.format(dfrm)
    var schema : JValue = JNothing
    var resultNode: AnalysisResult = null
    
    if (analysisNode.getCachedData == null || analysisNode.getCachedData.isEmpty)
      throw new Exception("Could not find analysis node with provided analysis ID")
    m_log.trace("json dataset: {}", json);
    m_log.trace("type: {}", typeInfo);
    if ( typeInfo.equals("pivot") )
    {
      var data : String= null
      if (dataSecurityKeyStr!=null || (!dataSecurityKeyStr.equals("") || (!dataSecurityKeyStr.equals("NA")))) {
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.PIVOT, json, dataSecurityKeyStr), json);
      }
      else {
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.PIVOT, json), json);

      }

      val finishedTS = System.currentTimeMillis;
      val myArray = parse(data);
      m_log.trace("pivot dataset: {}", myArray)
      
      var analysisResultNodeID: String = analysisId + "::" + System.nanoTime();
      // The below block is for execution result to store
		if (data !=null){
		    var nodeExists = false
		    try {
		      m_log debug s"Remove result: " + analysisResultNodeID
		      resultNode = AnalysisResult(analysisId, analysisResultNodeID)
		      nodeExists = true
		    }
		    catch {
		      case e: Exception => m_log debug("Tried to load node: ", e)
		    }
		    if (nodeExists) resultNode.delete
		
		schema  = JObject(JField("schema", JString("Does not need int the case of the Chart")))
		descriptor = new JObject(List(
        JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("id", JString(analysisId)),
        JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("execution_result", JString("success")),
        JField("type", JString("pivot")),
        JField("execution_result", JString("success")),
        JField("execution_finish_ts", JLong(finishedTS)),
        JField("exec-code", JInt(0)),
        JField("execution_start_ts", JString(timestamp))
      ))
      m_log debug s"Create result: with content: ${compact(render(descriptor))}"
		}
		else 
			{
			val errorMsg = "There is no result for query criteria";
	        descriptor = new JObject(List(
	        JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
	        JField("id", JString(analysisId)),
	        JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
	        JField("execution_result", JString("failed")),
	        JField("execution_finish_ts", JLong(-1L)),
          JField("type", JString("pivot")),
	        JField("exec-code", JInt(1)),
	        JField("execution_start_ts", JString(timestamp)),
	        JField("error_message", JString(errorMsg))
	      ))
     	}
      
      	var descriptorPrintable: JValue = null
	    resultNode = new AnalysisResult(analysisId, descriptor, analysisResultNodeID)
		if (data !=null)
		{
			resultNode.addObject("data", myArray, schema);
			val (res, msg) = resultNode.create;
	    	m_log debug s"Analysis result creation: $res ==> $msg"
		}
		else 
		{
		  descriptorPrintable = descriptor
	    }	   
      
      return myArray
    }
    if ( typeInfo.equals("chart") ){
      var data : String = null
      if (dataSecurityKeyStr!=null || (!dataSecurityKeyStr.equals("") || (!dataSecurityKeyStr.equals("NA")))) {
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.CHART, json, dataSecurityKeyStr), json);
      }
      else {
        data = SAWElasticSearchQueryExecutor.executeReturnAsString(
          new SAWElasticSearchQueryBuilder().getSearchSourceBuilder(EntityType.CHART, json), json);
      }
      val finishedTS = System.currentTimeMillis;
      val myArray = parse(data);
      var analysisResultNodeID: String = analysisId + "::" + System.nanoTime();
      // The below block is for execution result to store
		if (data !=null){
		    var nodeExists = false
		    try {
		      m_log debug s"Remove result: " + analysisResultNodeID
		      resultNode = AnalysisResult(analysisId, analysisResultNodeID)
		      nodeExists = true
		    }
		    catch {
		      case e: Exception => m_log debug("Tried to load node: ", e)
		    }
		    if (nodeExists) resultNode.delete
		
		schema  = JObject(JField("schema", JString("Does not need int the case of the Chart")))
		descriptor = new JObject(List(
        JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("id", JString(analysisId)),
        JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("execution_finish_ts", JLong(finishedTS)),
        JField("type", JString("chart")),
        JField("execution_result", JString("success")),
        JField("exec-code", JInt(0)),
        JField("execution_start_ts", JString(timestamp))
      ))
      m_log debug s"Create result: with content: ${compact(render(descriptor))}"
		}
		else 
			{
			val errorMsg = "There is no result for query criteria";
	        descriptor = new JObject(List(
	        JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
	        JField("id", JString(analysisId)),
	        JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
	        JField("execution_result", JString("failed")),
	        JField("execution_finish_ts", JLong(-1L)),
          JField("type", JString("chart")),
	        JField("exec-code", JInt(1)),
	        JField("execution_start_ts", JString(timestamp)),
	        JField("error_message", JString(errorMsg))
	      ))
     	}
      
      	var descriptorPrintable: JValue = null
	    resultNode = new AnalysisResult(analysisId, descriptor, analysisResultNodeID)
		if (data !=null)
		{
			resultNode.addObject("data", myArray, schema);
			val (res, msg) = resultNode.create;
	    	m_log debug s"Analysis result creation: $res ==> $msg"
		}
		else 
		{
		  descriptorPrintable = descriptor
	    }	   
      
      m_log.trace("chart dataset: {}", myArray)
      return myArray
    }
    else {
      // This is the part of report type starts here
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val query = if (queryRuntime != null) queryRuntime else QueryBuilder.build(analysisJSON, false)
      val execution = analysis.executeAndWait(ExecutionType.onetime, query)
      val analysisResultId: String = execution.getId
      //TODO:: Subject to change: to get ALL data use:  val resultData = execution.getAllData
      //TODO:: DLConfiguration.rowLimit can be replace with some Int value

      var data: JValue = null
      var resultData : java.util.List[java.util.Map[String, (String, Object)]] = null

      if (PaginateDataSet.INSTANCE.getCache(analysisResultId) != null && PaginateDataSet.INSTANCE.getCache(analysisResultId).get(0).size()>0)
      {
        m_log.trace("when data is available in cache analysisResultId: {}", analysisResultId);
        m_log.trace("when data is available in cache size of limit {}", limit);
        m_log.trace("when data is available in cache size of start {}", start);
        data = processReportResult(PaginateDataSet.INSTANCE.paginate(limit, start, analysisResultId));
        totalRows = PaginateDataSet.INSTANCE.sizeOfData();
        m_log.trace("totalRows {}", totalRows);
      }
      else {
        resultData = execution.getPreview(DLConfiguration.rowLimit);
        m_log.trace("when data is not available in cache analysisResultId: {}", analysisResultId);
        m_log.trace("when data is not available in cache size of limit {}", limit);
        m_log.trace("when data is not available in cache size of start {}", start);
        m_log.trace("when data is not available fresh execution of resultData {}", resultData.size());
        PaginateDataSet.INSTANCE.putCache(analysisResultId, resultData);
        data = processReportResult(PaginateDataSet.INSTANCE.paginate(limit, start, analysisResultId))
        totalRows = PaginateDataSet.INSTANCE.sizeOfData();
        m_log.info("totalRows {}", totalRows);
      }
      m_log debug s"Exec code: ${execution.getExecCode}, message: ${execution.getExecMessage}, created execution id: $analysisResultId"
      m_log debug s"start:  ${analysis.getStartTS} , finished  ${analysis.getFinishedTS} "
      m_log.trace("Spark SQL executor result: {}", pretty(render(data)))
      data
    }
  }

  import scala.collection.JavaConversions._
  def processReportResult(data: util.List[util.Map[String, (String, Object)]]) : JValue = {
    if ( data == null || data.isEmpty) return JArray(List())
    JArray(data.map(m => {
       JObject(m.keySet().map(k =>
          JField(k, m.get(k)._1 match {
            case "StringType" => JString(m.get(k)._2.asInstanceOf[String])
            case "IntegerType" => JInt(m.get(k)._2.asInstanceOf[Int])
            case "BooleanType" => JBool(m.get(k)._2.asInstanceOf[Boolean])
            case "LongType" => JLong(m.get(k)._2.asInstanceOf[Long])
            case "DoubleType" => JDouble(m.get(k)._2.asInstanceOf[Double])
          })
         ).toList
       )}
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
}

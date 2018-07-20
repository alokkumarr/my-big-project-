package sncr.metadata.analysis

import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.typesafe.config.Config
import files.HFileOperations
import org.apache.hadoop.fs.Path
import org.json4s.JsonAST.{JField, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.{Fields, MetadataDictionary}
import sncr.saw.common.config.SAWServiceConfig

class AnalysisExecutionHandler(val nodeId : String, sqlRuntime: String) {

  var status: String = "Unknown"

  def getStatus: String = status
  def getRowID : String = nodeId

  val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisExecutionHandler].getName)
  private val conf: Config = SAWServiceConfig.spark_conf


  var analysisResultNodeID: String = null

  private val inlineDSLimitBytes: Int = conf.getInt("sql-executor.inline-data-store-limit-bytes")
  private val inlineDSLimitRows: Int = conf.getInt("sql-executor.preview-rows-limit")


  val sqlExecInputFilename = conf.getString("sql-executor.input-file-location") + Path.SEPARATOR + nodeId.replace(MetadataDictionary.separator, "-") + "_" + System.nanoTime() + ".in"
  val resultExecOutputFile = conf.getString("sql-executor.result-file-location") + Path.SEPARATOR + nodeId.replace(MetadataDictionary.separator, "-") + "_" + System.nanoTime() + ".out"

  def getWaitTime : Int = conf.getInt("sql-executor.wait-time")

  protected val node : AnalysisNode = AnalysisNode(nodeId)
  m_log debug "Created analysis node"

  protected val analysisNodeMap = node.getCachedData

  val definition: JValue =
      analysisNodeMap(key_Definition.toString) match {
        case x:JValue => x
        case _  =>
          val m = "Incorrect AnalysisNode representation"; m_log error m
          throw new Exception(m)
    }
  var result: String = null
  def setResult(r: String): Unit = result = r


  var analysisDataObjects : List[DataObject] = List.empty

  var resultNode: AnalysisResult = null

  var objectCount: Int = 0

  m_log debug s"Check definition before extracting value ==> ${pretty(render(definition))}"

  val sqlManual = (definition \ "queryManual").extractOrElse[String]("")
  val sqlDefinition = (definition \ "query").extractOrElse[String]("")
  val sql = if (sqlManual != "") sqlManual else if (sqlRuntime != null) sqlRuntime else sqlDefinition
  val outputType = ( definition \ "outputFile" \ "outputFormat").extractOrElse[String]("")
  val outputLocation = ( definition \ "outputFile" \ "outputFileName").extractOrElse[String]("")
  val targetName = (definition \ "metricName").extract[String] + "_" + objectCount

  m_log debug s"SQL: $sql"
  if (sql.isEmpty || outputType.isEmpty || outputLocation.isEmpty  )
    throw new Exception("Invalid Analysis object, one of the attribute is null/empty: SQL, outputType, outputLocation")
  else
    m_log debug s"Analysis executions parameters, type : $outputType, location:  $outputLocation \n sql => $sql"

  def generateJobDescription(resultID : String): Unit = {

    m_log debug  s"SQL = $sql, output: type = $outputType, location = $outputLocation, target name = $targetName"
    val dataobjects = node.getRelatedNodes
    if (dataobjects.isEmpty)
      throw new Exception("Could not run Spark SQL jobs without data object")

    analysisDataObjects = dataobjects.map( el => DataObject(el._2) )

    val dataMapping : JArray = JArray( analysisDataObjects.flatMap( dataObject => {
      val dataLocations = dataObject.getDLLocations
      val data = dataObject.getCachedData

      m_log debug s"Loaded definition: ${data.mkString("[",",","]")}"
      val dataObjectDefinition = data(key_Definition.toString).asInstanceOf[JValue]

      val name = (dataObjectDefinition \ "name").extract[String]
      val _type = (dataObjectDefinition \ "type").extract[String]

      if (name.isEmpty || _type.isEmpty){
        throw new Exception( s"Incorrect DataObject: ${data(Fields.NodeId.toString)}")
      }
      dataLocations.map(  location => {
        val dm = (name, (location, _type))
        val dataMappingEntry = new JObject(
          List(JField("name", JString(name)),
            JField("location", JString(location)),
            JField("type", JString(_type))
          )
        )
        m_log debug s"Location: $name, $location, type: ${_type}"
        dataMappingEntry
      }).toList
    }))

    m_log debug s"Create input JSON document with data mapping: ${dataMapping.arr.mkString("[",",","]")}"
    val inpJson = new JObject(
      List(JField("uid", JString(resultID)),
           JField("dataMapping", dataMapping),
           JField("sqlStatement", JString(sql)),
           JField("outputType", JString(outputType)),
           JField("outputDataLocation", JString(outputLocation) ),
           JField("outputControlFile", JString(resultExecOutputFile)),
           JField("numberOfRecords", JInt(inlineDSLimitRows))))

    val outStream: OutputStream = HFileOperations.writeToFile(sqlExecInputFilename)
    outStream.write(pretty(render(inpJson)).getBytes)
    outStream.flush()
    outStream.close()
    m_log debug s"Job descriptor was generated, file: $sqlExecInputFilename !"
  }


  /**
    * The method creates AnalysisResult node:
    * 1. Creates node from Spark SQL executor
    * 2. if out parameter is not null - appends it this ResultNode represented in JSON format.
    *
    * @param out
    */
  def handleResult(out: OutputStream = null) : Unit = {
    val readData: String = HFileOperations.readFile(resultExecOutputFile)
    if (readData == null || readData.isEmpty)
      throw new Exception("Could not read SparkSQL Executor result file/ file is empty")

    val jsonResult = parse(readData, false, false)
    m_log.trace("Execution result: {}", shortMessage(pretty(render(jsonResult))))

    var nodeExists = false
    try {
      m_log debug s"Remove result: " + analysisResultNodeID
      resultNode = AnalysisResult(nodeId, analysisResultNodeID)
      nodeExists = true
    }
    catch {
      case e: Exception => m_log debug("Tried to load node: {}", e.toString)
    }
    if (nodeExists) resultNode.delete

    val analysisName = (definition \ "metricName").extractOpt[String]
    val analysisId = (definition \ "id").extractOpt[String]

    status = (jsonResult \ "status").extract[String]

    var descriptor: JObject = null

    val ldt: LocalDateTime = LocalDateTime.now()
    val timestamp: String = ldt.format(dfrm)
    var schema : JValue = JNothing

    if (status.equalsIgnoreCase("success")) {

      schema  = jsonResult \ "schema" match {
        case o: JObject => o
        case _ => JObject(JField("schema", JString("ERROR! Could not extract schema")))
      }

      val (finalOutputType, finalOutputLocation) = (jsonResult \ "outputTo").extractOrElse[String]("inline") match {
        case "inline" => ("json", "inline")
        case "hdfs" => (outputType, outputLocation)
        case _ => ("unknown", "unknown")
      }

      descriptor = new JObject(List(
        JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("id", JString(analysisId.get)),
        JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("sql", JString(sql)),
        JField("execution_result", JString(result)),
        JField("execution_timestamp", JString(timestamp)),
        JField("outputType", JString(finalOutputType)),
        JField("outputLocation", JString(finalOutputLocation))
      ))
      m_log debug s"Create result: with content: ${compact(render(descriptor))}"
    }
    else {
      val errorMsg = (jsonResult \ "errorMessage").extract[String]
      descriptor = new JObject(List(
        JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("id", JString(analysisId.get)),
        JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
        JField("sql", JString(sql)),
        JField("execution_result", JString(status)),
        JField("execution_timestamp", JString(timestamp)),
        JField("error_message", JString(errorMsg))
      ))
    }

    var descriptorPrintable: JValue = null
    resultNode = new AnalysisResult(nodeId, descriptor, analysisResultNodeID)
    if (status.equalsIgnoreCase("success")) {

      (jsonResult \ "outputTo").extractOrElse[String]("inline") match {
        case "inline" =>
          jsonResult \ "data" match {
            case x: JArray => resultNode.addObject("data", x, schema)
              descriptorPrintable = descriptor ++ x
            case o: JObject => resultNode.addObject("data", o, schema)
              descriptorPrintable = descriptor ++ o
            case JNothing => {
              /* When the "data" property is missing, as it can be if the Spark SQL
               * Executor sees the query result has zero rows, default
               * to an empty array to represent the result */
              val data = JArray(List())
              resultNode.addObject("data", data, schema)
              descriptorPrintable = descriptor ++ data
            }
            case _ => m_log error "Inline data misrepresented"
          }

        case "hdfs" =>
          val oLoc = (jsonResult \ "outputLocation").extract[String]
          if (!oLoc.equals(outputLocation)) throw new Exception(s"Inconsistency found between Spark SQL Executor and analysis node: $oLoc vs $outputLocation")
          val oType = (jsonResult \ "outputType").extract[String]
          if (!oType.equals(outputType)) throw new Exception(s"Inconsistency found between Spark SQL Executor and analysis node: $oType vs $outputType")
          resultNode.addObject("dataLocation", oLoc, schema)
          descriptorPrintable = new JObject(descriptor.obj ::: List(("dataLocation", JString(oLoc))))

        case _ => m_log warn "Data descriptor/data section not found" //throw new Exception("Unsupported data/output type found!")
      }
    }
    else {
      descriptorPrintable = descriptor
    }

    val (res, msg) = resultNode.create
    m_log debug s"Analysis result creation: $res ==> $msg"
    if (out != null) {
      out.write(pretty(render(descriptorPrintable)).getBytes())
      out.flush()
    }
    m_log debug "Result node: " + pretty(render(descriptorPrintable))
    removeFiles()
  }


  def getPreDefinedResultKey : String = analysisResultNodeID

  def setPreDefinedResultKey(resultId : String) : Unit =  analysisResultNodeID = resultId

  def removeFiles(): Unit =
  {
    m_log debug s"Remove temp files: $sqlExecInputFilename and $resultExecOutputFile"
    HFileOperations.deleteFile(sqlExecInputFilename)
    HFileOperations.deleteFile(resultExecOutputFile)
  }

  override protected def finalize(): Unit ={
    removeFiles()
  }

  private def shortMessage(message: String) = {
    message.substring(0, Math.min(message.length(), 2000))
  }
}

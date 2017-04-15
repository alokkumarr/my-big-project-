package sncr.metadata.analysis

import java.io.OutputStream

import files.HFileOperations
import org.json4s.JsonAST.{JField, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.{Fields, MetadataDictionary}

import scala.collection.immutable.Map

class AnalysisExecutionHandler(val analysisId: String) {

  def getAnalysisId = analysisId

  var analysisResultNodeID: String = null

  final val INLINE_DATA_STORE_LIMIT_BYTES: Int = 268435456
  final val INLINE_DATA_STORE_LIMIT_RECORDS: Int = 1000

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisExecutionHandler].getName)

  val sqlExecInputFilename = analysisId.replace(MetadataDictionary.separator, "-") + "_" + System.nanoTime() + ".in"
  val resultExecOutputFile = analysisId.replace(MetadataDictionary.separator, "-") + "_" + System.nanoTime() + ".out"


  protected val node : AnalysisNode = new AnalysisNode
  protected val keys : Map[String, Any] = Map((Fields.NodeId.toString,analysisId))
  protected val analysisNodeMap = node.read(keys)

  val definition: JValue =
    analysisNodeMap(key_Definition.toString) match {
      case x:JValue => x
      case _  => throw new Exception("Incorrect AnalysisNode representation")
  }

  var result: String = null
  def setResult(r: String): Unit = result = r

  var resultRowKey : String = null

  var analysisDataObjects : List[DataObject] = List.empty

  var resultNode: AnalysisResult = null

  var objectCount: Int = 0

  val sql = ( definition \ "analysis" \ "analysisQuery").extractOrElse[String]("")
  val outputType = ( definition \ "outputFile" \ "outputFormat").extractOrElse[String]("")
  val outputLocation = ( definition \ "outputFile" \ "outputFileName").extractOrElse[String]("")
  val targetName = (definition \ "name").extract[String] + "_" + objectCount

  if (sql.isEmpty || outputType.isEmpty || outputLocation.isEmpty  )
    throw new Exception("Invalid Analysis object, one of the attribute is null/empty: SQL, outputType, outputLocation")

  def generateJobDescription(resultID : String): Unit = {

    m_log debug  s"SQL = $sql, output: type = $outputType, location = $outputLocation, target name = $targetName"
    val headers = node.loadRelatedNodeHeaders
    val dataobjects = headers.filter(
      node => {
        node._2.isDefined &&
        node._2.get.contains(syskey_NodeCategory.toString) &&
        node._2.get.apply(syskey_NodeCategory.toString).asInstanceOf[String].equals(classOf[DataObject].getName)
      })

    if (dataobjects.isEmpty)
      throw new Exception("Could not run Spark SQL jobs without data object")

    analysisDataObjects = dataobjects.map( dobj =>{
    val DONodeId = dobj._2.get.apply(Fields.NodeId.toString).asInstanceOf[String]
    DataObject(DONodeId)} ).toList

    val dataMapping : JArray = JArray( analysisDataObjects.flatMap( dataObject => {
      val data = dataObject.load
      val dataLocations = dataObject.getDLLocations
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

    val inpJson = new JObject(
      List(JField("uid", JString(resultID)),
           JField("dataMapping", dataMapping),
           JField("sqlStatement", JString(sql)),
           JField("outputType", JString(outputType)),
           JField("outputDataLocation", JString(outputLocation) ),
           JField("outputControlFile", JString(resultExecOutputFile)),
           JField("numberOfRecords", JInt(INLINE_DATA_STORE_LIMIT_RECORDS))))

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
  def handleResult(out: OutputStream) : Unit =
  {
    val readData : String = HFileOperations.readFile(resultExecOutputFile)
    if (readData == null || readData.isEmpty)
        throw new Exception("Could not read SparkSQL Executor result file/ file is empty")

    val jsonResult = parse(readData, false, false)

    m_log debug s"Remove result: " + analysisResultNodeID
    resultNode = AnalysisResult(analysisResultNodeID)
    resultNode.delete

    val analysisName = (definition \ "name").extract[String]
    val analysisId = (definition \ "analysis" \ "analysisId").extract[String]
    val analysisName2 = (definition \ "analysisName").extract[String]
    val schema = (jsonResult \ "schema").extract[String]

    val descriptor = new JObject(List(
      JField("name", JString(analysisName)),
      JField("analysisId", JString(analysisId)),
      JField("analysisName", JString(analysisName2)),
      JField("sql", JString(sql)),
      JField("execution_result", JString(result)),
      JField("schema", JString(schema)),
      JField("outputType", JString(outputType)),
      JField("outputLocation", JString(outputLocation))
    ))
    m_log debug s"Create result: with content: ${compact(render(descriptor))}"

    resultNode = new AnalysisResult(analysisId,descriptor, analysisResultNodeID)

    var descriptorPrintable : JValue = null
    (jsonResult \ "outputTo").extract[String] match{
      case "inline" =>
        jsonResult \ "data" match {
          case x:JArray   => resultNode.addObject("data", x)
            descriptorPrintable = descriptor ++ x
          case o:JObject  => resultNode.addObject("data", o)
            descriptorPrintable = descriptor ++ o
          case _ => throw new Exception("Inline data array misrepresented")
        }

      case "hdfs"   =>
        val oLoc = (jsonResult \ "outputLocation").extract[String]
        if (!oLoc.equals(outputLocation)) throw new Exception( s"Inconsistency found between Spark SQL Executor and analysis node: $oLoc vs $outputLocation")
        val oType = (jsonResult \ "outputType").extract[String]
        if (!oType.equals(outputType)) throw new Exception(s"Inconsistency found between Spark SQL Executor and analysis node: $oType vs $outputType")
        resultNode.addObject("dataLocation", oLoc)
        descriptorPrintable = new JObject(descriptor.obj ::: List(("dataLocation", JString(oLoc))))

      case _ => throw new Exception("Unsupported data/output type found!")
    }
    val (res, msg) = resultNode.create
    m_log info s"Analysis result creation: $res ==> $msg"

    if( out != null) {
      out.write(pretty(render(descriptorPrintable)).getBytes())
      out.flush()
    }

    m_log debug "Result node: " + pretty(render(descriptorPrintable))
  }

  def getPreDefinedResultKey : String = resultRowKey

  def setPreDefinedResultKey(resultId : String) : Unit =  resultRowKey = resultId

  def removeFiles: Unit =
  {
    HFileOperations.deleteFile(sqlExecInputFilename)
    HFileOperations.deleteFile(resultExecOutputFile)
  }

}
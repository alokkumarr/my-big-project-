package sncr.metadata.analysis

import java.util.UUID

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Get, Put, Result}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.{JNothing, JValue}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 3/10/2017.
  */

class AnalysisResult(private[this] val parentAnalysisRowID : String,
                     private[this] val descriptor : String = null)
  extends ContentNode with SourceAsJson
{

  private[this] val descriptorAsJson = if (descriptor != null && !descriptor.isEmpty) parse(descriptor, false, false) else JNothing


  override def compileRead(g: Get) = {
    includeContent(g)
    g.addFamily(MDColumnFamilies(_cf_objects.id))
  }

  override def header(g : Get) = includeSearch(g)

  //TODO:: Add report processing: file of data themselves
  override def getData(res: Result): Option[Map[String, Any]] = {
      Option(getSearchFields(res) +
      (key_Definition.toString -> getSourceData(res)))
  }

  override val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNode].getName)

  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.AnalysisResults
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)


  override protected def initRow: String = {
    val urid = UUID.randomUUID()
    rowKey = Bytes.toBytes(urid.toString)
    m_log debug s"Generated RowKey = ${urid.toString}"
    urid.toString
  }

  protected def validate: (Int, String) = {
    if (descriptorAsJson == JNothing) {
      val msg = s"Descriptor is missing or empty"
      m_log debug Rejected.id + " ==> " + msg
      return (Rejected.id, msg)
    }
    if (parentAnalysisRowID == null || parentAnalysisRowID.isEmpty){
      val msg = s"Parent Analysis row ID is missing or empty"
      m_log debug Rejected.id + " ==> " + msg
      return (Rejected.id, msg)
    }
    (Success.id, "Request is correct")
  }


  def create: (Int, String) = {
    try {
      val (result, msg) = validate
      if (result != Success.id) return (result, msg)

      val searchValues: Map[String, Any] = AnalysisResult.extractSearchData(descriptorAsJson) +
       ("NodeId" -> Bytes.toString(rowKey)) + ("AnalysisNodeId" -> parentAnalysisRowID)
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })
      val put_op = createNode(NodeType.RelationContentNode.id, classOf[AnalysisNode].getName)
      if (commit(
          saveObjects(
          saveContent(put_op, descriptor, searchValues))))
        (Success.id, s"The Analysis Result [ ${Bytes.toString(rowKey)} ] has been created")
      else
        (Error.id, "Could not create Analysis Result")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store Analysis Result  [ ID = ${Bytes.toString(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

  def update(filter: Map[String, Any]): (Int, String) = {
    try {
      val (result, validate_msg) = validate
      if (result != Success.id) return (result, validate_msg)

      val (res, msg) = selectRowKey(filter)
      if (res != Success.id) return (res, msg)

      val searchValues: Map[String, Any] = AnalysisResult.extractSearchData(descriptorAsJson) +
        ("NodeId" -> Bytes.toString(rowKey)) + ("AnalysisNodeId" -> parentAnalysisRowID)
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })

      readCompiled(prepareRead).getOrElse(Map.empty)
      setRowKey(rowKey)

      if (commit(
        saveObjects(
        saveContent(update, descriptor, searchValues))))
        (Success.id, s"The Analysis Result [ ${Bytes.toString(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update Analysis Result")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store Analysis Result [ ID = ${Bytes.toString(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }



  def getObjects(res: Result): Map[String, Any]  =
  {
    val _objectDescriptorAsString = Bytes.toString(res.getValue(MDColumnFamilies(_cf_datalakelocations.id),Bytes.toBytes("_object_descriptor")))
    val _objectDescriptorAsJson = parse(_objectDescriptorAsString, false, false)
    _objects_descriptor = _objectDescriptorAsJson.foldField[Map[String, String]](Map.empty)(
//      (ob:Map[String, String], f:JField) => ob ++ Map(f._1 -> f._2.extract[String]) )
        (ob, f) => ob ++ Map(f._1 -> f._2.extract[String]) )

    _objects  = _objects_descriptor.keySet.foldLeft[Map[String, Any]](Map.empty)((_o, k) => _objects_descriptor(k) match{
      case  "Location" => {
          val location = Bytes.toString(res.getValue(MDColumnFamilies(_cf_datalakelocations.id),Bytes.toBytes(k)))
          m_log debug s"Object is external and should be loaded outside of app."
          _o + (k -> location)
      }
      case "JSON" => {
        val data = Bytes.toString(res.getValue(MDColumnFamilies(_cf_datalakelocations.id),Bytes.toBytes(k)))
        val json = parse(data, false, false)
        m_log debug s"Object is stored inline, in JSON format"
        _o + (k  -> json)
      }
      case "binary" => {
        val data = res.getValue(MDColumnFamilies(_cf_datalakelocations.id),Bytes.toBytes(k))
        m_log debug s"Object is stored inline, in parquet format"
        _o + (k  -> data)
      }
      case _ => m_log error s"Unsupported result type ${k}"; Map.empty
    }

    )
    _objects
  }

  final private[this] var _objects : Map[String, Any] = Map.empty
  final private[this] var _objects_descriptor : Map[String, String] = Map.empty


  def addObject( ref: String, data: Any) : Unit = _objects = _objects + (ref-> data)
  def removeObject (ref: String) : Unit = _objects = _objects - ref


  def saveObjects(p: Put) : Put =
  {
    _objects_descriptor = Map.empty
    _objects.keySet.foreach( ref => {
        val ob : Any = _objects(ref)
        val _data = ob match{
          case s: String => _objects_descriptor = _objects_descriptor + (ref -> "Location"); Bytes.toBytes(s)
          case v: JValue => _objects_descriptor = _objects_descriptor + (ref -> "JSON"); Bytes.toBytes(compact(render(v)))
          case b: Array[Byte] => _objects_descriptor = _objects_descriptor + (ref -> "binary"); b
          case _ => m_log error s"Unsupported analysis result type: ${Bytes.toString(rowKey)}, Analysis ID: $parentAnalysisRowID ."; null
        }
        if (_data != null) p.addColumn(MDColumnFamilies(_cf_objects.id), Bytes.toBytes(ref), _data)
      })
      val objectDescAsByteArray = Bytes.toBytes(compact(render(JObject(_objects_descriptor.map( obe => JField(obe._1, JString(obe._2))).toList))))
      p.addColumn(MDColumnFamilies(_cf_objects.id), Bytes.toBytes("_object_descriptor"), objectDescAsByteArray)
    p
  }


}


object AnalysisResult{


  val m_log: Logger = LoggerFactory.getLogger("AnalysisResultObject")
  val separator: String = "::"

  val searchFields = Map ("result" -> "String",
                          "execution_timestamp" -> "Time",
                          "data_location" -> "String")
  val requiredFields = List ("result", "execution_timestamp","data_location", "exported", "format" )

  def  extractSearchData(analysisResult: JValue) : Map[String, Any] = {

    //TODO::Create proper list when interface is ready
    List(
      (analysisResult, "result"),
      (analysisResult, "execution_timestamp"),
      (analysisResult, "data_location")
      ).map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, SearchDictionary.searchFields(jv._2)) )
        m_log trace s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }

}


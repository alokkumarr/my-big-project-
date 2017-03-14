package sncr.metadata.datalake

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Get, Put, Result}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JValue
import org.json4s.JsonAST.{JField, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.{MDNodeUtil, _}
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 3/4/2017.
  */
class DataObject(val descriptor : JValue, final private[this] var schema : JValue = JNothing)
  extends ContentNode
  with SourceAsJson{

  def this() = { this(JNothing, JNothing) }


  override def getSourceData(res: Result): JValue = super[SourceAsJson].getSourceData(res)

  def includeLocations(get: Get): Get = get.addFamily(MDColumnFamilies(_cf_datalakelocations.id))

  override def compileRead(g: Get) = includeLocations(
                                     includeContent(g))

//  final private[this] var schemaConvertedToString: String = compact(render(schema))

  private def getDataObjectSchema(res: Result): JValue =
  {
    val schemaConvertedToString = Bytes.toString(res.getValue(MDColumnFamilies(_cf_source.id),MDKeys(key_Schema.id)))
    m_log debug s"Convert schema of Schema to JSON: ${schemaConvertedToString}"
    schema = parse(schemaConvertedToString, false, false)
    schema
  }

  def getDataObjectSchemaAsString : String = compact(render(schema))
  def getDataObjectSchema : JValue = schema

  def setDataObjectSchemaFromString(a_schema : String ) : Unit = schema = parse(a_schema, false, false)


  override def getData(res: Result): Option[Map[String, Any]] = Option(
      getSearchFields(res) +
      (key_Definition.toString -> getSourceData(res) +
       key_Schema.toString -> getDataObjectSchema(res) +
       key_DL_DataLocation.toString -> getDL_DataLocation(res)
      )
  )


  override val m_log: Logger = LoggerFactory.getLogger(classOf[DataObject].getName)

  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.DatalakeMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc = DataObject.searchFields


  override protected def initRow: String = {
    val rowkey = (descriptor \ "name").extract[String] + AnalysisNode.separator +  System.currentTimeMillis()
    m_log debug s"Generated RowKey = $rowkey"
    rowkey
  }

  protected def validate: (Int, String) = {
    descriptor match {
      case null | JNothing => (Rejected.id, "Empty node, does not apply for requested operation")
      case _: JValue => {
        DataObject.requiredFields.foreach { f =>
          if ((descriptor \ f).extract[String].isEmpty) {
            val msg = s"Required field $f is missing or empty"
            m_log debug Rejected.id + " ==> " + msg
            return (Rejected.id, msg)
          }
        }
      }
    }
    if (schema == null || schema == JNothing){
      val msg = s"Schema is missing or empty"
      m_log debug Rejected.id + " ==> " + msg
      return (Rejected.id, msg)
    }
    (Success.id, "Request is correct")
  }

  def create: (Int, String) = {
    try {
      val (result, msg) = validate
      if (result != Success.id) return (result, msg)
      val put_op = createNode(NodeType.RelationContentNode.id, classOf[DataObject].getName)
      val searchValues: Map[String, Any] = AnalysisNode.extractSearchData(descriptor) + ("NodeId" -> new String(rowKey))

      searchValues.keySet.foreach(k => { m_log debug s"Add search field $k with value: ${searchValues(k).toString}"})
      if (commit(
          saveSchema(
          saveDL_Locations(
          saveContent(put_op, compact(render(descriptor)), searchValues)))))
        (Success.id, s"The Data Object [ ${new String(rowKey)} ] ha been created")
      else
        (Error.id, "Could not create Data Object")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }


  def update(filter: Map[String, Any]): (Int, String) = {
    try {
      val (result, validate_msg) = validate
      if (result != Success.id) return (result, validate_msg)

      val (res, msg) = selectRowKey(filter)
      if (res != Success.id) return (res, msg)
      readCompiled(prepareRead).getOrElse(Map.empty)
      setRowKey(rowKey)
      val searchValues: Map[String, Any] = AnalysisNode.extractSearchData(descriptor) + ("NodeId" -> new String(rowKey))
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })
      if (commit(
          saveSchema(
          saveDL_Locations(
          saveContent(update, compact(render(descriptor)), searchValues)))))
        (Success.id, s"The Data Object [ ${new String(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update Data Object")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

  def saveSchema(p: Put) : Put =
    p.addColumn(MDColumnFamilies(_cf_source.id), MDKeys(key_Schema.id), Bytes.toBytes(getDataObjectSchemaAsString))

  private[this] var dl_locations : Array[String] = Array.empty
  private[this] var dl_locationsAsJson : JValue = JNothing

  def convertToJson : JValue = {
    dl_locationsAsJson = null
    dl_locationsAsJson = new JObject ( (for (idx <- dl_locations.indices ) yield
      JField(String.valueOf(idx),JString(dl_locations(idx)))).toList :+
      JField("_number_of_locations", JInt(dl_locations.length)))
    dl_locationsAsJson
  }


  def getDL_DataLocation(res: Result): JValue  =
  {
    val n = res.getValue(MDColumnFamilies(_cf_datalakelocations.id),Bytes.toBytes("_number_of_locations"))
    val _number_of_locations = if (n != null) Bytes.toShort(n) else 0
    dl_locations = (for(i <- 0 until _number_of_locations ) yield {
      val lc = res.getValue(MDColumnFamilies(_cf_datalakelocations.id), Bytes.toBytes(i))
      if (lc != null) Bytes.toString(lc) else ""}).toArray.filter( _.nonEmpty)
    convertToJson
  }

  def saveDL_Locations(put: Put): Put =
  {
    put.addColumn(MDColumnFamilies(_cf_datalakelocations.id), Bytes.toBytes(dl_locations.length), Bytes.toBytes("_number_of_locations"))
    for ( i <- dl_locations.indices )
      put.addColumn(MDColumnFamilies(_cf_datalakelocations.id), Bytes.toBytes(i), Bytes.toBytes(dl_locations(i)))
    put
  }

  def addLocation( location: String) : JValue =
  {
    dl_locations = dl_locations :+ location
    convertToJson
  }


  def removeLocation( location: String) : JValue =
  {
    dl_locations = dl_locations.filterNot( l => l.equals(location))
    convertToJson
  }
}

object DataObject{

  val m_log: Logger = LoggerFactory.getLogger("DataObject")
  val separator: String = "::"

  def apply(d: String, s: String = null) :DataObject =
  {
    try {
      val descriptor = parse(d, false, false)
      if (s != null){
        val schema = parse(s, false, false)
        new DataObject(descriptor, schema)
      }
      else new DataObject(descriptor)
    }
    catch{
      case x: Exception => m_log error s"Could not parse Data object JSON document"; new DataObject()

    }

  }

  val searchFields = Map("name" -> "String", "owner" -> "String", "application" -> "String")

  val requiredFields = List("name", "type", "owner", "partition_type")

  def extractSearchData(descriptor: JValue) : Map[String, Any] = {
    List(
      (descriptor, ("name", "String")),
      (descriptor, ("owner", "String")),
      (descriptor, ("application", "String"))
     ).map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2._1, jv._2._2))
        m_log trace s"Field: ${jv._2._1}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${jv._2._2}\n, Value: $searchValue"
        if (result) jv._2._1 -> Option(searchValue) else jv._2._1 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }


}


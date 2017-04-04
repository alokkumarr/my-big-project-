package sncr.metadata.datalake

import com.typesafe.config.Config
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Get, Put, Result}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.{JField, JValue, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.{MDNodeUtil, _}
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 3/4/2017.
  */
class DataObject(final private[this] var descriptor : JValue, final private[this] var schema : JValue = JNothing, c: Config = null)
  extends ContentNode(c)
  with SourceAsJson{

  def setDescriptor : Unit = {
    if (descriptor != JNothing) {
      val (result, msg) = validate
      if (result != Success.id)
        throw new Exception(s"Could not create Data Object with provided descriptor, reason: $result - $msg")
      else
        setContent(compact(render(descriptor)))
    }
  }

  def setDescriptor(newDescriptor : String): Unit =
  {
    descriptor = parse(newDescriptor, false, false)
    setDescriptor
  }


  def this() = { this(JNothing, JNothing) }


  override def getSourceData(res: Result): (JValue, Array[Byte]) = super[SourceAsJson].getSourceData(res)

  protected def includeLocations(get: Get): Get = get.addFamily(MDColumnFamilies(_cf_datalakelocations.id))

  protected override def compileRead(g: Get) = includeLocations(
                                     includeContent(g))

//  final private[this] var schemaConvertedToString: String = compact(render(schema))

  private def getDataObjectSchema(res: Result): JValue =
  {
    val schemaConvertedToString = Bytes.toString(res.getValue(MDColumnFamilies(_cf_source.id),MDKeys(key_Schema.id)))
    if (schemaConvertedToString != null && schemaConvertedToString.nonEmpty) {
      m_log debug s"Convert schema of Schema to JSON: ${schemaConvertedToString}"
      schema = parse(schemaConvertedToString, false, false)
      schema
    }
    else
      JNothing
  }

  def getDataObjectSchemaAsString : String = compact(render(schema))
  def getDataObjectSchema : JValue = schema
  def setDataObjectSchemaFromString(a_schema : String ) : Unit = schema = parse(a_schema, false, false)

  override protected def getData(res: Result): Option[Map[String, Any]] = {
    val (dataAsJValue, dataAsByteArray) = getSourceData(res)
    setContent(dataAsByteArray)
    getDataObjectSchema(res)
    Option(
      getSearchFields(res) ++
      getSystemData(res) +
        (key_Definition.toString ->  dataAsJValue) +
        (key_Schema.toString -> getDataObjectSchema) +
        (key_DL_DataLocation.toString -> getDLDataLocationAsJson(res))
    )
  }


  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[DataObject].getName)

  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.DatalakeMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc = DataObject.searchFields


  override protected def initRow: String = {
    val rowkey = (descriptor \ "name").extract[String] + MetadataDictionary.separator +  System.currentTimeMillis()
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
      val put_op = createNode(NodeType.RelationContentNode.id, classOf[DataObject].getName)
      val searchValues: Map[String, Any] = DataObject.extractSearchData(descriptor) + (Fields.NodeId.toString -> new String(rowKey))
      setDescriptor
      searchValues.keySet.foreach(k => { m_log debug s"Add search field $k with value: ${searchValues(k).toString}"})
      if (commit(saveSchema(saveDL_Locations(saveContent(saveSearchData(put_op, searchValues))))))
        (NodeCreated.id, s"${Bytes.toString(rowKey)}")
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
      val (res, msg) = selectRowKey(filter)
      if (res != Success.id) return (res, msg)
      load
      setDescriptor

      val searchValues: Map[String, Any] = DataObject.extractSearchData(descriptor) + (Fields.NodeId.toString -> new String(rowKey))
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })
      if (commit(saveSchema(saveDL_Locations(saveContent(saveSearchData(update, searchValues))))))
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

  protected def saveSchema(p: Put) : Put =
    p.addColumn(MDColumnFamilies(_cf_source.id), MDKeys(key_Schema.id), Bytes.toBytes(getDataObjectSchemaAsString))

  protected var dl_locations : Array[String] = Array.empty
  protected var dl_locationsAsJson : JValue = JNothing

  def getDLLocations = dl_locations

  def normalize : JValue = {
    dl_locations = dl_locations.distinct
    dl_locationsAsJson = null
    dl_locationsAsJson = new JObject ( (for (idx <- dl_locations.indices ) yield
      JField(String.valueOf(idx),JString(dl_locations(idx)))).toList :+
      JField(Fields.NumOfLocations.toString, JInt(dl_locations.length)))
    m_log debug s"DL Locations: ${compact(render(dl_locationsAsJson))}"
    dl_locationsAsJson
  }


  def getDL_DataLocation(res: Result): Unit  =
  {
    val n = res.getValue(MDColumnFamilies(_cf_datalakelocations.id),Bytes.toBytes(Fields.NumOfLocations.toString))
    val _number_of_locations = if (n != null)
      try{  Bytes.toInt(n) } catch{ case x: Throwable=> 0 } else 0
    m_log trace s"Number of locations: ${_number_of_locations}"
    dl_locations = (for(i <- 0 until _number_of_locations ) yield  {
      val lc = res.getValue(MDColumnFamilies(_cf_datalakelocations.id), Bytes.toBytes(i))
      if (lc != null) Bytes.toString(lc) else ""}).toArray.filter( _.nonEmpty)
  }

  def getDLDataLocationAsJson(res: Result): JValue  =
  {
    getDL_DataLocation(res)
    normalize
  }

  def saveDL_Locations(put: Put): Put =
  {
    put.addColumn(MDColumnFamilies(_cf_datalakelocations.id), Bytes.toBytes(Fields.NumOfLocations.toString), Bytes.toBytes(dl_locations.length))
    for ( i <- dl_locations.indices ) {
      m_log trace s"Processing location $i : ${dl_locations(i)}"
      put.addColumn(MDColumnFamilies(_cf_datalakelocations.id), Bytes.toBytes(i), Bytes.toBytes(dl_locations(i)))
    }
    put
  }

  def addLocation( location: String) : JValue =
  {
    dl_locations = dl_locations :+ location
    m_log trace s"Add location: ${dl_locations.mkString("[",",","]")}"
    normalize
  }


  def removeLocation( location: String) : JValue =
  {
    dl_locations = dl_locations.filterNot( l => l.equals(location))
    m_log trace s"Add location: ${dl_locations.mkString("[",",","]")}"
    normalize
  }


  def updateSchema(): (Int, String) = {
    try {
      if (rowKey != null  && !rowKey.isEmpty) {
        if (commit(saveSchema(update)))
          (Success.id, s"The DataObject Schema [ ID =  ${Bytes.toString(rowKey)} ] has been updated")
        else
          (Error.id, "Could not update DataObject schema element")
      }
      else
      {
        (Error.id, "DataObject should be loaded/identified first")
      }
    }
    catch {
      case x: Exception => {
        val msg = s"Could not update DataObject schema [ ID = ${Bytes.toString(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

  def updateDLLocations(): (Int, String) = {
    try {
      if (rowKey != null  && !rowKey.isEmpty) {
        if (commit(saveDL_Locations(update)))
          (Success.id, s"The DataObject Schema [ ID = ${Bytes.toString(rowKey)} ] has been updated")
        else
          (Error.id, "Could not update DataObject datalake elements")
      }
      else
      {
        (Error.id, "DataObject should be loaded/identified first")
      }
    }
    catch {
      case x: Exception => {
        val msg = s"Could not update DataObject DL locations [ ID = ${Bytes.toString(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

}

object DataObject{

  val m_log: Logger = LoggerFactory.getLogger("DataObject")



  def apply(d: String, s: String, c: Config = null) :DataObject =
  {
    try {
      val descriptor = parse(d, false, false)
      val schema = parse(s, false, false)
      new DataObject(descriptor, schema, c)
    }
    catch{
      case x: Exception => m_log error s"Could not parse Data object JSON document"; new DataObject()
    }
  }

  def apply(rowId: String) :DataObject =
  {
    val dobj = new DataObject
    dobj.setRowKey(Bytes.toBytes(rowId))
    m_log debug s"Create DataObject with RowId = ${Bytes.toString(dobj.rowKey)}"
    dobj.load
    dobj
  }


  val searchFields = Map("name" -> "String", "owner" -> "String", "application" -> "String", "NodeId" -> "String")

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


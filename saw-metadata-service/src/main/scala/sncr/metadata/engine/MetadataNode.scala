package sncr.metadata.engine

import java.io.IOException

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._


/**
  * Created by srya0001 on 2/17/2017.
  */
class MetadataNode extends MetadataStore {

  override val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataNode].getName)

  var nodeType :Array[Byte] = Array.empty
  var nodeCategory :Array[Byte] = Array.empty

  protected def createNode(a_nodeType: Int, a_nodeCategory: String ): Put =
  {
    rowKey = Bytes.toBytes(initRow)
    nodeType = Bytes.toBytes(a_nodeType)
    nodeCategory = Bytes.toBytes(a_nodeCategory)
    val putNode = new Put(rowKey)
    putNode.addColumn(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeType.id),nodeType)
           .addColumn(MDColumnFamilies(systemProperties.id),MDKeys(key_Searchable.id),Bytes.toBytes(_cf_search.id))
           .addColumn(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeCategory.id),nodeCategory)
  }

  protected def initRow : String = ???

  /*read part*/


  protected def getHeaderData(res:Result): Option[Map[String, Any]]  = ???
  protected def header(g : Get) : Get = ???

  protected def getData(res:Result): Option[Map[String, Any]]  = ???
  protected def compileRead(getNode: Get): Get = ???

  def getSystemData(res:Result): Map[String, Any]  =
  {
    nodeType = res.getValue(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeType.id))
    nodeCategory = res.getValue(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeCategory.id))
    val search =  res.getValue(MDColumnFamilies(systemProperties.id),MDKeys(key_Searchable.id))
    Map( "NodeType" ->NodeType.apply(Bytes.toInt(nodeType)).toString,
         "NodeCategory" -> Bytes.toString(nodeCategory),
         "Searchable" -> key_Searchable.id
       )
  }

  def prepareRead: Get = compileRead(new Get(rowKey)).addFamily(MDColumnFamilies(systemProperties.id))

  def readCompiled( getNode: Get): Option[Map[String, Any]] = {
    try {
      m_log debug s"Load row: ${Bytes.toString(rowKey)}"
      val res = mdNodeStoreTable.get(getNode)
      if (res.isEmpty) return None
      getData(res)
    }
    catch{
      case x: IOException => m_log error ( s"Could not read node: ${new String(rowKey)}, Reason: ", x); None
    }
  }

  def readHeaderOnly: Option[Map[String, Any]] = {
    try {
      m_log debug s"Load row header: ${Bytes.toString(rowKey)}"
      val get = new Get(rowKey).addFamily(MDColumnFamilies(systemProperties.id))
      val getNodeHeader = header(get)
      val res = mdNodeStoreTable.get(getNodeHeader)
      if (res.isEmpty) return None
      getHeaderData(res)
    }
    catch{
      case x: IOException => m_log error ( s"Could not read node header: ${new String(rowKey)}, Reason: ", x); None
    }
  }


  protected def commit(putNode : Put): Boolean = {
    try {
      if (putNode == null){
        m_log error "Metadata node is empty"; return false
      }
      mdNodeStoreTable.put(putNode)
      m_log debug s"Commit completed"
    }
    catch{
      case x: IOException => m_log error ( s"Could not create/update node: ${new String(rowKey)}, Reason: ", x); return false
    }
    true
  }


  def _delete : (Int, String) =
  {
    try {
      val delGetOp: Get = new Get(rowKey)
      val getResult = mdNodeStoreTable.get(delGetOp)
      if (getResult.isEmpty) return (noDataFound.id, s"Node [ ID = ${new String(rowKey)}] not found" )
      val delOp: Delete = new Delete(rowKey)
      mdNodeStoreTable.delete(delOp)
    }
    catch{
      case x: IOException => {
        val msg = s"Could not delete node: ${new String(rowKey)}, Reason: "
        m_log error(msg, x)
        return (Error.id, msg)
      }
    }
    (Success.id, s"Node [ ID = ${new String(rowKey)}] was successfully removed")
  }

  protected def update : Put = new Put(rowKey)

  def selectRowKey(keys: Map[String, Any]) : (Int, String) = ???


  def read(filter: Map[String, Any]): Map[String, Any] = {
    val (res, msg) = selectRowKey(filter)
    if (res != Success.id) return Map.empty
    readCompiled(prepareRead).getOrElse(Map.empty)
  }


  def delete(keys: Map[String, Any]): (Int, String) = {
    val (res, msg) = selectRowKey(keys)
    if (res != Success.id) return (res, msg)
    _delete
  }


}

object MetadataNode extends MetadataStore{

  override val m_log: Logger = LoggerFactory.getLogger("MetadataNode")

  def create(mdTableName: String, compositeKey: String, content: String, searchDictionary: Map[String, String]): Boolean = {
    try {
      val tn: TableName = TableName.valueOf(mdTableName)
      mdNodeStoreTable = connection.getTable(tn)
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(MDColumnFamilies(_cf_source.id),
                              MDKeys(key_Definition.id),
                              Bytes.toBytes(content))
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + ("NodeId" -> Option(compositeKey))
      sval.keySet.foreach(k => {
        putOp = putOp.addColumn(MDColumnFamilies(_cf_search.id),
                                Bytes.toBytes(k),
                                Bytes.toBytes(sval(k).asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not retrieve  node: $compositeKey, Reason: ", x); return false
    }
    finally mdNodeStoreTable.close()
    true
  }


  def load(mdTableName: String, compositeKey: String, includeSearchFields: Boolean): Option[Map[String, Any]] = {
    load(mdTableName, Bytes.toBytes(compositeKey), includeSearchFields: Boolean)
  }

  def load(mdTableName: String, compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    val tn: TableName = TableName.valueOf(mdTableName)
    mdNodeStoreTable = connection.getTable(tn)
    load(mdNodeStoreTable, compositeKey, includeSearchFields )
  }


  import scala.collection.JavaConversions._
  private def load(mdNodeStoreTable: Table, compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    try {

      m_log debug s"Load row: ${new String(compositeKey)}"

      val getOp: Get = new Get(compositeKey)
      getOp.addFamily(MDKeys(_cf_source.id))

      if (includeSearchFields) getOp.addFamily(MDColumnFamilies(_cf_search.id))
      val res = mdNodeStoreTable.get(getOp)
      if (res.isEmpty) return None
      val content = res.getValue(MDColumnFamilies(_cf_source.id),MDKeys(key_Definition.id))
      m_log debug s"Read node: ${new String(content)}"
      if (includeSearchFields) {
        val sfKeyValues = res.getFamilyMap(MDColumnFamilies(_cf_search.id))

        m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " =>" + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"

        val sf : Map[String, String] = sfKeyValues.keySet().map( k =>
        { val k_s = new String(k)
          val v_s = new String(sfKeyValues.get(k))
          m_log debug s"Search field: $k_s, value: $v_s"
          k_s -> v_s
        }).toMap

        Option(sf + (key_Definition.toString -> new String(content)))
      }
      else {
        m_log debug s"Do not Include list of search fields into result"
        Option(Map(key_Definition.toString -> new String(content)))
      }
    }
    catch{
      case x: IOException => m_log error ( s"Could not read node: $compositeKey, Reason: ", x); None
    }
    finally mdNodeStoreTable.close()

  }


  def delete(mdTableName: String, compositeKey: String) : Boolean =
  {
    try {
      val tn: TableName = TableName.valueOf(mdTableName)
      mdNodeStoreTable = connection.getTable(tn)
      val delOp: Delete = new Delete(Bytes.toBytes(compositeKey))
      mdNodeStoreTable.delete(delOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not delete node: $compositeKey, Reason: ", x); return false
    }
    finally mdNodeStoreTable.close()
    true
  }

  def update(mdNodeStoreTable: Table, compositeKey: String, content: String, searchDictionary: Map[String, String]): Boolean = {
    try {
      val query = new Get(Bytes.toBytes(compositeKey))
      query.addFamily(MDColumnFamilies(_cf_source.id))
      val res = mdNodeStoreTable.get(query)
      if (res == null || res.isEmpty){m_log debug s"Row does not exist: ${compositeKey}"; return false}
      val data = query.getFamilyMap
      if (res.isEmpty || data == null || data.size == 0){
        m_log debug s"Row is empty: $compositeKey"
      }
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(MDColumnFamilies(_cf_source.id),MDKeys(key_Definition.id),Bytes.toBytes(content))
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + ("NodeId" -> Option(compositeKey))
      sval.keySet.foreach(k => {
        putOp = putOp.addColumn(MDColumnFamilies(_cf_search.id),Bytes.toBytes(k),Bytes.toBytes(sval(k).asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not retrieve  node: ${compositeKey}, Reason: ", x); return false
    }
    finally mdNodeStoreTable.close()
    true
  }

  def loadMDNodes(mdTableName: String, rowKeys: List[Array[Byte]], includeSearchFields : Boolean ): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows"
    val tn: TableName = TableName.valueOf(mdTableName)
    mdNodeStoreTable = connection.getTable(tn)
    val loadedNodes = rowKeys.map( k => load(mdNodeStoreTable, k, includeSearchFields)).filter(  _.isDefined ).map(v => v.get)
    mdNodeStoreTable.close()
    loadedNodes
  }


}

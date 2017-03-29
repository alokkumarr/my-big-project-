package sncr.metadata.engine

import java.io.IOException

import com.typesafe.config.Config
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._


/**
  * Created by srya0001 on 2/17/2017.
  */
class MetadataNode(c: Config = null) extends MetadataStore(c) {
  

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataNode].getName)

  protected var nodeType :Array[Byte] = Array.empty
  protected var nodeCategory :Array[Byte] = Array.empty
  protected var cachedData : Map[String, Any] = Map.empty

  def getCachedData = cachedData

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

  protected def prepareRead: Get = compileRead(new Get(rowKey)).addFamily(MDColumnFamilies(systemProperties.id))

  protected def readCompiled( getNode: Get): Option[Map[String, Any]] = {
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

  protected def _delete : (Int, String) =
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

  def load: Map[String, Any] = {
    cachedData = Map.empty
    if (rowKey == null || rowKey.length == 0) return Map.empty
    cachedData = readCompiled(prepareRead).getOrElse(Map.empty)
    cachedData
  }



  def read(filter: Map[String, Any]): Map[String, Any] = {
    cachedData = Map.empty
    val (res, msg) = selectRowKey(filter)
    if (res != Success.id) return Map.empty
    cachedData = readCompiled(prepareRead).getOrElse(Map.empty)
    cachedData
  }


  def delete: (Int, String) = {
    cachedData = Map.empty
    if (rowKey == null || rowKey.length == 0) return (Error.id, "Row ID is not set")
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
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + (Fields.NodeId.toString -> Option(compositeKey))
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


  def loadHeader(mdTableName: String, compositeKey: String, includeSearchFields: Boolean): Option[Map[String, Any]] = {
    loadHeader(mdTableName, Bytes.toBytes(compositeKey), includeSearchFields: Boolean)
  }

  def loadHeader(mdTableName: String, compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    val tn: TableName = TableName.valueOf(mdTableName)
    mdNodeStoreTable = connection.getTable(tn)
    loadHeader(mdNodeStoreTable, compositeKey, includeSearchFields )
  }


  import scala.collection.JavaConversions._
  private def loadHeader(mdNodeStoreTable: Table, compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    try {

      m_log debug s"Load row: ${new String(compositeKey)}"

      val getOp: Get = new Get(compositeKey)
      getOp.addFamily(MDColumnFamilies(_cf_source.id))
      getOp.addFamily(MDColumnFamilies(systemProperties.id))
      if (includeSearchFields) getOp.addFamily(MDColumnFamilies(_cf_search.id))

      val res = mdNodeStoreTable.get(getOp)
      if (res.isEmpty) return None

      val nodeType = Bytes.toShort(res.getValue(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeType.id)))
      val nodeCategory = Bytes.toString(res.getValue(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeCategory.id)))

      m_log debug s"Read node: $nodeType, Category: $nodeCategory"
      if (includeSearchFields) {
        val sfKeyValues = res.getFamilyMap(MDColumnFamilies(_cf_search.id))

        m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " =>" + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"

        val sf : Map[String, String] = sfKeyValues.keySet().map( k =>
        { val k_s = new String(k)
          val v_s = new String(sfKeyValues.get(k))
          m_log debug s"Search field: $k_s, value: $v_s"
          k_s -> v_s
        }).toMap
        Option(sf + (syskey_NodeType.toString -> NodeType(nodeType).toString,
                     syskey_NodeCategory.toString -> nodeCategory))
      }
      else {
        m_log debug s"Do not Include list of search fields into result"
        Option(Map("NodeType" -> NodeType(nodeType).toString))
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

  def loadMDNodeHeader(mdTableName: String, rowKeys: List[Array[Byte]], includeSearchFields : Boolean ): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows"
    val tn: TableName = TableName.valueOf(mdTableName)
    mdNodeStoreTable = connection.getTable(tn)
    val loadedNodes = rowKeys.map( k => loadHeader(mdNodeStoreTable, k, includeSearchFields)).filter(  _.isDefined ).map(v => v.get)
    mdNodeStoreTable.close()
    loadedNodes
  }


}

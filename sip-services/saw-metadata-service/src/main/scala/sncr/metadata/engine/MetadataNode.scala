package sncr.metadata.engine

import java.io.IOException

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._
import sncr.saw.common.config.SAWServiceConfig


/**
  * Created by srya0001 on 2/17/2017.
  */
class MetadataNode extends MetadataStore
{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataNode].getName)

  protected var nodeType :Array[Byte] = Array.empty
  protected var nodeCategory :Array[Byte] = Array.empty
  protected var cachedData : Map[String, Any] = Map.empty

  def getCachedData = cachedData
  def getTable = mdNodeStoreTable


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


  protected def getHeaderData(res:Result): Option[Map[String, Any]]  = ???
  protected def header(g : Get) : Get = ???

  protected def getData(res:Result): Option[Map[String, Any]]  = ???
  protected def compileRead(getNode: Get): Get = ???

  def getSystemData(res:Result): Map[String, Any]  =
  {
    nodeType = Option(res.getValue(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeType.id))).getOrElse(Array.emptyByteArray)
    nodeCategory = Option(res.getValue(MDColumnFamilies(systemProperties.id),MDKeys(syskey_NodeCategory.id))).getOrElse(Array.emptyByteArray)
    val lnodeType =  if (nodeType != Array.emptyByteArray) try{  Bytes.toInt(nodeType) } catch{ case x: Throwable=> 0 } else 0
    val lnodeCat=  if (nodeCategory != Array.emptyByteArray) try{  Bytes.toString(nodeCategory) } catch{ case x: Throwable=> "" } else ""
    Map( "NodeType" -> lnodeType,
         "NodeCategory" -> lnodeCat,
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
    m_log debug s"Load row by rowId: ${Bytes.toString(rowKey)}"
    cachedData = Map.empty
    if (rowKey == null || rowKey.length == 0) throw new Exception ("Row Id is not set")
    val row = readCompiled(prepareRead)
    if (row.isEmpty) throw new Exception (NodeDoesNotExist.toString)
    cachedData = row.get
    m_log trace s"Cache data: ${cachedData.mkString("{", ", ","}")}"
    cachedData
  }


  def read(filter: Map[String, Any]): Map[String, Any] = {
    val (res, msg) = selectRowKey(filter)
    if (res != Success.id) throw new Exception ("Filter does not contain Node Id")
    load
  }


  def delete: (Int, String) = {
    cachedData = Map.empty
    if (rowKey == null || rowKey.length == 0) return (Error.id, "Row ID is not set")
    _delete
  }


}

object MetadataNode extends MetadataStore{

  override val m_log: Logger = LoggerFactory.getLogger("sncr.metadata.MetadataNode")

  def create(mdTableName: String, compositeKey: String, content: String, searchDictionary: Map[String, String]): Boolean = {
    var lmdNodeStoreTable : Table = null
    try {
      val tn: TableName = TableName.valueOf(mdTableName)
      lmdNodeStoreTable = connection.getTable(tn)
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
      lmdNodeStoreTable.put(putOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not retrieve  node: $compositeKey, Reason: ", x); return false
      case t : Throwable =>  m_log error ( s"Unexpected problem occurred:  $compositeKey, Reason: ", t); return false
    }
    finally lmdNodeStoreTable.close()
    true
  }


  def loadHeader(mdTableName: String, compositeKey: String, includeSearchFields: Boolean): Option[Map[String, Any]] = {
    loadHeader(mdTableName, Bytes.toBytes(compositeKey), includeSearchFields: Boolean)
  }

  def loadHeader(mdTableName: String, compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    val tn: TableName = TableName.valueOf( SAWServiceConfig.metadataConfig.getString("path") + "/" + mdTableName)
    val lmdNodeStoreTable = connection.getTable(tn)
    loadHeader(lmdNodeStoreTable, compositeKey, includeSearchFields )
  }


  import scala.collection.JavaConversions._
  private def loadHeader(a_mdNodeStoreTable: Table, compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    try {

      m_log debug s"Load row: ${new String(compositeKey)}"

      val getOp: Get = new Get(compositeKey)
      getOp.addFamily(MDColumnFamilies(_cf_source.id))
      getOp.addFamily(MDColumnFamilies(systemProperties.id))
      if (includeSearchFields) getOp.addFamily(MDColumnFamilies(_cf_search.id))

      val res = a_mdNodeStoreTable.get(getOp)
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
      case io: IOException => m_log error ( s"Could not read node: ${Bytes.toString(compositeKey)}, Reason: ", io); None
      case e: Exception => m_log error ( s"Node can be corrupted: ${Bytes.toString(compositeKey)}, Reason: ", e); None
      case t : Throwable =>  m_log error ( s"Unexpected problem occurred:  ${Bytes.toString(compositeKey)}, Reason: ", t); None
    }
    finally a_mdNodeStoreTable.close()

  }


  def delete(mdTableName: String, compositeKey: String) : Boolean =
  {
    var lmdNodeStoreTable : Table = null
    try {
      val tn: TableName = TableName.valueOf(SAWServiceConfig.metadataConfig.getString("path") + "/" + mdTableName)
      lmdNodeStoreTable = connection.getTable(tn)
      val delOp: Delete = new Delete(Bytes.toBytes(compositeKey))
      lmdNodeStoreTable.delete(delOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not delete node: $compositeKey, Reason: ", x); return false
      case t : Throwable =>  m_log error ( s"Unexpected problem occurred:  $compositeKey, Reason: ", t); return false
    }
    finally lmdNodeStoreTable.close()
    true
  }

  def update(mdNodeStoreTable: Table, compositeKey: String, content: String, searchDictionary: Map[String, String]): Boolean = {
    try {
      val query = new Get(Bytes.toBytes(compositeKey))
      query.addFamily(MDColumnFamilies(_cf_source.id))
      val res = mdNodeStoreTable.get(query)
      if (res == null || res.isEmpty){m_log debug s"Row does not exist: $compositeKey"; return false}
      val data = query.getFamilyMap
      if (res.isEmpty || data == null || data.size == 0){
        m_log debug s"Row is empty: $compositeKey"
      }
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(MDColumnFamilies(_cf_source.id),MDKeys(key_Definition.id),Bytes.toBytes(content))
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + (Fields.NodeId.toString -> Option(compositeKey))
      sval.keySet.foreach(k => {
        putOp = putOp.addColumn(MDColumnFamilies(_cf_search.id),Bytes.toBytes(k),Bytes.toBytes(sval(k).asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not retrieve  node: $compositeKey, Reason: ", x); return false
      case t : Throwable =>  m_log error ( s"Unexpected problem occurred:  $compositeKey, Reason: ", t ); return false
    }
    finally mdNodeStoreTable.close()
    true
  }

  def loadMDNodeHeader(mdTableName: String, rowKeys: List[Array[Byte]], includeSearchFields : Boolean ): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows from table $mdTableName"
    val tn: TableName = TableName.valueOf(SAWServiceConfig.metadataConfig.getString("path") + "/" +  mdTableName)
    val lmdNodeStoreTable = connection.getTable(tn)
    m_log debug s"Table name: $lmdNodeStoreTable.getName"
    val loadedNodes = rowKeys.map( k => loadHeader(lmdNodeStoreTable, k, includeSearchFields)).filter(  _.isDefined ).map(v => v.get)
    lmdNodeStoreTable.close()
    loadedNodes
  }


}

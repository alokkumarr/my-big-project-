package sncr.metadata.engine

import java.io.IOException

import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._


/**
  * Created by srya0001 on 2/17/2017.
  */
trait MetadataNode extends MetadataStore {

  override val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataNode].getName)

  protected def createNode(nodeType: Int, nodeCategory: Int ): Put =
  {
    rowKey = Bytes.toBytes(initRow)
    val putNode = new Put(rowKey)
    putNode.addColumn(MDSections(systemProperties.id),MDKeys(syskey_NodeType.id),Bytes.toBytes(nodeType))
             .addColumn(MDSections(systemProperties.id),MDKeys(key_Searchable.id),Bytes.toBytes(searchSection.id))
             .addColumn(MDSections(systemProperties.id),MDKeys(syskey_NodeCategory.id),Bytes.toBytes(nodeCategory))
    putNode
  }

  protected def initRow : String = ???

/*read part*/

  protected def getData(res:Result): Option[Map[String, Any]]  = ???
  protected def getHeaderData(res:Result): Option[Map[String, Any]]  = ???
  protected def compileRead(getNode: Get): Get = ???
  protected def header(g : Get) : Get = ???

  def prepareRead: Get = compileRead(new Get(rowKey))

  def readCompiled( getNode: Get): Option[Map[String, Any]] = {
    try {
      m_log debug s"Load row: ${new String(rowKey)}"
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
      m_log debug s"Load row header: ${new String(rowKey)}"
      val getNodeHeader = header(new Get(rowKey))
      val res = mdNodeStoreTable.get(getNodeHeader)
      if (res.isEmpty) return None
      getHeaderData(res)
    }
    catch{
      case x: IOException => m_log error ( s"Could not read node header: ${new String(rowKey)}, Reason: ", x); None
    }
  }


  protected def saveNode(putNode : Put): Boolean = {
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


  def delete : (Int, String) =
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


  def loadNodes(rowKeys: List[Array[Byte]]): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows"
    rowKeys.map( k => {rowKey = k; readCompiled(prepareRead)} ).filter(  _.isDefined ).map(v => v.get)
  }

  def loadHeaders(rowKeys: List[Array[Byte]]): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows"
    rowKeys.map( k => {rowKey = k; readHeaderOnly} ).filter(  _.isDefined ).map(v => v.get)
  }

  def update : Put = new Put(rowKey)



}

object MetadataNode{

  val m_log: Logger = LoggerFactory.getLogger("MetadataNode")

  def create(mdNodeStoreTable: Table, compositeKey: String, content: String, searchDictionary: Map[String, String]): Boolean = {
    try {
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(MDSections(sourceSection.id),
                              MDKeys(key_Definition.id),
                              Bytes.toBytes(content))
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + ("NodeId" -> Option(compositeKey))
      sval.keySet.foreach(k => {
        putOp = putOp.addColumn(MDSections(searchSection.id),
                                Bytes.toBytes(k),
                                Bytes.toBytes(sval(k).asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not retrieve  node: $compositeKey, Reason: ", x); return false
    }
    true
  }


  def retrieve(mdNodeStoreTable: Table, compositeKey: String, includeSearchFields: Boolean): Option[Map[String, Any]] = {
    retrieve(mdNodeStoreTable, Bytes.toBytes(compositeKey), includeSearchFields: Boolean)
  }

  import scala.collection.JavaConversions._
  def retrieve(mdNodeStoreTable: Table, compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    try {
      m_log debug s"Load row: ${new String(compositeKey)}"

      val getOp: Get = new Get(compositeKey)
      getOp.addFamily(MDKeys(sourceSection.id))

      if (includeSearchFields) getOp.addFamily(MDSections(searchSection.id))

      val res = mdNodeStoreTable.get(getOp)

      if (res.isEmpty) return None

      val content = res.getValue(MDSections(sourceSection.id),MDKeys(key_Definition.id))

      m_log debug s"Read node: ${new String(content)}"
      if (includeSearchFields) {
        val sfKeyValues = res.getFamilyMap(MDSections(searchSection.id))

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
  }


  def delete(mdNodeStoreTable: Table, compositeKey: String) : Boolean =
  {
    try {
      val delOp: Delete = new Delete(Bytes.toBytes(compositeKey))
      mdNodeStoreTable.delete(delOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not delete node: $compositeKey, Reason: ", x); return false
    }
    true
  }

  def update(mdNodeStoreTable: Table, compositeKey: String, content: String, searchDictionary: Map[String, String]): Boolean = {
    try {
      val query = new Get(Bytes.toBytes(compositeKey))
      query.addFamily(MDSections(sourceSection.id))
      val res = mdNodeStoreTable.get(query)
      if (res == null || res.isEmpty){m_log debug s"Row does not exist: ${compositeKey}"; return false}
      val data = query.getFamilyMap
      if (res.isEmpty || data == null || data.size == 0){
        m_log debug s"Row is empty: $compositeKey"
      }
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(MDSections(sourceSection.id),MDKeys(key_Definition.id),Bytes.toBytes(content))
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + ("NodeId" -> Option(compositeKey))
      sval.keySet.foreach(k => {
        putOp = putOp.addColumn(MDSections(searchSection.id),Bytes.toBytes(k),Bytes.toBytes(sval(k).asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not retrieve  node: ${compositeKey}, Reason: ", x); false
    }
    true
  }

  def loadMDNodes(mdNodeStoreTable: Table, rowKeys: List[Array[Byte]], includeSearchFields : Boolean ): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows"
    rowKeys.map( k => retrieve(mdNodeStoreTable, k, includeSearchFields)).filter(  _.isDefined ).map(v => v.get)
  }


}

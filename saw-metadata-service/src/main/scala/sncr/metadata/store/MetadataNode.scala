package sncr.metadata.store

import java.io.IOException

import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.MDObjectStruct._
import sncr.metadata.ProcessingResult._

/**
  * Created by srya0001 on 2/17/2017.
  */
trait MetadataNode extends MetadataStore {

  override val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataNode].getName)

  var source : String = null
  var putOp : Put = null


  protected def createNode : Unit =
  {
    rowKey = Bytes.toBytes(createRowKey)
    putOp = new Put(rowKey)
  }

  protected def createRowKey : String = ???



  protected def addSource(content: String): Unit =
  {
    if (putOp == null ) createNode
    source = content
    m_log trace s"Save the document as content CF: $source"
    putOp = putOp.addColumn(MDKeys(sourceSection.id),
      MDKeys(columnContent.id),Bytes.toBytes(source))
  }


  protected def addSearchSection( search_val : Map[String, Any]): Unit =
  {
    if (putOp == null ) createNode
    search_val.keySet.foreach( k=>
    putOp = putOp.addColumn(MDKeys(searchSection.id),
    Bytes.toBytes(k), Bytes.toBytes(search_val(k).asInstanceOf[String])))
  }


  protected def saveNode: Boolean = {
    try {
      if (putOp == null){
          m_log error "Metadata node is empty"; return false
      }
      mdNodeStoreTable.put(putOp)
      putOp = null
      m_log debug s"Commit completed"
    }
    catch{
      case x: IOException => m_log error ( s"Could not create/update node: ${new String(rowKey)}, Reason: ", x); return false
    }
    true
  }

  import scala.collection.JavaConversions._
  def retrieve: Option[Map[String, Any]] = {
    try {
      m_log debug s"Load row: ${new String(rowKey)}"

      val getOp: Get = new Get(rowKey)

      //TODO:: make abstract calls, implement in each MD class separately
      // getContentStructure
      getOp.addFamily(MDKeys(sourceSection.id))
      getOp.addFamily(MDKeys(searchSection.id))
      //end TODO

      //TODO:: make abstract calls, implement in each MD class separately
      // extractContent
      val res = mdNodeStoreTable.get(getOp)
      if (res.isEmpty) return None
      val content = res.getValue(MDKeys(sourceSection.id),MDKeys(columnContent.id))
      m_log debug s"Read node: ${new String(content)}"
      //end TODO

      val sfKeyValues = res.getFamilyMap(MDKeys(searchSection.id))
      m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " =>" + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"
      val sf : Map[String, String] = sfKeyValues.keySet().map( k =>
      { val k_s = new String(k)
        val v_s = new String(sfKeyValues.get(k))
        m_log trace s"Search field: $k_s, value: $v_s"
        k_s -> v_s
      }).toMap

      Option(sf + (columnContent.toString -> new String(content)))
    }
    catch{
      case x: IOException => m_log error ( s"Could not read node: ${new String(rowKey)}, Reason: ", x); None
    }
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
    rowKeys.map( k => {rowKey = k; retrieve} ).filter(  _.isDefined ).map(v => v.get)
  }

  def update: Unit = putOp = new Put(rowKey)


 }

object MetadataNode{

  val m_log: Logger = LoggerFactory.getLogger("MetadataNode")

  def create(mdNodeStoreTable: Table, compositeKey: String, content: String, searchDictionary: Map[String, String]): Boolean = {
    try {
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(MDKeys(sourceSection.id),
                              MDKeys(columnContent.id),
                              Bytes.toBytes(content))
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + ("NodeId" -> Option(compositeKey))
      sval.keySet.foreach(k => {
        putOp = putOp.addColumn(MDKeys(searchSection.id),
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

      if (includeSearchFields) getOp.addFamily(MDKeys(searchSection.id))

      val res = mdNodeStoreTable.get(getOp)

      if (res.isEmpty) return None

      val content = res.getValue(MDKeys(sourceSection.id),MDKeys(columnContent.id))

      m_log debug s"Read node: ${new String(content)}"
      if (includeSearchFields) {
        val sfKeyValues = res.getFamilyMap(MDKeys(searchSection.id))

        m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " =>" + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"

        val sf : Map[String, String] = sfKeyValues.keySet().map( k =>
        { val k_s = new String(k)
          val v_s = new String(sfKeyValues.get(k))
          m_log debug s"Search field: $k_s, value: $v_s"
          k_s -> v_s
        }).toMap

        Option(sf + (columnContent.toString -> new String(content)))
      }
      else {
        m_log debug s"Do not Include list of search fields into result"
        Option(Map(columnContent.toString -> new String(content)))
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
      query.addFamily(MDKeys(sourceSection.id))
      val res = mdNodeStoreTable.get(query)
      if (res == null || res.isEmpty){m_log debug s"Row does not exist: ${compositeKey}"; return false}
      val data = query.getFamilyMap
      if (res.isEmpty || data == null || data.size == 0){
        m_log debug s"Row is empty: $compositeKey"
      }
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(MDKeys(sourceSection.id),
                              MDKeys(columnContent.id),
        Bytes.toBytes(content))
      val sval = MDNodeUtil.extractSearchData(content, searchDictionary) + ("NodeId" -> Option(compositeKey))
      sval.keySet.foreach(k => {
        putOp = putOp.addColumn(MDKeys(searchSection.id),
                                Bytes.toBytes(k),
                                Bytes.toBytes(sval(k).asInstanceOf[String]))
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

package sncr.metadata.engine

import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{FilterList, SingleColumnValueFilter}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import ProcessingResult._
import org.apache.hadoop.hbase.TableName
import sncr.saw.common.config.SAWServiceConfig


/**
  * Created by srya0001 on 2/19/2017.
  */
trait SearchMetadata extends MetadataStore{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[SearchMetadata].getName)
  var headerDesc: Map[String, String] = null

  import scala.collection.JavaConversions._
  def simpleMetadataSearch(keys: Map[String, Any], condition: String) : List[Array[Byte]] =
  {
    if (keys.isEmpty){
      m_log error "Filter/Key set is empty"
      return Nil
    }
    val filterList : FilterList = new FilterList( if (condition.equalsIgnoreCase("or")) FilterList.Operator.MUST_PASS_ONE else FilterList.Operator.MUST_PASS_ALL )
    m_log debug s"Create filter list with the following fields: ${keys.mkString("{", ",", "}")}"
    if (keys.keySet.isEmpty) {
      m_log error s"Filter is empty - the method should not be used"
      return Nil
    }
    keys.keySet.foreach( f => {
      val searchFieldValue : Array[Byte] = MDNodeUtil.convertValue(keys(f))
      m_log debug s"Field $f = ${keys(f)}"
      val filter1 : SingleColumnValueFilter = new SingleColumnValueFilter(
        Bytes.toBytes(MDObjectStruct._cf_search.toString),Bytes.toBytes(f),CompareOp.EQUAL,searchFieldValue)
        filter1.setFilterIfMissing(true)
        filterList.addFilter(filter1)
    })
    val q = new Scan
    q.setFilter(filterList)
    val sr : ResultScanner = mdNodeStoreTable.getScanner(q)
    val result = (for( r: Result <- sr) yield r.getRow.clone()).toList
    m_log debug s"Found: ${result.size} rows satisfied to filter: ${result.map ( new String( _ )).mkString("[", ",", "]")}"
    sr.close
    result
  }


  import scala.collection.JavaConversions._
  def scanMDNodes: List[Array[Byte]] =
  {
    val sr : ResultScanner = mdNodeStoreTable.getScanner(new Scan)
    val result = (for( r: Result <- sr) yield r.getRow.clone()).toList
    m_log trace s"Full scan MD Nodes: ${sr.size} rows satisfied to filter: ${result.map ( new String( _ )).mkString("[", ",", "]")}"
    sr.close
    result
  }

  def selectRowKey(keys: Map[String, Any]) : (Int, String) = {
    if (keys.contains(Fields.NodeId.toString)) {
      setRowKey(MDNodeUtil.convertValue(keys(Fields.NodeId.toString)))
      val msg = s"Selected Node [ ID = ${new String(rowKey)} ]"; m_log debug Success.toString + " ==> " + msg
      (Success.id,  msg)
    }
    else{
      val rowKeys = simpleMetadataSearch(keys, "and")
      if (rowKeys != Nil) {
        val rk = rowKeys.head
        setRowKey(rk)
        val msg = s"Selected Node [ ID = ${new String(rowKey)} ]"; m_log debug Success.toString + " ==> " + msg
        (Success.id, msg )
      }
      else {
        val msg = s"No row keys were selected"; m_log debug noDataFound.toString + " ==> " + msg
        (noDataFound.id, msg)
      }
    }
  }

  protected def includeSearch(getCNode: Get): Get = getCNode.addFamily(MDColumnFamilies(_cf_search.id))

//  def getHeaderData(res:Result): Option[Map[String, Any]] = Option(getSearchFields(res) )

  import scala.collection.JavaConversions._
  protected def getSearchFields(res:Result): Map[String, Any] =
  {
    val sfKeyValues = res.getFamilyMap(MDColumnFamilies(_cf_search.id))
    m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " => " + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"
    val sf : Map[String, Any] = sfKeyValues.keySet().filter( k => headerDesc.contains(Bytes.toString(k)) ).map( k =>
    { val k_s = Bytes.toString(k)
      val sf_type = headerDesc(k_s)
      val v_s = sf_type match {
        case "String" => Bytes.toString(sfKeyValues.get(k))
        case "Long" => Bytes.toLong(sfKeyValues.get(k))
        case "Boolean" => Bytes.toBoolean(sfKeyValues.get(k))
        case "Int" => Bytes.toInt(sfKeyValues.get(k))
        case "Time" => {
          val r = Bytes.toString(sfKeyValues.get(k))
          val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
          val ldt: LocalDateTime = LocalDateTime.parse(r, dfrm)
          (true, ldt)
        }

      }
      m_log trace s"Search field: $k_s, value: $v_s"
      k_s -> v_s
    }).toMap
    sf
  }


  def deleteAll(keys: Map[String, Any]): (Int, String) = {
    simpleMetadataSearch(keys, "and").foreach( rowID =>
      try {
        val delGetOp: Get = new Get(rowID)
        val getResult = mdNodeStoreTable.get(delGetOp)
        if (!getResult.isEmpty) {
          val delOp: Delete = new Delete(rowID)
          mdNodeStoreTable.delete(delOp)
        }
        else{
          m_log error s"Node [ ID = ${Bytes.toString(rowID)}] not found"
        }
      }
      catch{
        case x: IOException => {
          val msg = s"Could not delete node: ${Bytes.toString(rowID)} Reason: "
          m_log error(msg, x)
          return (Error.id, msg)
        }
      }
    )
    (Success.id, s"All found nodes  was successfully removed")
  }


}



object SearchMetadata extends MetadataStore{

  override val m_log: Logger = LoggerFactory.getLogger("SearchMetadata")

  import scala.collection.JavaConversions._
  def simpleSearch(mdTableName: String, keys: Map[String, Any], systemProps: Map[String, Any], condition: String) : List[Array[Byte]] =
  {
    val tn: TableName = TableName.valueOf(SAWServiceConfig.metadataConfig.getString("path") + "/" + mdTableName)
    mdNodeStoreTable = connection.getTable(tn)

    val filterList : FilterList = new FilterList( if (condition.equalsIgnoreCase("or")) FilterList.Operator.MUST_PASS_ONE else FilterList.Operator.MUST_PASS_ALL )

    m_log debug s"Create filter list with the following fields: ${keys.keySet.mkString("{", ",", s"}")} and System properties: ${systemProps.keySet.mkString("{", ",", "}")}"

    if (keys.isEmpty && systemProps.isEmpty ) {
      m_log error s"Filter is empty - the method should not be used"
      return null
    }

    keys.keySet.foreach( f => {
      val searchFieldValue : Array[Byte] = MDNodeUtil.convertValue(keys(f))
      m_log debug s"Field $f = ${keys(f)}"
      val filter1 : SingleColumnValueFilter =
      new SingleColumnValueFilter(MDObjectStruct.MDColumnFamilies(MDObjectStruct._cf_search.id),Bytes.toBytes(f),CompareOp.EQUAL,searchFieldValue)
      filter1.setFilterIfMissing(true)
      filterList.addFilter(filter1)
    })

    systemProps.keySet.foreach( f => {
      val searchFieldValue : Array[Byte] = MDNodeUtil.convertValue(systemProps(f))
      m_log debug s"Field $f = ${systemProps(f)}"
      val filter2 : SingleColumnValueFilter =
        new SingleColumnValueFilter(MDObjectStruct.MDColumnFamilies(MDObjectStruct.systemProperties.id),Bytes.toBytes(f),CompareOp.EQUAL,searchFieldValue)
      filter2.setFilterIfMissing(true)
      filterList.addFilter(filter2)
    })

    val q = new Scan
    q.setFilter(filterList)
    val sr : ResultScanner = mdNodeStoreTable.getScanner(q)

    val result = (for( r: Result <- sr) yield r.getRow.clone()).toList
    m_log debug s"Found: ${sr.size} rows satisfied to filter: ${result.map ( new String( _ )).mkString("[", ",", "]")}"
    sr.close
    mdNodeStoreTable.close()
    result
  }

  def scanMDNodes(mdNodeStoreTableName: String): List[Array[Byte]] = {
    val tn: TableName = TableName.valueOf(SAWServiceConfig.metadataConfig.getString("path") + "/" + mdNodeStoreTableName)
    val mdNodeStoreTable2 = connection.getTable(tn)
    scanMDNodes(mdNodeStoreTable2)
  }



  import scala.collection.JavaConversions._
  def scanMDNodes(mdNodeStoreTable: Table): List[Array[Byte]] =
  {
    val sr : ResultScanner = mdNodeStoreTable.getScanner(new Scan)
    val result = (for( r: Result <- sr) yield r.getRow.clone()).toList
    m_log trace s"Full scan MD Nodes: ${sr.size} rows satisfied to filter: ${result.map ( new String( _ )).mkString("[", ",", "]")}"
    sr.close
    result
  }


}

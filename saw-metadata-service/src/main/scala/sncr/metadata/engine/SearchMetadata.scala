package sncr.metadata.engine

import org.apache.hadoop.hbase.client.{Result, ResultScanner, Scan, Table}
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{FilterList, SingleColumnValueFilter}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import ProcessingResult._

/**
  * Created by srya0001 on 2/19/2017.
  */
trait SearchMetadata extends MetadataStore{

  override val m_log: Logger = LoggerFactory.getLogger(classOf[SearchMetadata].getName)

  import scala.collection.JavaConversions._
  def simpleMetadataSearch(keys: Map[String, Any], condition: String) : List[Array[Byte]] =
  {
    if (searchFields.isEmpty){
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
/*        searchFields(f) match{
          case "String" => Bytes.toBytes(keys(f).asInstanceOf[String])
          case "Int" => Bytes.toBytes(keys(f).asInstanceOf[Int])
          case "Long" => Bytes.toBytes(keys(f).asInstanceOf[Long])
          case "Boolean" => Bytes.toBytes(keys(f).asInstanceOf[Boolean])
          case _ => m_log error "Not supported data type"; null
        }
*/
        m_log debug s"Field $f = ${keys(f)}"

      val filter1 : SingleColumnValueFilter = new SingleColumnValueFilter(
        Bytes.toBytes(MDObjectStruct.searchSection.toString),
        Bytes.toBytes(f),
        CompareOp.EQUAL,
        searchFieldValue)
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
    if (keys.contains("NodeId")) {
      setRowKey(MDNodeUtil.convertValue(keys("NodeId")))
      val msg = s" Selected Node [ ID = ${new String(rowKey)} ]"; m_log debug Success.toString + " ==> " + msg
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


}



object SearchMetadata{

  val m_log: Logger = LoggerFactory.getLogger("MetadataNode")

  import scala.collection.JavaConversions._
  def searchMetadata(mdNodeStoreTable: Table, keys: Map[String, Any], searchFields: Map[String, String], condition: String) : List[Array[Byte]] =
  {
    val filterList : FilterList = new FilterList( if (condition.equalsIgnoreCase("or")) FilterList.Operator.MUST_PASS_ONE else FilterList.Operator.MUST_PASS_ALL )
    val filteringValues = keys.keySet.filter( searchFields.contains( _ ) )

    m_log debug s"Create filter list with the following fields: ${filteringValues.mkString("{", ",", "}")}"

    if (filteringValues.isEmpty) {
      m_log error s"Filter is empty - the method should not be used"
      return null
    }

    filteringValues.foreach( f => {
      val searchFieldValue : Array[Byte] =
        searchFields(f) match{
          case "String" => Bytes.toBytes(keys(f).asInstanceOf[String])
          case "Int" => Bytes.toBytes(keys(f).asInstanceOf[Int])
          case "Long" => Bytes.toBytes(keys(f).asInstanceOf[Long])
          case "Boolean" => Bytes.toBytes(keys(f).asInstanceOf[Boolean])
          case _ => m_log error "Not supported data type"; null
        }
      m_log debug s"Field $f = ${keys(f)}"


      m_log debug s"Field $f = ${keys(f)}"
      val filter1 : SingleColumnValueFilter =
            new SingleColumnValueFilter(
              MDObjectStruct.MDSections(MDObjectStruct.searchSection.id),
              Bytes.toBytes(f),
              CompareOp.EQUAL,
              searchFieldValue
      )
      filter1.setFilterIfMissing(true)
      filterList.addFilter(filter1)
    })

    val q = new Scan
    q.setFilter(filterList)
    val sr : ResultScanner = mdNodeStoreTable.getScanner(q)

    val result = (for( r: Result <- sr) yield r.getRow.clone()).toList
    m_log trace s"Found: ${sr.size} rows satisfied to filter: ${result.map ( new String( _ )).mkString("[", ",", "]")}"
    sr.close
    result
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

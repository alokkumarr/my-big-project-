package sncr.metadata.semantix

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.TableName
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import sncr.metadata.ProcessingResult._
import sncr.metadata.semantix.SearchDictionary.searchFields
import sncr.metadata.store.{MDNodeUtil, MetadataNode, SearchMetadata}
import sncr.metadata.{MDObjectStruct, tables}
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 2/19/2017.
  */
class SemanticNode(val ticket: JValue, val content_element: JValue, val module_name : String = "none") extends MetadataNode with SearchMetadata{


  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.SemanticMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  this.searchFields = SearchDictionary.searchFields

  override def createRowKey : String =
  {
    val rowkey = SemanticNode.rowKeyRule.foldLeft(new String)((z, t) => {
      m_log trace s"Z = $z, t = ${t.toString}"
      z +  (t._1 match {
        case "ticket" => (if (t == SemanticNode.rowKeyRule.head) "" else SemanticNode.separator) + (ticket \ t._2).extract[String]
        case "contents" => (if (t == SemanticNode.rowKeyRule.head) "" else SemanticNode.separator) + (content_element \ t._2).
          extractOrElse[String](if( t._2.equalsIgnoreCase("module")) module_name else "none")
      })
    })
    m_log debug s"Generated RowKey = $rowkey"
    rowkey
  }


  def storeNode: ( Int, String )=
  {
    try {
      createNode
      var sval : Map[String, Any] = SemanticNode.extractSearchData(ticket, content_element) + ("NodeId" -> new String(rowKey))
      if (!module_name.equalsIgnoreCase("none")) sval = sval + ("module" -> module_name )
      sval.keySet.foreach(k => {m_log trace s"Add search field $k with value: ${sval(k).asInstanceOf[String]}"})
      addSearchSection (sval)
      addSource(compact(render(content_element)))
      if (saveNode)
        (Success.id, s"The Semantic Node [ ${new String(rowKey)} ] has been created")
      else
        (Error.id, "Could not create document")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

  def modifyNode(keys: Map[String, Any]) : (Int, String) =
  {
    val (res, msg ) = selectRowKey(keys)
    if (res != Success.id) return (res, msg)
    retrieve.getOrElse(Map.empty)
    setRowKey(rowKey)
    var sval : Map[String, Any] = SemanticNode.extractSearchData(ticket, content_element) + ("NodeId" -> new String(rowKey))
    if (!module_name.equalsIgnoreCase("none")) sval = sval + ("module" -> module_name )

    sval.keySet.foreach(k => {m_log trace s"Add search field $k with value: ${sval(k).asInstanceOf[String]}"})
    update
    addSearchSection (sval)
    addSource(compact(render(content_element)))
    if (saveNode)
      (Success.id, s"The Semantic Node [ ${new String(rowKey)} ] has been updated")
    else
      (Error.id, "Could not update Semantic Node")
  }

  def retrieveNode(keys: Map[String, Any]) : Map[String, Any] =
  {
    val rowKeys = simpleMetadataSearch(keys, "and")
    if (rowKeys != Nil) setRowKey(rowKeys.head) else return Map.empty
    retrieve.getOrElse(Map.empty)
  }

  def selectRowKey(keys: Map[String, Any]) : (Int, String) = {
    if (keys.contains("NodeId")) {
      setRowKey(Bytes.toBytes(keys("NodeId").asInstanceOf[String]))
      (Success.id,  s" Selected Node [ ID = ${new String(rowKey)} ]")
    }
    else{
      val rowKeys = simpleMetadataSearch(keys, "and")
      if (rowKeys != Nil) {
        val rk = rowKeys.head
        setRowKey(rk)
        (Success.id,  s"Selected Node [ ID = ${new String(rowKey)} ]")
      }
      else {
        (noDataFound.id, s"No row keys were selected")
      }
    }
  }

  def removeNode(keys: Map[String, Any]) : (Int, String) =
  {
    val (res, msg ) = selectRowKey(keys)
    if (res != Success.id) return (res, msg)
    delete
  }

  def searchNodes(searchFilter: Map[String, Any]) : List[Map[String, Any]] = loadNodes(simpleMetadataSearch(searchFilter, "and"))

  def scanNodes : List[Map[String, Any]] = loadNodes( scanMDNodes )

}


object SemanticNode
{
  val separator: String = "::"
    val rowKeyRule = List(("ticket", "customer_code"),
                       ("contents", "module"),
                       ("contents", "type"),
                       ("ticket", "ticketId"))

  def extractSearchData(ticket: JValue, content_element: JValue) : Map[String, Any] = {
    List((content_element \ "customer_Prod_module_feature_sys_id", "customer_Prod_module_feature_sys_id"),
      (ticket \ "userName", "userName"),
      (ticket \ "dataSecurityKey", "dataSecurityKey"),
      (content_element \ "type", "type"),
      (content_element \ "metric_name", "metric_name"),
      (ticket \ "customer_code", "customer_code"))
      .map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, searchFields(jv._2)) )
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }
}



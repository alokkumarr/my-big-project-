package sncr.metadata.semantix

import org.apache.hadoop.hbase.TableName
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.ProcessingResult._
import sncr.metadata.semantix.SearchDictionary.searchFields
import sncr.metadata.store.{MDNodeUtil, MetadataNode, SearchMetadata}
import sncr.metadata.{MDObjectStruct, tables}
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 2/19/2017.
  */
class SemanticNode(val ticket: JValue, val content_element: JValue, val module_name : String = "none") extends MetadataNode with SearchMetadata{

  override val m_log: Logger = LoggerFactory.getLogger(classOf[SemanticNode].getName)

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
      completeNode("has been created")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

  def modifyNode(keys: Map[String, Any]) : (Int, String) =
  {
    try {

      val (res, msg ) = selectRowKey(keys)
      if (res != Success.id) return (res, msg)
      retrieve.getOrElse(Map.empty)
      setRowKey(rowKey)
      update
      completeNode("has been updated")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
    }



  private def completeNode(operName : String): (Int, String) =
  {
    var searchValues : Map[String, Any] = SemanticNode.extractSearchData(ticket, content_element) + ("NodeId" -> new String(rowKey))
    if (!module_name.equalsIgnoreCase("none")) searchValues = searchValues + ("module" -> module_name )
    searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})
    addSearchSection (searchValues)
    addSource(compact(render(content_element)))
    if (saveNode)
      (Success.id, s"The Semantic Node [ ${new String(rowKey)} ] $operName")
    else
      (Error.id, "Could not create/update Semantic Node")
  }


  def retrieveNode(keys: Map[String, Any]) : Map[String, Any] =
  {
    val (res, msg ) = selectRowKey(keys)
    if (res != Success.id) return Map.empty
    retrieve.getOrElse(Map.empty)
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
  val m_log: Logger = LoggerFactory.getLogger("SemanticNodeObject")

  val separator: String = "::"
  val rowKeyRule = List(("ticket", "customer_code"),
                       ("contents", "module"),
                       ("contents", "type"),
                       ("ticket", "ticketId"))

  def  extractSearchData(ticket: JValue, content_element: JValue) : Map[String, Any] = {
    List((content_element, "customer_Prod_module_feature_sys_id"),
      (ticket, "userName"),
      (ticket, "dataSecurityKey"),
      (content_element, "type"),
      (content_element, "metric_name"),
      (ticket, "customer_code"))
      .map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, searchFields(jv._2)) )
        m_log trace s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }

}



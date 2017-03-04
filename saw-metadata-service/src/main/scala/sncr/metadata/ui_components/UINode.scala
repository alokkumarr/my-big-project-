package sncr.metadata.ui_components

import java.util.UUID

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Result
import org.json4s.JsonAST.{JString, JValue}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.MDObjectStruct.{apply => _, _}
import sncr.metadata.ProcessingResult._
import sncr.metadata.engine.{MDNodeUtil, MetadataNode, SearchMetadata, SourceAsJson}
import sncr.metadata.ui_components.SearchDictionary.searchFields
import sncr.metadata.{MDObjectStruct, tables}
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 2/19/2017.
  */
class UINode(val ticket: JValue, val content_element: JValue, val ui_item_type : String = "none")
      extends MetadataNode
      with SearchMetadata
      with SourceAsJson {

  override def getSourceData(res:Result): JValue = super[SourceAsJson].getSourceData(res)

  override val m_log: Logger = LoggerFactory.getLogger(classOf[UINode].getName)

  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.SemanticMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  this.searchFields = SearchDictionary.searchFields

  override def createRowKey : String =
  {
    val rowkey = UINode.rowKeyRule.foldLeft(new String)((z, t) => {
      z + (if (z.isEmpty) "" else UINode.separator) +
        {val part_id = (content_element \ ui_item_type \ t).extractOpt[String]
         if (part_id.isDefined && part_id.nonEmpty) part_id
         else
          if(t.equalsIgnoreCase("_id")) UUID.randomUUID().toString else "none"}
      })
/*    val rowkey = UINode.rowKeyRule.foldLeft(new String)((z, t) => {
      m_log trace s"Z = $z, t = ${t.toString}"
      z + (if (t == UINode.rowKeyRule.head) "" else UINode.separator) + (content_element \ t._2).
          extractOrElse[String](if( t._2.equalsIgnoreCase("ui_item_type")) ui_item_type else "none")

    })
*/
    m_log debug s"Generated RowKey = $rowkey"
    rowkey
  }


  def storeNode: ( Int, String )=
  {
    try {
      createNode
      content_element.replace(List("_id"), JString(new String (rowKey))  )
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
    var searchValues : Map[String, Any] = UINode.extractSearchData(ticket, content_element) + ("NodeId" -> new String(rowKey))
    if (!ui_item_type.equalsIgnoreCase("none")) searchValues = searchValues + ("item_type" -> ui_item_type )
    searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})
    addSearchSection (searchValues)
    addSource(compact(render(content_element)))
    if (saveNode)
      (Success.id, s"The UI Node [ ${new String(rowKey)} ] $operName")
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


object UINode
{
  val m_log: Logger = LoggerFactory.getLogger("SemanticNodeObject")

  val separator: String = "::"
  val rowKeyRule = List("customer_code", "type", "_id")

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



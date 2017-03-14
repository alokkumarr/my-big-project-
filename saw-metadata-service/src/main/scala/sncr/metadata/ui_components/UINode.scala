package sncr.metadata.ui_components

import java.util.UUID

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Result
import org.json4s.JsonAST.{JString, JValue}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct.{apply => _, _}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.metadata.ui_components.SearchDictionary.searchFields
import sncr.saw.common.config.SAWServiceConfig
import org.apache.hadoop.hbase.client._

/**
  * Created by srya0001 on 2/19/2017.
  */
class UINode(val ticket: JValue, private[this] var content_element: JValue, val ui_item_type : String = "none")
      extends ContentNode
      with SourceAsJson {

  override def getSourceData(res:Result): JValue = super[SourceAsJson].getSourceData(res)

  override def compileRead(g : Get) = includeContent(g)

  override def header(g : Get) = includeSearch(g)

  override def getData(res:Result): Option[Map[String, Any]] =
    Option(getSearchFields(res) + (key_Definition.toString -> compact(render(getSourceData(res))) ))

  override val m_log: Logger = LoggerFactory.getLogger(classOf[UINode].getName)

  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.SemanticMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc =  SearchDictionary.searchFields

  override def initRow : String =
  {
    val rowkey = UINode.rowKeyRule.foldLeft(new String)((z, t) => {
      z +
        (if (z.isEmpty) "" else UINode.separator) +
        (t match {
          case "customer_code" => val cc_code = (content_element \ t).extractOpt[String]
                        if (cc_code.isEmpty)
                           (ticket \ t).extractOpt[String].getOrElse("unknown_cc")
                        else cc_code.get
          case "type" => ui_item_type
          case "_id" => val part_id = (content_element \ t).extractOpt[String]
                        if(part_id.isDefined && part_id.nonEmpty) part_id.get else UUID.randomUUID().toString
        })
      })
    m_log debug s"Generated RowKey = $rowkey"
    rowkey
  }


  def create: ( Int, String )=
  {
    try {
      val put_op = createNode(NodeType.ContentNode.id, classOf[UINode].getName)
      content_element.replace(List("_id"), JString(new String (rowKey))  )
      var searchValues : Map[String, Any] = UINode.extractSearchData(ticket, content_element) + ("NodeId" -> new String(rowKey))
      if (!ui_item_type.equalsIgnoreCase("none")) searchValues = searchValues + ("item_type" -> ui_item_type )
      searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})
      if (commit(saveContent(put_op, compact(render(content_element)), searchValues)))
        (Success.id, s"The UI Node [ ${new String(rowKey)} ] has been created")
      else
        (Error.id, "Could not create UI Node")

    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

  def update(keys: Map[String, Any]) : (Int, String) =
  {
    try {
      val (res, msg ) = selectRowKey(keys)
      if (res != Success.id) return (res, msg)
      val get_op = prepareRead
      readCompiled(get_op).getOrElse(Map.empty)
      setRowKey(rowKey)
      var searchValues : Map[String, Any] = UINode.extractSearchData(ticket, content_element) + ("NodeId" -> new String(rowKey))
      if (!ui_item_type.equalsIgnoreCase("none")) searchValues = searchValues + ("item_type" -> ui_item_type )
      searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})
      if (commit(saveContent(update, compact(render(content_element)), searchValues)))
        (Success.id, s"The UI Node [ ${new String(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update UI Node")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

}


object UINode
{
  val m_log: Logger = LoggerFactory.getLogger("UINodeObject")

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



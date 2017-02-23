package scala.sncr.metadata.semantix

import org.apache.hadoop.hbase.TableName
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.semantix.SearchDictionary
import sncr.metadata.store.{MDNodeUtil, MetadataNode}
import sncr.metadata.{MDObjectStruct, tables}
import sncr.saw.common.config.SAWServiceConfig

import scala.sncr.metadata.store.SearchMetadata

/**
  * Created by srya0001 on 2/19/2017.
  */
class SemanticNode(val ticket: JValue, val content_element: JValue) extends MetadataNode with SearchMetadata{


  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.SemanticMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)

  override def createRowKey : String =
  {
    SemanticNode.rowKeyRule.foldLeft(new String)((z, t) => z +
      t._1 match {
      case "ticket" => (if (t == SemanticNode.rowKeyRule.head) "" else SemanticNode.separator) + (ticket \ t._2).extract[String]
      case "contents" => (if (t == SemanticNode.rowKeyRule.head) "" else SemanticNode.separator) + (content_element \ t._2).extract[String] + SemanticNode.separator
    })
  }

//  "userName": "admin@att.com", // to be added to store from Ticket object
//  "dataSecurityKey" : "ATT",   // to be added to store from Ticket object



  def storeNode: ( Int, String )=
  {
    try {
      createNode

      //TODO: Rework
      val sval : Map[String, Any] = MDNodeUtil.extractSearchData(ticket, SearchDictionary.searchFields)  + ("NodeId" -> new String(rowKey))
      sval.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${sval(k).asInstanceOf[String]}"})
      addSearchSection (sval)
      addSource(compact(render(content_element)))
      if (saveNode) (Success.id, s"The Document [ ${new String(rowKey)} ] has been created")
      else (Error.id, "Could not create document")
    }
    catch
    {
      case x: Exception => m_log error ("Could not store document: ", x); ( Error.id, s"Could not create document: ${x.getMessage}")
    }
  }

  def modifyNode(keys: Map[String, Any]) : (Int, String) =
  {
    val rowKeys = simpleMetadataSearch(keys, "and")
    if (rowKeys != Nil) setRowKey(rowKeys.head) else
      return (Error.id, s"Could not find Node [ ID = ${new String(rowKey)} ]")

    (Success.id, s"Success fully updated [ ID = ${new String(rowKey)} ]")
  }

  def retrieveNode(keys: Map[String, Any]) : Map[String, Any] =
  {
    val rowKeys = simpleMetadataSearch(keys, "and")
    if (rowKeys != Nil) setRowKey(rowKeys.head) else return Map.empty
    retrieve.getOrElse(Map.empty)
  }

  def removeNode(keys: Map[String, Any]) : (Int, String) =
  {
    val rowKeys = simpleMetadataSearch(keys, "and")
    if (rowKeys != Nil ) {
      val rk = rowKeys.head
      setRowKey(rk)
      if (delete)
        (Success.id,  s" Node [ ID = ${new String(rowKey)} ] has been removed")
      else
        (Error.id, s" Could not delete Node [ ID = ${new String(rowKey)} ] from Metadata Store")
    }
    else (noDataFound.id,  s" Node ID: ${new String(rowKey)}")
  }

  def searchNodes(searchFilter: Map[String, Any]) : List[Map[String, Any]] =
      loadNodes(simpleMetadataSearch(searchFilter, "and"))



}


object SemanticNode
{
  val separator: String = "::"

  val rowKeyRule = Seq(("ticket", "customer_code"),
                       ("contents", "module"),
                       ("contents", "type"),
                       ("ticket", "ticketId"))

}



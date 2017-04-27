package sncr.metadata.engine.relations

import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.{RelationCategory, SearchMetadata}

/**
  * Created by srya0001 on 4/23/2017.
  */

trait CategorizedRelation extends SimpleRelation{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[CategorizedRelation].getName)

  relType = RelationCategory.CategorizedRelation.id

 //TODO:: Reimplement to use T-methods and avoid matching
 //TODO:: make it based on class structure not based on List of values
  def collectRelationData(res: Result): JValue =
  {
    m_log debug s"Retrieve all related nodes"

    val loadedNodes : List[Any] = elements.map( element => {
      element._1 match {
        case "DataObject" => DataObject(element._2)
        case _ => m_log error s"Attempt to use unknown node category in categorized relation"; null
      }
    }).toList
    val result = JObject( List (JField("storageType", JString("DL")),
                   JField( "objects", JArray( loadedNodes.map {
                    case dobj: DataObject => dobj.buildSemanticNodeModel
                    case _ => throw new Exception("The method is not implemented for this category")
                    } )),
                    JField("_number_of_elements", JInt(loadedNodes.size)))
    )

    m_log debug s"Relation ==> ${pretty(render(result))} "
    result
  }





  override def removeNodesFromRelation( rowKeys:List[(String, String)] ): JValue =
  {
    rowKeys.foreach( rk => {
      elements = elements.filterNot(el => rk._1.equals(el._1) && rk._2.equals(el._2))
    })
    m_log trace s"Remove nodes from relation: updated Node List = ${elements.mkString("[", ",", "]")}"
    normalize
  }

  override def removeNodeFromRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    elements = elements.filterNot( el => { m_log debug s"Table ${el._1} RowId: ${el._2}"; nodeCategory.equals(el._1) && a_rowID.equals(el._2)} )
    m_log trace s"Remove node from relation: Table = ${nodeCategory}, RowID = $a_rowID, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }


  override def addNodesToRelation(keys: Map[String, Any], systemProps:Map[String, Any]): JValue =
    throw new Exception("Method is not supported for Beta relation type")

  override def addNodeToRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    elements = elements ++ List((nodeCategory, a_rowID))
    m_log trace s"Add node to relation: Table = ${nodeCategory}, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }


  override def addNodesToRelation(keys: Map[String, Any], nodeCategory: String): JValue =
  {
    val rowIDs : List[Array[Byte]] = SearchMetadata.simpleSearch(nodeCategory, keys, Map.empty, "and")
    m_log trace s"Add nodes to relation: Table = $nodeCategory, updated RowIds = ${rowIDs.map( Bytes.toString ).mkString("[", ",", "]")}"
    elements = elements ++ rowIDs.map( id => (nodeCategory, Bytes.toString(id)))
    normalize
  }

}

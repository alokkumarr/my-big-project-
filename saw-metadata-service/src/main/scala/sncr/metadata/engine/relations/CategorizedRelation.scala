package sncr.metadata.engine.relations

import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.RelationCategory

/**
  * Created by srya0001 on 4/23/2017.
  */

trait CategorizedRelation extends BaseRelation{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[CategorizedRelation].getName)

  relType = RelationCategory.CategorizedRelation.id

 //TODO:: Reimplement to use T-methods and avoid matching
 //TODO:: make it based on class structure not based on List of values
  def collectRelationData: JValue =
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

  override def removeNodeFromRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    verifyIntegrity
    elements = elements.filterNot( el => { m_log debug s"Table ${el._1} RowId: ${el._2}"; nodeCategory.equals(el._1) && a_rowID.equals(el._2)} )
    m_log trace s"Remove node from relation: Table = ${nodeCategory}, RowID = $a_rowID, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }


  override def addNodeToRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    verifyIntegrity
    elements = elements ++ List((nodeCategory, a_rowID))
    m_log trace s"Add node to relation: Table = ${nodeCategory}, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }

}

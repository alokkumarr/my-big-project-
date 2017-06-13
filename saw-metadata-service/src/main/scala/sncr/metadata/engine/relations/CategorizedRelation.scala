package sncr.metadata.engine.relations

import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.native.JsonMethods._
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.RelationCategory

/**
  * Created by srya0001 on 4/23/2017.
  */

trait CategorizedRelation extends BaseRelation{

  relType = RelationCategory.CategorizedRelation.id

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

  def removeNodeFromRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    verifyIntegrity
    elements = elements.filterNot( el => { m_log debug s"Table ${el._1} RowId: ${el._2}"; nodeCategory.equals(el._1) && a_rowID.equals(el._2)} )
    m_log trace s"Remove node from relation: Table = $nodeCategory, RowID = $a_rowID, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }


  def addNodeToRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    verifyIntegrity
    elements = elements ++ List((nodeCategory, a_rowID))
    m_log trace s"Add node to relation: Table = ${nodeCategory}, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }

  var loadedRelationElements : List[Any] = List.empty

  def loadRelationElements : List[Any] =
  {
    if (loadedRelationElements.isEmpty)
    {
      loadedRelationElements = getRelatedNodes.map(pair =>
      { pair._1 match {
        case "DataObject" | "datalake_metadata" => DataObject(pair._2)
        case _ => throw new Exception("The method is not implemented for this category")
      }})
      loadedRelationElements
    }
    else
      loadedRelationElements
  }

}

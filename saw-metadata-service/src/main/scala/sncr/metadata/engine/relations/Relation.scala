package sncr.metadata.engine.relations

import org.apache.hadoop.hbase.client.Get
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.{NodeType, RelationCategory}
import org.apache.hadoop.hbase.client._
import sncr.metadata.engine.MDObjectStruct._
import org.apache.hadoop.hbase.util.Bytes
import scala.collection.mutable

/**
  * Created by srya0001 on 3/4/2017.
  */
trait Relation extends scala.collection.mutable.Seq[(Int, Array[Byte])] {

  var nodePut : Put = null
  var nodeGet : Get = null

  var relationRowID : Array[Byte] = null
  val relType = RelationCategory.RelationSimpleSet.id
  var attributes : mutable.Map[String, Any] = new mutable.HashMap[String, Any]



  def addRelation( rowID: Array[Byte]): Int =
  {

    nodeGet.addFamily(MDSections(systemProperties.id))
    nodeGet.addFamily(MDSections(relationsSection.id))
    nodeGet.addFamily(MDSections(relationAttributesSection.id))

    if (res.isEmpty) return Error.id

    Bytes.toInt(res.getValue(MDSections(systemProperties.id),MDKeys(syskey_NodeType.id)))
    match{
      case NodeType.RelationContentNode.id | NodeType.RelationNode.id => OperationDeclined.id
      case _ =>
        nodePut = new Put(relationRowID)
        nodePut.addColumn(MDSections(systemProperties.id),MDKeys(syskey_NodeType.id), Bytes.toBytes(NodeType.RelationContentNode.id))
        nodePut.addColumn(MDSections(systemProperties.id),MDKeys(syskey_RelCategory.id), Bytes.toBytes(RelationCategory.RelationSimpleSet.id))
        Success.id
    }
  }


  /**
    * Essentially creates relation from elements
    * with given RowID
    *
    * @param elements - relations members
    * @return
    */
  def connectElements(elements: List[Array[Byte]]) : Array[Byte] = {
    this = elements
    relationRowID
  }


  def writeRelation(rowID: Array[Byte])  : (Int, String) =
  {
    if (nodePut == null) nodePut = new Put(rowID)
    relationRowID = rowID


    (Success.id, s"The relation ${new String(rowID) } has been committed to the DB")
  }

  def readRelation(rowID: Array[Byte]) : (Int, String) =
  {
    if (nodeGet == null) nodeGet = new Get(rowID)
    relationRowID = rowID

    (Success.id, s"The relation ${new String(rowID) } has been read from the DB")
  }


  /**
    * Add on attribute to relation
    *
    * @param k - attribute key
    * @param v - attribute value
    * @return - operation result
    */
  def addAttribute(k:String, v:Any) : Int =
  {
    attributes(k) = v
    Success.id
  }

  /**
    * Add list of attributes
    *
    * @param attributes - map of key - value pairs
    *
    */
  def addAttributes(attributes: Map[String, Any]) : Unit = attributes.foreach( a => addAttribute(a._1, a._2))



}

object Relation {



}



package sncr.metadata.engine.relations

import org.apache.hadoop.hbase.client.{Get, _}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.RelationCategory

import scala.collection.mutable

/**
  * Created by srya0001 on 3/4/2017.
  */
trait Relation extends scala.collection.mutable.Seq[(Int, Array[Byte])] {

  val m_log: Logger = LoggerFactory.getLogger(classOf[Relation].getName)

  var relationRowID : Array[Byte] = null
  val relType = RelationCategory.RelationSimpleSet.id
  var attributes : mutable.Map[String, Any] = new mutable.HashMap[String, Any]

  override def update(idx: Int, elem: (Int, Array[Byte])): Unit = this(idx) = elem

  override def length: Int = this.length

  override def apply(idx: Int): (Int, Array[Byte]) = this(idx)

  override def iterator: Iterator[(Int, Array[Byte])] = this.iterator


  /*  Read calls */

  protected def compileRelationsCells(getCNode: Get): Get =
  {
    // getContentStructure
    getCNode.addFamily(MDSections(relationsSection.id))
    getCNode.addFamily(MDSections(relationAttributesSection.id))
    getCNode
  }


  import scala.collection.JavaConversions._
  protected def getSearchFields(res:Result): Map[String, String] =
  {
    val sfKeyValues = res.getFamilyMap(MDSections(relationAttributesSection.id))
    m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " =>" + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"
    val sf : Map[String, String] = sfKeyValues.keySet().map( k =>
    { val k_s = new String(k)
      val v_s = new String(sfKeyValues.get(k))
      m_log trace s"Search field: $k_s, value: $v_s"
      k_s -> v_s
    }).toMap
    sf
  }

  def addRelation(nodeGet: Get, rowID: Array[Byte]): Get =
  {
    nodeGet.addFamily(MDSections(systemProperties.id))
    nodeGet.addFamily(MDSections(relationsSection.id))
    nodeGet.addFamily(MDSections(relationAttributesSection.id))
  }


  /* Write calls */

  protected def addSearchSection( putCNode : Put, search_val : Map[String, Any]): Put =
  {
    if (putCNode == null ) return null
    search_val.keySet.foreach( k=>putCNode.addColumn(MDSections(searchSection.id),Bytes.toBytes(k), Bytes.toBytes(search_val(k).asInstanceOf[String])))
    putCNode
  }


  def addRelation(nodePut: Put, rowID: Array[Byte])  : Put =
  {
    if (nodePut == null) return null
    relationRowID = rowID
    nodePut
   }

  def readRelation(nodeGet : Get , rowID: Array[Byte]) : Get =
  {
    if (nodeGet == null) null
    relationRowID = rowID
    nodeGet
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



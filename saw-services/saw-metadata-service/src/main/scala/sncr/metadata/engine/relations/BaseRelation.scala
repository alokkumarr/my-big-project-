package sncr.metadata.engine.relations

import org.apache.hadoop.hbase.client.{Get, _}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JValue
import org.json4s.JsonAST._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine._

/**
  * Created by srya0001 on 3/4/2017.
  */
trait BaseRelation{

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[BaseRelation].getName)

  protected var relType : Int = RelationCategory.BaseRelation.id
  protected var elements : Array[(String, String)] = Array.empty
  protected var readNumOfElements : Int = 0
  protected var _elementsAsJSON : JValue = JNothing
  var loadedFlag = false


  protected def includeRelation(getNode: Get): Get =  getNode.addFamily(MDColumnFamilies(_cf_relations.id))

  protected def getRelationDataAsJson(res:Result) : JValue =
  {
    getRelationData(res)
    normalize
  }

  def loadAndNormalizeRelation[A <: MetadataNode](parentNode : A) : Unit =
  {
    if (loadedFlag) return
    val updateGet = new Get(parentNode.getRowKey)
    updateGet.addFamily(MDColumnFamilies(_cf_relations.id))
    val res = parentNode.getTable.get(updateGet)
    getRelationData(res)
    normalize
  }

  private def getRelationData(res:Result) : Unit =
  {
    val data = res.getValue(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(Fields.NumOfElements.toString))
    readNumOfElements = if (data != null ) Bytes.toInt(data) else 0
    val rc = res.getValue(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_relation_category"))
    relType = if (rc != null)
          try{  Bytes.toInt(rc) } catch{ case x: Throwable=> 0 } else 0
    m_log debug s"# of elements: $readNumOfElements"
    loadedFlag = true
    elements = elements ++ (for ( i <- 0 until readNumOfElements  ) yield {
         val t = res.getValue(MDColumnFamilies(_cf_relations.id), Bytes.toBytes(i + "_TAB"))
         val r = res.getValue(MDColumnFamilies(_cf_relations.id), Bytes.toBytes(i + "_RID"))
          m_log debug s"Processing pair: ${i}_TAB => ${Bytes.toString(t)},  ${i}_RID => ${Bytes.toString(r)}"
          if (t != null && r != null && t.nonEmpty && r.nonEmpty ) ( Bytes.toString(t), Bytes.toString(r))
          else (null, null)
    }).toArray.filter( p => p._1 != null && p._2 != null )
  }

  /* Write calls */

  def saveRelation(nodePut: Put)  : Put =
  {
    if (nodePut == null) return null
    normalize
    nodePut
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(Fields.NumOfElements.toString), Bytes.toBytes(elements.length))
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(Fields.RelationCategory.toString), Bytes.toBytes(relType))
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_json_"), Bytes.toBytes(compact(render(_elementsAsJSON))))
    m_log debug s"Saved values: # of nodes: ${elements.length}, Relation category:  ${relType}"
    for (i <- 0 until elements.length ) {nodePut
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(i + "_TAB" ), Bytes.toBytes(elements(i)._1))
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(i + "_RID" ), Bytes.toBytes(elements(i)._2))
      m_log debug s"Processing pair: ${i}_TAB => ${elements(i)._1},  ${i}_RID => ${elements(i)._2}" }
    nodePut
   }

  def elementsAsJson : JValue = _elementsAsJSON
  def getRelatedNodes : List[(String, String)] = elements.clone().toList
/*
  def removeNodeFromRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    verifyIntegrity
    val tableName = NodeCategoryMapper.NCM(nodeCategory).toString
    elements = elements.filterNot( el => { m_log debug s"Table ${el._1} RowId: ${el._2}"; tableName.equals(el._1) && a_rowID.equals(el._2)} )
    m_log trace s"Remove node from relation: Table = ${tableName}, RowID = $a_rowID, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }


  def addNodeToRelation(a_rowID : String, nodeCategory: String): JValue =
  {
    verifyIntegrity
    val tableName = NodeCategoryMapper.NCM(nodeCategory).toString
    elements = elements ++ List((tableName, a_rowID))
    m_log trace s"Add node to relation: Table = ${tableName}, updated RowIds = ${elements.mkString("[", ",", "]")}"
    normalize
  }
*/
  protected def normalize: JValue =
  {
    elements = elements.distinct
    readNumOfElements = elements.length
    _elementsAsJSON = null
    val lelements = new JArray(
      (for( i <- elements.indices ) yield JObject(List(JField( i + "_TAB", JString(elements(i)._1)),
                                                       JField( i + "_RID", JString(elements(i)._2))))).toList)
    _elementsAsJSON = new JObject( List(
      "elements" -> lelements,
      JField(Fields.NumOfElements.toString, JInt(readNumOfElements)),
      JField(Fields.RelationCategory.toString,  JString(RelationCategory(relType).toString))
    ) )
    m_log debug s"Converted relation: ${compact(render(_elementsAsJSON))}"
    _elementsAsJSON
  }


  protected def verifyIntegrity : Unit = if ( !loadedFlag ) throw new Exception( "Relation cannot be changed without loading them first")


}





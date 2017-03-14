package sncr.metadata.engine.relations

import org.apache.hadoop.hbase.client.{Get, _}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JValue
import org.json4s.JsonAST._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.{MetadataNode, RelationCategory, SearchMetadata, tables}

/**
  * Created by srya0001 on 3/4/2017.
  */
trait Relation{

  val m_log: Logger = LoggerFactory.getLogger(classOf[Relation].getName)

  var relType : Short = 0
  final private[this] var elements : Array[(String, String)] = Array.empty
  final private[this] var readNumOfElements : Short = 0
  final private[this] var _elementsAsJSON : JValue = JNothing


  /*  Read calls */

  protected def includeRelation(getNode: Get): Get =   getNode.addFamily(MDColumnFamilies(_cf_relations.id))

  protected def getRelationData(res:Result) : JValue =
  {
    val data = res.getValue(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_number_of_elements"))
    readNumOfElements = if (data != null ) Bytes.toShort(data) else 0
    val rc = res.getValue(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_relation_category"))
    relType = if (rc != null) Bytes.toShort(rc) else 0
    m_log debug s"# of elements: $readNumOfElements"
    elements = (for ( i <- 0 until readNumOfElements  ) yield {
         val t = res.getValue(MDColumnFamilies(_cf_relations.id), Bytes.toBytes(i + "_TAB"))
         val r = res.getValue(MDColumnFamilies(_cf_relations.id), Bytes.toBytes(i + "_RID"))
          if (t != null && r != null && t.nonEmpty && r.nonEmpty ) ( Bytes.toString(t), Bytes.toString(r))
          else (null, null)
    }).toArray.filter( p => p._1 != null && p._2 != null )
    convertToJson
  }

  /* Write calls */

  def saveRelation(nodePut: Put)  : Put =
  {
    if (nodePut == null) return null
    convertToJson
    nodePut
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_number_of_elements"), Bytes.toBytes(elements.size))
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_relation_category"), Bytes.toBytes(relType))
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_json_"), Bytes.toBytes(compact(render(_elementsAsJSON))))
    for (i <- 0 until elements.size ) {nodePut
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(i + "_TAB" ), Bytes.toBytes(elements(i)._1))
      .addColumn(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(i + "_RID" ), Bytes.toBytes(elements(i)._2))}
    nodePut
   }

  def elementsAsJson : JValue = _elementsAsJSON


  def loadRelatedNodes: Map[String, Option[Map[String, Any]]] = elements.map(pair => pair._2 -> MetadataNode.load(pair._1, pair._2, true)).toMap


  def getRelatedNodes : List[(String, String)] = elements.clone().toList


  def removeNodesFromRelation( rowKeys:List[(String, String)] ): JValue =
  {
    rowKeys.foreach( rk => elements = elements.filterNot( el => rk._1.equalsIgnoreCase(el._1) && rk._2.equalsIgnoreCase(el._2)))
    convertToJson
  }

  def addNodesToRelation(keys: Map[String, Any], systemProps:Map[String, Any]): JValue =
  {
    val rowIDs : List[List[(String, String)]] = tables.values.map(mdTableName => {
      val rowID : List[Array[Byte]] = SearchMetadata.simpleSearch(mdTableName.toString, keys, systemProps, "and")
     rowID.map( id => (mdTableName.toString, Bytes.toString(id)))
    }).toList
    elements = elements ++ rowIDs.flatMap( list_of_pairs => list_of_pairs )
    convertToJson
  }


  private def convertToJson: JValue =
  {
    _elementsAsJSON = null
    val lelements = new JArray(
      (for( i <- elements.indices ) yield JObject(List(JField( i + "_TAB", JString(elements(i)._1)),
                                                       JField( i + "_RID", JString(elements(i)._2))))).toList)
    _elementsAsJSON = new JObject( List(
      ("elements" -> lelements),
      ("_number_of_elements", JInt(readNumOfElements.toInt)),
      ("_relation_category",  JString(RelationCategory(relType).toString))
    ) )
    _elementsAsJSON
  }
}





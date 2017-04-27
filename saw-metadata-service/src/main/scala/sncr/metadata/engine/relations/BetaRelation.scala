package sncr.metadata.engine.relations

import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.{JArray, JInt, JObject, JValue}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.{Fields, MDObjectStruct, RelationCategory}

/**
  * Created by srya0001 on 4/23/2017.
  */

trait BetaRelation extends AtomRelation with BetaProducers{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[BetaRelation].getName)

  relType = RelationCategory.BetaRelation.id


//TODO::
  def collectRelationData(res: Result): JValue =
  {

    JNothing
  }

  override protected def getRelationData(res:Result) : Unit =
  {
    val data = res.getValue(MDColumnFamilies(_cf_relations.id),Bytes.toBytes(Fields.NumOfElements.toString))
    readNumOfElements = if (data != null ) Bytes.toInt(data) else 0
    val rc = res.getValue(MDColumnFamilies(_cf_relations.id),Bytes.toBytes("_relation_category"))
    relType = if (rc != null)
      try{  Bytes.toInt(rc) } catch{ case x: Throwable=> 0 } else 0
    m_log debug s"# of elements: $readNumOfElements"
    elements = (for ( i <- 0 until readNumOfElements  ) yield {
      val t = res.getValue(MDColumnFamilies(_cf_relations.id), Bytes.toBytes(i + "_TAB"))
      val r = res.getValue(MDColumnFamilies(_cf_relations.id), Bytes.toBytes(i + "_RID"))
      m_log debug s"Processing pair: ${i}_TAB => ${Bytes.toString(t)},  ${i}_RID => ${Bytes.toString(r)}"
//TODO:: Load content of Node - BetaProducer
      //loadContent(t, r)
      if (t != null && r != null && t.nonEmpty && r.nonEmpty ) ( Bytes.toString(t), Bytes.toString(r))
      else (null, null)

    }).toArray.filter( p => p._1 != null && p._2 != null )
  }


  import MDObjectStruct.formats
  override protected def normalize: JValue =
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

}

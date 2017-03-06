package sncr.metadata.engine

import org.apache.hadoop.hbase.client.{Get, Put, Result}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._

/**
  * Created by srya0001 on 3/5/2017.
  */
trait ContentNode{

  val m_log: Logger = LoggerFactory.getLogger(classOf[ContentNode].getName)



  protected def compileContentCells(getCNode: Get): Get =
  {
    // getContentStructure
    getCNode.addFamily(MDSections(sourceSection.id))
    getCNode.addFamily(MDSections(searchSection.id))
    getCNode
  }


  import scala.collection.JavaConversions._
  protected def getSearchFields(res:Result): Map[String, String] =
  {
    val sfKeyValues = res.getFamilyMap(MDSections(searchSection.id))
    m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " =>" + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"
    val sf : Map[String, String] = sfKeyValues.keySet().map( k =>
    { val k_s = new String(k)
      val v_s = new String(sfKeyValues.get(k))
      m_log trace s"Search field: $k_s, value: $v_s"
      k_s -> v_s
    }).toMap
    sf
  }


/* Writing part */

  protected def addSource(putCNode : Put,  content: String): Put =
  {
    if (putCNode == null ) return null
    m_log trace s"Save the document as content CF: $content"
    putCNode.addColumn(MDSections(sourceSection.id),MDKeys(key_Definition.id),Bytes.toBytes(content))
    putCNode
  }

  protected def addSearchSection( putCNode : Put, search_val : Map[String, Any]): Put =
  {
    if (putCNode == null ) return null
    search_val.keySet.foreach( k=>putCNode.addColumn(MDSections(searchSection.id),Bytes.toBytes(k), Bytes.toBytes(search_val(k).asInstanceOf[String])))
    putCNode
  }


  protected def addContent(put_op: Put,  content_element: String, searchValues: Map[String, Any] = Map.empty): Put =
  {
    addSearchSection (put_op, searchValues)
    addSource(put_op, content_element)
  }




}

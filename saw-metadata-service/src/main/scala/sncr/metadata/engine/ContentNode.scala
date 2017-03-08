package sncr.metadata.engine

import org.apache.hadoop.hbase.client.{Get, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._

/**
  * Created by srya0001 on 3/5/2017.
  */
trait ContentNode extends SearchableNode{

  override val m_log: Logger = LoggerFactory.getLogger(classOf[ContentNode].getName)

  protected def compileContentCells(getCNode: Get): Get = compileSearchCell(getCNode.addFamily(MDSections(sourceSection.id)))



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
    search_val.keySet.foreach( k=>putCNode.addColumn(MDSections(searchSection.id),Bytes.toBytes(k), MDNodeUtil.convertValue(search_val(k))))
    putCNode
  }


  protected def addContent(put_op: Put,  content_element: String, searchValues: Map[String, Any] = Map.empty): Put =
  {
    addSearchSection (put_op, searchValues)
    addSource(put_op, content_element)
  }




}

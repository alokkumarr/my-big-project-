package sncr.metadata.engine

import org.apache.hadoop.hbase.client.{Get, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._

/**
  * Created by srya0001 on 3/5/2017.
  */
class ContentNode extends MetadataNodeCanSearch{

  override val m_log: Logger = LoggerFactory.getLogger(classOf[ContentNode].getName)

  protected def includeContent(getCNode: Get): Get = includeSearch(getCNode.addFamily(MDColumnFamilies(_cf_source.id)))


/* Writing part */

  private def setSearchData(put_op : Put, search_val : Map[String, Any]): Put =
  {
    search_val.keySet.foreach( k=> put_op.addColumn(MDColumnFamilies(_cf_search.id),Bytes.toBytes(k), MDNodeUtil.convertValue(search_val(k))))
    put_op
  }


  protected def saveContent(put_op: Put, content_element: String, searchValues: Map[String, Any] = Map.empty): Put =
  {
    if (put_op == null ) return null
    setSearchData (put_op, searchValues)
    m_log trace s"Save the document as content CF: $content_element"
    put_op.addColumn(MDColumnFamilies(_cf_source.id),MDKeys(key_Definition.id),Bytes.toBytes(content_element))
  }




}

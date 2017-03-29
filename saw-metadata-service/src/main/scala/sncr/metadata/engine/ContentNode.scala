package sncr.metadata.engine

import com.typesafe.config.Config
import org.apache.hadoop.hbase.client.{Get, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._

/**
  * Created by srya0001 on 3/5/2017.
  */
class ContentNode(c: Config = null) extends MetadataNodeCanSearch(c){

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[ContentNode].getName)

  protected def includeContent(getCNode: Get): Get = includeSearch(getCNode.addFamily(MDColumnFamilies(_cf_source.id)))

  protected var rawContent : Array[Byte] = null

  def setContent(content_element: String): Unit = rawContent = Bytes.toBytes(content_element)

  def setContent(data: Array[Byte]) = rawContent = data

  def getRawData = rawContent



  /* Writing part */

  def saveSearchData(put_op : Put, search_val : Map[String, Any]): Put =
  {
    if (put_op == null ) return null
    search_val.keySet.foreach( k=> put_op.addColumn(MDColumnFamilies(_cf_search.id),Bytes.toBytes(k), MDNodeUtil.convertValue(search_val(k))))
    put_op
  }


  protected def saveContent(put_op: Put, searchValues: Map[String, Any] = Map.empty): Put =
  {
    if (put_op == null ) return null
    val contentAsString = Bytes.toString(rawContent)
    m_log trace s"Save the document as content CF: $contentAsString"
    put_op.addColumn(MDColumnFamilies(_cf_source.id),MDKeys(key_Definition.id),rawContent)
  }




}

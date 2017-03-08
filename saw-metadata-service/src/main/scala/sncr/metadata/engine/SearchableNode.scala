package sncr.metadata.engine

import org.apache.hadoop.hbase.client.{Get, Result}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 3/7/2017.
  */
trait SearchableNode extends MetadataNode
  with SearchMetadata
  with Header{

  override val m_log: Logger = LoggerFactory.getLogger(classOf[SearchableNode].getName)
  override def header(g : Get) = super[Header].compileSearchCell(g)
  override def getHeaderData(res:Result): Option[Map[String, Any]] = super[Header].getHeaderData(res)

  def find(searchFilter: Map[String, Any]): List[Map[String, Any]] = loadNodes(simpleMetadataSearch(searchFilter, "and"))
  def listHeaders(searchFilter: Map[String, Any]) : List[Map[String, Any]] = loadHeaders(simpleMetadataSearch(searchFilter, "and"))
  def scan: List[Map[String, Any]] = loadNodes(scanMDNodes)


}

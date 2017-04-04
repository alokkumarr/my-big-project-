package sncr.metadata.engine

import com.typesafe.config.Config
import org.apache.hadoop.hbase.client.{Get, Result}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 3/7/2017.
  */
class MetadataNodeCanSearch(c: Config = null)
  extends MetadataNode(c)
  with SearchMetadata
{
  if (c != null) super[SearchMetadata].setConf(c)

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataNodeCanSearch].getName)
  override def selectRowKey(keys: Map[String, Any]) : (Int, String) = super[SearchMetadata].selectRowKey(keys)


  /**
    * Search and load methods
 *
    * @param searchFilter
    * @return
    */
  def find(searchFilter: Map[String, Any]): List[Map[String, Any]] = loadNodes(simpleMetadataSearch(searchFilter, "and"))

  def scan: List[Map[String, Any]] = loadNodes(scanMDNodes)

  def loadNodes(rowKeys: List[Array[Byte]]): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows"
    rowKeys.map( k => {rowKey = k; readCompiled(prepareRead)} ).filter(  _.isDefined ).map(v => v.get)
  }

  /**
    * Header search method
 *
    * @param res
    * @return
    */

  protected override def getHeaderData(res:Result): Option[Map[String, Any]]  = Option(getSearchFields(res) )
  protected override def header(g : Get) : Get = includeSearch(g)

  def loadHeaders(rowKeys: List[Array[Byte]]): List[Map[String, Any]] =
  {
    m_log debug s"Load ${rowKeys.size} rows"
    rowKeys.map( k => {rowKey = k; readHeaderOnly} ).filter(  _.isDefined ).map(v => v.get)
  }

  def listHeaders(searchFilter: Map[String, Any]) : List[Map[String, Any]] = loadHeaders(simpleMetadataSearch(searchFilter, "and"))


}

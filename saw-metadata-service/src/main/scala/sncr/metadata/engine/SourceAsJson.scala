package sncr.metadata.engine

import org.apache.hadoop.hbase.client.Result
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import MDObjectStruct._

/**
  * Created by srya0001 on 3/3/2017.
  */
trait SourceAsJson {

  val m_log: Logger = LoggerFactory.getLogger(classOf[SourceAsJson].getName)

  protected def getSourceData(res:Result): JValue =
  {
    val content = res.getValue(MDSections(sourceSection.id),MDKeys(key_Definition.id))
    m_log debug s"Read node: ${new String(content)}"
    parse(new String(content), false, false)
  }

}

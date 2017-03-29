package sncr.metadata.engine

import org.apache.hadoop.hbase.client.Result
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import MDObjectStruct._
import com.mapr.org.apache.hadoop.hbase.util.Bytes

/**
  * Created by srya0001 on 3/3/2017.
  */
trait SourceAsJson {

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[SourceAsJson].getName)

  protected def getSourceData(res:Result): (JValue, Array[Byte]) =
  {
    val content = res.getValue(MDColumnFamilies(_cf_source.id),MDKeys(key_Definition.id))
    m_log trace s"Convert content of node to JSON: ${Bytes.toString(content)}"
    (parse(Bytes.toString(content), false, false), content)
  }

}

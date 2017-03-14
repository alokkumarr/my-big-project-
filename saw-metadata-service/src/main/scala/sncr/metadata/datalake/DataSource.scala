package sncr.metadata.datalake

import java.util.UUID

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Get, Result}
import org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.metadata.engine.relations.RelationNode
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 3/10/2017.
  */
class DataSource extends RelationNode{


  override def compileRead(g: Get) = {
    includeRelation(g)
  }

  override def getData(res: Result): Option[Map[String, Any]] = {
      None
  }

  override val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNode].getName)

  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.DatalakeMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)


  override protected def initRow: String = {
    val urid = UUID.randomUUID()
    rowKey = Bytes.toBytes(urid.toString)
    m_log debug s"Generated RowKey = ${urid.toString}"
    urid.toString
  }

  protected def validate: (Int, String) = {
    (Success.id, "Request is correct")
  }


  def create: (Int, String) = {
    try {
      val (result, msg) = validate
      if (result != Success.id) return (result, msg)
      val put_op = createNode(NodeType.RelationContentNode.id, classOf[AnalysisNode].getName)
      if (commit(saveRelation(put_op)))
        (Success.id, s"The DataSource relation [ ${new String(rowKey)} ] has been created")
      else
        (Error.id, "Could not create DataSource relation")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store relation [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

  def update(filter: Map[String, Any]): (Int, String) = {
    try {
      val (result, validate_msg) = validate
      if (result != Success.id) return (result, validate_msg)

      val (res, msg) = selectRowKey(filter)
      if (res != Success.id) return (res, msg)
      readCompiled(prepareRead).getOrElse(Map.empty)
      setRowKey(rowKey)
      if (commit(saveRelation(update)))
        (Success.id, s"The DataSource relation [ ${new String(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update DataSource relation")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store relation [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

}


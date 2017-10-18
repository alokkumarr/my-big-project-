package sncr.metadata.engine

import _root_.sncr.saw.common.config.SAWServiceConfig
import com.typesafe.config.Config
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.security.UserGroupInformation
import org.slf4j.{Logger, LoggerFactory}
/**
  * Created by srya0001 on 1/27/2017.
  *
  * The class is written for MapR-DB
  * To make it compatible with HBase the following should be changed:
  * hbaseConf.addResource(new Path("//conf/hbase-site.xml"))
  * hbaseConf.addResource(new Path("//etc/hadoop/core-site.xml"))
  * hbaseConf.set("hbase.master", master-host + ":16010")
  *
  *
  */
abstract class MetadataStore{

  var mdConfig : Config = SAWServiceConfig.metadataConfig
  protected val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataStore].getName)
  private val default_user = "mapr"

  lazy val hbaseConf = HBaseConfiguration.create
  val zQuorum = mdConfig.getString("zookeeper-quorum")
  val user = if (mdConfig.getString("user") != null &&
    !mdConfig.getString("user").isEmpty)
    mdConfig.getString("user")
  else default_user

  lazy val realUser: UserGroupInformation = UserGroupInformation.createRemoteUser(user)
  UserGroupInformation.setLoginUser(realUser)
  hbaseConf.set("hbase.zookeeper.quorum", zQuorum)
  lazy val connection = ConnectionFactory.createConnection(hbaseConf)
  lazy val admin = connection.getAdmin

  /* Initialize store schema on startup */
  MetadataStoreSchema.init(admin)

  var mdNodeStoreTable : Table = null
//  var searchFields : Map[String, Any] = Map.empty

  protected var rowKey: Array[Byte] = null
  def setRowKey(rK: Array[Byte]): Unit = { rowKey = rK }
  def getRowKey = rowKey

  def close: Unit = mdNodeStoreTable.close()
  override protected def finalize(): Unit = close

}




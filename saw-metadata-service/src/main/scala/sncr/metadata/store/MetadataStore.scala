package sncr.metadata.store

import _root_.sncr.saw.common.config.SAWServiceConfig
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
abstract class MetadataStore {


  val m_log: Logger = LoggerFactory.getLogger(classOf[MetadataStore].getName)
  private val default_user = "mapr"
  val hbaseConf = HBaseConfiguration.create
  val zQuorum = SAWServiceConfig.metadataConfig.getString("zookeeper-quorum")
  val user = if (SAWServiceConfig.metadataConfig.getString("user") != null &&
    !SAWServiceConfig.metadataConfig.getString("user").isEmpty)
    SAWServiceConfig.metadataConfig.getString("user")
  else default_user
  val realUser: UserGroupInformation = UserGroupInformation.createRemoteUser(user)
  UserGroupInformation.setLoginUser(realUser)
  m_log debug "Create connection and admin object"
  hbaseConf.set("hbase.zookeeper.quorum", zQuorum)
  val connection = ConnectionFactory.createConnection(hbaseConf)
  val admin = connection.getAdmin

  var mdNodeStoreTable : Table = null
  var searchFields : Map[String, Any] = Map.empty


  def close: Unit = mdNodeStoreTable.close()

}




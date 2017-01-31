package synchronoss.objectstore

import org.slf4j.{Logger, LoggerFactory}


/**
  * Created by srya0001 on 1/27/2017.
  */
class ObjectStore {

  val m_log: Logger = LoggerFactory.getLogger(classOf[ObjectStore].getName)

  /*
  TSConfig.metadataConfig
  val hbaseConf  = HBaseConfiguration.create
  m_log = LoggerFactory.getLogger(Run.getClass)
  m_log debug "Add resources:"
  hbaseConf.addResource(new Path("/hadoop/hbase-current/conf/hbase-site.xml"))
  hbaseConf.addResource(new Path("/hadoop/hadoop-current/etc/hadoop/core-site.xml"))
  hbaseConf.set("hbase.zookeeper.quorum", "bluequasar")
  hbaseConf.set("hbase.master", "bluequasar:16010")
  val connection = ConnectionFactory.createConnection(config)
*/



}

package sncr.datalake

import java.io.IOException

import com.typesafe.config.Config
import files.HFileOperations
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}
import sncr.saw.common.config.SAWServiceConfig

/**
  *  The object holds Spark execution and MapR filesystem configuration variables and
  *  methods.
  *  the initSpark method should be called in first place.
  *  The reason to keep Spark configuration step separate from object initialization <init>
  *    is to make it more flexible to use.
  */
object DLConfiguration {

  private val logger: Logger = LoggerFactory.getLogger("sncr.datalake.DLConfiguration")
  private var initialized: Boolean = false

  def getConfig: Configuration = DLConfiguration.config
  def getSC: SparkContext = DLConfiguration.ctx
  def getFS: FileSystem = DLConfiguration.fs
  def getSparkConfig : SparkConf = sparkConf

  /**
    * Spark context/configuration holders.
    */
  protected val cfg: Config = SAWServiceConfig.spark_conf
  protected var ctx : SparkContext = null
  protected var sparkConf : SparkConf = null



  logger debug "Configure MAPR: "
  protected val config = new Configuration
  config.set("fs.defaultFS", "maprfs:///")
  protected var fs: FileSystem = null
  try {
    fs = FileSystem.get(config)
  }
  catch {
    case e: IOException => {
      logger.error("Could not get FS: ", e)
    }
  }

  /**
    * Row limit is configured/hardcoded value
    */

  val rowLimit = if (cfg.hasPath ("sql-executor.inline-data-store-limit-rows") ) cfg.getInt ("sql-executor.inline-data-store-limit-rows") else 100
  val jarLocation = cfg.getString ("sql-executor.jar-location")
  val commonLocation = cfg.getString ("sql-executor.output-location")
  val semanticLayerTempLocation = cfg.getString ("sql-executor.semantic-layer-tmp-location")
  val defaultOutputType = cfg.getString ("sql-executor.output-type")

  val jarFiles = HFileOperations.listJarFiles(jarLocation, ".jar")
  logger debug s"Attach to Spark job the following jar files: ${jarFiles.mkString("[", ", ", "]")}"


  /**
    *  The method should be called to create and configure Spark context.
    * PLease note, method attaches to Spark context jars files that will be copied to
    * worker nodes.
    * it is recommended to store such jars in separate directory to avoid overhead network traffic and
    * even jar incompatibility issues.
    *
    */

  def initSpark(): Unit = {
    if (initialized) return
    logger info s"Spark settings: \n Master: ${cfg.getString ("master")} \n Worker memory: ${cfg.getString ("executor.memory")} \n  Cores: ${cfg.getString ("cores.max")} \n Driver memory: ${cfg.getString ("driver.memory")} "
    sparkConf = new SparkConf()
    sparkConf.setAppName ("SAW-DataAccess")
    sparkConf.set ("spark.master", cfg.getString ("master") )
    sparkConf.set ("spark.executor.memory", cfg.getString ("executor.memory") )
    sparkConf.set ("spark.cores.max", cfg.getString ("cores.max") )
    sparkConf.set ("driver.memory", cfg.getString ("driver.memory") )
    sparkConf.set ("spark.sql.inMemoryColumnarStorage.compressed", "true")
    sparkConf.set ("spark.sql.inMemoryColumnarStorage.batchSize", String.valueOf (rowLimit) )
    ctx = SparkContext.getOrCreate(sparkConf)
    jarFiles.foreach(f => ctx.addJar( jarLocation + Path.SEPARATOR + f))
    initialized = true
  }
}


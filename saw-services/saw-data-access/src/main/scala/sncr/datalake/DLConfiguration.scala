package sncr.datalake

import java.io._
import java.util.zip.{ZipEntry, ZipOutputStream}

import com.typesafe.config.Config
import files.HFileOperations
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}
import sncr.saw.common.config.SAWServiceConfig

import scala.reflect.io.File

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

  val rowLimit = if (cfg.hasPath("sql-executor.preview-rows-limit")) cfg.getInt("sql-executor.preview-rows-limit") else 100
  val publishRowLimit = if (cfg.hasPath("sql-executor.publish-rows-limit")) cfg.getInt("sql-executor.publish-rows-limit") else -1
  val executorRestartThreshold = if (cfg.hasPath("sql-executor.executor-restart-threshold"))
    cfg.getInt("sql-executor.executor-restart-threshold") else 100
  val jarLocation = cfg.getString ("sql-executor.jar-location")
  val commonLocation = cfg.getString ("sql-executor.output-location")
  val semanticLayerTempLocation = cfg.getString ("sql-executor.semantic-layer-tmp-location")
  val defaultOutputType = cfg.getString ("sql-executor.output-type")
  val waitTime= if (cfg.hasPath("sql-executor.wait-time")) cfg.getInt("sql-executor.wait-time") else 60

  val jarFiles = HFileOperations.listJarFiles(jarLocation, ".jar")
  logger debug s"Attach to Spark job the following jar files: ${jarFiles.mkString("[", ", ", "]")}"
  if(cfg.hasPath("yarn")) {
    val yarnJar = if(cfg.hasPath("yarn.spark.jars")) cfg.getString("yarn.spark.jars") else ""
    val sparkZips = if(cfg.hasPath("yarn.spark.zips")) cfg.getString("yarn.spark.zips") else ""
    // create zip file if not exists.
    if(File(yarnJar).exists && !File(sparkZips).exists)
      createSparkZipFile(yarnJar,sparkZips)
  }
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
    sparkConf = new SparkConf()
    val executor = System.getProperty("saw.executor", "unknown")
    sparkConf.setAppName("SAW-Executor (" + executor + ")")
    sparkConf.set ("spark.master", cfg.getString ("master") )
    setIfPathExists(sparkConf, "spark.yarn.queue", cfg, getPathByExecutor("yarn.queue", executor))
    sparkConf.set ("spark.executor.memory", cfg.getString(getPathByExecutor("executor.memory", executor)))
    sparkConf.set ("spark.cores.max", cfg.getString(getPathByExecutor("cores.max", executor)))
    // Added to support for Spark core setting with YARN, it will be handled later on with additional
    // spark parameter. Currently being handled using existing parameter configuration cores.max.
    sparkConf.set ("spark.executor.cores", cfg.getString(getPathByExecutor("cores.max", executor)))
    sparkConf.set ("driver.memory", cfg.getString ("driver.memory"))
    setIfPathExists(sparkConf,"spark.hadoop.yarn.resourcemanager.hostname",cfg,"yarn.resourcemanager.hostname")
    setIfPathExists(sparkConf,"spark.yarn.jars",cfg,"yarn.spark.jars")
    setIfPathExists(sparkConf,"spark.yarn.archive",cfg,"yarn.spark.zips")
    setIfPathExists(sparkConf,"spark.executor.instances",cfg,getPathByExecutor("executor.instances", executor))
    setIfPathExists(sparkConf, "spark.driver.port", cfg, "driver.port")
    setIfPathExists(sparkConf, "spark.driver.host", cfg, "driver.host")
    setIfPathExists(sparkConf, "spark.driver.bindAddress", cfg, "driver.bindAddress")
    setIfPathExists(sparkConf, "spark.driver.blockManager.port", cfg, "driver.blockManager.port")
    setAdditionalSparkProperties(sparkConf,executor)
    sparkConf.set ("spark.sql.inMemoryColumnarStorage.compressed", "true")
    sparkConf.set ("spark.sql.inMemoryColumnarStorage.batchSize", String.valueOf (rowLimit) )
    sparkConf.set ("spark.sql.caseSensitive", "false")
    /* Disable the UI to avoid port collision with multiple executors */
    sparkConf.set("spark.ui.enabled", "false")
    ctx = SparkContext.getOrCreate(sparkConf)
    jarFiles.foreach(f => ctx.addJar( jarLocation + Path.SEPARATOR + f))
    initialized = true
  }

  private def getPathByExecutor(key: String, executor: String) = {
    val executorType = if (executor.startsWith("fast-")) "fast" else "regular"
    key + "." + executorType
  }

  private def setIfPathExists(sparkConf: SparkConf, sparkProperty: String, cfg: Config, path: String) {
    logger.debug("Checking if configuration path exists: {}", path)
    if (cfg.hasPath(path)) {
      logger.debug("Configuration path found, so setting Spark property: {}", sparkProperty)
      sparkConf.set(sparkProperty, cfg.getString(path))
    } else {
      logger.debug("Configuration path not found")
    }
  }

  /**
    * This properties blocks can be used, if any additional spark properties require to be set on
    *  regular and fast spark executors.
    * In case of specific executor (Fast or Regular) based setting can be set as below
    *  1) Properties Name should be start with "spark."
    *  2) Properties Name should be ended by executor name. ".fast" or ".regular"
    *  For example : spark.driver.port.fast = "9801"
    * In case of generic executor (Applicable for both Fast and Regular executors) setting can be set as below
    *  1) Properties Name should be start with "spark."
    *  For example : spark.rpc.retry.wait = "10s"
    *
    * @param sparkConf
    * @param executor
    */
  private def setAdditionalSparkProperties (sparkConf: SparkConf, executor : String): Unit =
  {
    val iterator = cfg.entrySet().iterator()
    val executorList : (String,String) = ("fast","regular")
    val executorType = if (executor.startsWith("fast-")) executorList._1 else executorList._2
    while (iterator.hasNext) {
      val property = iterator.next()
      if (property.getKey.startsWith("spark.") && property.getKey.endsWith(executorType))
        sparkConf.set (property.getKey.replace("."+executorType,""),
          cfg.getString(property.getKey))
      else if (property.getKey.startsWith("spark.") && !(property.getKey.endsWith(executorList._1)
        || property.getKey.endsWith(executorList._2)))
        sparkConf.set(property.getKey,
          cfg.getString(property.getKey))
    }
  }
  /**
    * Create zip file to set the spark.yarn.archive properties.
    * @param jarLocation
    * @param out
    * @return
    */
  private def createSparkZipFile(jarLocation :String, out :String): String =
  {
    logger.info("sparkzip file doesn't exists trying to create in location: "+out)
    val jarFiles = HFileOperations.listJarFiles(jarLocation, ".jar")
    val zip = new ZipOutputStream(new FileOutputStream(out))
    jarFiles.foreach { name =>
      zip.putNextEntry(new ZipEntry(name))
      val in = new BufferedInputStream(new FileInputStream(jarLocation+File.separator+name))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()
    logger.trace("sparkZip created successfully.")
    out
  }
}


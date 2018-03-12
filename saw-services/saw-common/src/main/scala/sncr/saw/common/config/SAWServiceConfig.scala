package sncr.saw.common.config

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 12/16/2016.
  */
object SAWServiceConfig {

  private val m_log: Logger = LoggerFactory.getLogger("sncr.saw.common.config.Config")

  val confFileLocation = System.getProperty("config")
  var conf: Config = null

  if (confFileLocation == null || confFileLocation.isEmpty ){
    m_log debug s"Use default configuration file: file name: application.conf and path APP_HOME/conf - specifically for Play based apps "
    conf = ConfigFactory.load
  }
  else{
    val confFileName = confFileLocation + "/application.conf"
    conf = ConfigFactory.parseFile(new File(confFileName))
  }


  lazy val es_conf: Config = conf.getConfig("es")
  lazy val dl_conf: Config = conf.getConfig("dl")
  lazy val metadataConfig : Config = conf.getConfig("metadata")
  lazy val executorConfig :Config = conf.getConfig("report.executor")
  lazy val spark_conf : Config = conf.getConfig("spark")

}

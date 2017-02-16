package sncr.saw.common.config

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 12/16/2016.
  */
object SAWServiceConfig {

  private val m_log: Logger = LoggerFactory.getLogger("TSConfig")
  lazy val conf: Config = ConfigFactory.load

  lazy val es_conf: Config = conf.getConfig("es")
  lazy val security_settings = conf.getConfig("security")
  lazy val dl_conf: Config = conf.getConfig("dl")
  lazy val metadataConfig : Config = conf.getConfig("metadata")
  lazy val spark_conf : Config = conf.getConfig("spark")


}

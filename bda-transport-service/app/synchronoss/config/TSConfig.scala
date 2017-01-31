package synchronoss.config

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 12/16/2016.
  */
object TSConfig {

  private val m_log: Logger = LoggerFactory.getLogger("TSConfig")
  lazy val conf: Config = ConfigFactory.load

  lazy val es_conf: Config = conf.getConfig("es")
  lazy val security_settings = conf.getConfig("security")
  lazy val dl_conf: Config = conf.getConfig("dl")
  lazy val metadataConfig : Config = conf.getConfig("metadata")


}

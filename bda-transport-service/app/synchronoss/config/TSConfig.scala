package synchronoss.config

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 12/16/2016.
  */
object TSConfig {

  private val m_log: Logger = LoggerFactory.getLogger("TSConfig")
  val conf: Config = ConfigFactory.load

  val es_conf: Config = conf.getConfig("es")
  val dl_conf: Config = conf.getConfig("dl")


  m_log.debug("Read properties:")

  //render.replaceAll("\"", "")





}

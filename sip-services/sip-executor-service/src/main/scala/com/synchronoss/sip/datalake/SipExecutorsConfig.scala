package com.synchronoss.sip.datalake

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

object SipExecutorsConfig {


  val confFileLocation = System.getProperty("config")
  var conf: Config = null

  if (confFileLocation == null || confFileLocation.isEmpty) {
    conf = ConfigFactory.load
  }
  else {
    val confFileName = confFileLocation + "/application.conf"
    conf = ConfigFactory.parseFile(new File(confFileName)).resolve()
  }

  lazy val semanticService: Config = conf.getConfig("semantic")
  lazy val sipSsl: Config = conf.getConfig("sip.ssl")
  lazy val sslConfig: Config = conf.getConfig("ssl-config")
  lazy val dl_conf: Config = conf.getConfig("dl")
  lazy val metadataConfig: Config = conf.getConfig("metadata")
  lazy val executorConfig: Config = conf.getConfig("report.executor")
  lazy val spark_conf: Config = conf.getConfig("spark")

}

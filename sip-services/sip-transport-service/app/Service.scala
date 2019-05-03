import java.net.Socket
import java.util.{Timer, TimerTask}

import info.faljse.SDNotify.SDNotify
import org.slf4j.{Logger, LoggerFactory}

import sncr.saw.common.config.SAWServiceConfig

/**
  * Wait until application has started up and is listening on port,
  * and then send a notification to the service manager about start-up
  * completion.
  */
object Service {
  private val RetryWaitSeconds = 3
  private val log: Logger = LoggerFactory.getLogger(classOf[Module].getName)

  def waitAndNotify() {
    if (isPortOpen()) {
      sendNotify()
    } else {
      log.info("Waiting for app to listen on port")
      val timer = new Timer()
      timer.schedule(new TimerTask() {
        def run() {
          waitAndNotify()
        }
      }, RetryWaitSeconds * 1000)
    }
  }

  private def isPortOpen(): Boolean = {
    try {
      val port = SAWServiceConfig.conf.getInt("http.port")
      val socket = new Socket("localhost", port)
      socket.close()
      return true
    } catch {
      case e: Exception => log.debug("Exception: " + e)
    }
    false
  }

  private def sendNotify() = {
    log.info("Notifying service manager about start-up completion")
    SDNotify.sendNotify()
  }
}

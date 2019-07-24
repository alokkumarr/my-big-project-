package sncr.datalake

import org.slf4j.Logger

/**
 * Measures time spent executing block and logs it with the given
 * message time
 */
object TimeLogger {
  def logWithTime[T](log: Logger, message: String, block: => T): T = {
    val start = System.currentTimeMillis
    val result = block
    val stop = System.currentTimeMillis
    val time = "%.3f".format((stop - start) / 1000.0)
    log.debug("Time: {}: {} seconds", message: Any, time)
    result
  }
}

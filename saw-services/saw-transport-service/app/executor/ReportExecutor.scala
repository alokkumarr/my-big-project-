package executor

import org.slf4j.{Logger, LoggerFactory}

/**
 * Report executor for executing analysis report queries based on
 * requests received from MapR Streams queue
 */
class ReportExecutor {
  val log: Logger = LoggerFactory.getLogger(classOf[ReportExecutor].getName)

  init

  private def init {
    /* Use property to detect if launched as regular Transport Service or
     * as executor */
    val property = System.getProperty("saw.executor", "false")
    val isExecutor = property.equals("true")
    log.debug("Is executor: {}", isExecutor)
    if (!isExecutor) {
      /* This is the regular Transport Service application, so stop here */
      return
    }
    /* This is the Transport Service Executor application, so continue */
    runExecutor
  }

  private def runExecutor {
    log.info("Starting executor")
    try {
      val queue = new ReportExecutorQueue
      /* Process one message and let the executor exit so that the system
       * service restarts it to create a fresh new instance to process
       * the next message */
      queue.receive
    } catch {
      case e: Exception => log.error("Exception", e)
    }
    log.info("Finished")
    /* Do not enter the actual Transport Service Play application, so for
     * an exit here */
    System.exit(0)
  }
}

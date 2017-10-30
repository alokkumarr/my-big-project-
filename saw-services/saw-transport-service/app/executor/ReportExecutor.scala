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
    val executor = System.getProperty("saw.executor", "none")
    if (executor.equals("none")) {
      /* This is the regular Transport Service application, so stop here */
      return
    }
    /* This is the Transport Service Executor application, so continue */
    log.info("Starting executor: {}", executor)
    val Array(executorType, _) = executor.split("-", 2)
    runExecutor(executorType)
  }

  private def runExecutor(executorType: String) {
    try {
      val queue = new ReportExecutorQueue(executorType)
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

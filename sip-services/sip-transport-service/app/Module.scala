import java.io.PrintStream

import com.google.inject.AbstractModule

import executor.ReportExecutor

/**
 * Register report executor to be run at application startup.  The
 * executor is implemented as a separate entrypoint into the Transport
 * Service, which is started as a Play application singleton.
 */
class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[ReportExecutor]).asEagerSingleton()
  }

  /* Workaround: Silence warnings from SLF4J about multiple bindings.
   * The Transport Service classpath is brittle, and will anyway go
   * away in an upcoming refactoring.  So silence the messages to
   * avoid cluttering logs.  */
  System.setErr(new PrintStream(System.err) {
    override def println(l: String) {
      if (!l.startsWith("SLF4J: ")
        && !l.startsWith("ERROR StatusLogger Log4j2")) {
        super.println(l);
      }
    }
  })

  /* Check if running as regular Transport Service, not executor */
  val executor = System.getProperty("saw.executor", "none")
  if (executor.equals("none")) {
    /* Notify systemd of service startup, as required for socket
     * activation */
    Service.waitAndNotify()
  }
}

import java.io.PrintStream

import com.google.inject.AbstractModule

/**
 * Configure application module to execute service startup tasks
 */
class Module extends AbstractModule {
  override def configure() = {}

  /* Notify systemd of service startup */
  Service.waitAndNotify()
}

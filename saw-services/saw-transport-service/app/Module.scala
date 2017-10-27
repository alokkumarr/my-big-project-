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
}

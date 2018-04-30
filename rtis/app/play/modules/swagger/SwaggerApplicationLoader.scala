package play.modules.swagger

import play.api.ApplicationLoader.Context
import play.api.inject.ApplicationLifecycle
import play.api.{ApplicationLoader, BuiltInComponents}

import scala.concurrent.Future

trait SwaggerApplicationLoader extends ApplicationLoader {
  def components(context: Context) : BuiltInComponents

  def load(context: Context) = {
    val comp = components(context)
    val application = comp.application


    val life : ApplicationLifecycle = new ApplicationLifecycle() {
      def addStopHook(hook: () => Future[_]): Unit = ()
    }
    new SwaggerPluginImpl(life, comp.router, application)
    application
  }
}

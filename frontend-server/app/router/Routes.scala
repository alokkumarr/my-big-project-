
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/real-time-analytics/frontend-server/conf/routes
// @DATE:Thu Oct 20 10:21:37 EDT 2016

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._

import _root_.controllers.Assets.Asset
import _root_.play.libs.F

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:4
  Application_0: controllers.Application,
  // @LINE:9
  GenericHandler_3: controllers.GenericHandler,
  // @LINE:12
  ApiHelpController_2: controllers.ApiHelpController,
  // @LINE:16
  RTISControl_1: controllers.RTISControl,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:4
    Application_0: controllers.Application,
    // @LINE:9
    GenericHandler_3: controllers.GenericHandler,
    // @LINE:12
    ApiHelpController_2: controllers.ApiHelpController,
    // @LINE:16
    RTISControl_1: controllers.RTISControl
  ) = this(errorHandler, Application_0, GenericHandler_3, ApiHelpController_2, RTISControl_1, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Application_0, GenericHandler_3, ApiHelpController_2, RTISControl_1, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.Application.index"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """i""", """controllers.Application.i"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """i""", """controllers.Application.iPost"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """publishevent""", """controllers.GenericHandler.event(APP_KEY:String, APP_VERSION:String, APP_MODULE:String, EVENT_ID:String, EVENT_DATE:String, EVENT_TYPE:Option[String], payload:Option[String])"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api-docs""", """controllers.ApiHelpController.getResources"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.RTISControl.executeCmd(CMD:String, PARAMETERS:Option[String])"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.RTISControl.executeExtendedCmd(CMD:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """sr""", """controllers.RTISControl.sr(parameters:Option[String])"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:4
  private[this] lazy val controllers_Application_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_Application_index0_invoker = createInvoker(
    Application_0.index,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "index",
      Nil,
      "GET",
      """ Routes
 This file defines all application routes (Higher priority routes first)
 ~~~~""",
      this.prefix + """"""
    )
  )

  // @LINE:5
  private[this] lazy val controllers_Application_i1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("i")))
  )
  private[this] lazy val controllers_Application_i1_invoker = createInvoker(
    Application_0.i,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "i",
      Nil,
      "GET",
      """""",
      this.prefix + """i"""
    )
  )

  // @LINE:6
  private[this] lazy val controllers_Application_iPost2_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("i")))
  )
  private[this] lazy val controllers_Application_iPost2_invoker = createInvoker(
    Application_0.iPost,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "iPost",
      Nil,
      "POST",
      """""",
      this.prefix + """i"""
    )
  )

  // @LINE:9
  private[this] lazy val controllers_GenericHandler_event3_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("publishevent")))
  )
  private[this] lazy val controllers_GenericHandler_event3_invoker = createInvoker(
    GenericHandler_3.event(fakeValue[String], fakeValue[String], fakeValue[String], fakeValue[String], fakeValue[String], fakeValue[Option[String]], fakeValue[Option[String]]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GenericHandler",
      "event",
      Seq(classOf[String], classOf[String], classOf[String], classOf[String], classOf[String], classOf[Option[String]], classOf[Option[String]]),
      "POST",
      """Generic event handler""",
      this.prefix + """publishevent"""
    )
  )

  // @LINE:12
  private[this] lazy val controllers_ApiHelpController_getResources4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api-docs")))
  )
  private[this] lazy val controllers_ApiHelpController_getResources4_invoker = createInvoker(
    ApiHelpController_2.getResources,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ApiHelpController",
      "getResources",
      Nil,
      "GET",
      """ Swagger - Root Resources Listing""",
      this.prefix + """api-docs"""
    )
  )

  // @LINE:16
  private[this] lazy val controllers_RTISControl_executeCmd5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_RTISControl_executeCmd5_invoker = createInvoker(
    RTISControl_1.executeCmd(fakeValue[String], fakeValue[Option[String]]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.RTISControl",
      "executeCmd",
      Seq(classOf[String], classOf[Option[String]]),
      "GET",
      """""",
      this.prefix + """control"""
    )
  )

  // @LINE:17
  private[this] lazy val controllers_RTISControl_executeExtendedCmd6_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_RTISControl_executeExtendedCmd6_invoker = createInvoker(
    RTISControl_1.executeExtendedCmd(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.RTISControl",
      "executeExtendedCmd",
      Seq(classOf[String]),
      "POST",
      """""",
      this.prefix + """control"""
    )
  )

  // @LINE:18
  private[this] lazy val controllers_RTISControl_sr7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("sr")))
  )
  private[this] lazy val controllers_RTISControl_sr7_invoker = createInvoker(
    RTISControl_1.sr(fakeValue[Option[String]]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.RTISControl",
      "sr",
      Seq(classOf[Option[String]]),
      "GET",
      """""",
      this.prefix + """sr"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:4
    case controllers_Application_index0_route(params) =>
      call { 
        controllers_Application_index0_invoker.call(Application_0.index)
      }
  
    // @LINE:5
    case controllers_Application_i1_route(params) =>
      call { 
        controllers_Application_i1_invoker.call(Application_0.i)
      }
  
    // @LINE:6
    case controllers_Application_iPost2_route(params) =>
      call { 
        controllers_Application_iPost2_invoker.call(Application_0.iPost)
      }
  
    // @LINE:9
    case controllers_GenericHandler_event3_route(params) =>
      call(params.fromQuery[String]("APP_KEY", None), params.fromQuery[String]("APP_VERSION", None), params.fromQuery[String]("APP_MODULE", None), params.fromQuery[String]("EVENT_ID", None), params.fromQuery[String]("EVENT_DATE", None), params.fromQuery[Option[String]]("EVENT_TYPE", None), params.fromQuery[Option[String]]("payload", None)) { (APP_KEY, APP_VERSION, APP_MODULE, EVENT_ID, EVENT_DATE, EVENT_TYPE, payload) =>
        controllers_GenericHandler_event3_invoker.call(GenericHandler_3.event(APP_KEY, APP_VERSION, APP_MODULE, EVENT_ID, EVENT_DATE, EVENT_TYPE, payload))
      }
  
    // @LINE:12
    case controllers_ApiHelpController_getResources4_route(params) =>
      call { 
        controllers_ApiHelpController_getResources4_invoker.call(ApiHelpController_2.getResources)
      }
  
    // @LINE:16
    case controllers_RTISControl_executeCmd5_route(params) =>
      call(params.fromQuery[String]("CMD", None), params.fromQuery[Option[String]]("PARAMETERS", None)) { (CMD, PARAMETERS) =>
        controllers_RTISControl_executeCmd5_invoker.call(RTISControl_1.executeCmd(CMD, PARAMETERS))
      }
  
    // @LINE:17
    case controllers_RTISControl_executeExtendedCmd6_route(params) =>
      call(params.fromQuery[String]("CMD", None)) { (CMD) =>
        controllers_RTISControl_executeExtendedCmd6_invoker.call(RTISControl_1.executeExtendedCmd(CMD))
      }
  
    // @LINE:18
    case controllers_RTISControl_sr7_route(params) =>
      call(params.fromQuery[Option[String]]("parameters", None)) { (parameters) =>
        controllers_RTISControl_sr7_invoker.call(RTISControl_1.sr(parameters))
      }
  }
}

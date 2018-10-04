
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Wed Oct 03 15:10:59 EDT 2018

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
  Application_5: controllers.Application,
  // @LINE:9
  GenericHandler_1: controllers.GenericHandler,
  // @LINE:12
  ApiHelpController_4: controllers.ApiHelpController,
  // @LINE:15
  Assets_3: controllers.Assets,
  // @LINE:22
  RTISControl_2: controllers.RTISControl,
  // @LINE:28
  GenericLog_0: controllers.GenericLog,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:4
    Application_5: controllers.Application,
    // @LINE:9
    GenericHandler_1: controllers.GenericHandler,
    // @LINE:12
    ApiHelpController_4: controllers.ApiHelpController,
    // @LINE:15
    Assets_3: controllers.Assets,
    // @LINE:22
    RTISControl_2: controllers.RTISControl,
    // @LINE:28
    GenericLog_0: controllers.GenericLog
  ) = this(errorHandler, Application_5, GenericHandler_1, ApiHelpController_4, Assets_3, RTISControl_2, GenericLog_0, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Application_5, GenericHandler_1, ApiHelpController_4, Assets_3, RTISControl_2, GenericLog_0, prefix)
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
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """docs""", """controllers.ApiHelpController.viewSwaggerUI"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """docs/""" + "$" + """file<.+>""", """controllers.Assets.at(path:String = "/public/swagger-ui", file:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.RTISControl.executeCmd(CMD:String, PARAMETERS:Option[String])"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.RTISControl.executeExtendedCmd(CMD:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """sr""", """controllers.RTISControl.sr(parameters:Option[String])"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """genericlog""", """controllers.GenericLog.doPost(CID:String, LOG_TYPE:String)"""),
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
    Application_5.index,
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
    Application_5.i,
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
    Application_5.iPost,
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
    GenericHandler_1.event(fakeValue[String], fakeValue[String], fakeValue[String], fakeValue[String], fakeValue[String], fakeValue[Option[String]], fakeValue[Option[String]]),
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
    ApiHelpController_4.getResources,
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

  // @LINE:14
  private[this] lazy val controllers_ApiHelpController_viewSwaggerUI5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("docs")))
  )
  private[this] lazy val controllers_ApiHelpController_viewSwaggerUI5_invoker = createInvoker(
    ApiHelpController_4.viewSwaggerUI,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ApiHelpController",
      "viewSwaggerUI",
      Nil,
      "GET",
      """""",
      this.prefix + """docs"""
    )
  )

  // @LINE:15
  private[this] lazy val controllers_Assets_at6_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("docs/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_at6_invoker = createInvoker(
    Assets_3.at(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "at",
      Seq(classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """docs/""" + "$" + """file<.+>"""
    )
  )

  // @LINE:22
  private[this] lazy val controllers_RTISControl_executeCmd7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_RTISControl_executeCmd7_invoker = createInvoker(
    RTISControl_2.executeCmd(fakeValue[String], fakeValue[Option[String]]),
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

  // @LINE:23
  private[this] lazy val controllers_RTISControl_executeExtendedCmd8_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_RTISControl_executeExtendedCmd8_invoker = createInvoker(
    RTISControl_2.executeExtendedCmd(fakeValue[String]),
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

  // @LINE:24
  private[this] lazy val controllers_RTISControl_sr9_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("sr")))
  )
  private[this] lazy val controllers_RTISControl_sr9_invoker = createInvoker(
    RTISControl_2.sr(fakeValue[Option[String]]),
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

  // @LINE:28
  private[this] lazy val controllers_GenericLog_doPost10_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("genericlog")))
  )
  private[this] lazy val controllers_GenericLog_doPost10_invoker = createInvoker(
    GenericLog_0.doPost(fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GenericLog",
      "doPost",
      Seq(classOf[String], classOf[String]),
      "POST",
      """""",
      this.prefix + """genericlog"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:4
    case controllers_Application_index0_route(params) =>
      call { 
        controllers_Application_index0_invoker.call(Application_5.index)
      }
  
    // @LINE:5
    case controllers_Application_i1_route(params) =>
      call { 
        controllers_Application_i1_invoker.call(Application_5.i)
      }
  
    // @LINE:6
    case controllers_Application_iPost2_route(params) =>
      call { 
        controllers_Application_iPost2_invoker.call(Application_5.iPost)
      }
  
    // @LINE:9
    case controllers_GenericHandler_event3_route(params) =>
      call(params.fromQuery[String]("APP_KEY", None), params.fromQuery[String]("APP_VERSION", None), params.fromQuery[String]("APP_MODULE", None), params.fromQuery[String]("EVENT_ID", None), params.fromQuery[String]("EVENT_DATE", None), params.fromQuery[Option[String]]("EVENT_TYPE", None), params.fromQuery[Option[String]]("payload", None)) { (APP_KEY, APP_VERSION, APP_MODULE, EVENT_ID, EVENT_DATE, EVENT_TYPE, payload) =>
        controllers_GenericHandler_event3_invoker.call(GenericHandler_1.event(APP_KEY, APP_VERSION, APP_MODULE, EVENT_ID, EVENT_DATE, EVENT_TYPE, payload))
      }
  
    // @LINE:12
    case controllers_ApiHelpController_getResources4_route(params) =>
      call { 
        controllers_ApiHelpController_getResources4_invoker.call(ApiHelpController_4.getResources)
      }
  
    // @LINE:14
    case controllers_ApiHelpController_viewSwaggerUI5_route(params) =>
      call { 
        controllers_ApiHelpController_viewSwaggerUI5_invoker.call(ApiHelpController_4.viewSwaggerUI)
      }
  
    // @LINE:15
    case controllers_Assets_at6_route(params) =>
      call(Param[String]("path", Right("/public/swagger-ui")), params.fromPath[String]("file", None)) { (path, file) =>
        controllers_Assets_at6_invoker.call(Assets_3.at(path, file))
      }
  
    // @LINE:22
    case controllers_RTISControl_executeCmd7_route(params) =>
      call(params.fromQuery[String]("CMD", None), params.fromQuery[Option[String]]("PARAMETERS", None)) { (CMD, PARAMETERS) =>
        controllers_RTISControl_executeCmd7_invoker.call(RTISControl_2.executeCmd(CMD, PARAMETERS))
      }
  
    // @LINE:23
    case controllers_RTISControl_executeExtendedCmd8_route(params) =>
      call(params.fromQuery[String]("CMD", None)) { (CMD) =>
        controllers_RTISControl_executeExtendedCmd8_invoker.call(RTISControl_2.executeExtendedCmd(CMD))
      }
  
    // @LINE:24
    case controllers_RTISControl_sr9_route(params) =>
      call(params.fromQuery[Option[String]]("parameters", None)) { (parameters) =>
        controllers_RTISControl_sr9_invoker.call(RTISControl_2.sr(parameters))
      }
  
    // @LINE:28
    case controllers_GenericLog_doPost10_route(params) =>
      call(params.fromQuery[String]("CID", None), params.fromQuery[String]("LOG_TYPE", None)) { (CID, LOG_TYPE) =>
        controllers_GenericLog_doPost10_invoker.call(GenericLog_0.doPost(CID, LOG_TYPE))
      }
  }
}

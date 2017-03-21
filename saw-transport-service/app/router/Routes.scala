
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/markus/saw-services/saw-transport-service/conf/routes
// @DATE:Tue Mar 21 06:52:20 EDT 2017

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
  MTSControl_5: controllers.MTSControl,
  // @LINE:6
  TS_4: controllers.TS,
  // @LINE:7
  MD_2: controllers.MD,
  // @LINE:8
  EXE_1: controllers.EXE,
  // @LINE:9
  ANA_0: controllers.ANA,
  // @LINE:11
  MCT_3: controllers.MCT,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:4
    MTSControl_5: controllers.MTSControl,
    // @LINE:6
    TS_4: controllers.TS,
    // @LINE:7
    MD_2: controllers.MD,
    // @LINE:8
    EXE_1: controllers.EXE,
    // @LINE:9
    ANA_0: controllers.ANA,
    // @LINE:11
    MCT_3: controllers.MCT
  ) = this(errorHandler, MTSControl_5, TS_4, MD_2, EXE_1, ANA_0, MCT_3, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, MTSControl_5, TS_4, MD_2, EXE_1, ANA_0, MCT_3, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.MTSControl.index"""),
    ("""POST""", this.prefix, """controllers.TS.handleRequest"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """md""", """controllers.MD.handleRequest"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """exe""", """controllers.EXE.handleRequest"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis""", """controllers.ANA.handleRequest"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """ObjectSearch""", """controllers.MCT.handleTagRequest(LCID:String, query:Option[String])"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """ObjectSearch""", """controllers.MCT.extendedTagRequest(LCID:String, indexname:Option[String], objecttype:Option[String])"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.MTSControl.executeCmd(CMD:String, PARAMETERS:Option[String])"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.MTSControl.executeExtendedCmd(CMD:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """sr""", """controllers.MTSControl.sr(parameters:Option[String])"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:4
  private[this] lazy val controllers_MTSControl_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_MTSControl_index0_invoker = createInvoker(
    MTSControl_5.index,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MTSControl",
      "index",
      Nil,
      "GET",
      """ Routes
 This file defines all application routes (Higher priority routes first)
 ~~~~""",
      this.prefix + """"""
    )
  )

  // @LINE:6
  private[this] lazy val controllers_TS_handleRequest1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_TS_handleRequest1_invoker = createInvoker(
    TS_4.handleRequest,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.TS",
      "handleRequest",
      Nil,
      "POST",
      """""",
      this.prefix + """"""
    )
  )

  // @LINE:7
  private[this] lazy val controllers_MD_handleRequest2_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("md")))
  )
  private[this] lazy val controllers_MD_handleRequest2_invoker = createInvoker(
    MD_2.handleRequest,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MD",
      "handleRequest",
      Nil,
      "POST",
      """""",
      this.prefix + """md"""
    )
  )

  // @LINE:8
  private[this] lazy val controllers_EXE_handleRequest3_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("exe")))
  )
  private[this] lazy val controllers_EXE_handleRequest3_invoker = createInvoker(
    EXE_1.handleRequest,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.EXE",
      "handleRequest",
      Nil,
      "POST",
      """""",
      this.prefix + """exe"""
    )
  )

  // @LINE:9
  private[this] lazy val controllers_ANA_handleRequest4_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis")))
  )
  private[this] lazy val controllers_ANA_handleRequest4_invoker = createInvoker(
    ANA_0.handleRequest,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.ANA",
      "handleRequest",
      Nil,
      "POST",
      """""",
      this.prefix + """analysis"""
    )
  )

  // @LINE:11
  private[this] lazy val controllers_MCT_handleTagRequest5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ObjectSearch")))
  )
  private[this] lazy val controllers_MCT_handleTagRequest5_invoker = createInvoker(
    MCT_3.handleTagRequest(fakeValue[String], fakeValue[Option[String]]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MCT",
      "handleTagRequest",
      Seq(classOf[String], classOf[Option[String]]),
      "GET",
      """""",
      this.prefix + """ObjectSearch"""
    )
  )

  // @LINE:12
  private[this] lazy val controllers_MCT_extendedTagRequest6_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ObjectSearch")))
  )
  private[this] lazy val controllers_MCT_extendedTagRequest6_invoker = createInvoker(
    MCT_3.extendedTagRequest(fakeValue[String], fakeValue[Option[String]], fakeValue[Option[String]]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MCT",
      "extendedTagRequest",
      Seq(classOf[String], classOf[Option[String]], classOf[Option[String]]),
      "POST",
      """""",
      this.prefix + """ObjectSearch"""
    )
  )

  // @LINE:16
  private[this] lazy val controllers_MTSControl_executeCmd7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_MTSControl_executeCmd7_invoker = createInvoker(
    MTSControl_5.executeCmd(fakeValue[String], fakeValue[Option[String]]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MTSControl",
      "executeCmd",
      Seq(classOf[String], classOf[Option[String]]),
      "GET",
      """""",
      this.prefix + """control"""
    )
  )

  // @LINE:17
  private[this] lazy val controllers_MTSControl_executeExtendedCmd8_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_MTSControl_executeExtendedCmd8_invoker = createInvoker(
    MTSControl_5.executeExtendedCmd(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MTSControl",
      "executeExtendedCmd",
      Seq(classOf[String]),
      "POST",
      """""",
      this.prefix + """control"""
    )
  )

  // @LINE:19
  private[this] lazy val controllers_MTSControl_sr9_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("sr")))
  )
  private[this] lazy val controllers_MTSControl_sr9_invoker = createInvoker(
    MTSControl_5.sr(fakeValue[Option[String]]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MTSControl",
      "sr",
      Seq(classOf[Option[String]]),
      "GET",
      """""",
      this.prefix + """sr"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:4
    case controllers_MTSControl_index0_route(params) =>
      call { 
        controllers_MTSControl_index0_invoker.call(MTSControl_5.index)
      }
  
    // @LINE:6
    case controllers_TS_handleRequest1_route(params) =>
      call { 
        controllers_TS_handleRequest1_invoker.call(TS_4.handleRequest)
      }
  
    // @LINE:7
    case controllers_MD_handleRequest2_route(params) =>
      call { 
        controllers_MD_handleRequest2_invoker.call(MD_2.handleRequest)
      }
  
    // @LINE:8
    case controllers_EXE_handleRequest3_route(params) =>
      call { 
        controllers_EXE_handleRequest3_invoker.call(EXE_1.handleRequest)
      }
  
    // @LINE:9
    case controllers_ANA_handleRequest4_route(params) =>
      call { 
        controllers_ANA_handleRequest4_invoker.call(ANA_0.handleRequest)
      }
  
    // @LINE:11
    case controllers_MCT_handleTagRequest5_route(params) =>
      call(params.fromQuery[String]("LCID", None), params.fromQuery[Option[String]]("query", None)) { (LCID, query) =>
        controllers_MCT_handleTagRequest5_invoker.call(MCT_3.handleTagRequest(LCID, query))
      }
  
    // @LINE:12
    case controllers_MCT_extendedTagRequest6_route(params) =>
      call(params.fromQuery[String]("LCID", None), params.fromQuery[Option[String]]("indexname", None), params.fromQuery[Option[String]]("objecttype", None)) { (LCID, indexname, objecttype) =>
        controllers_MCT_extendedTagRequest6_invoker.call(MCT_3.extendedTagRequest(LCID, indexname, objecttype))
      }
  
    // @LINE:16
    case controllers_MTSControl_executeCmd7_route(params) =>
      call(params.fromQuery[String]("CMD", None), params.fromQuery[Option[String]]("PARAMETERS", None)) { (CMD, PARAMETERS) =>
        controllers_MTSControl_executeCmd7_invoker.call(MTSControl_5.executeCmd(CMD, PARAMETERS))
      }
  
    // @LINE:17
    case controllers_MTSControl_executeExtendedCmd8_route(params) =>
      call(params.fromQuery[String]("CMD", None)) { (CMD) =>
        controllers_MTSControl_executeExtendedCmd8_invoker.call(MTSControl_5.executeExtendedCmd(CMD))
      }
  
    // @LINE:19
    case controllers_MTSControl_sr9_route(params) =>
      call(params.fromQuery[Option[String]]("parameters", None)) { (parameters) =>
        controllers_MTSControl_sr9_invoker.call(MTSControl_5.sr(parameters))
      }
  }
}

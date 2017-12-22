
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/pman0003/Codebase/bda/saw-1335/saw/saw-services/saw-transport-service/conf/routes
// @DATE:Fri Dec 22 11:56:03 IST 2017

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
  MTSControl_6: controllers.MTSControl,
  // @LINE:6
  TS_4: controllers.TS,
  // @LINE:10
  MD_2: controllers.MD,
  // @LINE:14
  Analysis_1: controllers.Analysis,
  // @LINE:18
  AnalysisExecutions_5: controllers.AnalysisExecutions,
  // @LINE:27
  Semantic_0: controllers.Semantic,
  // @LINE:29
  MCT_3: controllers.MCT,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:4
    MTSControl_6: controllers.MTSControl,
    // @LINE:6
    TS_4: controllers.TS,
    // @LINE:10
    MD_2: controllers.MD,
    // @LINE:14
    Analysis_1: controllers.Analysis,
    // @LINE:18
    AnalysisExecutions_5: controllers.AnalysisExecutions,
    // @LINE:27
    Semantic_0: controllers.Semantic,
    // @LINE:29
    MCT_3: controllers.MCT
  ) = this(errorHandler, MTSControl_6, TS_4, MD_2, Analysis_1, AnalysisExecutions_5, Semantic_0, MCT_3, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, MTSControl_6, TS_4, MD_2, Analysis_1, AnalysisExecutions_5, Semantic_0, MCT_3, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.MTSControl.index"""),
    ("""POST""", this.prefix, """controllers.TS.handleRequest"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """md""", """controllers.MD.handleRequest"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis""", """controllers.Analysis.list(view:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis""", """controllers.Analysis.getMetadataByID(analysisId:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis""", """controllers.Analysis.process"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/executions""", """controllers.AnalysisExecutions.list(analysisId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/results""", """controllers.AnalysisExecutions.list(analysisId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/executions/""" + "$" + """executionId<[^/]+>/data""", """controllers.AnalysisExecutions.getExecutionData(analysisId:String, executionId:String, page:Int ?= 1, pageSize:Int ?= 10, analysisType:String ?= "report")"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/executions""", """controllers.AnalysisExecutions.execute(analysisId:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """semantic""", """controllers.Semantic.handleRequest"""),
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
    MTSControl_6.index,
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

  // @LINE:10
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

  // @LINE:14
  private[this] lazy val controllers_Analysis_list3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis")))
  )
  private[this] lazy val controllers_Analysis_list3_invoker = createInvoker(
    Analysis_1.list(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Analysis",
      "list",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """analysis"""
    )
  )

  // @LINE:15
  private[this] lazy val controllers_Analysis_getMetadataByID4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis")))
  )
  private[this] lazy val controllers_Analysis_getMetadataByID4_invoker = createInvoker(
    Analysis_1.getMetadataByID(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Analysis",
      "getMetadataByID",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """analysis"""
    )
  )

  // @LINE:16
  private[this] lazy val controllers_Analysis_process5_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis")))
  )
  private[this] lazy val controllers_Analysis_process5_invoker = createInvoker(
    Analysis_1.process,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Analysis",
      "process",
      Nil,
      "POST",
      """""",
      this.prefix + """analysis"""
    )
  )

  // @LINE:18
  private[this] lazy val controllers_AnalysisExecutions_list6_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/executions")))
  )
  private[this] lazy val controllers_AnalysisExecutions_list6_invoker = createInvoker(
    AnalysisExecutions_5.list(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AnalysisExecutions",
      "list",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """analysis/""" + "$" + """analysisId<[^/]+>/executions"""
    )
  )

  // @LINE:20
  private[this] lazy val controllers_AnalysisExecutions_list7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/results")))
  )
  private[this] lazy val controllers_AnalysisExecutions_list7_invoker = createInvoker(
    AnalysisExecutions_5.list(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AnalysisExecutions",
      "list",
      Seq(classOf[String]),
      "GET",
      """ Note: Keep for API backwards compatibility, remove in future""",
      this.prefix + """analysis/""" + "$" + """analysisId<[^/]+>/results"""
    )
  )

  // @LINE:21
  private[this] lazy val controllers_AnalysisExecutions_getExecutionData8_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/executions/"), DynamicPart("executionId", """[^/]+""",true), StaticPart("/data")))
  )
  private[this] lazy val controllers_AnalysisExecutions_getExecutionData8_invoker = createInvoker(
    AnalysisExecutions_5.getExecutionData(fakeValue[String], fakeValue[String], fakeValue[Int], fakeValue[Int], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AnalysisExecutions",
      "getExecutionData",
      Seq(classOf[String], classOf[String], classOf[Int], classOf[Int], classOf[String]),
      "GET",
      """""",
      this.prefix + """analysis/""" + "$" + """analysisId<[^/]+>/executions/""" + "$" + """executionId<[^/]+>/data"""
    )
  )

  // @LINE:23
  private[this] lazy val controllers_AnalysisExecutions_execute9_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/executions")))
  )
  private[this] lazy val controllers_AnalysisExecutions_execute9_invoker = createInvoker(
    AnalysisExecutions_5.execute(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AnalysisExecutions",
      "execute",
      Seq(classOf[String]),
      "POST",
      """""",
      this.prefix + """analysis/""" + "$" + """analysisId<[^/]+>/executions"""
    )
  )

  // @LINE:27
  private[this] lazy val controllers_Semantic_handleRequest10_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("semantic")))
  )
  private[this] lazy val controllers_Semantic_handleRequest10_invoker = createInvoker(
    Semantic_0.handleRequest,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Semantic",
      "handleRequest",
      Nil,
      "POST",
      """""",
      this.prefix + """semantic"""
    )
  )

  // @LINE:29
  private[this] lazy val controllers_MCT_handleTagRequest11_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ObjectSearch")))
  )
  private[this] lazy val controllers_MCT_handleTagRequest11_invoker = createInvoker(
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

  // @LINE:30
  private[this] lazy val controllers_MCT_extendedTagRequest12_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ObjectSearch")))
  )
  private[this] lazy val controllers_MCT_extendedTagRequest12_invoker = createInvoker(
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

  // @LINE:34
  private[this] lazy val controllers_MTSControl_executeCmd13_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_MTSControl_executeCmd13_invoker = createInvoker(
    MTSControl_6.executeCmd(fakeValue[String], fakeValue[Option[String]]),
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

  // @LINE:35
  private[this] lazy val controllers_MTSControl_executeExtendedCmd14_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_MTSControl_executeExtendedCmd14_invoker = createInvoker(
    MTSControl_6.executeExtendedCmd(fakeValue[String]),
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

  // @LINE:37
  private[this] lazy val controllers_MTSControl_sr15_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("sr")))
  )
  private[this] lazy val controllers_MTSControl_sr15_invoker = createInvoker(
    MTSControl_6.sr(fakeValue[Option[String]]),
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
        controllers_MTSControl_index0_invoker.call(MTSControl_6.index)
      }
  
    // @LINE:6
    case controllers_TS_handleRequest1_route(params) =>
      call { 
        controllers_TS_handleRequest1_invoker.call(TS_4.handleRequest)
      }
  
    // @LINE:10
    case controllers_MD_handleRequest2_route(params) =>
      call { 
        controllers_MD_handleRequest2_invoker.call(MD_2.handleRequest)
      }
  
    // @LINE:14
    case controllers_Analysis_list3_route(params) =>
      call(params.fromQuery[String]("view", None)) { (view) =>
        controllers_Analysis_list3_invoker.call(Analysis_1.list(view))
      }
  
    // @LINE:15
    case controllers_Analysis_getMetadataByID4_route(params) =>
      call(params.fromQuery[String]("analysisId", None)) { (analysisId) =>
        controllers_Analysis_getMetadataByID4_invoker.call(Analysis_1.getMetadataByID(analysisId))
      }
  
    // @LINE:16
    case controllers_Analysis_process5_route(params) =>
      call { 
        controllers_Analysis_process5_invoker.call(Analysis_1.process)
      }
  
    // @LINE:18
    case controllers_AnalysisExecutions_list6_route(params) =>
      call(params.fromPath[String]("analysisId", None)) { (analysisId) =>
        controllers_AnalysisExecutions_list6_invoker.call(AnalysisExecutions_5.list(analysisId))
      }
  
    // @LINE:20
    case controllers_AnalysisExecutions_list7_route(params) =>
      call(params.fromPath[String]("analysisId", None)) { (analysisId) =>
        controllers_AnalysisExecutions_list7_invoker.call(AnalysisExecutions_5.list(analysisId))
      }
  
    // @LINE:21
    case controllers_AnalysisExecutions_getExecutionData8_route(params) =>
      call(params.fromPath[String]("analysisId", None), params.fromPath[String]("executionId", None), params.fromQuery[Int]("page", Some(1)), params.fromQuery[Int]("pageSize", Some(10)), params.fromQuery[String]("analysisType", Some("report"))) { (analysisId, executionId, page, pageSize, analysisType) =>
        controllers_AnalysisExecutions_getExecutionData8_invoker.call(AnalysisExecutions_5.getExecutionData(analysisId, executionId, page, pageSize, analysisType))
      }
  
    // @LINE:23
    case controllers_AnalysisExecutions_execute9_route(params) =>
      call(params.fromPath[String]("analysisId", None)) { (analysisId) =>
        controllers_AnalysisExecutions_execute9_invoker.call(AnalysisExecutions_5.execute(analysisId))
      }
  
    // @LINE:27
    case controllers_Semantic_handleRequest10_route(params) =>
      call { 
        controllers_Semantic_handleRequest10_invoker.call(Semantic_0.handleRequest)
      }
  
    // @LINE:29
    case controllers_MCT_handleTagRequest11_route(params) =>
      call(params.fromQuery[String]("LCID", None), params.fromQuery[Option[String]]("query", None)) { (LCID, query) =>
        controllers_MCT_handleTagRequest11_invoker.call(MCT_3.handleTagRequest(LCID, query))
      }
  
    // @LINE:30
    case controllers_MCT_extendedTagRequest12_route(params) =>
      call(params.fromQuery[String]("LCID", None), params.fromQuery[Option[String]]("indexname", None), params.fromQuery[Option[String]]("objecttype", None)) { (LCID, indexname, objecttype) =>
        controllers_MCT_extendedTagRequest12_invoker.call(MCT_3.extendedTagRequest(LCID, indexname, objecttype))
      }
  
    // @LINE:34
    case controllers_MTSControl_executeCmd13_route(params) =>
      call(params.fromQuery[String]("CMD", None), params.fromQuery[Option[String]]("PARAMETERS", None)) { (CMD, PARAMETERS) =>
        controllers_MTSControl_executeCmd13_invoker.call(MTSControl_6.executeCmd(CMD, PARAMETERS))
      }
  
    // @LINE:35
    case controllers_MTSControl_executeExtendedCmd14_route(params) =>
      call(params.fromQuery[String]("CMD", None)) { (CMD) =>
        controllers_MTSControl_executeExtendedCmd14_invoker.call(MTSControl_6.executeExtendedCmd(CMD))
      }
  
    // @LINE:37
    case controllers_MTSControl_sr15_route(params) =>
      call(params.fromQuery[Option[String]]("parameters", None)) { (parameters) =>
        controllers_MTSControl_sr15_invoker.call(MTSControl_6.sr(parameters))
      }
  }
}

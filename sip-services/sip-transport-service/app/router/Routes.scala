
// @GENERATOR:play-routes-compiler
// @SOURCE:saw-services/saw-transport-service/conf/routes
// @DATE:Tue Oct 16 17:30:43 IST 2018

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
  MTSControl_9: controllers.MTSControl,
  // @LINE:6
  TS_7: controllers.TS,
  // @LINE:10
  MD_3: controllers.MD,
  // @LINE:12
  KPIBuilder_6: controllers.KPIBuilder,
  // @LINE:16
  Analysis_1: controllers.Analysis,
  // @LINE:18
  GlobalFilter_2: controllers.GlobalFilter,
  // @LINE:21
  AnalysisExecutions_8: controllers.AnalysisExecutions,
  // @LINE:30
  Semantic_0: controllers.Semantic,
  // @LINE:32
  MCT_5: controllers.MCT,
  // @LINE:44
  Actuator_4: controllers.Actuator,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:4
    MTSControl_9: controllers.MTSControl,
    // @LINE:6
    TS_7: controllers.TS,
    // @LINE:10
    MD_3: controllers.MD,
    // @LINE:12
    KPIBuilder_6: controllers.KPIBuilder,
    // @LINE:16
    Analysis_1: controllers.Analysis,
    // @LINE:18
    GlobalFilter_2: controllers.GlobalFilter,
    // @LINE:21
    AnalysisExecutions_8: controllers.AnalysisExecutions,
    // @LINE:30
    Semantic_0: controllers.Semantic,
    // @LINE:32
    MCT_5: controllers.MCT,
    // @LINE:44
    Actuator_4: controllers.Actuator
  ) = this(errorHandler, MTSControl_9, TS_7, MD_3, KPIBuilder_6, Analysis_1, GlobalFilter_2, AnalysisExecutions_8, Semantic_0, MCT_5, Actuator_4, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, MTSControl_9, TS_7, MD_3, KPIBuilder_6, Analysis_1, GlobalFilter_2, AnalysisExecutions_8, Semantic_0, MCT_5, Actuator_4, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.MTSControl.index"""),
    ("""POST""", this.prefix, """controllers.TS.handleRequest"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """md""", """controllers.MD.handleRequest"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """kpi""", """controllers.KPIBuilder.process"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis""", """controllers.Analysis.list(view:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/md""", """controllers.Analysis.getMetadataByID(analysisId:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """filters""", """controllers.GlobalFilter.process"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis""", """controllers.Analysis.process"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/executions""", """controllers.AnalysisExecutions.list(analysisId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/results""", """controllers.AnalysisExecutions.list(analysisId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/executions/""" + "$" + """executionId<[^/]+>/data""", """controllers.AnalysisExecutions.getExecutionData(analysisId:String, executionId:String, page:Int ?= 1, pageSize:Int ?= 10, analysisType:String ?= "report", executionType:String ?= null )"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/executions/data""", """controllers.AnalysisExecutions.getLatestExecutionData(analysisId:String, page:Int ?= 1, pageSize:Int ?= 10, analysisType:String ?= "report", executionType:String ?= null )"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """analysis/""" + "$" + """analysisId<[^/]+>/executions""", """controllers.AnalysisExecutions.execute(analysisId:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """semantic""", """controllers.Semantic.handleRequest"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """ObjectSearch""", """controllers.MCT.handleTagRequest(LCID:String, query:Option[String])"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """ObjectSearch""", """controllers.MCT.extendedTagRequest(LCID:String, indexname:Option[String], objecttype:Option[String])"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.MTSControl.executeCmd(CMD:String, PARAMETERS:Option[String])"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """control""", """controllers.MTSControl.executeExtendedCmd(CMD:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """sr""", """controllers.MTSControl.sr(parameters:Option[String])"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """actuator/health""", """controllers.Actuator.health"""),
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
    MTSControl_9.index,
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
    TS_7.handleRequest,
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
    MD_3.handleRequest,
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

  // @LINE:12
  private[this] lazy val controllers_KPIBuilder_process3_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("kpi")))
  )
  private[this] lazy val controllers_KPIBuilder_process3_invoker = createInvoker(
    KPIBuilder_6.process,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.KPIBuilder",
      "process",
      Nil,
      "POST",
      """""",
      this.prefix + """kpi"""
    )
  )

  // @LINE:16
  private[this] lazy val controllers_Analysis_list4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis")))
  )
  private[this] lazy val controllers_Analysis_list4_invoker = createInvoker(
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

  // @LINE:17
  private[this] lazy val controllers_Analysis_getMetadataByID5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/md")))
  )
  private[this] lazy val controllers_Analysis_getMetadataByID5_invoker = createInvoker(
    Analysis_1.getMetadataByID(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Analysis",
      "getMetadataByID",
      Seq(classOf[String]),
      "GET",
      """""",
      this.prefix + """analysis/md"""
    )
  )

  // @LINE:18
  private[this] lazy val controllers_GlobalFilter_process6_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("filters")))
  )
  private[this] lazy val controllers_GlobalFilter_process6_invoker = createInvoker(
    GlobalFilter_2.process,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.GlobalFilter",
      "process",
      Nil,
      "POST",
      """""",
      this.prefix + """filters"""
    )
  )

  // @LINE:19
  private[this] lazy val controllers_Analysis_process7_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis")))
  )
  private[this] lazy val controllers_Analysis_process7_invoker = createInvoker(
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

  // @LINE:21
  private[this] lazy val controllers_AnalysisExecutions_list8_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/executions")))
  )
  private[this] lazy val controllers_AnalysisExecutions_list8_invoker = createInvoker(
    AnalysisExecutions_8.list(fakeValue[String]),
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

  // @LINE:23
  private[this] lazy val controllers_AnalysisExecutions_list9_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/results")))
  )
  private[this] lazy val controllers_AnalysisExecutions_list9_invoker = createInvoker(
    AnalysisExecutions_8.list(fakeValue[String]),
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

  // @LINE:24
  private[this] lazy val controllers_AnalysisExecutions_getExecutionData10_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/executions/"), DynamicPart("executionId", """[^/]+""",true), StaticPart("/data")))
  )
  private[this] lazy val controllers_AnalysisExecutions_getExecutionData10_invoker = createInvoker(
    AnalysisExecutions_8.getExecutionData(fakeValue[String], fakeValue[String], fakeValue[Int], fakeValue[Int], fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AnalysisExecutions",
      "getExecutionData",
      Seq(classOf[String], classOf[String], classOf[Int], classOf[Int], classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """analysis/""" + "$" + """analysisId<[^/]+>/executions/""" + "$" + """executionId<[^/]+>/data"""
    )
  )

  // @LINE:25
  private[this] lazy val controllers_AnalysisExecutions_getLatestExecutionData11_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/executions/data")))
  )
  private[this] lazy val controllers_AnalysisExecutions_getLatestExecutionData11_invoker = createInvoker(
    AnalysisExecutions_8.getLatestExecutionData(fakeValue[String], fakeValue[Int], fakeValue[Int], fakeValue[String], fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AnalysisExecutions",
      "getLatestExecutionData",
      Seq(classOf[String], classOf[Int], classOf[Int], classOf[String], classOf[String]),
      "GET",
      """""",
      this.prefix + """analysis/""" + "$" + """analysisId<[^/]+>/executions/data"""
    )
  )

  // @LINE:26
  private[this] lazy val controllers_AnalysisExecutions_execute12_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("analysis/"), DynamicPart("analysisId", """[^/]+""",true), StaticPart("/executions")))
  )
  private[this] lazy val controllers_AnalysisExecutions_execute12_invoker = createInvoker(
    AnalysisExecutions_8.execute(fakeValue[String]),
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

  // @LINE:30
  private[this] lazy val controllers_Semantic_handleRequest13_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("semantic")))
  )
  private[this] lazy val controllers_Semantic_handleRequest13_invoker = createInvoker(
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

  // @LINE:32
  private[this] lazy val controllers_MCT_handleTagRequest14_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ObjectSearch")))
  )
  private[this] lazy val controllers_MCT_handleTagRequest14_invoker = createInvoker(
    MCT_5.handleTagRequest(fakeValue[String], fakeValue[Option[String]]),
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

  // @LINE:33
  private[this] lazy val controllers_MCT_extendedTagRequest15_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("ObjectSearch")))
  )
  private[this] lazy val controllers_MCT_extendedTagRequest15_invoker = createInvoker(
    MCT_5.extendedTagRequest(fakeValue[String], fakeValue[Option[String]], fakeValue[Option[String]]),
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

  // @LINE:37
  private[this] lazy val controllers_MTSControl_executeCmd16_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_MTSControl_executeCmd16_invoker = createInvoker(
    MTSControl_9.executeCmd(fakeValue[String], fakeValue[Option[String]]),
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

  // @LINE:38
  private[this] lazy val controllers_MTSControl_executeExtendedCmd17_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("control")))
  )
  private[this] lazy val controllers_MTSControl_executeExtendedCmd17_invoker = createInvoker(
    MTSControl_9.executeExtendedCmd(fakeValue[String]),
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

  // @LINE:40
  private[this] lazy val controllers_MTSControl_sr18_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("sr")))
  )
  private[this] lazy val controllers_MTSControl_sr18_invoker = createInvoker(
    MTSControl_9.sr(fakeValue[Option[String]]),
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

  // @LINE:44
  private[this] lazy val controllers_Actuator_health19_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("actuator/health")))
  )
  private[this] lazy val controllers_Actuator_health19_invoker = createInvoker(
    Actuator_4.health,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Actuator",
      "health",
      Nil,
      "GET",
      """""",
      this.prefix + """actuator/health"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:4
    case controllers_MTSControl_index0_route(params) =>
      call { 
        controllers_MTSControl_index0_invoker.call(MTSControl_9.index)
      }
  
    // @LINE:6
    case controllers_TS_handleRequest1_route(params) =>
      call { 
        controllers_TS_handleRequest1_invoker.call(TS_7.handleRequest)
      }
  
    // @LINE:10
    case controllers_MD_handleRequest2_route(params) =>
      call { 
        controllers_MD_handleRequest2_invoker.call(MD_3.handleRequest)
      }
  
    // @LINE:12
    case controllers_KPIBuilder_process3_route(params) =>
      call { 
        controllers_KPIBuilder_process3_invoker.call(KPIBuilder_6.process)
      }
  
    // @LINE:16
    case controllers_Analysis_list4_route(params) =>
      call(params.fromQuery[String]("view", None)) { (view) =>
        controllers_Analysis_list4_invoker.call(Analysis_1.list(view))
      }
  
    // @LINE:17
    case controllers_Analysis_getMetadataByID5_route(params) =>
      call(params.fromQuery[String]("analysisId", None)) { (analysisId) =>
        controllers_Analysis_getMetadataByID5_invoker.call(Analysis_1.getMetadataByID(analysisId))
      }
  
    // @LINE:18
    case controllers_GlobalFilter_process6_route(params) =>
      call { 
        controllers_GlobalFilter_process6_invoker.call(GlobalFilter_2.process)
      }
  
    // @LINE:19
    case controllers_Analysis_process7_route(params) =>
      call { 
        controllers_Analysis_process7_invoker.call(Analysis_1.process)
      }
  
    // @LINE:21
    case controllers_AnalysisExecutions_list8_route(params) =>
      call(params.fromPath[String]("analysisId", None)) { (analysisId) =>
        controllers_AnalysisExecutions_list8_invoker.call(AnalysisExecutions_8.list(analysisId))
      }
  
    // @LINE:23
    case controllers_AnalysisExecutions_list9_route(params) =>
      call(params.fromPath[String]("analysisId", None)) { (analysisId) =>
        controllers_AnalysisExecutions_list9_invoker.call(AnalysisExecutions_8.list(analysisId))
      }
  
    // @LINE:24
    case controllers_AnalysisExecutions_getExecutionData10_route(params) =>
      call(params.fromPath[String]("analysisId", None), params.fromPath[String]("executionId", None), params.fromQuery[Int]("page", Some(1)), params.fromQuery[Int]("pageSize", Some(10)), params.fromQuery[String]("analysisType", Some("report")), params.fromQuery[String]("executionType", Some(null ))) { (analysisId, executionId, page, pageSize, analysisType, executionType) =>
        controllers_AnalysisExecutions_getExecutionData10_invoker.call(AnalysisExecutions_8.getExecutionData(analysisId, executionId, page, pageSize, analysisType, executionType))
      }
  
    // @LINE:25
    case controllers_AnalysisExecutions_getLatestExecutionData11_route(params) =>
      call(params.fromPath[String]("analysisId", None), params.fromQuery[Int]("page", Some(1)), params.fromQuery[Int]("pageSize", Some(10)), params.fromQuery[String]("analysisType", Some("report")), params.fromQuery[String]("executionType", Some(null ))) { (analysisId, page, pageSize, analysisType, executionType) =>
        controllers_AnalysisExecutions_getLatestExecutionData11_invoker.call(AnalysisExecutions_8.getLatestExecutionData(analysisId, page, pageSize, analysisType, executionType))
      }
  
    // @LINE:26
    case controllers_AnalysisExecutions_execute12_route(params) =>
      call(params.fromPath[String]("analysisId", None)) { (analysisId) =>
        controllers_AnalysisExecutions_execute12_invoker.call(AnalysisExecutions_8.execute(analysisId))
      }
  
    // @LINE:30
    case controllers_Semantic_handleRequest13_route(params) =>
      call { 
        controllers_Semantic_handleRequest13_invoker.call(Semantic_0.handleRequest)
      }
  
    // @LINE:32
    case controllers_MCT_handleTagRequest14_route(params) =>
      call(params.fromQuery[String]("LCID", None), params.fromQuery[Option[String]]("query", None)) { (LCID, query) =>
        controllers_MCT_handleTagRequest14_invoker.call(MCT_5.handleTagRequest(LCID, query))
      }
  
    // @LINE:33
    case controllers_MCT_extendedTagRequest15_route(params) =>
      call(params.fromQuery[String]("LCID", None), params.fromQuery[Option[String]]("indexname", None), params.fromQuery[Option[String]]("objecttype", None)) { (LCID, indexname, objecttype) =>
        controllers_MCT_extendedTagRequest15_invoker.call(MCT_5.extendedTagRequest(LCID, indexname, objecttype))
      }
  
    // @LINE:37
    case controllers_MTSControl_executeCmd16_route(params) =>
      call(params.fromQuery[String]("CMD", None), params.fromQuery[Option[String]]("PARAMETERS", None)) { (CMD, PARAMETERS) =>
        controllers_MTSControl_executeCmd16_invoker.call(MTSControl_9.executeCmd(CMD, PARAMETERS))
      }
  
    // @LINE:38
    case controllers_MTSControl_executeExtendedCmd17_route(params) =>
      call(params.fromQuery[String]("CMD", None)) { (CMD) =>
        controllers_MTSControl_executeExtendedCmd17_invoker.call(MTSControl_9.executeExtendedCmd(CMD))
      }
  
    // @LINE:40
    case controllers_MTSControl_sr18_route(params) =>
      call(params.fromQuery[Option[String]]("parameters", None)) { (parameters) =>
        controllers_MTSControl_sr18_invoker.call(MTSControl_9.sr(parameters))
      }
  
    // @LINE:44
    case controllers_Actuator_health19_route(params) =>
      call { 
        controllers_Actuator_health19_invoker.call(Actuator_4.health)
      }
  }
}

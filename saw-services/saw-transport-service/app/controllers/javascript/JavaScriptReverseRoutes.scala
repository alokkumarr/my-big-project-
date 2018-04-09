
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/pman0003/Codebase/bda/resolveConflict/saw/saw-services/saw-transport-service/conf/routes
// @DATE:Tue Mar 27 17:30:28 IST 2018

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:16
  class ReverseAnalysis(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:19
    def process: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Analysis.process",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis"})
        }
      """
    )
  
    // @LINE:17
    def getMetadataByID: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Analysis.getMetadataByID",
      """
        function(analysisId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis/md" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("analysisId", analysisId0)])})
        }
      """
    )
  
    // @LINE:16
    def list: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Analysis.list",
      """
        function(view0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("view", view0)])})
        }
      """
    )
  
  }

  // @LINE:18
  class ReverseGlobalFilter(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:18
    def process: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.GlobalFilter.process",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "filters"})
        }
      """
    )
  
  }

  // @LINE:12
  class ReverseKPIBuilder(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:12
    def process: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.KPIBuilder.process",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "kpi"})
        }
      """
    )
  
  }

  // @LINE:30
  class ReverseSemantic(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:30
    def handleRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Semantic.handleRequest",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "semantic"})
        }
      """
    )
  
  }

  // @LINE:10
  class ReverseMD(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:10
    def handleRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MD.handleRequest",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "md"})
        }
      """
    )
  
  }

  // @LINE:32
  class ReverseMCT(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:33
    def extendedTagRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MCT.extendedTagRequest",
      """
        function(LCID0,indexname1,objecttype2) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "ObjectSearch" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("LCID", LCID0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("indexname", indexname1), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("objecttype", objecttype2)])})
        }
      """
    )
  
    // @LINE:32
    def handleTagRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MCT.handleTagRequest",
      """
        function(LCID0,query1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "ObjectSearch" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("LCID", LCID0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("query", query1)])})
        }
      """
    )
  
  }

  // @LINE:4
  class ReverseMTSControl(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:40
    def sr: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.sr",
      """
        function(parameters0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "sr" + _qS([(""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("parameters", parameters0)])})
        }
      """
    )
  
    // @LINE:37
    def executeCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.executeCmd",
      """
        function(CMD0,PARAMETERS1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("PARAMETERS", PARAMETERS1)])})
        }
      """
    )
  
    // @LINE:38
    def executeExtendedCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.executeExtendedCmd",
      """
        function(CMD0) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0)])})
        }
      """
    )
  
    // @LINE:4
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
  }

  // @LINE:21
  class ReverseAnalysisExecutions(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:26
    def execute: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AnalysisExecutions.execute",
      """
        function(analysisId0) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("analysisId", encodeURIComponent(analysisId0)) + "/executions"})
        }
      """
    )
  
    // @LINE:21
    def list: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AnalysisExecutions.list",
      """
        function(analysisId0) {
        
          if (true) {
            return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("analysisId", encodeURIComponent(analysisId0)) + "/executions"})
          }
        
        }
      """
    )
  
    // @LINE:24
    def getExecutionData: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AnalysisExecutions.getExecutionData",
      """
        function(analysisId0,executionId1,page2,pageSize3,analysisType4) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("analysisId", encodeURIComponent(analysisId0)) + "/executions/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("executionId", encodeURIComponent(executionId1)) + "/data" + _qS([(page2 == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("page", page2)), (pageSize3 == null ? null : (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("pageSize", pageSize3)), (analysisType4 == null ? null : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("analysisType", analysisType4))])})
        }
      """
    )
  
  }

  // @LINE:6
  class ReverseTS(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:6
    def handleRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.TS.handleRequest",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + """"})
        }
      """
    )
  
  }


}

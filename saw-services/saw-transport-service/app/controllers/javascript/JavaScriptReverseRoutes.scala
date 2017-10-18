
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/Shared/WORK/SAW-BE/saw-transport-service/conf/routes
// @DATE:Fri Aug 18 00:45:57 EDT 2017

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:14
  class ReverseAnalysis(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
    def process: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Analysis.process",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis"})
        }
      """
    )
  
    // @LINE:14
    def list: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Analysis.list",
      """
        function(view0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("view", view0)])})
        }
      """
    )
  
  }

  // @LINE:26
  class ReverseSemantic(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:26
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

  // @LINE:28
  class ReverseMCT(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:29
    def extendedTagRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MCT.extendedTagRequest",
      """
        function(LCID0,indexname1,objecttype2) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "ObjectSearch" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("LCID", LCID0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("indexname", indexname1), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("objecttype", objecttype2)])})
        }
      """
    )
  
    // @LINE:28
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

  
    // @LINE:36
    def sr: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.sr",
      """
        function(parameters0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "sr" + _qS([(""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("parameters", parameters0)])})
        }
      """
    )
  
    // @LINE:33
    def executeCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.executeCmd",
      """
        function(CMD0,PARAMETERS1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("PARAMETERS", PARAMETERS1)])})
        }
      """
    )
  
    // @LINE:34
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

  // @LINE:17
  class ReverseAnalysisExecutions(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:22
    def execute: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AnalysisExecutions.execute",
      """
        function(analysisId0) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("analysisId", encodeURIComponent(analysisId0)) + "/executions"})
        }
      """
    )
  
    // @LINE:17
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
  
    // @LINE:20
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

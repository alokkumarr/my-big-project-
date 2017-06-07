
// @GENERATOR:play-routes-compiler
// @SOURCE:saw-services/saw-transport-service/conf/routes
// @DATE:Tue Jun 06 15:58:00 EDT 2017

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

  
    // @LINE:14
    def process: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Analysis.process",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis"})
        }
      """
    )
  
  }

  // @LINE:22
  class ReverseSemantic(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:22
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

  // @LINE:24
  class ReverseMCT(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:25
    def extendedTagRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MCT.extendedTagRequest",
      """
        function(LCID0,indexname1,objecttype2) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "ObjectSearch" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("LCID", LCID0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("indexname", indexname1), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("objecttype", objecttype2)])})
        }
      """
    )
  
    // @LINE:24
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

  
    // @LINE:32
    def sr: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.sr",
      """
        function(parameters0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "sr" + _qS([(""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("parameters", parameters0)])})
        }
      """
    )
  
    // @LINE:29
    def executeCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.executeCmd",
      """
        function(CMD0,PARAMETERS1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("PARAMETERS", PARAMETERS1)])})
        }
      """
    )
  
    // @LINE:30
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

  // @LINE:15
  class ReverseAnalysisExecutions(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
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
  
    // @LINE:18
    def getExecutionData: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AnalysisExecutions.getExecutionData",
      """
        function(analysisId0,executionId1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "analysis/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("analysisId", encodeURIComponent(analysisId0)) + "/executions/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("executionId", encodeURIComponent(executionId1)) + "/data"})
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

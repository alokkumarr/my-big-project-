
// @GENERATOR:play-routes-compiler
// @SOURCE:saw-services/saw-transport-service/conf/routes
// @DATE:Tue Jun 06 15:58:00 EDT 2017

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers {

  // @LINE:14
  class ReverseAnalysis(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:14
    def process(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "analysis")
    }
  
  }

  // @LINE:22
  class ReverseSemantic(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:22
    def handleRequest(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "semantic")
    }
  
  }

  // @LINE:10
  class ReverseMD(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:10
    def handleRequest(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "md")
    }
  
  }

  // @LINE:24
  class ReverseMCT(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:25
    def extendedTagRequest(LCID:String, indexname:Option[String], objecttype:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "ObjectSearch" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("LCID", LCID)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("indexname", indexname)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("objecttype", objecttype)))))
    }
  
    // @LINE:24
    def handleTagRequest(LCID:String, query:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "ObjectSearch" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("LCID", LCID)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("query", query)))))
    }
  
  }

  // @LINE:4
  class ReverseMTSControl(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:32
    def sr(parameters:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "sr" + queryString(List(Some(implicitly[QueryStringBindable[Option[String]]].unbind("parameters", parameters)))))
    }
  
    // @LINE:29
    def executeCmd(CMD:String, PARAMETERS:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("PARAMETERS", PARAMETERS)))))
    }
  
    // @LINE:30
    def executeExtendedCmd(CMD:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)))))
    }
  
    // @LINE:4
    def index(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix)
    }
  
  }

  // @LINE:15
  class ReverseAnalysisExecutions(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
    def list(analysisId:String): Call = {
    
      (analysisId: @unchecked) match {
      
        // @LINE:15
        case (analysisId)  =>
          import ReverseRouteContext.empty
          Call("GET", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions")
      
      }
    
    }
  
    // @LINE:18
    def getExecutionData(analysisId:String, executionId:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions/" + implicitly[PathBindable[String]].unbind("executionId", dynamicString(executionId)) + "/data")
    }
  
  }

  // @LINE:6
  class ReverseTS(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:6
    def handleRequest(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix)
    }
  
  }


}

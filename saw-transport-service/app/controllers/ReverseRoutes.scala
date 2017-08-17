
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/Shared/WORK/SAW-BE/saw-transport-service/conf/routes
// @DATE:Thu Aug 17 11:45:50 EDT 2017

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

  
    // @LINE:15
    def process(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "analysis")
    }
  
    // @LINE:14
    def list(view:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "analysis" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("view", view)))))
    }
  
  }

  // @LINE:26
  class ReverseSemantic(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:26
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

  // @LINE:28
  class ReverseMCT(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:29
    def extendedTagRequest(LCID:String, indexname:Option[String], objecttype:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "ObjectSearch" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("LCID", LCID)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("indexname", indexname)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("objecttype", objecttype)))))
    }
  
    // @LINE:28
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

  
    // @LINE:36
    def sr(parameters:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "sr" + queryString(List(Some(implicitly[QueryStringBindable[Option[String]]].unbind("parameters", parameters)))))
    }
  
    // @LINE:33
    def executeCmd(CMD:String, PARAMETERS:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("PARAMETERS", PARAMETERS)))))
    }
  
    // @LINE:34
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

  // @LINE:17
  class ReverseAnalysisExecutions(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:22
    def execute(analysisId:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions")
    }
  
    // @LINE:17
    def list(analysisId:String): Call = {
    
      (analysisId: @unchecked) match {
      
        // @LINE:17
        case (analysisId)  =>
          import ReverseRouteContext.empty
          Call("GET", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions")
      
      }
    
    }
  
    // @LINE:20
    def getExecutionData(analysisId:String, executionId:String, start:Int = 0, limit:Int = 10, analysisType:String = "report"): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions/" + implicitly[PathBindable[String]].unbind("executionId", dynamicString(executionId)) + "/data" + queryString(List(if(start == 0) None else Some(implicitly[QueryStringBindable[Int]].unbind("start", start)), if(limit == 10) None else Some(implicitly[QueryStringBindable[Int]].unbind("limit", limit)), if(analysisType == "report") None else Some(implicitly[QueryStringBindable[String]].unbind("analysisType", analysisType)))))
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

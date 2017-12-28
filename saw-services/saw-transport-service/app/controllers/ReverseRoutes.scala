
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/pman0003/Codebase/bda/saw-1335/saw/saw-services/saw-transport-service/conf/routes
// @DATE:Sat Dec 23 13:01:38 IST 2017

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

  
    // @LINE:16
    def process(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "analysis")
    }
  
    // @LINE:15
    def getMetadataByID(analysisId:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "analysis/md" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("analysisId", analysisId)))))
    }
  
    // @LINE:14
    def list(view:String): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "analysis" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("view", view)))))
    }
  
  }

  // @LINE:27
  class ReverseSemantic(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:27
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

  // @LINE:29
  class ReverseMCT(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:30
    def extendedTagRequest(LCID:String, indexname:Option[String], objecttype:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "ObjectSearch" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("LCID", LCID)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("indexname", indexname)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("objecttype", objecttype)))))
    }
  
    // @LINE:29
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

  
    // @LINE:37
    def sr(parameters:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "sr" + queryString(List(Some(implicitly[QueryStringBindable[Option[String]]].unbind("parameters", parameters)))))
    }
  
    // @LINE:34
    def executeCmd(CMD:String, PARAMETERS:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("PARAMETERS", PARAMETERS)))))
    }
  
    // @LINE:35
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

  // @LINE:18
  class ReverseAnalysisExecutions(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:23
    def execute(analysisId:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions")
    }
  
    // @LINE:18
    def list(analysisId:String): Call = {
    
      (analysisId: @unchecked) match {
      
        // @LINE:18
        case (analysisId)  =>
          import ReverseRouteContext.empty
          Call("GET", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions")
      
      }
    
    }
  
    // @LINE:21
    def getExecutionData(analysisId:String, executionId:String, page:Int = 1, pageSize:Int = 10, analysisType:String = "report"): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "analysis/" + implicitly[PathBindable[String]].unbind("analysisId", dynamicString(analysisId)) + "/executions/" + implicitly[PathBindable[String]].unbind("executionId", dynamicString(executionId)) + "/data" + queryString(List(if(page == 1) None else Some(implicitly[QueryStringBindable[Int]].unbind("page", page)), if(pageSize == 10) None else Some(implicitly[QueryStringBindable[Int]].unbind("pageSize", pageSize)), if(analysisType == "report") None else Some(implicitly[QueryStringBindable[String]].unbind("analysisType", analysisType)))))
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

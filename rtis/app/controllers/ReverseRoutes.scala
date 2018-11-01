
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Fri Oct 19 16:37:52 EDT 2018

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers {

  // @LINE:23
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:23
    def at(file:String): Call = {
      implicit val _rrc = new ReverseRouteContext(Map(("path", "/public/swagger-ui")))
      Call("GET", _prefix + { _defaultPrefix } + "docs/" + implicitly[PathBindable[String]].unbind("file", file))
    }
  
  }

  // @LINE:30
  class ReverseRTISControl(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:32
    def sr(parameters:Option[String]): Call = {
    
      (parameters: @unchecked) match {
      
        // @LINE:32
        case (parameters)  =>
          import ReverseRouteContext.empty
          Call("GET", _prefix + { _defaultPrefix } + "sr" + queryString(List(Some(implicitly[QueryStringBindable[Option[String]]].unbind("parameters", parameters)))))
      
      }
    
    }
  
    // @LINE:30
    def executeCmd(CMD:String, PARAMETERS:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("PARAMETERS", PARAMETERS)))))
    }
  
    // @LINE:31
    def executeExtendedCmd(CMD:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)))))
    }
  
  }

  // @LINE:39
  class ReverseGenericLog(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:39
    def doPost(CID:String, LOG_TYPE:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "genericlog" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CID", CID)), Some(implicitly[QueryStringBindable[String]].unbind("LOG_TYPE", LOG_TYPE)))))
    }
  
  }

  // @LINE:14
  class ReverseGenericHandler(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:14
    def event(APP_KEY:String, APP_VERSION:String, APP_MODULE:String, EVENT_ID:String, EVENT_DATE:String, EVENT_TYPE:Option[String]): Call = {
    
      (APP_KEY: @unchecked, APP_VERSION: @unchecked, APP_MODULE: @unchecked, EVENT_ID: @unchecked, EVENT_DATE: @unchecked, EVENT_TYPE: @unchecked) match {
      
        // @LINE:14
        case (APP_KEY, APP_VERSION, APP_MODULE, EVENT_ID, EVENT_DATE, EVENT_TYPE)  =>
          import ReverseRouteContext.empty
          Call("POST", _prefix + { _defaultPrefix } + "publishevent" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("APP_KEY", APP_KEY)), Some(implicitly[QueryStringBindable[String]].unbind("APP_VERSION", APP_VERSION)), Some(implicitly[QueryStringBindable[String]].unbind("APP_MODULE", APP_MODULE)), Some(implicitly[QueryStringBindable[String]].unbind("EVENT_ID", EVENT_ID)), Some(implicitly[QueryStringBindable[String]].unbind("EVENT_DATE", EVENT_DATE)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("EVENT_TYPE", EVENT_TYPE)))))
      
      }
    
    }
  
  }

  // @LINE:20
  class ReverseApiHelpController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:22
    def viewSwaggerUI(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "docs")
    }
  
    // @LINE:20
    def getResources(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "api-docs")
    }
  
  }

  // @LINE:4
  class ReverseApplication(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:6
    def iPost(): Call = {
    
      () match {
      
        // @LINE:6
        case ()  =>
          import ReverseRouteContext.empty
          Call("POST", _prefix + { _defaultPrefix } + "i")
      
      }
    
    }
  
    // @LINE:5
    def i(): Call = {
    
      () match {
      
        // @LINE:5
        case ()  =>
          import ReverseRouteContext.empty
          Call("GET", _prefix + { _defaultPrefix } + "i")
      
      }
    
    }
  
    // @LINE:4
    def index(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix)
    }
  
  }


}

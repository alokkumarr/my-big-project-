
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Wed Dec 06 11:51:19 EST 2017

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers {

  // @LINE:16
  class ReverseRTISControl(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:18
    def sr(parameters:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "sr" + queryString(List(Some(implicitly[QueryStringBindable[Option[String]]].unbind("parameters", parameters)))))
    }
  
    // @LINE:16
    def executeCmd(CMD:String, PARAMETERS:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("PARAMETERS", PARAMETERS)))))
    }
  
    // @LINE:17
    def executeExtendedCmd(CMD:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)))))
    }
  
  }

  // @LINE:22
  class ReverseGenericLog(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:22
    def doPost(CID:String, LOG_TYPE:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "genericlog" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CID", CID)), Some(implicitly[QueryStringBindable[String]].unbind("LOG_TYPE", LOG_TYPE)))))
    }
  
  }

  // @LINE:9
  class ReverseGenericHandler(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def event(APP_KEY:String, APP_VERSION:String, APP_MODULE:String, EVENT_ID:String, EVENT_DATE:String, EVENT_TYPE:Option[String], payload:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "publishevent" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("APP_KEY", APP_KEY)), Some(implicitly[QueryStringBindable[String]].unbind("APP_VERSION", APP_VERSION)), Some(implicitly[QueryStringBindable[String]].unbind("APP_MODULE", APP_MODULE)), Some(implicitly[QueryStringBindable[String]].unbind("EVENT_ID", EVENT_ID)), Some(implicitly[QueryStringBindable[String]].unbind("EVENT_DATE", EVENT_DATE)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("EVENT_TYPE", EVENT_TYPE)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("payload", payload)))))
    }
  
  }

  // @LINE:12
  class ReverseApiHelpController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:12
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
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "i")
    }
  
    // @LINE:5
    def i(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "i")
    }
  
    // @LINE:4
    def index(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix)
    }
  
  }


}

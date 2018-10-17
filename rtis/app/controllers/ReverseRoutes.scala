
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/nareshgangishetty/swagger-work/sip/rtis/conf/routes
// @DATE:Wed Oct 03 15:10:59 EDT 2018

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers {

  // @LINE:15
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
    def at(file:String): Call = {
      implicit val _rrc = new ReverseRouteContext(Map(("path", "/public/swagger-ui")))
      Call("GET", _prefix + { _defaultPrefix } + "docs/" + implicitly[PathBindable[String]].unbind("file", file))
    }
  
  }

  // @LINE:22
  class ReverseRTISControl(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:24
    def sr(parameters:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "sr" + queryString(List(Some(implicitly[QueryStringBindable[Option[String]]].unbind("parameters", parameters)))))
    }
  
    // @LINE:22
    def executeCmd(CMD:String, PARAMETERS:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("PARAMETERS", PARAMETERS)))))
    }
  
    // @LINE:23
    def executeExtendedCmd(CMD:String): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)))))
    }
  
  }

  // @LINE:28
  class ReverseGenericLog(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:28
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

  
    // @LINE:14
    def viewSwaggerUI(): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "docs")
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

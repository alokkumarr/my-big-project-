
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/markus/saw-services/saw-transport-service/conf/routes
// @DATE:Thu Mar 23 01:49:42 EDT 2017

import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers {

  // @LINE:8
  class ReverseANA(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:8
    def handleRequest(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "analysis")
    }
  
  }

  // @LINE:7
  class ReverseMD(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def handleRequest(): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "md")
    }
  
  }

  // @LINE:10
  class ReverseMCT(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:11
    def extendedTagRequest(LCID:String, indexname:Option[String], objecttype:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("POST", _prefix + { _defaultPrefix } + "ObjectSearch" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("LCID", LCID)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("indexname", indexname)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("objecttype", objecttype)))))
    }
  
    // @LINE:10
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

  
    // @LINE:18
    def sr(parameters:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "sr" + queryString(List(Some(implicitly[QueryStringBindable[Option[String]]].unbind("parameters", parameters)))))
    }
  
    // @LINE:15
    def executeCmd(CMD:String, PARAMETERS:Option[String]): Call = {
      import ReverseRouteContext.empty
      Call("GET", _prefix + { _defaultPrefix } + "control" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("CMD", CMD)), Some(implicitly[QueryStringBindable[Option[String]]].unbind("PARAMETERS", PARAMETERS)))))
    }
  
    // @LINE:16
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

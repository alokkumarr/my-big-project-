
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/SAW/saw-services/saw-transport-service/conf/routes
// @DATE:Wed Mar 01 11:53:47 EST 2017

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:8
  class ReverseEXE(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:8
    def handleRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.EXE.handleRequest",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "exe"})
        }
      """
    )
  
  }

  // @LINE:7
  class ReverseMD(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def handleRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MD.handleRequest",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "md"})
        }
      """
    )
  
  }

  // @LINE:10
  class ReverseMCT(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:11
    def extendedTagRequest: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MCT.extendedTagRequest",
      """
        function(LCID0,indexname1,objecttype2) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "ObjectSearch" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("LCID", LCID0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("indexname", indexname1), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("objecttype", objecttype2)])})
        }
      """
    )
  
    // @LINE:10
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

  
    // @LINE:18
    def sr: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.sr",
      """
        function(parameters0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "sr" + _qS([(""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("parameters", parameters0)])})
        }
      """
    )
  
    // @LINE:15
    def executeCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MTSControl.executeCmd",
      """
        function(CMD0,PARAMETERS1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("PARAMETERS", PARAMETERS1)])})
        }
      """
    )
  
    // @LINE:16
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

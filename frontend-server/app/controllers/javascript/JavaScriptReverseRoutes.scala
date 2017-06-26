
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/real-time-analytics/frontend-server/conf/routes
// @DATE:Thu Oct 20 10:21:37 EDT 2016

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:12
  class ReverseApiHelpController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:12
    def getResources: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.ApiHelpController.getResources",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api-docs"})
        }
      """
    )
  
  }

  // @LINE:9
  class ReverseGenericHandler(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def event: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.GenericHandler.event",
      """
        function(APP_KEY0,APP_VERSION1,APP_MODULE2,EVENT_ID3,EVENT_DATE4,EVENT_TYPE5,payload6) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "publishevent" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("APP_KEY", APP_KEY0), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("APP_VERSION", APP_VERSION1), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("APP_MODULE", APP_MODULE2), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("EVENT_ID", EVENT_ID3), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("EVENT_DATE", EVENT_DATE4), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("EVENT_TYPE", EVENT_TYPE5), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("payload", payload6)])})
        }
      """
    )
  
  }

  // @LINE:16
  class ReverseRTISControl(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:18
    def sr: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.RTISControl.sr",
      """
        function(parameters0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "sr" + _qS([(""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("parameters", parameters0)])})
        }
      """
    )
  
    // @LINE:16
    def executeCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.RTISControl.executeCmd",
      """
        function(CMD0,PARAMETERS1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("PARAMETERS", PARAMETERS1)])})
        }
      """
    )
  
    // @LINE:17
    def executeExtendedCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.RTISControl.executeExtendedCmd",
      """
        function(CMD0) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0)])})
        }
      """
    )
  
  }

  // @LINE:4
  class ReverseApplication(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:6
    def iPost: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.iPost",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "i"})
        }
      """
    )
  
    // @LINE:5
    def i: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.i",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "i"})
        }
      """
    )
  
    // @LINE:4
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
  }


}

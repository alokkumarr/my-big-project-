
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Fri Oct 19 16:37:52 EDT 2018

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:4
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:23
  class ReverseAssets(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:23
    def at: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Assets.at",
      """
        function(file1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "docs/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file1)})
        }
      """
    )
  
  }

  // @LINE:30
  class ReverseRTISControl(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:32
    def sr: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.RTISControl.sr",
      """
        function(parameters0) {
        
          if (true) {
            return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "sr" + _qS([(""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("parameters", parameters0)])})
          }
        
        }
      """
    )
  
    // @LINE:30
    def executeCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.RTISControl.executeCmd",
      """
        function(CMD0,PARAMETERS1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("PARAMETERS", PARAMETERS1)])})
        }
      """
    )
  
    // @LINE:31
    def executeExtendedCmd: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.RTISControl.executeExtendedCmd",
      """
        function(CMD0) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "control" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CMD", CMD0)])})
        }
      """
    )
  
  }

  // @LINE:39
  class ReverseGenericLog(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:39
    def doPost: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.GenericLog.doPost",
      """
        function(CID0,LOG_TYPE1) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "genericlog" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("CID", CID0), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("LOG_TYPE", LOG_TYPE1)])})
        }
      """
    )
  
  }

  // @LINE:14
  class ReverseGenericHandler(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:14
    def event: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.GenericHandler.event",
      """
        function(APP_KEY0,APP_VERSION1,APP_MODULE2,EVENT_ID3,EVENT_DATE4,EVENT_TYPE5) {
        
          if (true) {
            return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "publishevent" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("APP_KEY", APP_KEY0), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("APP_VERSION", APP_VERSION1), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("APP_MODULE", APP_MODULE2), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("EVENT_ID", EVENT_ID3), (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("EVENT_DATE", EVENT_DATE4), (""" + implicitly[QueryStringBindable[Option[String]]].javascriptUnbind + """)("EVENT_TYPE", EVENT_TYPE5)])})
          }
        
        }
      """
    )
  
  }

  // @LINE:20
  class ReverseApiHelpController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:22
    def viewSwaggerUI: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.ApiHelpController.viewSwaggerUI",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "docs"})
        }
      """
    )
  
    // @LINE:20
    def getResources: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.ApiHelpController.getResources",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api-docs"})
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
        
          if (true) {
            return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "i"})
          }
        
        }
      """
    )
  
    // @LINE:5
    def i: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.i",
      """
        function() {
        
          if (true) {
            return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "i"})
          }
        
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

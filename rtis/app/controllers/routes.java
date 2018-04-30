
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Wed Dec 06 11:51:19 EST 2017

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseRTISControl RTISControl = new controllers.ReverseRTISControl(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseGenericLog GenericLog = new controllers.ReverseGenericLog(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseGenericHandler GenericHandler = new controllers.ReverseGenericHandler(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseApiHelpController ApiHelpController = new controllers.ReverseApiHelpController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseApplication Application = new controllers.ReverseApplication(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseRTISControl RTISControl = new controllers.javascript.ReverseRTISControl(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseGenericLog GenericLog = new controllers.javascript.ReverseGenericLog(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseGenericHandler GenericHandler = new controllers.javascript.ReverseGenericHandler(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseApiHelpController ApiHelpController = new controllers.javascript.ReverseApiHelpController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseApplication Application = new controllers.javascript.ReverseApplication(RoutesPrefix.byNamePrefix());
  }

}

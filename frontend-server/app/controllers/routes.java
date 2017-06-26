
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/real-time-analytics/frontend-server/conf/routes
// @DATE:Thu Oct 20 10:21:37 EDT 2016

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseApiHelpController ApiHelpController = new controllers.ReverseApiHelpController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseGenericHandler GenericHandler = new controllers.ReverseGenericHandler(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseRTISControl RTISControl = new controllers.ReverseRTISControl(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseApplication Application = new controllers.ReverseApplication(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseApiHelpController ApiHelpController = new controllers.javascript.ReverseApiHelpController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseGenericHandler GenericHandler = new controllers.javascript.ReverseGenericHandler(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseRTISControl RTISControl = new controllers.javascript.ReverseRTISControl(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseApplication Application = new controllers.javascript.ReverseApplication(RoutesPrefix.byNamePrefix());
  }

}

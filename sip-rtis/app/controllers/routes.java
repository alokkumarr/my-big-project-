
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/RTIS-logstash/frontend-server/conf/routes
// @DATE:Fri Oct 19 16:37:52 EDT 2018

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseRTISControl RTISControl = new controllers.ReverseRTISControl(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseGenericLog GenericLog = new controllers.ReverseGenericLog(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseGenericHandler GenericHandler = new controllers.ReverseGenericHandler(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseApiHelpController ApiHelpController = new controllers.ReverseApiHelpController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseApplication Application = new controllers.ReverseApplication(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseRTISControl RTISControl = new controllers.javascript.ReverseRTISControl(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseGenericLog GenericLog = new controllers.javascript.ReverseGenericLog(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseGenericHandler GenericHandler = new controllers.javascript.ReverseGenericHandler(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseApiHelpController ApiHelpController = new controllers.javascript.ReverseApiHelpController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseApplication Application = new controllers.javascript.ReverseApplication(RoutesPrefix.byNamePrefix());
  }

}

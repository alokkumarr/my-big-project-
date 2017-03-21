
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/markus/saw-services/saw-transport-service/conf/routes
// @DATE:Tue Mar 21 06:52:20 EDT 2017

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseANA ANA = new controllers.ReverseANA(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseEXE EXE = new controllers.ReverseEXE(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMD MD = new controllers.ReverseMD(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMCT MCT = new controllers.ReverseMCT(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMTSControl MTSControl = new controllers.ReverseMTSControl(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseTS TS = new controllers.ReverseTS(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseANA ANA = new controllers.javascript.ReverseANA(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseEXE EXE = new controllers.javascript.ReverseEXE(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMD MD = new controllers.javascript.ReverseMD(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMCT MCT = new controllers.javascript.ReverseMCT(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMTSControl MTSControl = new controllers.javascript.ReverseMTSControl(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseTS TS = new controllers.javascript.ReverseTS(RoutesPrefix.byNamePrefix());
  }

}

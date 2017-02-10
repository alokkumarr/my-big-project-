
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/projects/BDA/bda-middle-tier/bda-transport-service/conf/routes
// @DATE:Thu Feb 02 14:24:46 EST 2017

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseMTSControl MTSControl = new controllers.ReverseMTSControl(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseTS TS = new controllers.ReverseTS(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseMTSControl MTSControl = new controllers.javascript.ReverseMTSControl(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseTS TS = new controllers.javascript.ReverseTS(RoutesPrefix.byNamePrefix());
  }

}

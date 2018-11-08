
// @GENERATOR:play-routes-compiler
// @SOURCE:saw-services/saw-transport-service/conf/routes
// @DATE:Tue Oct 16 17:30:43 IST 2018

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseAnalysis Analysis = new controllers.ReverseAnalysis(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseActuator Actuator = new controllers.ReverseActuator(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseGlobalFilter GlobalFilter = new controllers.ReverseGlobalFilter(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseKPIBuilder KPIBuilder = new controllers.ReverseKPIBuilder(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseSemantic Semantic = new controllers.ReverseSemantic(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMD MD = new controllers.ReverseMD(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMCT MCT = new controllers.ReverseMCT(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMTSControl MTSControl = new controllers.ReverseMTSControl(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAnalysisExecutions AnalysisExecutions = new controllers.ReverseAnalysisExecutions(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseTS TS = new controllers.ReverseTS(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseAnalysis Analysis = new controllers.javascript.ReverseAnalysis(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseActuator Actuator = new controllers.javascript.ReverseActuator(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseGlobalFilter GlobalFilter = new controllers.javascript.ReverseGlobalFilter(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseKPIBuilder KPIBuilder = new controllers.javascript.ReverseKPIBuilder(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseSemantic Semantic = new controllers.javascript.ReverseSemantic(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMD MD = new controllers.javascript.ReverseMD(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMCT MCT = new controllers.javascript.ReverseMCT(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMTSControl MTSControl = new controllers.javascript.ReverseMTSControl(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAnalysisExecutions AnalysisExecutions = new controllers.javascript.ReverseAnalysisExecutions(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseTS TS = new controllers.javascript.ReverseTS(RoutesPrefix.byNamePrefix());
  }

}

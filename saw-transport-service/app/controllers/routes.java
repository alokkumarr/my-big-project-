
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/Shared/WORK/SAW-BE/saw-transport-service/conf/routes
// @DATE:Fri Aug 18 00:45:57 EDT 2017

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseAnalysis Analysis = new controllers.ReverseAnalysis(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseSemantic Semantic = new controllers.ReverseSemantic(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMD MD = new controllers.ReverseMD(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMCT MCT = new controllers.ReverseMCT(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseMTSControl MTSControl = new controllers.ReverseMTSControl(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAnalysisExecutions AnalysisExecutions = new controllers.ReverseAnalysisExecutions(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseTS TS = new controllers.ReverseTS(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseAnalysis Analysis = new controllers.javascript.ReverseAnalysis(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseSemantic Semantic = new controllers.javascript.ReverseSemantic(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMD MD = new controllers.javascript.ReverseMD(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMCT MCT = new controllers.javascript.ReverseMCT(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseMTSControl MTSControl = new controllers.javascript.ReverseMTSControl(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAnalysisExecutions AnalysisExecutions = new controllers.javascript.ReverseAnalysisExecutions(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseTS TS = new controllers.javascript.ReverseTS(RoutesPrefix.byNamePrefix());
  }

}

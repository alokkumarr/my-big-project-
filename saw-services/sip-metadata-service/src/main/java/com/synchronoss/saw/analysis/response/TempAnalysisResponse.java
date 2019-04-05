package com.synchronoss.saw.analysis.response;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TempAnalysisResponse {
  private String analysisId;
  private ObjectNode analysis;
  private String message;

  public ObjectNode getAnalysis() {
    return analysis;
  }

  public void setAnalysis(ObjectNode analysis) {
    this.analysis = analysis;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getAnalysisId() {
    return analysisId;
  }

  public void setAnalysisId(String analysisId) {
    this.analysisId = analysisId;
  }
}

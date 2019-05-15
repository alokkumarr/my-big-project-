package com.synchronoss.saw.analysis.response;

import com.synchronoss.saw.analysis.modal.Analysis;

public class AnalysisResponse {

  private String analysisId;
  private Analysis analysis;
  private String message;

  public Analysis getAnalysis() {
    return analysis;
  }

  public void setAnalysis(Analysis analysis) {
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

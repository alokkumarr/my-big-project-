package com.synchronoss.saw.analysis.modal;

import org.codehaus.jackson.annotate.JsonProperty;

public class AnalysisPrivileges {

  @JsonProperty("_id")
  private String analysisId;

  @JsonProperty("category")
  private String category;

  @JsonProperty("accessPermission")
  private boolean accessPermission;

  @JsonProperty("executePermission")
  private boolean executePermission;

  @JsonProperty("message")
  private String message;

  public String getAnalysisId() {
    return analysisId;
  }

  public void setAnalysisId(String analysisId) {
    this.analysisId = analysisId;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public boolean isAccessPermission() {
    return accessPermission;
  }

  public void setAccessPermission(boolean accessPermission) {
    this.accessPermission = accessPermission;
  }

  public boolean isExecutePermission() {
    return executePermission;
  }

  public void setExecutePermission(boolean executePermission) {
    this.executePermission = executePermission;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

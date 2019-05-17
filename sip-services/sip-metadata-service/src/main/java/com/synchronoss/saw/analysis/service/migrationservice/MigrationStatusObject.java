package com.synchronoss.saw.analysis.service.migrationservice;

/** This Bean is used to write individual Analysis definition migration status to maprDB. */
public class MigrationStatusObject {
  String analysisId;
  String type;
  boolean isAnalysisMigrated;
  boolean isExecutionsMigrated;
  String message;

  public String getAnalysisId() {
    return analysisId;
  }

  public void setAnalysisId(String analysisId) {
    this.analysisId = analysisId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Flag to indicate Analysis definition migration.
   *
   * @return
   */
  public boolean isAnalysisMigrated() {
    return isAnalysisMigrated;
  }

  public void setAnalysisMigrated(boolean analysisMigrated) {
    isAnalysisMigrated = analysisMigrated;
  }

  /**
   * Flag to indicate Executions results migration.
   *
   * @return boolean value
   */
  public boolean isExecutionsMigrated() {
    return isExecutionsMigrated;
  }

  /**
   * Set to true if Execution Results are migrated.
   *
   * @param executionsMigrated
   */
  public void setExecutionsMigrated(boolean executionsMigrated) {
    isExecutionsMigrated = executionsMigrated;
  }
}

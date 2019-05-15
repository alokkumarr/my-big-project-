package com.synchronoss.saw.scheduler.modal;

import java.io.Serializable;

public class DSLExecutionBean implements Serializable {

  private static final long serialVersionUID = 8510739855197957265l;

  String dslQueryId;
  String executedBy;
  String executionId;
  String executionType;
  String finishedTime;
  String startTime;
  String status;

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getDslQueryId() {
    return dslQueryId;
  }

  public void setDslQueryId(String dslQueryId) {
    this.dslQueryId = dslQueryId;
  }

  public String getExecutedBy() {
    return executedBy;
  }

  public void setExecutedBy(String executedBy) {
    this.executedBy = executedBy;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public String getExecutionType() {
    return executionType;
  }

  public void setExecutionType(String executionType) {
    this.executionType = executionType;
  }

  public String getFinishedTime() {
    return finishedTime;
  }

  public void setFinishedTime(String finishedTime) {
    this.finishedTime = finishedTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

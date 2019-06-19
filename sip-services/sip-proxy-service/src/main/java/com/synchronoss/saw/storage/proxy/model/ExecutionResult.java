package com.synchronoss.saw.storage.proxy.model;

import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.SipQuery;

public class ExecutionResult {


  String executionId;
  String dslQueryId;
  ExecutionType executionType;
  String status;
  Long startTime;
  Long finishedTime;
  SipQuery sipQuery;
  Analysis analysis;
  String executedBy;
  Object data;

  /**
   * Gets dslQueryId.
   *
   * @return value of dslQueryId
   */
  public String getDslQueryId() {
    return dslQueryId;
  }

  /** Sets dslQueryId. */
  public void setDslQueryId(String dslQueryId) {
    this.dslQueryId = dslQueryId;
  }

  /**
   * Gets executionId.
   *
   * @return value of executionId
   */
  public String getExecutionId() {
    return executionId;
  }

  /** Sets executionId. */
  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  /**
   * Gets executionType.
   *
   * @return value of executionType
   */
  public ExecutionType getExecutionType() {
    return executionType;
  }

  /** Sets executionType. */
  public void setExecutionType(ExecutionType executionType) {
    this.executionType = executionType;
  }

  /**
   * Gets status.
   *
   * @return value of status
   */
  public String getStatus() {
    return status;
  }

  /** Sets status. */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets startTime.
   *
   * @return value of startTime
   */
  public Long getStartTime() {
    return startTime;
  }

  /** Sets startTime. */
  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  /**
   * Gets finishedTime.
   *
   * @return value of finishedTime
   */
  public Long getFinishedTime() {
    return finishedTime;
  }

  /** Sets finishedTime. */
  public void setFinishedTime(Long finishedTime) {
    this.finishedTime = finishedTime;
  }

  /**
   * Gets sipQuery.
   *
   * @return value of sipQuery
   */
  public SipQuery getSipQuery() {
    return sipQuery;
  }

  /** Sets sipQuery. */
  public void setSipQuery(SipQuery sipQuery) {
    this.sipQuery = sipQuery;
  }

  /**
   * Gets executedBy.
   *
   * @return value of executedBy
   */
  public String getExecutedBy() {
    return executedBy;
  }

  /** Sets executedBy. */
  public void setExecutedBy(String executedBy) {
    this.executedBy = executedBy;
  }

  /**
   * Gets data.
   *
   * @return value of data
   */
  public Object getData() {
    return data;
  }

  /** Sets result. */
  public void setData(Object data) {
    this.data = data;
  }

  /**
   * Gets analysis.
   *
   * @return value of Analysis
   */
  public Analysis getAnalysis() {
    return analysis;
  }

  /** Sets analysis. */
  public void setAnalysis(Analysis analysis) {
    this.analysis = analysis;
  }
}

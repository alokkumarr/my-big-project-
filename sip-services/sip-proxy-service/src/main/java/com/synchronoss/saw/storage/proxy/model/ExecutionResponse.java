package com.synchronoss.saw.storage.proxy.model;

import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.SipQuery;

public class ExecutionResponse {

  Object data;
  Analysis analysis;
  String executedBy;

  /**
   * Gets data.
   *
   * @return value of data
   */
  public Object getData() {
    return data;
  }

  /** Sets data. */
  public void setData(Object data) {
    this.data = data;
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

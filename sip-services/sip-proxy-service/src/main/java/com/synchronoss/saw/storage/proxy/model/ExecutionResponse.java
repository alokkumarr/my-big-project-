package com.synchronoss.saw.storage.proxy.model;

import com.synchronoss.saw.model.SipQuery;

public class ExecutionResponse {

  Object data;
  SipQuery sipQuery;
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
}

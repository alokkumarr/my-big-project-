package com.synchronoss.saw.storage.proxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ExecuteAnalysisResponse {

  Object data;
  long totalRows;
  String executionId;
  String query;
  String userId;

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
   * Gets totalRows.
   *
   * @return value of totalRows
   */
  public long getTotalRows() {
    return totalRows;
  }

  /** Sets totalRows. */
  public void setTotalRows(long totalRows) {
    this.totalRows = totalRows;
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
   * Gets query.
   *
   * @return query
   */
  public String getQuery() { return query; }

  /** Sets query. */
  public void setQuery(String query) { this.query = query; }

  /**
   * Gets userId.
   *
   * @return value of userId
   */
  public String getUserId() {
    return userId;
  }

  /** Sets userId. */
  public void setUserId(String userId) {
    this.userId = userId;
  }
}

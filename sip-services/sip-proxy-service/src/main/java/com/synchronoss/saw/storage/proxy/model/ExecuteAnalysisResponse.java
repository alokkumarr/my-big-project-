package com.synchronoss.saw.storage.proxy.model;

public class ExecuteAnalysisResponse {

  Object data;
  String executionId;

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
}

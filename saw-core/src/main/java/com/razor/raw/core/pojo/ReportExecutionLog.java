package com.razor.raw.core.pojo;

import java.io.Serializable;

public class ReportExecutionLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long reportExecutionLogId;
	private long reportsId;
	private String description;
	private String startTime;
	private String endTime;
	private String createdUser;
	
	public long getReportExecutionLogId() {
		return reportExecutionLogId;
	}
	public void setReportExecutionLogId(long reportExecutionLogId) {
		this.reportExecutionLogId = reportExecutionLogId;
	}
	public long getReportsId() {
		return reportsId;
	}
	public void setReportsId(long reportsId) {
		this.reportsId = reportsId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	
	
}

package com.razor.raw.generation.rest.bean;

import java.io.Serializable;

public class ReportBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private long reportID;
	/**
	 * @return the reportID
	 */
	public long getReportID() {
		return reportID;
	}
	/**
	 * @param reportID the reportID to set
	 */
	public void setReportID(long reportID) {
		this.reportID = reportID;
	}
	private String  reportName;
	private String  reportDescription;
	private String  createdUser;
	private String  createdDate;
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportDescription() {
		return reportDescription;
	}
	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}
	public String getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	

}

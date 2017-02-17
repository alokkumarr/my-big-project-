package com.razor.scheduler.common;

import java.io.Serializable;
import java.util.List;

import com.razor.raw.core.pojo.ScheduledReport;
/**
 * 
 * @author girija.sankar
 * This bean is used to return the response for schedule View
 */
public class ScheduleReportRespBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1234017462315306573L;
	private List<ScheduledReport> scheduledReportList;
	private long totalCount;
	private String statusMessage;
	
	/**
	 * @return the scheduledReportList
	 */
	public List<ScheduledReport> getScheduledReportList() {
		return scheduledReportList;
	}
	/**
	 * @param scheduledReportList the scheduledReportList to set
	 */
	public void setScheduledReportList(List<ScheduledReport> scheduledReportList) {
		this.scheduledReportList = scheduledReportList;
	}
	/**
	 * 
	 * @return total count of report
	 */
	public long getTotalCount() {
		return totalCount;
	}
	/**
	 * 
	 * @param totalCount
	 */
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
}

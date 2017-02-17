package com.razor.raw.generation.rest.bean;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author ajay.kumar
 * This bean is used to return the response for Report View
 */
public class ReportViewBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Map<String, String>> reportViewList;
	private long totalCount;
	private String statusMessage;
	
	
	 
	/**
	 * 
	 * @return total count of report
	 */
	public long getTotalCount() {
		return totalCount;
	}
	/**
	 * @return the reportViewList
	 */
	public List<Map<String, String>> getReportViewList() {
		return reportViewList;
	}
	/**
	 * @param reportViewList the reportViewList to set
	 */
	public void setReportViewList(List<Map<String, String>> reportViewList) {
		this.reportViewList = reportViewList;
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

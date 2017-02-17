package com.razor.raw.generation.rest.bean;

import java.io.Serializable;

import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.Report;

/**
 * 
 * @author AJAY.KUMAR
 * This file is used to send request to dril 
 */
public class ReportViewReqBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Report report;
	private int recordLimit;
	/**
	 * 
	 * @return Report
	 */
	public Report getReport() {
		return report;
	}
	/**
	 * 
	 * @param report
	 */
	public void setReport(Report report) {
		this.report = report;
	}
	
	public int getRecordLimit() {
		return recordLimit;
	}
	public void setRecordLimit(int recordLimit) {
		this.recordLimit = recordLimit;
	}
	
	
}

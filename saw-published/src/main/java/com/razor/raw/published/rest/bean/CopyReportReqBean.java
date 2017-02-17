package com.razor.raw.published.rest.bean;

import com.razor.raw.core.pojo.PublishedReport;

/**
 * @author sunil.belakeri
 *
 * 
 */
public class CopyReportReqBean {
	
	String reportName;
	String reportDescription;
	String tetantId;
	String prodId;
	String destCategoryId;
	PublishedReport publishedReport;
	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}
	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	/**
	 * @return the reportDescription
	 */
	public String getReportDescription() {
		return reportDescription;
	}
	/**
	 * @param reportDescription the reportDescription to set
	 */
	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}
	/**
	 * @return the tetantId
	 */
	public String getTetantId() {
		return tetantId;
	}
	/**
	 * @param tetantId the tetantId to set
	 */
	public void setTetantId(String tetantId) {
		this.tetantId = tetantId;
	}
	/**
	 * @return the destCategoryId
	 */
	public String getDestCategoryId() {
		return destCategoryId;
	}
	/**
	 * @param destCategoryId the destCategoryId to set
	 */
	public void setDestCategoryId(String destCategoryId) {
		this.destCategoryId = destCategoryId;
	}
	/**
	 * @return the prodId
	 */
	public String getProdId() {
		return prodId;
	}
	/**
	 * @param prodId the prodId to set
	 */
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	
	/**
	 * @return the publishedReport
	 */
	public PublishedReport getPublishedReport() {
		return publishedReport;
	}
	/**
	 * @param publishedReport the publishedReport to set
	 */
	public void setPublishedReport(PublishedReport publishedReport) {
		this.publishedReport = publishedReport;
	}
	
	
	
	
	

}

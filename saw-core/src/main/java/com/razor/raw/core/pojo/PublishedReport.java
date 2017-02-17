package com.razor.raw.core.pojo;

import java.io.Serializable;

/**
 * 
 * @author surendra.rajaneni
 *
 */
public class PublishedReport implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3575456615875959272L;
	private long publishReportId = 0;
	private long reportId = 0;
	private long reportCategoryId = 0;
	private String tenantId;
	private String productId;
	private String reportName;
	private String reportDescription;
	private String reportLocaton;
	private boolean displayStatus;
	private boolean scheduled;
	private String createdUser;
	private String createdDate;
	private String format;
	
	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
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
	public long getPublishReportId() {
		return publishReportId;
	}
	public void setPublishReportId(long publishReportId) {
		this.publishReportId = publishReportId;
	}
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	public long getReportCategoryId() {
		return reportCategoryId;
	}
	public void setReportCategoryId(long reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}
	
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportLocaton() {
		return reportLocaton;
	}
	public void setReportLocaton(String reportLocaton) {
		this.reportLocaton = reportLocaton;
	}
	public boolean isDisplayStatus() {
		return displayStatus;
	}
	public void setDisplayStatus(boolean displayStatus) {
		this.displayStatus = displayStatus;
	}
	public boolean isScheduled() {
		return scheduled;
	}
	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
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
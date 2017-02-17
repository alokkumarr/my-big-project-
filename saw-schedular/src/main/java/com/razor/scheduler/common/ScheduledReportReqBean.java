package com.razor.scheduler.common;

import java.io.Serializable;

/**
 * 
 * @author girija.sankar
 * This file is used to send request to DB 
 */
public class ScheduledReportReqBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3887909046057074142L;
	
	private long  reportCategoryId;
	private String reportCategoryName;
	private String  tenantId;
	private String  productId;
	private String  userId;
	
	
	public long getReportCategoryId() {
		return reportCategoryId;
	}
	public void setReportCategoryId(long reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}
	public String getReportCategoryName() {
		return reportCategoryName;
	}
	public void setReportCategoryName(String reportCategoryName) {
		this.reportCategoryName = reportCategoryName;
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
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	
	
}

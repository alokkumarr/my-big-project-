package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.List;

public class ReportCategory implements Serializable {

	private static final long serialVersionUID = 2163859927137026996L;
	
	private long reportCategoryId;
	private String tenantId;
	private String productId = null;
	private String reportCategoryName = null;
	private String reportCategoryDescription = null;
	private long reportSuperCategoryId = 0;
	private boolean displayStatus;
	private boolean restricted;
	private String createdUser = null;
	private String modifiedUser = null;
	private String createdDate = null;
	private String modifiedDate = null;
	private List<ReportCategory> reportCategories = null;
	
	
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
	public String getReportCategoryName() {
		return reportCategoryName;
	}
	public void setReportCategoryName(String reportCategoryName) {
		this.reportCategoryName = reportCategoryName;
	}
	public String getReportCategoryDescription() {
		return reportCategoryDescription;
	}
	public void setReportCategoryDescription(String reportCategoryDescription) {
		this.reportCategoryDescription = reportCategoryDescription;
	}
	public long getReportSuperCategoryId() {
		return reportSuperCategoryId;
	}
	public void setReportSuperCategoryId(long reportSuperCategoryId) {
		this.reportSuperCategoryId = reportSuperCategoryId;
	}
	public boolean isDisplayStatus() {
		return displayStatus;
	}
	public void setDisplayStatus(boolean displayStatus) {
		this.displayStatus = displayStatus;
	}
	public boolean isRestricted() {
		return restricted;
	}
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	public String getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	public String getModifiedUser() {
		return modifiedUser;
	}
	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public List<ReportCategory> getReportCategories() {
		return reportCategories;
	}
	public void setReportCategories(List<ReportCategory> reportCategories) {
		this.reportCategories = reportCategories;
	}
}

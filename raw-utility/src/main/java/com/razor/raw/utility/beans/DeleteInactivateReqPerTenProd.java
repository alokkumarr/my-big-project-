package com.razor.raw.utility.beans;

import java.util.ArrayList;

/**
 * @author sunil.belakeri
 *
 * 
 */
public class DeleteInactivateReqPerTenProd {
	
	private Long reportId;
	private String reportName;
	private String tenantID;
	private String productId;
	private String reportCategoryName;
	private ArrayList<String> reportSuperCategoryList;
	private String modifiedUser;
	
	
	/**
	 * @return the reportId
	 */
	public Long getReportId() {
		return reportId;
	}
	/**
	 * @param reportId the reportId to set
	 */
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
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
	 * @return the tenantID
	 */
	public String getTenantID() {
		return tenantID;
	}
	/**
	 * @param tenantID the tenantID to set
	 */
	public void setTenantID(String tenantID) {
		this.tenantID = tenantID;
	}
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the reportCategoryName
	 */
	public String getReportCategoryName() {
		return reportCategoryName;
	}
	/**
	 * @param reportCategoryName the reportCategoryName to set
	 */
	public void setReportCategoryName(String reportCategoryName) {
		this.reportCategoryName = reportCategoryName;
	}
	/**
	 * @return the reportSuperCategoryList
	 */
	public ArrayList<String> getReportSuperCategoryList() {
		return reportSuperCategoryList;
	}
	/**
	 * @param reportSuperCategoryList the reportSuperCategoryList to set
	 */
	public void setReportSuperCategoryList(ArrayList<String> reportSuperCategoryList) {
		this.reportSuperCategoryList = reportSuperCategoryList;
	}
	/**
	 * @return the modifiedUser
	 */
	public String getModifiedUser() {
		return modifiedUser;
	}
	/**
	 * @param modifiedUser the modifiedUser to set
	 */
	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	
}

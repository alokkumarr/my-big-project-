package com.sncr.saw.security.common.bean.repo.admin.category;

public class SubCategoryDetails {
	private long subCategoryId;
	private String subCategoryName;
	private String subCategoryDesc;
	private long activestatusInd;
	private boolean systemCategory;
	
	public String getSubCategoryDesc() {
		return subCategoryDesc;
	}
	public void setSubCategoryDesc(String subCategoryDesc) {
		this.subCategoryDesc = subCategoryDesc;
	}
	public long getActivestatusInd() {
		return activestatusInd;
	}
	public void setActivestatusInd(long activestatusInd) {
		this.activestatusInd = activestatusInd;
	}
	public long getSubCategoryId() {
		return subCategoryId;
	}
	public void setSubCategoryId(long subCategoryId) {
		this.subCategoryId = subCategoryId;
	}
	public String getSubCategoryName() {
		return subCategoryName;
	}
	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}
	public boolean isSystemCategory() {
		return systemCategory;
	}
	public void setSystemCategory(boolean systemCategory) {
		this.systemCategory = systemCategory;
	}
}

package com.sncr.saw.security.common.bean.repo.admin.category;

import java.util.List;

public class SubCategoryDetails {

	private boolean autoCreate;
	private long subCategoryId;
	private String subCategoryName;
	private String subCategoryDesc;
	private long activestatusInd;
	private List<String> privilege;

	public boolean isAutoCreate() {
		return autoCreate;
	}
	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}
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
	public List<String> getPrivilege() {
		return privilege;
	}
	public void setPrivilege(List<String> privilege) {
		this.privilege = privilege;
	}
}

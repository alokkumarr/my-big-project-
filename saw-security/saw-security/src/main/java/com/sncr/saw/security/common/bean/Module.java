package com.sncr.saw.security.common.bean;

import java.sql.Date;

public class Module {

	private String moduleName;
	private Long moduleId;			// generated field in database
	private String moduleCode;
	private String moduleDesc;
	private Integer activeStatusInd;
	private Date createdDate;
	private String createdBy;
	private Date inactivatedDate;
	private String inactivatedBy;
	private Date modifiedAt;
	private String modifiedBy;

	public Module() {
	}

	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getModuleDesc() {
		return moduleDesc;
	}

	public void setModuleDesc(String moduleDesc) {
		this.moduleDesc = moduleDesc;
	}

	public Integer getActiveStatusInd() {
		return activeStatusInd;
	}

	public void setActiveStatusInd(Integer activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getInactivatedDate() {
		return inactivatedDate;
	}

	public void setInactivatedDate(Date inactivatedDate) {
		this.inactivatedDate = inactivatedDate;
	}

	public String getInactivatedBy() {
		return inactivatedBy;
	}

	public void setInactivatedBy(String inactivatedBy) {
		this.inactivatedBy = inactivatedBy;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}

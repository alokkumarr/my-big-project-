package com.sncr.saw.security.common.bean;

public class Role {

	private Long roleId;
	private Long custSysId;
	private String roleName;
	private String roleCode;
	private String roleDesc;
	private String roleType;
	private String createdBy;
	private String modifiedBy;
	private boolean autoCreate;
	private String customerCode;
	private String inactivatedBy;
	private String dataSecurityKey;
	private String activeStatusInd;

	public Role() {
	}

	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getCustSysId() {
		return custSysId;
	}

	public void setCustSysId(Long custSysId) {
		this.custSysId = custSysId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleType() {
		return roleType;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public boolean isAutoCreate() {
		return autoCreate;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getDataSecurityKey() {
		return dataSecurityKey;
	}

	public void setDataSecurityKey(String dataSecurityKey) {
		this.dataSecurityKey = dataSecurityKey;
	}

	public String getActiveStatusInd() {
		return activeStatusInd;
	}

	public void setActiveStatusInd(String activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getInactivatedBy() {
		return inactivatedBy;
	}

	public void setInactivatedBy(String inactivatedBy) {
		this.inactivatedBy = inactivatedBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}

package com.sncr.saw.security.common.bean.repo.admin.role;

public class RoleDetails {
	
	private long roleSysId;
	private long custSysId;
	private String roleName;
	private String roleDesc;
	private String roleType;
	private String dsk;
	private String activeStatusInd;
	private String masterLoginId;
	private String customerCode;
	private Boolean myAnalysis;
	private Boolean privExists;
	
	
	public Boolean getPrivExists() {
		return privExists;
	}
	public void setPrivExists(Boolean privExists) {
		this.privExists = privExists;
	}	
	public Boolean getMyAnalysis() {
		return myAnalysis;
	}
	public void setMyAnalysis(Boolean myAnalysis) {
		this.myAnalysis = myAnalysis;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getMasterLoginId() {
		return masterLoginId;
	}
	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}
	public long getRoleSysId() {
		return roleSysId;
	}
	public void setRoleSysId(long roleSysId) {
		this.roleSysId = roleSysId;
	}
	public long getCustSysId() {
		return custSysId;
	}
	public void setCustSysId(long custSysId) {
		this.custSysId = custSysId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getDsk() {
		return dsk;
	}
	public void setDsk(String dsk) {
		this.dsk = dsk;
	}
	public String getActiveStatusInd() {
		return activeStatusInd;
	}
	public void setActiveStatusInd(String activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}
		
}

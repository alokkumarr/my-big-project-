package com.sncr.saw.security.common.bean.external.response;

import com.synchronoss.bda.sip.jwt.token.RoleType;

import java.io.Serializable;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class Role implements Serializable {

	private static final long serialVersionUID = -6682525829922872306L;

	private String message;
	private long roleSysId;
	private String roleName;
	private String roleDesc;
	private RoleType roleType;
	private long customerSysId;
	private String customerCode;
	private String activeStatusInd;

	public long getRoleSysId() {
		return roleSysId;
	}
	public void setRoleSysId(long roleSysId) {
		this.roleSysId = roleSysId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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
	public RoleType getRoleType() {
		return roleType;
	}
	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}
	public long getCustomerSysId() {
		return customerSysId;
	}
	public void setCustomerSysId(long customerSysId) {
		this.customerSysId = customerSysId;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getActiveStatusInd() {
		return activeStatusInd;
	}
	public void setActiveStatusInd(String activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}
}

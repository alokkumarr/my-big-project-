package com.sncr.saw.security.common.bean.repo.admin;

import java.util.List;

import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;



public class RolesList {

	private List<RoleDetails> Roles;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	

	public List<RoleDetails> getRoles() {
		return Roles;
	}

	public void setRoles(List<RoleDetails> roles) {
		Roles = roles;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getValidityMessage() {
		return ValidityMessage;
	}

	public void setValidityMessage(String validityMessage) {
		ValidityMessage = validityMessage;
	}
}

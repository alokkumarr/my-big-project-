package com.sncr.saw.security.common.bean.repo.admin;

import java.util.List;

import com.sncr.saw.security.common.bean.Role;



public class RolesDropDownList {
	
	private List<Role> roles;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	
	

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

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	
}

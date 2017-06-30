package com.sncr.saw.security.common.bean.repo.admin;

import java.util.List;

import com.sncr.saw.security.common.bean.repo.admin.privilege.PrivilegeDetails;

public class PrivilegesList {

	private List<PrivilegeDetails> Privileges;
	private String error;
	private String ValidityMessage;
	private Boolean valid;

	

	public List<PrivilegeDetails> getPrivileges() {
		return Privileges;
	}

	public void setPrivileges(List<PrivilegeDetails> privileges) {
		Privileges = privileges;
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

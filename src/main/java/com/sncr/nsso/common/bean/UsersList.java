package com.sncr.nsso.common.bean;

import java.util.List;

public class UsersList {

	private List<User> Users;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	
	
		
	public List<User> getUsers() {
		return Users;
	}

	public void setUsers(List<User> users) {
		Users = users;
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

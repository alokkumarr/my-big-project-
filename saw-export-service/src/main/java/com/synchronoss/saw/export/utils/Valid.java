package com.synchronoss.saw.export.utils;

import java.io.Serializable;

public class Valid implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6682525829922872306L;
	
	private Boolean valid;
	private String validityMessage;
	private String error;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public String getValidityMessage() {
		return validityMessage;
	}
	public void setValidityMessage(String validityMessage) {
		this.validityMessage = validityMessage;
	}
	
	

}

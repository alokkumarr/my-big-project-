/**
 * 
 */
package com.sncr.nsso.common.bean;

import java.io.Serializable;

/**
 * @author gsan0003
 *
 */
public class Valid implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6682525829922872306L;
	
	private Boolean valid;
	private String validityMessage;
	
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

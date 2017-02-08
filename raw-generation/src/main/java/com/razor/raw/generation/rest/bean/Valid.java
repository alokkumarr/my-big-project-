/**
 * 
 */
package com.razor.raw.generation.rest.bean;

import java.io.Serializable;

/**
 * @author gsan0003
 *
 */
public class Valid implements Serializable {

	private static final long serialVersionUID = -4585608954081111514L;

	private Boolean valid;
	private String validityMessage;
	/**
	 * @return the valid
	 */
	public Boolean getValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	/**
	 * @return the validityMessage
	 */
	public String getValidityMessage() {
		return validityMessage;
	}
	/**
	 * @param validityMessage the validityMessage to set
	 */
	public void setValidityMessage(String validityMessage) {
		this.validityMessage = validityMessage;
	}

	

	

}

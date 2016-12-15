/**
 * 
 */
package com.sncr.nsso.common.bean;

import java.io.Serializable;

/**
 * @author gsan0003
 *
 */
public class ResetValid implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3738467208852302262L;
	
	private Boolean valid;
	private String masterLoginID;
	private String validityReason;
	
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public String getMasterLoginID() {
		return masterLoginID;
	}
	public void setMasterLoginID(String masterLoginID) {
		this.masterLoginID = masterLoginID;
	}
	public String getValidityReason() {
		return validityReason;
	}
	public void setValidityReason(String validityReason) {
		this.validityReason = validityReason;
	}
	
	
	

}

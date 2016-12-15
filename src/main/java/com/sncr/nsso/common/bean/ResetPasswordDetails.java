/**
 * 
 */
package com.sncr.nsso.common.bean;

import java.io.Serializable;

/**
 * @author gsan0003
 *
 */
public class ResetPasswordDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8968098936563531559L;
	private String masterLoginId;
	private String eMail;
	private String firstName;
	/**
	 * @return the masterLoginId
	 */
	public String getMasterLoginId() {
		return masterLoginId;
	}
	/**
	 * @param masterLoginId the masterLoginId to set
	 */
	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}
	/**
	 * @return the eMail
	 */
	public String geteMail() {
		return eMail;
	}
	/**
	 * @param eMail the eMail to set
	 */
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	
	
}

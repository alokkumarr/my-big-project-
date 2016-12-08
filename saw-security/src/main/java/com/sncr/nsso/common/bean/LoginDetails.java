/**
 * 
 */
package com.sncr.nsso.common.bean;

import java.io.Serializable;

/**
 * @author gsan0003
 *
 */
public class LoginDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8968098936563531559L;
	private String masterLoginId;
	private String password;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}

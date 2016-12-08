/**
 * 
 */
package com.sncr.nsso.common.bean;

/**
 * @author gsan0003
 *
 */
public class ChangePasswordDetails {

	private String masterLoginId;
	private String oldPassword;
	private String newPassword;
	private String cnfNewPassword;
	
	
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
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}
	/**
	 * @param oldPassword the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}
	/**
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	/**
	 * @return the cnfNewPassword
	 */
	public String getCnfNewPassword() {
		return cnfNewPassword;
	}
	/**
	 * @param cnfNewPassword the cnfNewPassword to set
	 */
	public void setCnfNewPassword(String cnfNewPassword) {
		this.cnfNewPassword = cnfNewPassword;
	}
}

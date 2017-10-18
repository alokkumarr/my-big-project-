/**
 * 
 */
package com.sncr.saw.security.common.bean.repo;

import java.util.Date;

/**
 * @author gsan0003
 *
 */
public class PasswordDetails {

	private Date pwdModifiedDate;
	private Integer passwordExpiryDays;
	
	public Date getPwdModifiedDate() {
		return pwdModifiedDate;
	}
	public void setPwdModifiedDate(Date pwdModifiedDate) {
		this.pwdModifiedDate = pwdModifiedDate;
	}
	public Integer getPasswordExpiryDays() {
		return passwordExpiryDays;
	}
	public void setPasswordExpiryDays(Integer passwordExpiryDays) {
		this.passwordExpiryDays = passwordExpiryDays;
	}
	
}

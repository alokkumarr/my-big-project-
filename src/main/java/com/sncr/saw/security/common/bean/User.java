/**
 * 
 */
package com.sncr.saw.security.common.bean;

import java.io.Serializable;

import com.sncr.saw.security.common.bean.repo.TicketDetails;



/**
 * @author girija.sankar
 * 
 */

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6710950219794990634L;

	public User() {
	}

	private Long userId;
	private String loginId;
	private String masterLoginId;	
	private String activeStatusInd;
	private String firstName;	
	private String lastName;
	private String middleName;
	private String productName;	
	private TicketDetails ticketDetails = null;
	private String userName;
	private String roleName;	
	private String userType;
	private Long roleId;
	private String email;
	private String status;	
	private Long validMins;
	private Long customerId;
	private String password;
	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

    public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getMasterLoginId() {
		return masterLoginId;
	}

	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}

	
	public String getProductName() {
		return productName;
	}



	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the ticketDetails
	 */
	public TicketDetails getTicketDetails() {
		return ticketDetails;
	}


	/**
	 * @param ticketDetails the ticketDetails to set
	 */
	public void setTicketDetails(TicketDetails ticketDetails) {
		this.ticketDetails = ticketDetails;
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



	/**
	 * @return the activeStatusInd
	 */
	public String getActiveStatusInd() {
		return activeStatusInd;
	}



	/**
	 * @param activeStatusInd the activeStatusInd to set
	 */
	public void setActiveStatusInd(String activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}



	/**
	 * @return the validMins
	 */
	public Long getValidMins() {
		return validMins;
	}



	/**
	 * @param validMins the validMins to set
	 */
	public void setValidMins(Long validMins) {
		this.validMins = validMins;
	}

	
	
}

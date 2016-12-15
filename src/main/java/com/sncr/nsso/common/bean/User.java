/**
 * 
 */
package com.sncr.nsso.common.bean;

import java.io.Serializable;

import com.sncr.nsso.common.bean.repo.TicketDetails;

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

	/**
	 * login id of the user
	 */
	private String loginId;
	/**
	 * master login id of the user.
	 */
	private String masterLoginId;
	
	private Integer activeStatusInd;
	private String firstName;
	
	private String productName;
	
	private TicketDetails ticketDetails;
	
	/**
	 * Full name of the user.
	 */
	private String userName;
	/**
	 * User role name
	 * 
	 * @return
	 */
	private String roleName;
	
	private String userType;;
	
	
	private Long validMins;
	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}



	/**
	 * @param userType the userType to set
	 */
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
	public Integer getActiveStatusInd() {
		return activeStatusInd;
	}



	/**
	 * @param activeStatusInd the activeStatusInd to set
	 */
	public void setActiveStatusInd(Integer activeStatusInd) {
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

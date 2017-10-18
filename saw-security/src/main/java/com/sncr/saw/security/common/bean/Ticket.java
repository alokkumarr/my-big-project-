/**
 * 
 */
package com.sncr.saw.security.common.bean;

import com.sncr.saw.security.common.bean.repo.Products;
import com.sncr.saw.security.common.constants.SSOCommonConstants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * It will hold information relevant for product. This should only be used in
 * the SSOAdpater layer of the underlying product and not in SSO.
 * 
 * @author vaibhav.kapoor
 * 
 */
@XmlRootElement(name = SSOCommonConstants.TICKET)
public class Ticket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7084499578301213806L;
	// below props are must have ones
	private String ticketId;
	private String windowId;
	private String masterLoginId;
	private String userFullName;
	private String defaultProdID;
	private String roleCode;
	private String roleType;
	private Long createdTime;	
	private List<DSKDetails> dataSecurityKey;
	private String error;
	private String custID;
	private String custCode;
	private Long userId;
	private ArrayList<Products> products;	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public ArrayList<Products> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Products> products) {
		this.products = products;
	}

	public String getCustID() {
		return custID;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getError() {
		return error;
	}

	
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * This ticket will be valid till this time.
	 */
	private Long validUpto;
	/**
	 * Whether this ticket is currently active.
	 */
	private boolean valid;
	
	/**
	 * Reason the validity/invalidity of the ticket.
	 */
	private String validityReason;	
	
	public List<DSKDetails> getDataSecurityKey() {
		return dataSecurityKey;
	}

	public void setDataSecurityKey(List<DSKDetails> dataSecurityKey) {
		this.dataSecurityKey = dataSecurityKey;
	}


	public String getDefaultProdID() {
		return defaultProdID;
	}

	public void setDefaultProdID(String defaultProdID) {
		this.defaultProdID = defaultProdID;
	}

	/**
	 * All underlying products need to provide this implementation.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Ticket Id = " + ticketId + "\n");
		sb.append("Master Login Id = " + masterLoginId + "\n");
		sb.append("User Name = " + userFullName + "\n");
		sb.append("Product Code = " + defaultProdID + "\n");
		sb.append("Role Type = " + roleType + "\n");
		sb.append("createdTime = " + createdTime + "\n");
		sb.append("validUpto = " + validUpto + "\n");
		sb.append("validityReason = " + validityReason + "\n");
		return sb.toString();
	}

	
	@XmlElement(name = SSOCommonConstants.TICKET_ID)  
	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	@XmlElement(name = SSOCommonConstants.MASTER_LOGIN_ID)
	public String getMasterLoginId() {
		return masterLoginId;
	}

	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the createdTime
	 */
	@XmlElement(name = SSOCommonConstants.CREATED_TIME)
	public Long getCreatedTime() {
		return createdTime;
	}

	private Long validMins;
	
	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the validUpto
	 */
	@XmlElement(name = SSOCommonConstants.VALID_UPTO)
	public Long getValidUpto() {
		return validUpto;
	}

	/**
	 * @param validUpto the validUpto to set
	 */
	public void setValidUpto(Long validUpto) {
		this.validUpto = validUpto;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		if (valid) {
			if (System.currentTimeMillis() < this.validUpto) {
				this.valid = true;
			} else {
				this.valid = false;
			}
		}
		return valid;
	}
	
	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the validityReason
	 */
	@XmlElement(name = SSOCommonConstants.VALIDITY_REASON)
	public String getValidityReason() {
		return validityReason;
	}

	/**
	 * @param validityReason the validityReason to set
	 */
	public void setValidityReason(String validityReason) {
		this.validityReason = validityReason;
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

	/**
	 * @return the roleType
	 */
	public String getRoleType() {
		return roleType;
	}

	/**
	 * @param roleType the roleType to set
	 */
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	/**
	 * @return the userName
	 */
	public String getUserFullName() {
		return userFullName;
	}

	/**
	 * @param userFullName the userName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	/**
	 * @return the windowId
	 */
	public String getWindowId() {
		return windowId;
	}

	/**
	 * @param windowId the windowId to set
	 */
	public void setWindowId(String windowId) {
		this.windowId = windowId;
	}

	
	
	
}

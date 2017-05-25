/**
 * 
 */
package com.sncr.nsso.common.bean.repo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author gsan0003
 *
 */
public class TicketDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7546190895561288031L;
	private String userName;

	private String landingProd;
	private String custID;
	private String custCode;
	private String compName;
	private String roleCode;
	private String roleType;
	private String dataSKey;
	private ArrayList<Products> products;
	

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	
	public String getLandingProd() {
		return landingProd;
	}

	
	public void setLandingProd(String landingProd) {
		this.landingProd = landingProd;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getDataSKey() {
		return dataSKey;
	}

	public void setDataSKey(String dataSKey) {
		this.dataSKey = dataSKey;
	}

	

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the compName
	 */
	public String getCompName() {
		return compName;
	}

	/**
	 * @param compName
	 *            the compName to set
	 */
	public void setCompName(String compName) {
		this.compName = compName;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the roleType
	 */
	public String getRoleType() {
		return roleType;
	}

	/**
	 * @param roleType
	 *            the roleType to set
	 */
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public ArrayList<Products> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Products> products) {
		this.products = products;
	}
}

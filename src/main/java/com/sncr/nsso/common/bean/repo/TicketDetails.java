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
	private String roleName;
	private String roleType;
	private String dataSKey;
	private ArrayList<ProductModules> productModules;
	private ArrayList<ProductModuleFeatures> productModuleFeatures;

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	
	public String getLandingProd() {
		return landingProd;
	}

	public ArrayList<ProductModuleFeatures> getProductModuleFeatures() {
		return productModuleFeatures;
	}

	public void setProductModuleFeatures(ArrayList<ProductModuleFeatures> productModuleFeatures) {
		this.productModuleFeatures = productModuleFeatures;
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

	public ArrayList<ProductModules> getProductModules() {
		return productModules;
	}

	public void setProductModules(ArrayList<ProductModules> productModules) {
		this.productModules = productModules;
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
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

}

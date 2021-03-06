/**
 * 
 */
package com.sncr.saw.security.common.bean.repo;

import com.synchronoss.bda.sip.dsk.DskDetails;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gsan0003
 *
 */
public class TicketDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7546190895561288031L;
	private String userFullName;

	private String landingProd;
	private String custID;
	private String custCode;
	private String compName;
	private String roleCode;
	private RoleType roleType;
  private List<String> CustomConfig;
	private Long userId;
	private ArrayList<Products> products;
	private Integer isJvCustomer;
	private Integer filterByCustomerCode;
	private SipDskAttribute dskAttribute;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

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

    /**
     * Gets CustomConfig
     *
     * @return value of CustomConfig
     */
    public List<String> getCustomConfig() {
        return CustomConfig;
    }

    /**
     * Sets CustomConfig
     */
    public void setCustomConfig(List<String> customConfig) {
        CustomConfig = customConfig;
    }

    /**
	 * @return the userName
	 */
	public String getUserFullName() {
		return userFullName;
	}

	/**
	 * @param userFullName
	 *            the userName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
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
	 * @param roleCode
	 *            the roleName to set
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the roleType
	 */
	public RoleType getRoleType() {
		return roleType;
	}

	public Integer getFilterByCustomerCode() {
		return filterByCustomerCode;
	}

	public void setFilterByCustomerCode(Integer filterByCustomerCode) {
		this.filterByCustomerCode = filterByCustomerCode;
	}

	/**
	 * @param roleType
	 *            the roleType to set
	 */
	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}

	public ArrayList<Products> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Products> products) {
		this.products = products;
	}

	public Integer getIsJvCustomer() {
		return isJvCustomer;
	}

	public void setIsJvCustomer(Integer isJvCustomer) {
		this.isJvCustomer = isJvCustomer;
	}

	public SipDskAttribute getDskAttribute() {
		return dskAttribute;
	}

	public void setDskAttribute(SipDskAttribute dskAttribute) {
		this.dskAttribute = dskAttribute;
	}
}

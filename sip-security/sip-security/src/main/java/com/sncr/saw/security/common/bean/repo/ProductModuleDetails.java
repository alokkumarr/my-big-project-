package com.sncr.saw.security.common.bean.repo;


/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class ProductModuleDetails {

	private static final long serialVersionUID = 6710950219766990634L;

	String moduleName;
	String productName;
	Long customerSysId;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getCustomerSysId() {
		return customerSysId;
	}

	public void setCustomerSysId(Long customerSysId) {
		this.customerSysId = customerSysId;
	}
}

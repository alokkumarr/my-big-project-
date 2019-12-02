package com.sncr.saw.security.common.bean.repo;


/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class ProductModuleDetails {

	private static final long serialVersionUID = 6710950219766990634L;

	Long moduleId;
	Long productId;
	Long customerSysId;
	Long customerProdModSysId;

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getCustomerSysId() {
		return customerSysId;
	}

	public void setCustomerSysId(Long customerSysId) {
		this.customerSysId = customerSysId;
	}

	public Long getCustomerProdModSysId() {
		return customerProdModSysId;
	}

	public void setCustomerProdModSysId(Long customerProdModSysId) {
		this.customerProdModSysId = customerProdModSysId;
	}
}

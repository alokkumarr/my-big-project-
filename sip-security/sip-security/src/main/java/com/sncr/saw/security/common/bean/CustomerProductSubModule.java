package com.sncr.saw.security.common.bean;

/**
 * Created by pman0003 on 9/21/2017.
 */
public class CustomerProductSubModule {
    private Long roleId;
    private Long productId;
    private Long moduleId;
    private Long customerId;
    private String categoryCode;


    /**
     * Gets roleId
     *
     * @return value of roleId
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Sets value of roleId
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets productId
     *
     * @return value of productId
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets value of productId
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * Gets moduleId
     *
     * @return value of moduleId
     */
    public Long getModuleId() {
        return moduleId;
    }

    /**
     * Sets value of moduleId
     */
    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * Gets customerId
     *
     * @return value of customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * Sets value of customerId
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets categoryCode
     *
     * @return value of categoryCode
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Sets value of categoryCode
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
}

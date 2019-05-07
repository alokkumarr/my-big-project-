package com.sncr.saw.security.common.bean;

import java.sql.Date;

public class CustProdModule {
    private Long custProdModSysId;        // CUST_PROD_MOD_SYS_ID -> generated auto increment
    private Long customerId;            // CUSTOMER_SYS_ID
    private Long productId;                // CUST_PROD_SYS_ID
    private Long moduleId;                // PROD_MOD_SYS_ID
    private Integer activeStatusInd;    // ACTIVE_STATUS_IND
    private String moduleUrl;            // MODULE_URL
    private Integer DEFAULT;            // DEFAULT
    private Date createdDate;            // CREATED_DATE
    private String createdBy;            // CREATED_BY
    private Date inactivatedDate;        // INACTIVATED_DATE
    private String inactivatedBy;        // INACTIVATED_BY
    private Date modifiedDate;            // MODIFIED_DATE
    private String modifiedBy;            // MODIFIED_BY

    public CustProdModule() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getCustProdModSysId() {
        return custProdModSysId;
    }

    public void setCustProdModSysId(Long custProdModSysId) {
        this.custProdModSysId = custProdModSysId;
    }

    public Integer getActiveStatusInd() {
        return activeStatusInd;
    }

    public void setActiveStatusInd(Integer activeStatusInd) {
        this.activeStatusInd = activeStatusInd;
    }

    public String getModuleUrl() {
        return moduleUrl;
    }

    public void setModuleUrl(String moduleUrl) {
        this.moduleUrl = moduleUrl;
    }

    public Integer getDEFAULT() {
        return DEFAULT;
    }

    public void setDEFAULT(Integer DEFAULT) {
        this.DEFAULT = DEFAULT;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getInactivatedDate() {
        return inactivatedDate;
    }

    public void setInactivatedDate(Date inactivatedDate) {
        this.inactivatedDate = inactivatedDate;
    }

    public String getInactivatedBy() {
        return inactivatedBy;
    }

    public void setInactivatedBy(String inactivatedBy) {
        this.inactivatedBy = inactivatedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}

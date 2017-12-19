package com.sncr.saw.security.app.model;

import java.sql.Date;

public class CustProdModuleFeatures {

    private Long custProdModFeatureSysId;
    private Long custProdModSysId;              // References `customer_product_modules` (`CUST_PROD_MOD_SYS_ID`)
    private String defaultURL;
    private String featureName;
    private String featureDesc;
    private String featureCode;
    private String featureType;
    private Integer default_val;                // for column named 'DEFAULT'
    private Integer activeStatusInd;
    private Date createdDate;
    private String createdBy;
    private Date inactivatedDate;
    private String inactivatedBy;
    private Date modifiedDate;
    private String modifiedBy;

    public CustProdModuleFeatures() {
    }

    public Long getCustProdModFeatureSysId() {
        return custProdModFeatureSysId;
    }

    public void setCustProdModFeatureSysId(Long custProdModFeatureSysId) {
        this.custProdModFeatureSysId = custProdModFeatureSysId;
    }

    public Long getCustProdModSysId() {
        return custProdModSysId;
    }

    public void setCustProdModSysId(Long custProdModSysId) {
        this.custProdModSysId = custProdModSysId;
    }

    public String getDefaultURL() {
        return defaultURL;
    }

    public void setDefaultURL(String defaultURL) {
        this.defaultURL = defaultURL;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureDesc() {
        return featureDesc;
    }

    public void setFeatureDesc(String featureDesc) {
        this.featureDesc = featureDesc;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public Integer getDefault_val() {
        return default_val;
    }

    public void setDefault_val(Integer default_val) {
        this.default_val = default_val;
    }

    public Integer getActiveStatusInd() {
        return activeStatusInd;
    }

    public void setActiveStatusInd(Integer activeStatusInd) {
        this.activeStatusInd = activeStatusInd;
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

package com.sncr.saw.security.app.model;

import java.io.Serializable;
import java.sql.Date;

public class Customer implements Serializable {

    private static final long serialVersionUID = 6710950219794990633L;

    public Customer() {
    }

    private Long custId;                    // NOT NULL
    private String custCode;                // NOT NULL
    private String companyName;             // NOT NULL
    private String companyBusiness;         // DEFAULT NULL
    private Long landingProdSysId;          // NOT NULL
    private Integer activeStatusInd;        // NOT NULL
    private Date createdDate;               // NOT NULL
    private String createdBy;               // NOT NULL
    private Date inactivatedDate;           // DEFAULT NULL
    private String inactivatedBy;           // DEFAULT NULL
    private Date modifiedDate;              // DEFAULT NULL
    private String modifiedBy;              // DEFAULT NULL
    private Integer passwordExpiryDate;     // NOT NULL
    private String domainName;              // NOT NULL


    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyBusiness() {
        return companyBusiness;
    }

    public void setCompanyBusiness(String companyBusiness) {
        this.companyBusiness = companyBusiness;
    }

    public Long getLandingProdSysId() {
        return landingProdSysId;
    }

    public void setLandingProdSysId(Long landingProdSysId) {
        this.landingProdSysId = landingProdSysId;
    }

    public Integer getActiveStatusInd() {
        return activeStatusInd;
    }

    public void setActiveStatusInd(Integer activeStatusInd) {
        this.activeStatusInd = activeStatusInd;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getInactivatedBy() {
        return inactivatedBy;
    }

    public void setInactivatedBy(String inactivatedBy) {
        this.inactivatedBy = inactivatedBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getPasswordExpiryDate() {
        return passwordExpiryDate;
    }

    public void setPasswordExpiryDate(Integer passwordExpiryDate) {
        this.passwordExpiryDate = passwordExpiryDate;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getInactivatedDate() {
        return inactivatedDate;
    }

    public void setInactivatedDate(Date inactivatedDate) {
        this.inactivatedDate = inactivatedDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}

package com.sncr.saw.security.app.model;

import java.sql.Date;

public class CustomerProduct {

    private Long custProdId;
    private Long custSysId;
    private Long prodSysId;
    private Integer activeStatusInd;
    private Date createdDate;
    private String createdBy;
    private Date inactivatedDate;
    private String inactivatedBy;
    private Date modifiedDate;
    private String modifiedBy;

    public CustomerProduct() {
    }

    public Long getCustProdId() {
        return custProdId;
    }

    public void setCustProdId(Long custProdId) {
        this.custProdId = custProdId;
    }

    public Long getCustSysId() {
        return custSysId;
    }

    public void setCustSysId(Long custSysId) {
        this.custSysId = custSysId;
    }

    public Long getProdSysId() {
        return prodSysId;
    }

    public void setProdSysId(Long prodSysId) {
        this.prodSysId = prodSysId;
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

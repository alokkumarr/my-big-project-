package com.sncr.saw.security.app.model;

import java.sql.Date;

public class ProductModules {

    private Long prodModId;
    private Long prodSysId;
    private Long moduleSysId;
    private Integer activeStatusInd;
    private Date createdDate;
    private String createdBy;
    private Date inactivatedDate;
    private String inactivatedBy;
    private Date modifiedDate;
    private String modifiedBy;

    public ProductModules() {
    }

    public Long getProdModId() {
        return prodModId;
    }

    public void setProdModId(Long prodModId) {
        this.prodModId = prodModId;
    }

    public Long getProdSysId() {
        return prodSysId;
    }

    public void setProdSysId(Long prodSysId) {
        this.prodSysId = prodSysId;
    }

    public Long getModuleSysId() {
        return moduleSysId;
    }

    public void setModuleSysId(Long moduleSysId) {
        this.moduleSysId = moduleSysId;
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

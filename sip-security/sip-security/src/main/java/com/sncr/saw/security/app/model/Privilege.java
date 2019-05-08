package com.sncr.saw.security.app.model;

import java.sql.Date;

public class Privilege {

    private Long privilegeSysId;                // generated auto increment
    private Long custProdSysId;                 // REFERENCES `customer_products` (`CUST_PROD_SYS_ID`)
    private Long custProdModSysId;
    private Long custProdModFeatureSysId;
    private Long roleSysId;
    private Long analysisSysId;                 // DEFAULT NULL
    private String privilegeCode;
    private String privilegeDesc;
    private Integer activeStatusInd;
    private Date createdDate;
    private String createdBy;
    private Date inactivatedDate;
    private String inactivatedBy;
    private Date modifiedDate;
    private String modifiedBy;

    public Privilege() {
    }

    public Long getPrivilegeSysId() {
        return privilegeSysId;
    }

    public void setPrivilegeSysId(Long privilegeSysId) {
        this.privilegeSysId = privilegeSysId;
    }

    public Long getCustProdSysId() {
        return custProdSysId;
    }

    public void setCustProdSysId(Long custProdSysId) {
        this.custProdSysId = custProdSysId;
    }

    public Long getCustProdModSysId() {
        return custProdModSysId;
    }

    public void setCustProdModSysId(Long custProdModSysId) {
        this.custProdModSysId = custProdModSysId;
    }

    public Long getCustProdModFeatureSysId() {
        return custProdModFeatureSysId;
    }

    public void setCustProdModFeatureSysId(Long custProdModFeatureSysId) {
        this.custProdModFeatureSysId = custProdModFeatureSysId;
    }

    public Long getRoleSysId() {
        return roleSysId;
    }

    public void setRoleSysId(Long roleSysId) {
        this.roleSysId = roleSysId;
    }

    public Long getAnalysisSysId() {
        return analysisSysId;
    }

    public void setAnalysisSysId(Long analysisSysId) {
        this.analysisSysId = analysisSysId;
    }

    public String getPrivilegeCode() {
        return privilegeCode;
    }

    public void setPrivilegeCode(String privilegeCode) {
        this.privilegeCode = privilegeCode;
    }

    public String getPrivilegeDesc() {
        return privilegeDesc;
    }

    public void setPrivilegeDesc(String privilegeDesc) {
        this.privilegeDesc = privilegeDesc;
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

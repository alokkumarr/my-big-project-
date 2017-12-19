package com.sncr.saw.security.common.bean.repo.admin.category;

/**
 * Created by pman0003 on 9/26/2017.
 */
public class SubCategoryWithPrivilegeDetails {
    private Long subCategoryId;
    private String subCategoryName;
    private Long privilegeCode;
    private Long privilegeId;
    private String subCategoryCode;

    /**
     * Gets subCategoryId
     *
     * @return value of subCategoryId
     */
    public long getSubCategoryId() {
        return subCategoryId;
    }

    /**
     * Sets value of subCategoryId
     */
    public void setSubCategoryId(long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    /**
     * Gets subCategoryName
     *
     * @return value of subCategoryName
     */
    public String getSubCategoryName() {
        return subCategoryName;
    }

    /**
     * Sets value of subCategoryName
     */
    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    /**
     * Gets privilegeCode
     *
     * @return value of privilegeCode
     */
    public Long getPrivilegeCode() {
        return privilegeCode;
    }

    /**
     * Sets value of privilegeCode
     */
    public void setPrivilegeCode(Long privilegeCode) {
        this.privilegeCode = privilegeCode;
    }

    /**
     * Gets privilegeId
     *
     * @return value of privilegeId
     */
    public long getPrivilegeId() {
        return privilegeId;
    }

    /**
     * Sets value of privilegeId
     */
    public void setPrivilegeId(long privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * Gets subCategoryCode
     *
     * @return value of subCategoryCode
     */
    public String getSubCategoryCode() {
        return subCategoryCode;
    }

    /**
     * Sets value of subCategoryCode
     */
    public void setSubCategoryCode(String subCategoryCode) {
        this.subCategoryCode = subCategoryCode;
    }
}

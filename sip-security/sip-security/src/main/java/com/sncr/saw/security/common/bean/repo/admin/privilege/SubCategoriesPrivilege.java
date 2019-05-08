package com.sncr.saw.security.common.bean.repo.admin.privilege;

/**
 * Created by pman0003 on 9/27/2017.
 */
public class SubCategoriesPrivilege {

    private Long privilegeCode;

    private String privilegeDesc;

    private Long privilegeId;

    private Long subCategoryId;

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
     * Gets privilegeDesc
     *
     * @return value of privilegeDesc
     */
    public String getPrivilegeDesc() {
        return privilegeDesc;
    }

    /**
     * Sets value of privilegeDesc
     */
    public void setPrivilegeDesc(String privilegeDesc) {
        this.privilegeDesc = privilegeDesc;
    }

    /**
     * Gets privilegeId
     *
     * @return value of privilegeId
     */
    public Long getPrivilegeId() {
        return privilegeId;
    }

    /**
     * Sets value of privilegeId
     */
    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * Gets subCategoryId
     *
     * @return value of subCategoryId
     */
    public Long getSubCategoryId() {
        return subCategoryId;
    }

    /**
     * Sets value of subCategoryId
     */
    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }
}

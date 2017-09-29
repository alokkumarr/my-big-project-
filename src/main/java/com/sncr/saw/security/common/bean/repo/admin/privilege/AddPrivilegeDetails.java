package com.sncr.saw.security.common.bean.repo.admin.privilege;

/**
 * Created by pman0003 on 9/27/2017.
 */
import java.util.List;

public class AddPrivilegeDetails {
    private String categoryCode;
    private Long categoryId;
    private String categoryType;
    private Long customerId;
    private String masterLoginId;
    private Long moduleId;
    private Long productId;
    private Long roleId;
    private List<SubCategoriesPrivilege> subCategoriesPrivilege = null;

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

    /**
     * Gets categoryId
     *
     * @return value of categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets value of categoryId
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets categoryType
     *
     * @return value of categoryType
     */
    public String getCategoryType() {
        return categoryType;
    }

    /**
     * Sets value of categoryType
     */
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
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
     * Gets masterLoginId
     *
     * @return value of masterLoginId
     */
    public String getMasterLoginId() {
        return masterLoginId;
    }

    /**
     * Sets value of masterLoginId
     */
    public void setMasterLoginId(String masterLoginId) {
        this.masterLoginId = masterLoginId;
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
     * Gets subCategoriesPrivilege
     *
     * @return value of subCategoriesPrivilege
     */
    public List<SubCategoriesPrivilege> getSubCategoriesPrivilege() {
        return subCategoriesPrivilege;
    }

    /**
     * Sets value of subCategoriesPrivilege
     */
    public void setSubCategoriesPrivilege(List<SubCategoriesPrivilege> subCategoriesPrivilege) {
        this.subCategoriesPrivilege = subCategoriesPrivilege;
    }
}
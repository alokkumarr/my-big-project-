package com.sncr.saw.security.common.util;

import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.external.response.CategoryDetails;
import com.sncr.saw.security.common.bean.external.response.CategoryList;
import com.sncr.saw.security.common.bean.external.response.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.AddPrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.SubCategoriesPrivilege;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.sip.utils.PrivilegeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class SecurityUtils {

  /**
   * Filter the list of category to return categories in response
   *
   * @param roleCategoryPrivilege
   * @return @list category details
   */
  public static List<CategoryDetails> getResponseCategoryDetails(RoleCategoryPrivilege roleCategoryPrivilege, RoleCatPrivilegeResponse response,
                                                                 List<com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails> customerCatList) {
    List<com.sncr.saw.security.common.bean.external.response.CategoryDetails> finalCategory = new ArrayList<>();
    if (customerCatList != null) {
      List<com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails> filterCategory = customerCatList.stream()
          .filter(cd -> cd.getModuleName().equalsIgnoreCase(roleCategoryPrivilege.getModuleName())
              && cd.getProductName().equalsIgnoreCase(roleCategoryPrivilege.getProductName())).collect(Collectors.toList());

      if (roleCategoryPrivilege.getCategory() != null && !roleCategoryPrivilege.getCategory().isEmpty()) {
        for (com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails details : roleCategoryPrivilege.getCategory()) {
          filterCategory.forEach(filterCat -> {
            String catName = details.getCategoryName();
            if (filterCat.getCategoryName().equalsIgnoreCase(catName)) {
              com.sncr.saw.security.common.bean.external.response.CategoryDetails catDetails = new com.sncr.saw.security.common.bean.external.response.CategoryDetails();
              catDetails.setActiveStatusInd(filterCat.getActiveStatusInd());
              catDetails.setCategoryCode(filterCat.getCategoryCode());
              catDetails.setCategoryName(filterCat.getCategoryName());
              catDetails.setCategoryType(filterCat.getCategoryType());
              catDetails.setCategoryDesc(filterCat.getCategoryDesc() != null ? filterCat.getCategoryDesc() : null);
              catDetails.setCustomerId(filterCat.getCustomerId());
              catDetails.setCategoryId(filterCat.getCategoryId());
              List<com.sncr.saw.security.common.bean.external.response.SubCategoryDetails> subCategoryList = buildSubCategoryDetails(filterCat);
              catDetails.setSubCategory(subCategoryList);
              response.setProductId(filterCat.getProductId());
              response.setModuleId(filterCat.getModuleId());
              finalCategory.add(catDetails);
            }
          });
        }
      }
    }
    return finalCategory;
  }

  /**
   * Build subcategory response from the Filter subcategory.
   *
   * @param filterCat
   * @return sub category response
   */
  private static List<com.sncr.saw.security.common.bean.external.response.SubCategoryDetails> buildSubCategoryDetails(
      com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails filterCat) {
    List<com.sncr.saw.security.common.bean.external.response.SubCategoryDetails> subCategoryList = new ArrayList<>();
    if (filterCat.getSubCategory() != null && !filterCat.getSubCategory().isEmpty()) {
      filterCat.getSubCategory().forEach(subDetails -> {
        com.sncr.saw.security.common.bean.external.response.SubCategoryDetails subCategoryDetails =
            new com.sncr.saw.security.common.bean.external.response.SubCategoryDetails();
        subCategoryDetails.setActiveStatusInd(subDetails.getActivestatusInd());
        subCategoryDetails.setSubCategoryId(subDetails.getSubCategoryId());
        subCategoryDetails.setSubCategoryName(subDetails.getSubCategoryName());
        subCategoryDetails.setSubCategoryDesc(subDetails.getSubCategoryDesc());
        subCategoryList.add(subCategoryDetails);
      });
    }
    return subCategoryList;
  }

  /**
   * Build category bean from the category and sub category details
   *
   * @param customerSysId
   * @param category
   * @param subCategoryDetails
   * @return CategoryDetails
   */
  public static com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails buildCategoryBean(Long customerSysId,
                                                                                                        com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails category,
                                                                                                        SubCategoryDetails subCategoryDetails) {
    com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails categoryDetails = new com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails();
    categoryDetails.setCustomerId(customerSysId);
    if (subCategoryDetails != null && subCategoryDetails.getSubCategoryName() != null) {
      categoryDetails.setCategoryName(subCategoryDetails.getSubCategoryName());
    } else {
      categoryDetails.setCategoryName(category.getCategoryName());
    }
    if (subCategoryDetails != null && subCategoryDetails.getSubCategoryDesc() != null) {
      categoryDetails.setCategoryDesc(subCategoryDetails.getSubCategoryDesc());
    } else {
      categoryDetails.setCategoryDesc(category.getCategoryDesc());
    }
    categoryDetails.setActiveStatusInd(1L);
    return categoryDetails;
  }

  /**
   * Build role details bean from role, customerSysId, master login id
   *
   * @param masterLoginId
   * @param customerSysId
   * @param inputRole
   * @return
   */
  public static RoleDetails buildRoleDetails(String masterLoginId, Long customerSysId, com.sncr.saw.security.common.bean.Role inputRole) {
    RoleDetails role = new RoleDetails();
    role.setActiveStatusInd(Boolean.valueOf(inputRole.getActiveStatusInd()) ? "1" : "0");
    role.setCustomerCode(inputRole.getCustomerCode());
    role.setMasterLoginId(masterLoginId);
    role.setRoleName(inputRole.getRoleName());
    role.setRoleDesc(inputRole.getRoleDesc());
    role.setRoleType(RoleType.fromValue(inputRole.getRoleType()));
    role.setCustSysId(customerSysId);
    return role;
  }

  /**
   * Build the privilege bean from role/category/privileges bean
   *
   * @param masterLoginId
   * @param response
   * @param roleId
   * @param category
   * @param privileges
   * @return AddPrivilegeDetails bean
   */
  public static AddPrivilegeDetails buildPrivilegeBean(String masterLoginId, RoleCatPrivilegeResponse response, Long roleId,
                                                       com.sncr.saw.security.common.bean.external.response.CategoryDetails category,
                                                       List<String> privileges) {
    Long subCategoryId = category.getSubCategory().get(0).getSubCategoryId();
    AddPrivilegeDetails privilegeDetails = new AddPrivilegeDetails();
    privilegeDetails.setCategoryCode(category.getCategoryCode());
    privilegeDetails.setCategoryId(category.getCategoryId());
    privilegeDetails.setCategoryType(category.getCategoryType());
    privilegeDetails.setCustomerId(category.getCustomerId());
    privilegeDetails.setProductId(response.getProductId());
    privilegeDetails.setModuleId(response.getModuleId());
    privilegeDetails.setMasterLoginId(masterLoginId);
    privilegeDetails.setRoleId(roleId);

    List<SubCategoriesPrivilege> subCategoriesPrivilegeList = new ArrayList<>();
    SubCategoriesPrivilege subCategoriesPrivilege = new SubCategoriesPrivilege();
    Long privilegesCode = PrivilegeUtils.getPrivilegeCode(privileges);
    subCategoriesPrivilege.setPrivilegeCode(privilegesCode);
    subCategoriesPrivilege.setSubCategoryId(subCategoryId);
    subCategoriesPrivilege.setPrivilegeDesc(String.join(",", privileges));
    subCategoriesPrivilege.setPrivilegeId(0L);
    subCategoriesPrivilegeList.add(subCategoriesPrivilege);
    privilegeDetails.setSubCategoriesPrivilege(subCategoriesPrivilegeList);
    return privilegeDetails;
  }

  /**
   * Fetch the category details bases upon the product / module Id
   *
   * @param categoryPrivilege
   * @param customerCatList
   * @return list of category details
   */
  public static List<CategoryDetails> fetchResponseCategoryDetails(RoleCategoryPrivilege categoryPrivilege,
                                                                   List<com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails> customerCatList) {

    List<com.sncr.saw.security.common.bean.external.response.CategoryDetails> finalCategory = new ArrayList<>();
    if (customerCatList != null) {
      List<com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails> filterCategory = customerCatList.stream()
          .filter(cd -> cd.getModuleName().equalsIgnoreCase(categoryPrivilege.getModuleName())
              && cd.getProductName().equalsIgnoreCase(categoryPrivilege.getProductName()))
          .collect(Collectors.toList());

      filterCategory.forEach(filterCat -> {
        com.sncr.saw.security.common.bean.external.response.CategoryDetails catDetails =
            new com.sncr.saw.security.common.bean.external.response.CategoryDetails();
        catDetails.setActiveStatusInd(filterCat.getActiveStatusInd() != null ? filterCat.getActiveStatusInd() : 0L);
        catDetails.setCategoryCode(filterCat.getCategoryCode());
        catDetails.setCategoryName(filterCat.getCategoryName());
        catDetails.setCategoryType(filterCat.getCategoryType());
        catDetails.setCategoryDesc(filterCat.getCategoryDesc());
        catDetails.setCustomerId(filterCat.getCustomerId());
        catDetails.setCategoryId(filterCat.getCategoryId());
        catDetails.setSubCategory(buildSubCategoryDetails(filterCat));
        finalCategory.add(catDetails);
      });
    }
    return finalCategory;
  }

  /**
   * Build response with the message
   *
   * @param catList
   * @param message
   */
  public static void buildMessage(CategoryList catList, String message, boolean haveCategory) {
    if (haveCategory) {
      catList.setCategories(null);
    }
    catList.setValid(false);
    catList.setMessage(message);
  }
}

package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.repository.RoleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.external.response.CategoryList;
import com.sncr.saw.security.common.bean.external.response.Role;
import com.sncr.saw.security.common.bean.external.response.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.AddPrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.util.SecurityUtils;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to perform the external security related operation
 *
 * @author alok.kumarr
 * @since 3.5.0
 */
@Service
public class ExternalSecurityService {

  private static final Logger logger = LoggerFactory.getLogger(ExternalSecurityService.class);

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserRepository userRepository;

  /**
   * Create Role , Category, Subcategory and Privilege
   *
   * @param httpResponse
   * @param roleCategoryPrivilege
   * @param masterLoginId
   * @param moduleDetails
   * @param customerSysId
   */
  public RoleCatPrivilegeResponse createRoleCategoryPrivilege(HttpServletResponse httpResponse, RoleCategoryPrivilege roleCategoryPrivilege,
                                                              String masterLoginId, ProductModuleDetails moduleDetails, Long customerSysId) {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    // add the product/module id
    response.setProductId(moduleDetails.getProductId());
    response.setModuleId(moduleDetails.getModuleId());
    response.setProductName(roleCategoryPrivilege.getProductName());
    response.setModuleName(roleCategoryPrivilege.getModuleName());

    com.sncr.saw.security.common.bean.Role inputRole = roleCategoryPrivilege.getRole();
    Role responseRole = new Role();
    if (inputRole != null) {
      if (inputRole.getCustomerCode() == null || inputRole.getCustomerCode().isEmpty()) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Customer Code can't be blank or empty.");
      } else if (inputRole.getRoleType() == null || inputRole.getRoleType().isEmpty()) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Role Type can't be blank or empty.");
      } else if (!RoleType.validRoleType(inputRole.getRoleType())) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Only ADMIN|USER Role Type are allowed.");
      } else if (inputRole.getRoleName() == null || inputRole.getRoleName().isEmpty()) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Role Name can't be blank or empty.");
      } else if (inputRole.getCustomerCode() != null && inputRole.isAutoCreate()
          && !roleRepository.validateRoleByIdAndCustomerCode(customerSysId, inputRole)) {
        // build role details bean from input
        RoleDetails role = SecurityUtils.buildRoleDetails(masterLoginId, customerSysId, inputRole);
        try {
          if (role != null) {
            Valid valid = userRepository.addRole(role);
            if (valid.getValid()) {
              // fetched only matched roles
              userRepository.getRoles(role.getCustSysId()).stream().forEach(fetchedRole -> {
                if (inputRole.getRoleName().equalsIgnoreCase(fetchedRole.getRoleName())) {
                  String customerCode = fetchedRole.getCustomerCode() != null ? fetchedRole.getCustomerCode() : inputRole.getCustomerCode();
                  responseRole.setCustomerCode(customerCode);
                  responseRole.setActiveStatusInd(fetchedRole.getActiveStatusInd());
                  responseRole.setRoleSysId(fetchedRole.getRoleSysId());
                  responseRole.setCustomerSysId(fetchedRole.getCustSysId());
                  responseRole.setRoleName(fetchedRole.getRoleName());
                  responseRole.setRoleDesc(fetchedRole.getRoleDesc());
                  responseRole.setRoleType(fetchedRole.getRoleType());
                }
              });
              response.setValid(true);
              responseRole.setMessage("Role created for Customer Product Module Combination.");
            } else {
              response.setValid(false);
              responseRole.setMessage("Role could not be added. " + valid.getError());
            }
          } else {
            response.setValid(false);
            responseRole.setMessage("Mandatory request params are missing for roles.");
          }
        } catch (Exception e) {
          response.setValid(false);
          String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
          responseRole.setMessage(message + " Please contact server Administrator for Roles.");
        }
      } else {
        response.setValid(false);
        String roleMessage = !inputRole.isAutoCreate() ? "Role can't be add for flag false."
            : "Role already exist in the system for Customer Product Module Combination.";
        responseRole.setMessage(roleMessage);
      }
      response.setRole(responseRole);
    }

    // add category
    CategoryList catList = new CategoryList();
    List<CategoryDetails> categoryPrivilegeLis = roleCategoryPrivilege.getCategory();
    if (categoryPrivilegeLis != null && !categoryPrivilegeLis.isEmpty()) {
      for (CategoryDetails category : categoryPrivilegeLis) {
        if (category.isAutoCreate()) {
          CategoryDetails categoryDetails = SecurityUtils.buildCategoryBean(customerSysId, category, null);
          categoryDetails.setProductId(moduleDetails.getProductId());
          categoryDetails.setModuleId(moduleDetails.getModuleId());
          categoryDetails.setMasterLoginId(masterLoginId);
          // add category
          addCategory(catList, categoryDetails, roleCategoryPrivilege, response);
          response.setCategoryList(catList);

          if (catList.getValid()) {
            List<SubCategoryDetails> subCategories = category.getSubCategory();
            if (subCategories != null && !subCategories.isEmpty()) {
              for (SubCategoryDetails subCategoryDetails : subCategories) {
                if (subCategoryDetails.isAutoCreate()) {
                  CategoryDetails details = SecurityUtils.buildCategoryBean(customerSysId, null, subCategoryDetails);
                  details.setProductId(moduleDetails.getProductId());
                  details.setModuleId(moduleDetails.getModuleId());
                  details.setMasterLoginId(masterLoginId);
                  List<com.sncr.saw.security.common.bean.external.response.CategoryDetails> categoryList = catList.getCategories() != null
                      ? catList.getCategories() : new ArrayList<>();
                  String categoryCode = categoryList.stream().filter(cat -> category.getCategoryName().equalsIgnoreCase(cat.getCategoryName()))
                      .findAny().get().getCategoryCode();
                  details.setCategoryCode(categoryCode);

                  // add sub categories
                  boolean checkSubCategory = userRepository.checkSubCatExists(details);
                  if (!checkSubCategory) {
                    details.setSubCategoryInd(subCategoryDetails.isAutoCreate());
                    addCategory(catList, details, roleCategoryPrivilege, response);
                    response.setCategoryList(catList);

                    // add privilege for the subcategory
                    if (subCategoryDetails.getPrivilege() != null && !subCategoryDetails.getPrivilege().isEmpty() && catList.getCategories() != null) {
                      com.sncr.saw.security.common.bean.external.response.CategoryDetails detailsCategory = catList.getCategories().stream()
                          .filter(cat -> category.getCategoryName().equalsIgnoreCase(cat.getCategoryName()))
                          .findAny().get();

                      if (responseRole.getRoleSysId() > 0) {
                        AddPrivilegeDetails privilegeDetails = SecurityUtils.buildPrivilegeBean(masterLoginId, response, responseRole.getRoleSysId(), detailsCategory, subCategoryDetails.getPrivilege());
                        Valid valid = userRepository.upsertPrivilege(privilegeDetails);
                        if (valid.getValid()) {
                          String pMessage = "Category,Subcategory,Privileges added : " + String.join(",", subCategoryDetails.getPrivilege());
                          detailsCategory.getSubCategory().forEach(subCat -> subCat.setPrivilege(subCategoryDetails.getPrivilege()));
                          response.setMessage(pMessage);
                        }
                      } else {
                        response.getCategoryList().setValid(false);
                        response.getCategoryList().setMessage("Privileges can't be added without role Id.");
                      }
                    }
                  } else {
                    SecurityUtils.buildMessage(catList, "Sub Category Name already exists for this Customer Product Module Combination.", false);
                    response.setCategoryList(catList);
                  }
                } else {
                  SecurityUtils.buildMessage(catList, "Sub categories can't be add for flag false.", false);
                  response.setCategoryList(catList);
                }
              }
            }
          }
        } else {
          SecurityUtils.buildMessage(catList, "Categories can't be add for flag false.", false);
          response.setCategoryList(catList);
        }
      }
    }
    logger.trace("Role , category, privilege Response : {}", response.toString());
    return response;
  }

  /**
   * Add category details and build messge.
   *
   * @param catList
   * @param categoryDetails
   * @param request
   * @param response
   */
  private void addCategory(CategoryList catList, CategoryDetails categoryDetails, RoleCategoryPrivilege request, RoleCatPrivilegeResponse response) {
    try {
      if (categoryDetails != null) {
        if (!userRepository.checkIsModulePresent(categoryDetails.getModuleId(), "ALERTS")) {
          boolean checkCatExist = !categoryDetails.isSubCategoryInd() ? !userRepository.checkCatExists(categoryDetails) : false;
          boolean checkSubCatExist = categoryDetails.isSubCategoryInd() ? userRepository.checkSubCatExists(categoryDetails) : false;
          if (checkCatExist || !checkSubCatExist) {
            addCategorySubcategory(catList, categoryDetails, request, response);
          } else if (checkCatExist) {
            SecurityUtils.buildMessage(catList, "Category Name already exists for this Customer Product Module Combination.", false);
          } else if (checkSubCatExist) {
            SecurityUtils.buildMessage(catList, "Sub Category Name already exists for this Customer Product Module Combination.", false);
          }
        } else {
          SecurityUtils.buildMessage(catList, "Adding Categories and Sub Categories for Alert Module is not allowed.", false);
        }
      } else {
        SecurityUtils.buildMessage(catList, "Mandatory request params are missing.", false);
      }
    } catch (Exception e) {
      String message = (e instanceof DataAccessException) ? "Database error. Please contact server Administrator."
          : "Error. Please contact server Administrator";
      SecurityUtils.buildMessage(catList, message, true);
    }
  }

  /**
   * Build category and subcategory.
   *
   * @param catList
   * @param categoryDetails
   * @param request
   * @param response
   */
  private void addCategorySubcategory(CategoryList catList, CategoryDetails categoryDetails, RoleCategoryPrivilege request, RoleCatPrivilegeResponse response) {
    Valid valid = userRepository.addCategory(categoryDetails);
    if (valid.getValid()) {
      List<CategoryDetails> customerCatList =
          userRepository.getCategories(categoryDetails.getCustomerId());
      catList.setCategories(SecurityUtils.getResponseCategoryDetails(request, response, customerCatList));
      catList.setValid(true);
      catList.setMessage("Category/SubCategory created for Customer Product Module Combination.");
    } else {
      SecurityUtils.buildMessage(catList, "Category/SubCategory could not be added. " + valid.getError(), true);
    }
  }

  /**
   * Fetch role, category, subcategory and privilege
   *
   * @param request
   * @param productName
   * @param moduleName
   * @param moduleDetails
   * @param customerSysId
   * @return response
   */
  public RoleCatPrivilegeResponse fetchRoleCategoryPrivilege(@RequestBody RoleCategoryPrivilege request, String productName, String moduleName, ProductModuleDetails moduleDetails, Long customerSysId) {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    // fetch roles
    Role responseRole = new Role();
    if (request.getRole().getRoleName() != null && !request.getRole().getRoleName().isEmpty()) {
      userRepository.getRoles(customerSysId).stream().forEach(fetchedRole -> {
        if (request.getRole().getRoleName().equalsIgnoreCase(fetchedRole.getRoleName())) {
          responseRole.setCustomerCode(fetchedRole.getCustomerCode());
          responseRole.setActiveStatusInd(fetchedRole.getActiveStatusInd());
          responseRole.setRoleSysId(fetchedRole.getRoleSysId());
          responseRole.setCustomerSysId(fetchedRole.getCustSysId());
          responseRole.setRoleName(fetchedRole.getRoleName());
          responseRole.setRoleDesc(fetchedRole.getRoleDesc());
          responseRole.setRoleType(fetchedRole.getRoleType());
          responseRole.setMessage("Role fetched for Customer Product Module Combination.");

          // fetch category/subcategory for this role
          CategoryList categoryList = new CategoryList();
          List<CategoryDetails> customerCatList =
              userRepository.getCategories(customerSysId);
          categoryList.setCategories(SecurityUtils.fetchResponseCategoryDetails(request, customerCatList));
          categoryList.setMessage("Category/Subcategory fetched for Customer Product Module Combination.");

          response.setValid(true);
          response.setRole(responseRole);
          response.setCategoryList(categoryList);
        }
      });
    }

    // add the product/module id
    response.setProductName(productName);
    response.setModuleName(moduleName);
    response.setProductId(moduleDetails.getProductId());
    response.setModuleId(moduleDetails.getModuleId());
    return response;
  }
}

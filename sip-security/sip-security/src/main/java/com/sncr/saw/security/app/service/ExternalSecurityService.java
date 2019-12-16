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
import com.sncr.saw.security.common.bean.repo.admin.privilege.SubCategoriesPrivilege;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.sip.utils.PrivilegeUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

  private final static String NAME_REGEX = "[`~!@#$%^&*()+={}|\"':;?/>.<,*:/?\\[\\]\\\\]";

  private boolean haveCategoryCheck = false;
  private boolean haveSubCategoryFlag = false;

  /**
   * Create Role , Category, Subcategory and Privilege
   *
   * @param httpResponse
   * @param roleCategoryPrivilege
   * @param masterLoginId
   * @param moduleDetails
   */
  public RoleCatPrivilegeResponse createRoleCategoryPrivilege(HttpServletResponse httpResponse, RoleCategoryPrivilege roleCategoryPrivilege,
                                                              String masterLoginId, ProductModuleDetails moduleDetails) {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    // add the product/module id
    response.setProductId(moduleDetails.getProductId());
    response.setModuleId(moduleDetails.getModuleId());
    response.setProductName(roleCategoryPrivilege.getProductName());
    response.setModuleName(roleCategoryPrivilege.getModuleName());
    Long customerId = moduleDetails.getCustomerSysId();
    com.sncr.saw.security.common.bean.Role inputRole = roleCategoryPrivilege.getRole();
    RoleDetails roleDetails = new RoleDetails();
    Role responseRole = new Role();
    if (inputRole != null) {
      if (inputRole.getCustomerCode() == null || inputRole.getCustomerCode().isEmpty()) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Customer Code can't be blank or empty.");
      } else if (!inputRole.getCustomerCode().equalsIgnoreCase(moduleDetails.getCustomerCode())) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Customer Code not matched with the user ticket.");
      } else if (inputRole.getRoleType() == null || inputRole.getRoleType().isEmpty()) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Role Type can't be blank or empty.");
      } else if (!RoleType.validRoleType(inputRole.getRoleType())) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Only ADMIN|USER Role Type are allowed.");
      } else if (inputRole.getRoleName() == null || inputRole.getRoleName().isEmpty()) {
        httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        responseRole.setMessage("Role Name can't be blank or empty.");
      } else if (inputRole.isAutoCreate()) {
        roleDetails = roleRepository.fetchRoleByIdAndCustomerCode(customerId, inputRole);
        if (roleDetails.getRoleSysId() == 0 || roleDetails.getRoleName() == null || roleDetails.getRoleName().isEmpty()) {
          // build role details bean from input
          RoleDetails role = buildRoleDetails(masterLoginId, moduleDetails, inputRole);
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
          responseRole.setRoleSysId(roleDetails.getRoleSysId());
          responseRole.setRoleName(roleDetails.getRoleName());
          responseRole.setRoleDesc(inputRole.getRoleDesc());
          responseRole.setActiveStatusInd(roleDetails.getActiveStatusInd());
          responseRole.setCustomerSysId(moduleDetails.getCustomerSysId());
          responseRole.setCustomerCode(moduleDetails.getCustomerCode());
          responseRole.setRoleType(RoleType.valueOf(inputRole.getRoleType()));
          responseRole.setMessage("Role already exist in the system for Customer Product Module Combination.");
        }
      } else {
        response.setValid(false);
        responseRole.setMessage("Role can't be add for flag false.");
      }
      response.setRole(responseRole);
    }

    // add category
    CategoryList catList = new CategoryList();
    List<CategoryDetails> categoryPrivilegeLis = roleCategoryPrivilege.getCategories();
    if (categoryPrivilegeLis != null && !categoryPrivilegeLis.isEmpty()) {
      for (CategoryDetails category : categoryPrivilegeLis) {
        if (category.isAutoCreate()) {
          CategoryDetails categoryDetails = buildCategoryBean(customerId, category, null);
          categoryDetails.setProductId(moduleDetails.getProductId());
          categoryDetails.setModuleId(moduleDetails.getModuleId());
          categoryDetails.setMasterLoginId(masterLoginId);
          // add category
          addCategory(catList, categoryDetails, roleCategoryPrivilege, response);
          response.setCategoryList(catList);

          if (catList.getValid() || haveCategoryCheck) {
            List<SubCategoryDetails> subCategories = category.getSubCategories();
            if (subCategories != null && !subCategories.isEmpty()) {
              for (SubCategoryDetails subCategoryDetails : subCategories) {
                boolean validPrivileges = subCategoryDetails.getPrivilege().stream().allMatch(s -> PrivilegeUtils.validPrivilege(s));
                if (subCategoryDetails.isAutoCreate() && validPrivileges) {
                  CategoryDetails details = buildCategoryBean(customerId, null, subCategoryDetails);
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
                    addSubCategory(catList, details, roleCategoryPrivilege, response);
                    response.setCategoryList(catList);

                    // add privilege for the subcategory
                    if (subCategoryDetails.getPrivilege() != null && !subCategoryDetails.getPrivilege().isEmpty() && catList.getCategories() != null) {
                      com.sncr.saw.security.common.bean.external.response.CategoryDetails detailsCategory = catList.getCategories().stream()
                          .filter(cat -> category.getCategoryName().equalsIgnoreCase(cat.getCategoryName()))
                          .findAny().get();

                      if (responseRole.getRoleSysId() > 0) {
                        List<String> privilege = subCategoryDetails.getPrivilege();
                        String subCategoryName = subCategoryDetails.getSubCategoryName();
                        AddPrivilegeDetails privilegeDetails = buildPrivilegeBean(masterLoginId, response, responseRole.getRoleSysId(), detailsCategory, privilege, subCategoryName);
                        Valid valid = userRepository.upsertPrivilege(privilegeDetails);
                        if (valid.getValid()) {
                          String pMessage = "Privileges added for sub category: " + String.join(",", privilege);
                          detailsCategory.getSubCategory().forEach(subCat -> subCat.setPrivilege(privilege));
                          response.setMessage(pMessage);
                        }
                      } else {
                        response.getCategoryList().setValid(false);
                        response.getCategoryList().setMessage("Privileges can't be added without role Id.");
                      }
                    }
                  } else {
                    buildMessage(catList, "Sub Category Name already exists for this Customer Product Module Combination.", false, false);
                    response.setCategoryList(catList);
                  }
                } else {
                  if (!subCategoryDetails.isAutoCreate()) {
                    buildMessage(catList, "Sub categories can't be add/fetch for flag false.", false, true);
                  } else if (!validPrivileges) {
                    buildMessage(catList, "Please provide the correct privileges for subcategory.", false, true);
                  }
                  response.setCategoryList(catList);
                }
              }
            }
          }
        } else {
          buildMessage(catList, "Categories can't be add/fetch for flag false.", true, false);
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
          boolean checkCatExist = userRepository.checkCatExists(categoryDetails);
          haveCategoryCheck = checkCatExist;
          if (!checkCatExist && !categoryDetails.isSubCategoryInd()) {
            addCategorySubcategory(catList, categoryDetails, request, response, true, false);
          } else {
            buildCategoryResponse(catList, categoryDetails, request, response);
            buildMessage(catList, "Category Name already exists for this Customer Product Module Combination.", false, false);
          }
        } else {
          buildMessage(catList, "Adding Categories and Sub Categories for Alert Module is not allowed.", false, false);
        }
      } else {
        buildMessage(catList, "Mandatory request params are missing.", false, false);
      }
    } catch (Exception e) {
      String message = (e instanceof DataAccessException) ? "Database error. Please contact server Administrator."
          : "Error. Please contact server Administrator";
      buildMessage(catList, message, true, false);
    }
  }


  /**
   * Add category details and build messge.
   *
   * @param catList
   * @param categoryDetails
   * @param request
   * @param response
   */
  private void addSubCategory(CategoryList catList, CategoryDetails categoryDetails, RoleCategoryPrivilege request, RoleCatPrivilegeResponse response) {
    try {
      if (categoryDetails != null) {
        if (!userRepository.checkIsModulePresent(categoryDetails.getModuleId(), "ALERTS")) {
          boolean checkSubCatExist = categoryDetails.isSubCategoryInd() ? userRepository.checkSubCatExists(categoryDetails) : false;
          if (categoryDetails.isSubCategoryInd()) {
            if (!checkSubCatExist) {
              addCategorySubcategory(catList, categoryDetails, request, response, false, true);
            } else if (!haveSubCategoryFlag) {
              buildMessage(catList, "Sub categories can't be add/fetch for flag false.", false, false);
            } else {
              buildMessage(catList, "Sub Category Name already exists for this Customer Product Module Combination.", false, false);
            }
          }
        } else {
          buildMessage(catList, "Adding Categories and Sub Categories for Alert Module is not allowed.", true, false);
        }
      } else {
        buildMessage(catList, "Mandatory request params are missing.", true, false);
      }
    } catch (Exception e) {
      String message = (e instanceof DataAccessException) ? "Database error. Please contact server Administrator."
          : "Error. Please contact server Administrator";
      buildMessage(catList, message, true, false);
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
  private void addCategorySubcategory(CategoryList catList, CategoryDetails categoryDetails, RoleCategoryPrivilege request, RoleCatPrivilegeResponse response, boolean isCat, boolean isSubCat) {
    Valid valid = userRepository.addCategory(categoryDetails);
    if (valid.getValid()) {
      buildCategoryResponse(catList, categoryDetails, request, response);
      catList.setValid(true);
      catList.setMessage("Category/SubCategory created for Customer Product Module Combination.");
    } else {
      buildMessage(catList, "Category/SubCategory already exist in the system.", false, false);
    }
  }

  private void buildCategoryResponse(CategoryList catList, CategoryDetails categoryDetails, RoleCategoryPrivilege request, RoleCatPrivilegeResponse response) {
    List<CategoryDetails> customerCatList =
        userRepository.getCategories(categoryDetails.getCustomerId());
    catList.setCategories(getResponseCategoryDetails(request, response, customerCatList));
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
      List<RoleDetails> roles = userRepository.getRoles(customerSysId);
      if (roles != null && !roles.isEmpty()) {
        List<RoleDetails> rolesList = roles.stream().filter(roleDetails -> request.getRole().getRoleName().trim().equalsIgnoreCase(roleDetails.getRoleName())).collect(Collectors.toList());
        if (rolesList != null && !rolesList.isEmpty()) {
          rolesList.forEach(fetchedRole -> {
            String customerCode = fetchedRole.getCustomerCode() != null && !fetchedRole.getCustomerCode().isEmpty()
                ? fetchedRole.getCustomerCode() : moduleDetails.getCustomerCode();
            responseRole.setCustomerCode(customerCode);
            responseRole.setActiveStatusInd(fetchedRole.getActiveStatusInd());
            responseRole.setRoleSysId(fetchedRole.getRoleSysId());
            responseRole.setCustomerSysId(fetchedRole.getCustSysId());
            responseRole.setRoleName(fetchedRole.getRoleName());
            responseRole.setRoleDesc(fetchedRole.getRoleDesc());
            responseRole.setRoleType(fetchedRole.getRoleType());
            responseRole.setMessage("Role fetched for Customer Product Module Combination.");

            // fetch category/subcategory for this role
            CategoryList categoryList = new CategoryList();
            if ("Active".equalsIgnoreCase(fetchedRole.getActiveStatusInd())) {
              List<CategoryDetails> customerCatList =
                  userRepository.getCategories(customerSysId);
              categoryList.setCategories(fetchResponseCategoryDetails(request, customerCatList));
              categoryList.setMessage("Category/Subcategory fetched for Customer Product Module Combination.");
              categoryList.setValid(true);
            } else {
              categoryList.setMessage("No Category/Subcategory displayed for Inactive Roles.");
              categoryList.setValid(false);
            }
            response.setValid(true);
            response.setCategoryList(categoryList);
            response.setMessage("Record fetched successfully for Customer Role, Product and Module Combination.");
          });
        } else {
          response.setValid(false);
          response.setMessage("Role Name not exist in system for Product and Module Combination.");
        }
      } else {
        response.setValid(false);
        response.setMessage("No details fetched for Customer Role, Product and Module Combination.");
      }
    }

    // add the product/module id
    response.setProductName(productName);
    response.setModuleName(moduleName);
    response.setRole(responseRole);
    response.setProductId(moduleDetails.getProductId());
    response.setModuleId(moduleDetails.getModuleId());
    return response;
  }

  /**
   * Filter the list of category to return categories in response
   *
   * @param request
   * @return @list category details
   */
  public static List<com.sncr.saw.security.common.bean.external.response.CategoryDetails> getResponseCategoryDetails(RoleCategoryPrivilege request, RoleCatPrivilegeResponse response,
                                                                                                                     List<com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails> customerCatList) {
    List<com.sncr.saw.security.common.bean.external.response.CategoryDetails> finalCategory = new ArrayList<>();
    if (customerCatList != null) {
      List<com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails> filterCategory = customerCatList.stream()
          .filter(cd -> cd.getModuleName().equalsIgnoreCase(request.getModuleName())
              && cd.getProductName().equalsIgnoreCase(request.getProductName())).collect(Collectors.toList());

      if (request.getCategories() != null && !request.getCategories().isEmpty()) {
        for (com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails details : request.getCategories()) {
          List<SubCategoryDetails> requestSubCat = details.getSubCategories();
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
              List<com.sncr.saw.security.common.bean.external.response.SubCategoryDetails> subCategoryList = buildSubCategoryDetails(requestSubCat, filterCat);
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
  private static List<com.sncr.saw.security.common.bean.external.response.SubCategoryDetails> buildSubCategoryDetails(List<SubCategoryDetails> subCatList,
                                                                                                                      com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails filterCat) {
    List<com.sncr.saw.security.common.bean.external.response.SubCategoryDetails> subCategoryList = new ArrayList<>();
    if (filterCat.getSubCategories() != null && !filterCat.getSubCategories().isEmpty()) {
      filterCat.getSubCategories().forEach(subDetails -> {
        boolean matchedSub = subCatList.stream().anyMatch(subCat -> subCat.getSubCategoryName().trim().equalsIgnoreCase(subDetails.getSubCategoryName()));
        if (matchedSub) {
          com.sncr.saw.security.common.bean.external.response.SubCategoryDetails subCategoryDetails =
              new com.sncr.saw.security.common.bean.external.response.SubCategoryDetails();
          subCategoryDetails.setActiveStatusInd(subDetails.getActivestatusInd());
          subCategoryDetails.setSubCategoryId(subDetails.getSubCategoryId());
          subCategoryDetails.setSubCategoryName(subDetails.getSubCategoryName());
          subCategoryDetails.setSubCategoryDesc(subDetails.getSubCategoryDesc());
          subCategoryList.add(subCategoryDetails);
        }
      });
    }
    return subCategoryList;
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
    if (filterCat.getSubCategories() != null && !filterCat.getSubCategories().isEmpty()) {
      filterCat.getSubCategories().forEach(subDetails -> {
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
   * @param moduleDetails
   * @param inputRole
   * @return
   */
  public static RoleDetails buildRoleDetails(String masterLoginId, ProductModuleDetails moduleDetails, com.sncr.saw.security.common.bean.Role inputRole) {
    RoleDetails role = new RoleDetails();
    role.setActiveStatusInd(Boolean.valueOf(inputRole.getActiveStatusInd()) ? "1" : "0");
    role.setCustomerCode(moduleDetails.getCustomerCode());
    role.setMasterLoginId(masterLoginId);
    role.setRoleName(inputRole.getRoleName());
    role.setRoleDesc(inputRole.getRoleDesc());
    role.setRoleType(RoleType.fromValue(inputRole.getRoleType()));
    role.setCustSysId(moduleDetails.getCustomerSysId());
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
                                                       List<String> privileges, String subCategoryName) {

    AddPrivilegeDetails privilegeDetails = new AddPrivilegeDetails();
    if (category.getSubCategory() != null && !category.getSubCategory().isEmpty()) {
      category.getSubCategory().forEach(subCategoryDetails -> {
        if (subCategoryDetails.getSubCategoryName().equalsIgnoreCase(subCategoryName)) {
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
          Long subCategoryId = subCategoryDetails.getSubCategoryId();
          Long privilegesCode = PrivilegeUtils.getPrivilegeCode(privileges);

          subCategoriesPrivilege.setPrivilegeCode(privilegesCode);
          subCategoriesPrivilege.setSubCategoryId(subCategoryId);
          subCategoriesPrivilege.setPrivilegeDesc(String.join(",", privileges));
          subCategoriesPrivilege.setPrivilegeId(0L);
          subCategoriesPrivilegeList.add(subCategoriesPrivilege);
          privilegeDetails.setSubCategoriesPrivilege(subCategoriesPrivilegeList);
        }
      });
    }
    return privilegeDetails;
  }

  /**
   * Fetch the category details bases upon the product / module Id
   *
   * @param categoryPrivilege
   * @param customerCatList
   * @return list of category details
   */
  public static List<com.sncr.saw.security.common.bean.external.response.CategoryDetails> fetchResponseCategoryDetails(RoleCategoryPrivilege categoryPrivilege,
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
  public static void buildMessage(CategoryList catList, String message, boolean haveCategory, boolean haveSubCategory) {
    if (haveCategory) {
      catList.setCategories(null);
    } else if (haveSubCategory) {
      catList.getCategories().stream().forEach(cat -> cat.setSubCategory(null));
    }
    catList.setValid(false);
    catList.setMessage(message);
  }

  /**
   * Validate fine name with special charecter
   *
   * @param name
   * @return true if matched else false
   */
  public boolean validateName(String name) {
    Pattern special = Pattern.compile(NAME_REGEX);
    Matcher hasSpecial = special.matcher(name);
    return hasSpecial.find();
  }
}

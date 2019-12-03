package com.sncr.saw.security.app.controller.external;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.ProductModuleRepository;
import com.sncr.saw.security.app.repository.RoleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.sncr.saw.security.common.bean.repo.admin.CategoryList;
import com.sncr.saw.security.common.bean.repo.admin.RolesList;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.util.JWTUtils;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
@Api(value = "The controller provides to perform admin security operation in synchronoss analytics platform ")
@RestController
@RequestMapping("/sip-security/external")
public class ExternalSecurityController {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NSSOProperties nSSOProperties;
	@Autowired
	private ProductModuleRepository productModuleRepository;

	@RequestMapping(value = "/createRoleCategoryPrivilege", method = RequestMethod.POST)
	public Object createRoleCategoryPrivilege(HttpServletRequest request,
																						HttpServletResponse httpResponse,
																						@RequestBody RoleCategoryPrivilege roleCategoryPrivilege) {
		RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
		if (roleCategoryPrivilege == null) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setValid(false);
			response.setValidityMessage("Request body can't be blank or empty.");
			return response;
		}

		String jwtToken = JWTUtils.getToken(request);
		String[] extractValuesFromToken = JWTUtils.parseToken(jwtToken, nSSOProperties.getJwtSecretKey());
		String roleType = extractValuesFromToken[3];
		String masterLoginId = extractValuesFromToken[4];
		if (masterLoginId != null && !userRepository.validateUser(masterLoginId) && !RoleType.ADMIN.equals(roleType)) {
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setValid(false);
			response.setValidityMessage("You are not authorized to perform this operation.");
			return response;
		}

		ProductModuleDetails moduleDetails = productModuleRepository.fetchModuleProductDetail(masterLoginId, roleCategoryPrivilege.getProductName(), roleCategoryPrivilege.getModuleName());
		final Long customerSysId = moduleDetails != null ? moduleDetails.getCustomerSysId() : null;
		if (customerSysId == null || customerSysId == 0) {
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setValid(false);
			response.setValidityMessage("Product and Module does not exist for this user.");
			return response;
		}

		Role inputRole = roleCategoryPrivilege.getRole();
		if (inputRole != null) {
			RolesList roleList = new RolesList();
			if (inputRole.getCustomerCode().isEmpty() || inputRole.getRoleType().isEmpty()) {
				httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				roleList.setValid(false);
				roleList.setValidityMessage("Customer Code and Role Name can't be blank or empty.");
			} else if (inputRole.getCustomerCode() != null && inputRole.isAutoCreate() && !roleRepository.validateRoleByIdAndCustomerCode(customerSysId, inputRole)) {
				// build role details bean from input
				RoleDetails role = buildRoleDetails(masterLoginId, customerSysId, inputRole);

				try {
					if (role != null) {
						Valid valid = userRepository.addRole(role);
						if (valid.getValid()) {
							roleList.setRoles(userRepository.getRoles(role.getCustSysId()));
							roleList.setValid(true);
						} else {
							roleList.setValid(false);
							roleList.setValidityMessage("Role could not be added. " + valid.getError());
						}
					} else {
						roleList.setValid(false);
						roleList.setValidityMessage("Mandatory request params are missing for roles.");
					}
				} catch (Exception e) {
					roleList.setValid(false);
					String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
					roleList.setValidityMessage(message + " Please contact server Administrator for Roles.");
					roleList.setError(e.getMessage());
				}
			} else {
				roleList.setValid(false);
				roleList.setValidityMessage("Role already exist in the system for Customer Product Module Combination");
			}
			response.setRolesList(roleList);
		}

		CategoryList catList = new CategoryList();
		List<CategoryDetails> categoryPrivilegeLis = roleCategoryPrivilege.getCategory();
		if (categoryPrivilegeLis != null && !categoryPrivilegeLis.isEmpty()) {
			for (CategoryDetails category : categoryPrivilegeLis) {
				if (category.isAutoCreate()) {
					CategoryDetails categoryDetails = buildCategoryBean(customerSysId, category, null);
					categoryDetails.setProductId(moduleDetails.getProductId());
					categoryDetails.setModuleId(moduleDetails.getModuleId());
					categoryDetails.setMasterLoginId(masterLoginId);
					// add category
					addCategory(catList, categoryDetails, roleCategoryPrivilege);

					if (catList.getValid()) {
						List<SubCategoryDetails> subCategories = category.getSubCategory();
						if (subCategories != null && !subCategories.isEmpty()) {
							for (SubCategoryDetails subCategoryDetails : subCategories) {
								if (subCategoryDetails.isAutoCreate()) {
									CategoryDetails details = buildCategoryBean(customerSysId, null, subCategoryDetails);
									details.setProductId(moduleDetails.getProductId());
									details.setModuleId(moduleDetails.getModuleId());
									details.setMasterLoginId(masterLoginId);
									List<CategoryDetails> categoryList = catList.getCategories() != null ? catList.getCategories() : new ArrayList<>();
									String categoryCode = categoryList.stream().filter(cat -> category.getCategoryName().equalsIgnoreCase(cat.getCategoryName()))
											.findAny().get().getCategoryCode();
									details.setCategoryCode(categoryCode);

									// add sub categories
									boolean checkSubCategory = userRepository.checkSubCatExists(details);
									if (!checkSubCategory) {
										details.setSubCategoryInd(subCategoryDetails.isAutoCreate());
										addCategory(catList, details, roleCategoryPrivilege);
									} else {
										catList.setValid(false);
										catList.setValidityMessage("Sub Category Name already exists for this Customer Product Module Combination.");
									}
								} else {
									catList.setValid(false);
									catList.setValidityMessage("Sub categories can't be add for flag false.");
								}
							}
						}
					}
				} else {
					catList.setValid(false);
					catList.setValidityMessage("Categories can't be add for flag false.");
				}
			}
			response.setCategoryList(catList);
		}
		return response;
	}

	private void addCategory(CategoryList catList, CategoryDetails categoryDetails, RoleCategoryPrivilege roleCategoryPrivilege) {
		Valid valid;
		try {
			if (categoryDetails != null) {
				if (!userRepository.checkIsModulePresent(categoryDetails.getModuleId(), "ALERTS")) {
					boolean checkCatExist = !categoryDetails.isSubCategoryInd() ? !userRepository.checkCatExists(categoryDetails) : false;
					boolean checkSubCatExist = categoryDetails.isSubCategoryInd() ? userRepository.checkSubCatExists(categoryDetails) : false;
					if (checkCatExist || !checkSubCatExist) {
						valid = userRepository.addCategory(categoryDetails);
						if (valid.getValid()) {
							List<CategoryDetails> finalCategory = getResponseCategoryDetails(categoryDetails.getCustomerId(), roleCategoryPrivilege);
							catList.setCategories(finalCategory);
							catList.setValid(true);
						} else {
							catList.setValid(false);
							catList.setValidityMessage("Category/SubCategory could not be added. " + valid.getError());
						}
					} else if (checkCatExist) {
						catList.setValid(false);
						catList.setValidityMessage("Category Name already exists for this Customer Product Module Combination. ");
					} else if (checkSubCatExist) {
						catList.setValid(false);
						catList.setValidityMessage("Sub Category Name already exists for this Customer Product Module Combination. ");
					}
				} else {
					catList.setValid(false);
					catList.setValidityMessage("Adding Categories and Sub Categories for Alert Module is not allowed. ");
				}
			} else {
				catList.setValid(false);
				catList.setValidityMessage("Mandatory request params are missing");
			}
		} catch (Exception e) {
			catList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			catList.setValidityMessage(message + " Please contact server Administrator");
			catList.setError(e.getMessage());
		}
	}

	/**
	 * Filter the list of category to return categories in response
	 *
	 * @param customerId
	 * @param roleCategoryPrivilege
	 * @return @list category details
	 */
	private List<CategoryDetails> getResponseCategoryDetails(Long customerId, RoleCategoryPrivilege roleCategoryPrivilege) {
		List<CategoryDetails> finalCategory = new ArrayList<>();
		List<CategoryDetails> customerCatList = userRepository.getCategories(customerId);
		if (customerCatList != null) {
			List<CategoryDetails> filterCategory = customerCatList.stream().filter(cd -> cd.getModuleName().equalsIgnoreCase(roleCategoryPrivilege.getModuleName())
					&& cd.getProductName().equalsIgnoreCase(roleCategoryPrivilege.getProductName())).collect(Collectors.toList());

			if (roleCategoryPrivilege.getCategory() != null && !roleCategoryPrivilege.getCategory().isEmpty()) {
				for (CategoryDetails details : roleCategoryPrivilege.getCategory()) {
					filterCategory.forEach(filterCat -> {
						String catName = details.getCategoryName();
						if (filterCat.getCategoryName().equalsIgnoreCase(catName)) {
							filterCat.setAutoCreate(true);
							finalCategory.add(filterCat);
						}
					});
				}
			}
		}
		return finalCategory;
	}

	/**
	 * Build category bean from the category and sub category details
	 *
	 * @param customerSysId
	 * @param category
	 * @param subCategoryDetails
	 * @return CategoryDetails
	 */
	private CategoryDetails buildCategoryBean(Long customerSysId, CategoryDetails category, SubCategoryDetails subCategoryDetails) {
		CategoryDetails categoryDetails = new CategoryDetails();
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
	private RoleDetails buildRoleDetails(String masterLoginId, Long customerSysId, Role inputRole) {
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
}

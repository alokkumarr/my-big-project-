package com.sncr.saw.security.app.controller.external;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.ProductModuleRepository;
import com.sncr.saw.security.app.repository.RoleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.external.response.CategoryList;
import com.sncr.saw.security.common.bean.external.response.Role;
import com.sncr.saw.security.common.bean.external.response.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.util.JWTUtils;
import com.sncr.saw.security.common.util.SecurityUtils;
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
		response.setProductName(roleCategoryPrivilege.getProductName());
		response.setModuleName(roleCategoryPrivilege.getModuleName());
		if (roleCategoryPrivilege == null) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setValid(false);
			response.setMessage("Request body can't be blank or empty.");
			return response;
		}

		String jwtToken = JWTUtils.getToken(request);
		String[] extractValuesFromToken = JWTUtils.parseToken(jwtToken, nSSOProperties.getJwtSecretKey());
		String roleType = extractValuesFromToken[3];
		String masterLoginId = extractValuesFromToken[4];
		if (masterLoginId != null && !userRepository.validateUser(masterLoginId) && !RoleType.ADMIN.equals(roleType)) {
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setValid(false);
			response.setMessage("You are not authorized to perform this operation.");
			return response;
		}

		ProductModuleDetails moduleDetails = productModuleRepository.fetchModuleProductDetail(masterLoginId,
				roleCategoryPrivilege.getProductName(),
				roleCategoryPrivilege.getModuleName());
		final Long customerSysId = moduleDetails != null ? moduleDetails.getCustomerSysId() : null;
		if (customerSysId == null || customerSysId == 0) {
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setValid(false);
			response.setMessage("Product and Module does not exist for this user.");
			return response;
		}

		// add the product/module id
		response.setProductId(moduleDetails.getProductId());
		response.setModuleId(moduleDetails.getModuleId());

		com.sncr.saw.security.common.bean.Role inputRole = roleCategoryPrivilege.getRole();
		if (inputRole != null) {
			Role responseRole = new Role();
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
					addCategory(catList, categoryDetails, roleCategoryPrivilege);

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
										addCategory(catList, details, roleCategoryPrivilege);

										// add privilege for the subcategory

									} else {
										SecurityUtils.buildMessage(catList, "Sub Category Name already exists for this Customer Product Module Combination.");
									}
								} else {
									SecurityUtils.buildMessage(catList, "Sub categories can't be add for flag false.");
								}
							}
						}
					}
				} else {
					SecurityUtils.buildMessage(catList, "Categories can't be add for flag false.");
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
							List<com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails> customerCatList =
									userRepository.getCategories(categoryDetails.getCustomerId());
							catList.setCategories(SecurityUtils.getResponseCategoryDetails(roleCategoryPrivilege, customerCatList));
							catList.setValid(true);
							catList.setMessage("Category/SubCategory created for Customer Product Module Combination.");
						} else {
							SecurityUtils.buildMessage(catList, "Category/SubCategory could not be added. " + valid.getError());
						}
					} else if (checkCatExist) {
						SecurityUtils.buildMessage(catList, "Category Name already exists for this Customer Product Module Combination.");
					} else if (checkSubCatExist) {
						SecurityUtils.buildMessage(catList, "Sub Category Name already exists for this Customer Product Module Combination.");
					}
				} else {
					SecurityUtils.buildMessage(catList, "Adding Categories and Sub Categories for Alert Module is not allowed.");
				}
			} else {
				SecurityUtils.buildMessage(catList, "Mandatory request params are missing.");
			}
		} catch (Exception e) {
			String message = (e instanceof DataAccessException) ? "Database error. Please contact server Administrator."
					: "Error. Please contact server Administrator";
			SecurityUtils.buildMessage(catList, message);
		}
	}
}

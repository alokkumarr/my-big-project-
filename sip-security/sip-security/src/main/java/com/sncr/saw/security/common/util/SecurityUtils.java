package com.sncr.saw.security.common.util;

import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.external.response.CategoryDetails;
import com.sncr.saw.security.common.bean.external.response.CategoryList;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.synchronoss.bda.sip.jwt.token.RoleType;

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
	public static List<CategoryDetails> getResponseCategoryDetails(RoleCategoryPrivilege roleCategoryPrivilege,
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
							catDetails.setSubCategory(filterCat.getSubCategory() != null ? filterCat.getSubCategory() : null);
							finalCategory.add(catDetails);
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
	public static com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails buildCategoryBean(Long customerSysId, com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails category, SubCategoryDetails subCategoryDetails) {
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
	 * Build response with the message
	 *
	 * @param catList
	 * @param message
	 */
	public static void buildMessage(CategoryList catList, String message) {
		catList.setCategories(null);
		catList.setValid(false);
		catList.setMessage(message);
	}
}

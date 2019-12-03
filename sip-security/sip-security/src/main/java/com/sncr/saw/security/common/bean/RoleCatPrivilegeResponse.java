package com.sncr.saw.security.common.bean;

import com.sncr.saw.security.common.bean.repo.admin.CategoryList;
import com.sncr.saw.security.common.bean.repo.admin.RolesList;

import java.io.Serializable;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class RoleCatPrivilegeResponse implements Serializable {

	private static final long serialVersionUID = -6682525829922872306L;

	private Boolean valid;
	private String validityMessage;
	private String error;

	private RolesList rolesList;
	private CategoryList categoryList;

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getValidityMessage() {
		return validityMessage;
	}

	public void setValidityMessage(String validityMessage) {
		this.validityMessage = validityMessage;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public RolesList getRolesList() {
		return rolesList;
	}

	public void setRolesList(RolesList rolesList) {
		this.rolesList = rolesList;
	}

	public CategoryList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(CategoryList categoryList) {
		this.categoryList = categoryList;
	}
}

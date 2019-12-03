package com.sncr.saw.security.common.bean.external.response;

import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;

import java.io.Serializable;
import java.util.List;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class RoleCatPrivilegeResponse implements Serializable {

	private static final long serialVersionUID = -6682525829922872306L;

	private Boolean valid;
	private String message;
	private long moduleId;
	private long productId;
	private String moduleName;
	private String productName;

	private Role role;
	private CategoryList categoryList;

	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public CategoryList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(CategoryList categoryList) {
		this.categoryList = categoryList;
	}
}

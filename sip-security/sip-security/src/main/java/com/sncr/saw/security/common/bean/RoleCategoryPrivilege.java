package com.sncr.saw.security.common.bean;

import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class RoleCategoryPrivilege implements Serializable {

	private static final long serialVersionUID = 6710950219794990634L;

	private Role role;
	private String message;
	private String moduleName;
	private String productName;
	private List<CategoryDetails> categoryDetails = null;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

	public List<CategoryDetails> getCategoryDetails() {
		return categoryDetails;
	}

	public void setCategoryDetails(List<CategoryDetails> categoryDetails) {
		this.categoryDetails = categoryDetails;
	}

	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
}

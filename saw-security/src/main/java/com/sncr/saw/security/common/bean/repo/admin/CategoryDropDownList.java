package com.sncr.saw.security.common.bean.repo.admin;

import java.util.List;
import com.sncr.saw.security.common.bean.Category;

public class CategoryDropDownList {
	private List<Category> Category;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	
	public List<Category> getCategory() {
		return Category;
	}
	public void setCategory(List<Category> category) {
		Category = category;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getValidityMessage() {
		return ValidityMessage;
	}
	public void setValidityMessage(String validityMessage) {
		ValidityMessage = validityMessage;
	}
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	
	
}

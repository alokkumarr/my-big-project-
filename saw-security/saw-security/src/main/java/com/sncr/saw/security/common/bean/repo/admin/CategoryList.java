package com.sncr.saw.security.common.bean.repo.admin;

import java.util.List;
import com.sncr.saw.security.common.bean.Category;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;

public class CategoryList {
	private List<CategoryDetails> Categories;
	private List<Category> Category;
	private List<SubCategoryDetails> SubCategories;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	
	
	
	public List<SubCategoryDetails> getSubCategories() {
		return SubCategories;
	}
	public void setSubCategories(List<SubCategoryDetails> subCategories) {
		SubCategories = subCategories;
	}
	public List<CategoryDetails> getCategories() {
		return Categories;
	}
	public void setCategories(List<CategoryDetails> categories) {
		Categories = categories;
	}
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

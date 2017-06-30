package com.sncr.saw.security.common.bean;

import java.util.ArrayList;

public class Category {
    private String categoryName;
    private Long categoryId;
    private String categoryType;
    private ArrayList<Category> subCategory;
    
        
	
	public ArrayList<Category> getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(ArrayList<Category> subCategory) {
		this.subCategory = subCategory;
	}
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
    
    
}

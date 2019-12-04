package com.sncr.saw.security.common.bean.external.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class CategoryDetails implements Serializable {

	private static final long serialVersionUID = -6682525829922872306L;

	private long customerId;
	private long categoryId;
	private String categoryName;
	private String categoryType;
	private String categoryCode;
	private String categoryDesc;
	private Long activeStatusInd;
	private List<SubCategoryDetails> subCategory;


	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public Long getActiveStatusInd() {
		return activeStatusInd;
	}

	public void setActiveStatusInd(Long activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}

	public List<SubCategoryDetails> getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(List<SubCategoryDetails> subCategory) {
		this.subCategory = subCategory;
	}
}

package com.sncr.saw.security.common.bean.repo.admin.category;

import java.util.List;

public class CategoryDetails {

	private long customerId;
	private long productId;
	private String productName;
	private long moduleId;
	private String moduleName;
	private long categoryId;
	private String categoryName;
	private boolean iscatNameChanged;
	private String categoryType;
	private String categoryCode;
	private String categoryDesc;
	private String _default;
	private Long activeStatusInd;
	private String masterLoginId;
	private boolean subCategoryInd;	
	private List<SubCategoryDetails> subCategories;
	
		
	public boolean isIscatNameChanged() {
		return iscatNameChanged;
	}
	public void setIscatNameChanged(boolean iscatNameChanged) {
		this.iscatNameChanged = iscatNameChanged;
	}
	public Long getActiveStatusInd() {
		return activeStatusInd;
	}
	public void setActiveStatusInd(Long activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}
	public String getMasterLoginId() {
		return masterLoginId;
	}
	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}
	public boolean isSubCategoryInd() {
		return subCategoryInd;
	}
	public void setSubCategoryInd(boolean subCategoryInd) {
		this.subCategoryInd = subCategoryInd;
	}
	public String get_default() {
		return _default;
	}
	public void set_default(String _default) {
		this._default = _default;
	}
	public String getCategoryDesc() {
		return categoryDesc;
	}
	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}
	public List<SubCategoryDetails> getSubCategories() {
		return subCategories;
	}
	public void setSubCategories(List<SubCategoryDetails> subCategories) {
		this.subCategories = subCategories;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
	
	
}

package com.sncr.saw.security.common.bean.repo.admin;

public class DeleteCategory {
	private Long categoryId;
	private Long customerId;
	private String categoryCode;
	private String masterLoginId;
	
	
	
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getMasterLoginId() {
		return masterLoginId;
	}
	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}	
}

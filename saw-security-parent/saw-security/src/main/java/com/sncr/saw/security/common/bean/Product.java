package com.sncr.saw.security.common.bean;

import java.sql.Date;

public class Product {

	private String productName;
	private Long productId;				// generated field in db
	private String productCode;
	private String productDesc;
	private Integer activeStatusInd;
	private Date createdDate;
	private String createdBy;
	private Date inactivatedDate;
	private String inactivatedBy;
	private Date modifiedDate;
	private String modifiedBy;

	public Product() {
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public Integer getActiveStatusInd() {
		return activeStatusInd;
	}

	public void setActiveStatusInd(Integer activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getInactivatedBy() {
		return inactivatedBy;
	}

	public void setInactivatedBy(String inactivatedBy) {
		this.inactivatedBy = inactivatedBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getInactivatedDate() {
		return inactivatedDate;
	}

	public void setInactivatedDate(Date inactivatedDate) {
		this.inactivatedDate = inactivatedDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}

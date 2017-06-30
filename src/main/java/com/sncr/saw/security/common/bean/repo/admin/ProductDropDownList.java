package com.sncr.saw.security.common.bean.repo.admin;

import java.util.List;

import com.sncr.saw.security.common.bean.Product;

public class ProductDropDownList {
	private List<Product> products;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
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

package com.sncr.nsso.common.bean.repo;

import java.io.Serializable;
import java.util.ArrayList;

public class Products implements Serializable {
	

	private static final long serialVersionUID = -7208115566251565952L;
	
	private String productName;
	private String productDesc;
	private String productCode;
	
	private ArrayList<ProductModules> productModules;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public ArrayList<ProductModules> getProductModules() {
		return productModules;
	}

	public void setProductModules(ArrayList<ProductModules> productModules) {
		this.productModules = productModules;
	}

}

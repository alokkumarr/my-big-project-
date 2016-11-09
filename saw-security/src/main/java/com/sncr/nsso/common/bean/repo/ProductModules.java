package com.sncr.nsso.common.bean.repo;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductModules implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7684700328875735559L;
	private String prodCode;
	private String productModName;
	private String productModDesc;
	private String productModCode;
	
	private ArrayList<ProductModuleFeature> prodModFeature;

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getProductModName() {
		return productModName;
	}

	public void setProductModName(String productModName) {
		this.productModName = productModName;
	}

	public String getProductModDesc() {
		return productModDesc;
	}

	public void setProductModDesc(String productModDesc) {
		this.productModDesc = productModDesc;
	}

	public String getProductModCode() {
		return productModCode;
	}

	public void setProductModCode(String productModCode) {
		this.productModCode = productModCode;
	}

	public ArrayList<ProductModuleFeature> getProdModFeature() {
		return prodModFeature;
	}

	public void setProdModFeature(ArrayList<ProductModuleFeature> prodModFeature) {
		this.prodModFeature = prodModFeature;
	}
}

package com.sncr.saw.security.common.bean.repo;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductModuleFeatures implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8987788958758953471L;
	
	private String prodCode;
	private String prodModCode;
	private String prodModName;
	private String prodModDesc;
	
	private ArrayList<ProductModuleFeature> prodModFeature;
	
	public String getprodModNames() {
		return prodModName;
	}
	public void setprodModName(String prodModName) {
		this.prodModName = prodModName;
	}
	public String getprodModDesc() {
		return prodModDesc;
	}
	public void setProdModDesc(String prodModDesc) {
		this.prodModDesc = prodModDesc;
	}	
	public String getProdCode() {
		return prodCode;
	}
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getProdModCode() {
		return prodModCode;
	}
	public void setProdModCode(String prodModCode) {
		this.prodModCode = prodModCode;
	}
	public ArrayList<ProductModuleFeature> getProdModFeature() {
		return prodModFeature;
	}
	public void setProdModFeatrPriv(ArrayList<ProductModuleFeature> prodModFeature) {
		this.prodModFeature = prodModFeature;
	}
	
}

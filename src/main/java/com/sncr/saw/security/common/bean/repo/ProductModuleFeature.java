package com.sncr.saw.security.common.bean.repo;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductModuleFeature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8987788958758953471L;
	
	private String prodCode;
	private String prodModCode;
	private String prodModFeatureName;
	private String prodModFeatureDesc;
	private String defaultURL;
	private long privilegeCode;
	private long prodModFeatureID;
	private String prodModFeatureType;
	private String defaultFeature;	
	private String prodModFeatrCode;
	private ArrayList<ProductModuleFeature> ProductModuleSubFeatures;
	
		
	public ArrayList<ProductModuleFeature> getProductModuleSubFeatures() {
		return ProductModuleSubFeatures;
	}
	public void setProductModuleSubFeatures(ArrayList<ProductModuleFeature> productModuleSubFeatures) {
		ProductModuleSubFeatures = productModuleSubFeatures;
	}
	public String getProdModFeatureType() {
		return prodModFeatureType;
	}
	public void setProdModFeatureType(String prodModFeatureType) {
		this.prodModFeatureType = prodModFeatureType;
	}
	public long getProdModFeatureID() {
		return prodModFeatureID;
	}
	public void setProdModFeatureID(long prodModFeatureID) {
		this.prodModFeatureID = prodModFeatureID;
	}
	public long getPrivilegeCode() {
		return privilegeCode;
	}
	public void setPrivilegeCode(long privilegeCode) {
		this.privilegeCode = privilegeCode;
	}
	public String getDefaultFeature() {
		return defaultFeature;
	}
	public void setDefaultFeature(String defaultFeature) {
		this.defaultFeature = defaultFeature;
	}
	
	public String getProdModFeatureName() {
		return prodModFeatureName;
	}
	public void setProdModFeatureName(String prodModFeatureName) {
		this.prodModFeatureName = prodModFeatureName;
	}
	public String getProdModFeatureDesc() {
		return prodModFeatureDesc;
	}
	public void setProdModFeatureDesc(String prodModFeatureDesc) {
		this.prodModFeatureDesc = prodModFeatureDesc;
	}
	public String getDefaultURL() {
		return defaultURL;
	}
	public void setDefaultURL(String defaultURL) {
		this.defaultURL = defaultURL;
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
	public String getProdModFeatrCode() {
		return prodModFeatrCode;
	}
	public void setProdModFeatrCode(String prodModFeatrCode) {
		this.prodModFeatrCode = prodModFeatrCode;
	}
	
	
}

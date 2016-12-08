package com.sncr.nsso.common.bean.repo;

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
	
	private ArrayList<ProductModuleFeaturePrivileges> prodModFeatrPriv;
	
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
	public ArrayList<ProductModuleFeaturePrivileges> getProdModFeatrPriv() {
		return prodModFeatrPriv;
	}
	public void setProdModFeatrPriv(ArrayList<ProductModuleFeaturePrivileges> prodModFeatrPriv) {
		this.prodModFeatrPriv = prodModFeatrPriv;
	}
	
}

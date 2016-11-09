package com.sncr.nsso.common.bean.repo;

import java.io.Serializable;

public class ProductModuleFeaturePrivileges implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3351748171169103043L;
	private String privName;
	private String privCode;
	private String privDesc;
	private String prodModFeatrName;
	
	public String getProdModFeatrName() {
		return prodModFeatrName;
	}
	public void setProdModFeatrName(String prodModFeatrName) {
		this.prodModFeatrName = prodModFeatrName;
	}
	public String getPrivName() {
		return privName;
	}
	public void setPrivName(String privName) {
		this.privName = privName;
	}
	public String getPrivCode() {
		return privCode;
	}
	public void setPrivCode(String privCode) {
		this.privCode = privCode;
	}
	public String getPrivDesc() {
		return privDesc;
	}
	public void setPrivDesc(String privDesc) {
		this.privDesc = privDesc;
	}
	
}

/**
 * 
 */
package com.synchronoss.saw.composite.model;

import java.io.Serializable;

/**
 * @author gsan0003
 *
 */
public class ResetPwdDtls implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1252457984966548117L;
	private String masterLoginId;
	private String productUrl;
	public String getMasterLoginId() {
		return masterLoginId;
	}
	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
}

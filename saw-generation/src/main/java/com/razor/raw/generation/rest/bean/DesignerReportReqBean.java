package com.razor.raw.generation.rest.bean;

import java.io.Serializable;
import java.util.List;

import com.razor.raw.core.pojo.Parameter;
import com.razor.raw.core.pojo.Parameters;

/**
 * 

 * This file is used to send request to dril 
 */
public class DesignerReportReqBean implements Serializable{
 
	private static final long serialVersionUID = -5631054864749564671L;
	private String query;
	private String tenantId;
	private String productId;
	private String reportQueryType;
	private List<Parameter> parameters;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getReportQueryType() {
		return reportQueryType;
	}
	public void setReportQueryType(String reportQueryType) {
		this.reportQueryType = reportQueryType;
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	
	
	
}

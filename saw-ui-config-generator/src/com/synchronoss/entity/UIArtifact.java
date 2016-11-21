package com.synchronoss.entity;

public class UIArtifact {
	private String displayName;
	private String type;
	private String dashboardName;
	private String esIndexName;
	private String esIndexColName;
	public boolean isEnabled = true;
	private Object displayIndex;
	
	public String displayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String type() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String dashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String groupName) {
		this.dashboardName = groupName;
	}
	public String esIndexName() {
		return esIndexName;
	}
	public void setEsIndexName(String esIndexName) {
		this.esIndexName = esIndexName;
	}
	public String esIndexColName() {
		return esIndexColName;
	}
	public void setEsIndexColName(String esIndexColName) {
		this.esIndexColName = esIndexColName;
	}
	public Object displayIndex() {
		return displayIndex;
	}
	public void setDisplayIndex(Object object) {
		this.displayIndex = object;
	}
}

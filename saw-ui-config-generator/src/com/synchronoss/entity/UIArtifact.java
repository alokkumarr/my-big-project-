package com.synchronoss.entity;

import java.util.ArrayList;

import com.google.gson.Gson;

public class UIArtifact {
	private String displayName;
	private String type;
	private String dashboardName;
	private String esIndexName;
	private String esIndexColName;
	
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
	
	public static void test(){
		Gson gson = new Gson();
		ArrayList<UIArtifact> artifactArray = new ArrayList<UIArtifact>();
		UIArtifact artifact = new UIArtifact();
		artifact.setDisplayName("Ashish");
		artifact.setDashboardName("Human");
		artifact.setType("Single");
		artifact.setEsIndexColName("ASHISH");
		artifact.setEsIndexName("ASHISH");
		artifactArray.add(artifact);
		System.out.println(gson.toJson(artifactArray));
	}
}

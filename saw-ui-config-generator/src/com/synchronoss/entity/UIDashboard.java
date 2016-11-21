package com.synchronoss.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class UIDashboard {
	private String dashboardName;
	private Number dashboardDisplayIndex;
	private HashMap<String, ArrayList<UIArtifact>> data;
	public boolean isEnabled = true;
	
	public String dashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}
	public Number dashboardDisplayIndex() {
		return dashboardDisplayIndex;
	}
	public void setDashboardDisplayIndex(Number dashboardDisplayIndex) {
		this.dashboardDisplayIndex = dashboardDisplayIndex;
	}
	public HashMap<String, ArrayList<UIArtifact>> data() {
		return data;
	}
	public void setData(HashMap<String, ArrayList<UIArtifact>> data) {
		this.data = data;
	}
}

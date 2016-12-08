package com.synchronoss.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class FinalUIArtifactObject {
	private String esIndexName;
	private HashMap<String, ArrayList<UIArtifact>> data;
	
	public String esIndexName() {
		return esIndexName;
	}
	public void setEsIndexName(String esIndexName) {
		this.esIndexName = esIndexName;
	}
	public HashMap<String, ArrayList<UIArtifact>> data() {
		return data;
	}
	public void setData(HashMap<String, ArrayList<UIArtifact>> data) {
		this.data = data;
	}
}

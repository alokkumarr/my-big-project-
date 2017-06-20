package com.sncr.saw.security.common.bean.repo.analysis;

import java.io.Serializable;

public class Analysis implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2793557256649099378L;
	
	
	private Long featureId;
	private Long analysisId;
	private String analysisName;
	private int activeStatusInd;
	private String userId;
	public Long getFeatureId() {
		return featureId;
	}
	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}
	public Long getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	public String getAnalysisName() {
		return analysisName;
	}
	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}
	public int getActiveStatusInd() {
		return activeStatusInd;
	}
	public void setActiveStatusInd(int activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "Analysis [featureId=" + featureId + ", analysisId=" + analysisId + ", analysisName=" + analysisName
				+ ", activeStatusInd=" + activeStatusInd + ", userId=" + userId + "]";
	}
	
	
	
	
	
	
}

package com.sncr.saw.security.common.bean.repo.analysis;

import java.io.Serializable;
import java.util.Date;

public class AnalysisSummary implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5216176132626637410L;
	
	
	
	private Long featureId;  
	private Long analysisId;
	private String analysisName; 
	private Integer activeStatusInd; 
	private Date createdDate; 
	private String createdBy; 
	private Date inactivatedDate; 
	private String inactivatedBy;  
	private Date modifiedDate; 
	private String modifiedBy;
	private String userId;
	
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	public Long getFeatureId() {
		return featureId;
	}
	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}
	public Integer getActiveStatusInd() {
		return activeStatusInd;
	}
	public void setActiveStatusInd(Integer activeStatusInd) {
		this.activeStatusInd = activeStatusInd;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getInactivatedDate() {
		return inactivatedDate;
	}
	public void setInactivatedDate(Date inactivatedDate) {
		this.inactivatedDate = inactivatedDate;
	}
	public String getInactivatedBy() {
		return inactivatedBy;
	}
	public void setInactivatedBy(String inactivatedBy) {
		this.inactivatedBy = inactivatedBy;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	
	
	
}

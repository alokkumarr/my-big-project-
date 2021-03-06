package com.sncr.saw.security.common.bean.repo.analysis;

import java.io.Serializable;
import java.util.List;



public class AnalysisSummaryList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8231403788524495716L;
	
	private Boolean valid;
	private String validityMessage;
	private String error;
	private List<AnalysisSummary> artifactSummaryList;
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public String getValidityMessage() {
		return validityMessage;
	}
	public void setValidityMessage(String validityMessage) {
		this.validityMessage = validityMessage;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public List<AnalysisSummary> getArtifactSummaryList() {
		return artifactSummaryList;
	}
	public void setArtifactSummaryList(List<AnalysisSummary> artifactSummaryList) {
		this.artifactSummaryList = artifactSummaryList;
	}
	
	
	
}

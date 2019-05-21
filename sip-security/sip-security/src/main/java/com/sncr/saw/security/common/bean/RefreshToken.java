package com.sncr.saw.security.common.bean;

public class RefreshToken {

	/**
	 * This ticket will be valid till this time.
	 */
	private Long validUpto;
	/**
	 * Whether this ticket is currently active.
	 */
	private boolean valid;
	
	/**
	 * Reason the validity/invalidity of the ticket.
	 */
	private String validityReason;
	private String masterLoginId;
	
	

	public String getMasterLoginId() {
		return masterLoginId;
	}

	public void setMasterLoginId(String masterLoginId) {
		this.masterLoginId = masterLoginId;
	}

	public Long getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(Long validUpto) {
		this.validUpto = validUpto;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getValidityReason() {
		return validityReason;
	}

	public void setValidityReason(String validityReason) {
		this.validityReason = validityReason;
	}	
	
	
}

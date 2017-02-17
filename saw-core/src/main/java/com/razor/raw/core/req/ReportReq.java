package com.razor.raw.core.req;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.razor.raw.core.pojo.OutputFile;
import com.razor.raw.core.pojo.Report;

/**
 * 
 * @author surendra.rajaneni
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportReq implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 479831211486311797L;
	private String name;
	private String description;
	private String tenantId;
	private String productId;
	private String emailId;
	private String publishedBy;
	private boolean copyToFTP;
	private boolean attachReportToMail;
	private boolean isScheduled;
	private OutputFile outputFile;
	// this field uses for schedule report where all parameters and columns are prepared and sent to queue
	private Report report;
	
	
	
	/**
	 * @return the isScheduled
	 */
	public boolean isScheduled() {
		return isScheduled;
	}

	/**
	 * @param isScheduled the isScheduled to set
	 */
	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OutputFile getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(OutputFile outputFile) {
		this.outputFile = outputFile;
	}

	public String getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}

	public boolean isCopyToFTP() {
		return copyToFTP;
	}

	public void setCopyToFTP(boolean copyToFTP) {
		this.copyToFTP = copyToFTP;
	}

	public boolean isAttachReportToMail() {
		return attachReportToMail;
	}

	public void setAttachReportToMail(boolean attachReportToMail) {
		this.attachReportToMail = attachReportToMail;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    

}

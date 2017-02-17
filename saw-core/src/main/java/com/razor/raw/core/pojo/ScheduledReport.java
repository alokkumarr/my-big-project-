/**
 * 
 */
package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gsan0003
 *
 */
public class ScheduledReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1343136943598830265L;
	private long  reportId;
	private long  reportCategoryId;
	private String  tenantId;
	private String  productId;
	private String  reportName;
	private String  reportDescription;
	private String emailId;
	private String scheduleType;
	private String cronExpression;
	
	private String jobName;
	private String jobGroup;
	private String fileNameFormat;
	private String distribution;
	private boolean copyToFTP = false;
	private boolean attachReportToMail = false;
	private String lastModDate;
	
	
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
	/**
	 * @return the reportId
	 */
	public long getReportId() {
		return reportId;
	}
	/**
	 * @param reportId the reportId to set
	 */
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	/**
	 * @return the reportCategoryId
	 */
	public long getReportCategoryId() {
		return reportCategoryId;
	}
	/**
	 * @param reportCategoryId the reportCategoryId to set
	 */
	public void setReportCategoryId(long reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}
	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}
	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}
	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	/**
	 * @return the reportDescription
	 */
	public String getReportDescription() {
		return reportDescription;
	}
	/**
	 * @param reportDescription the reportDescription to set
	 */
	public void setReportDescription(String reportDescription) {
		this.reportDescription = reportDescription;
	}
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	/**
	 * @return the scheduleType
	 */
	public String getScheduleType() {
		return scheduleType;
	}
	/**
	 * @param scheduleType the scheduleType to set
	 */
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	/**
	 * @return the jobGroup
	 */
	public String getJobGroup() {
		return jobGroup;
	}
	/**
	 * @param jobGroup the jobGroup to set
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	/**
	 * @return the fileNameFormat
	 */
	public String getFileNameFormat() {
		return fileNameFormat;
	}
	/**
	 * @param fileNameFormat the fileNameFormat to set
	 */
	public void setFileNameFormat(String fileNameFormat) {
		this.fileNameFormat = fileNameFormat;
	}
	/**
	 * @return the distribution
	 */
	public String getDistribution() {
		return distribution;
	}
	/**
	 * @param distribution the distribution to set
	 */
	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	/**
	 * @return the cronExpression
	 */
	public String getCronExpression() {
		return cronExpression;
	}
	/**
	 * @param cronExpression the cronExpression to set
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	
	
	/**
	 * @return the lastModDate
	 */
	public String getLastModDate() {
		return lastModDate;
	}
	/**
	 * @param lastModDate the lastModDate to set
	 */
	public void setLastModDate(String lastModDate) {
		this.lastModDate = lastModDate;
	}
	
}

package com.razor.scheduler.jobs.report;

import java.io.Serializable;

import com.razor.raw.core.pojo.Report;

/**
 * Is used to schedule the Report
 * ReportReq object will be generated by this class
 * @author surendra.rajaneni
 *
 */
public class ReportDefinition implements Serializable {

	private static final long serialVersionUID = -5097607756721777176L;
	private Report report;
	String cronExpressionGen = null;  //"0 0/1 * * * ?";
	private String reportName = null;
	private String step = "step1";
	private String queryErrorMessage = null;
	private boolean showColFormat = false;
	private boolean showReportCategory = false;
	private boolean showSetParamValues = false;
	private String username;
	private String reportDesc = null;
	private String errorCode = null;
	private boolean reportValidationRequired;
	private String scheduleType;
	private String productID;
	private String tenantId;
	private String emailId;
	private boolean copyToFTP = false;
	private boolean attachReportToMail = false;
	private String fileNameFormat;
	private String reportFileNameTobeGenerated;
	private boolean recursiveSchedule;
	/**
	 * 
	 * @return the cronExpressionGen
	 */
	public String getCronExpressionGen() {
		return cronExpressionGen;
	}
	/**
	 * 
	 * @param cronExpressionGen to set cronExpressionGen
	 */
	public void setCronExpressionGen(String cronExpressionGen) {
		this.cronExpressionGen = cronExpressionGen;
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
	 * @param reportFileNameTobeGenerated the reportFileNameTobeGenerated to set
	 */
	public void setReportFileNameTobeGenerated(String reportFileNameTobeGenerated) {
		this.reportFileNameTobeGenerated = reportFileNameTobeGenerated;
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
	 * @return the productID
	 */
	public String getProductID() {
		return productID;
	}
	/**
	 * @param productID the productID to set
	 */
	public void setProductID(String productID) {
		this.productID = productID;
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
	 * @return the copyToFTP
	 */
	public boolean isCopyToFTP() {
		return copyToFTP;
	}
	/**
	 * @param copyToFTP the copyToFTP to set
	 */
	public void setCopyToFTP(boolean copyToFTP) {
		this.copyToFTP = copyToFTP;
	}
	/**
	 * @return the attachReportToMail
	 */
	public boolean isAttachReportToMail() {
		return attachReportToMail;
	}
	/**
	 * @param attachReportToMail the attachReportToMail to set
	 */
	public void setAttachReportToMail(boolean attachReportToMail) {
		this.attachReportToMail = attachReportToMail;
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
	 * @return the reportValidationRequired
	 */
	public boolean isReportValidationRequired() {
		return reportValidationRequired;
	}
	/**
	 * @param reportValidationRequired the reportValidationRequired to set
	 */
	public void setReportValidationRequired(boolean reportValidationRequired) {
		this.reportValidationRequired = reportValidationRequired;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the lastUpdatedTime
	 */

	
	/**
	 * This method used for clean report definition object
	 * 
	 */
	public void cleanReportDefinition(){
		setReportName(null);
		setReportDesc(null);
	}
	/**
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}
	/**
	 * @param report the report to set
	 */
	public void setReport(Report report) {
		this.report = report;
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
	 * @return the step
	 */
	public String getStep() {
		return step;
	}
	/**
	 * @param step the step to set
	 */
	public void setStep(String step) {
		this.step = step;
	}
	/**
	 * @return the queryErrorMessage
	 */
	public String getQueryErrorMessage() {
		return queryErrorMessage;
	}
	/**
	 * @param queryErrorMessage the queryErrorMessage to set
	 */
	public void setQueryErrorMessage(String queryErrorMessage) {
		this.queryErrorMessage = queryErrorMessage;
	}
	/**
	 * @return the showColFormat
	 */
	public boolean isShowColFormat() {
		return showColFormat;
	}
	/**
	 * @param showColFormat the showColFormat to set
	 */
	public void setShowColFormat(boolean showColFormat) {
		this.showColFormat = showColFormat;
	}
	/**
	 * @return the showReportCategory
	 */
	public boolean isShowReportCategory() {
		return showReportCategory;
	}
	/**
	 * @param showReportCategory the showReportCategory to set
	 */
	public void setShowReportCategory(boolean showReportCategory) {
		this.showReportCategory = showReportCategory;
	}
	/**
	 * @return the showSetParamValues
	 */
	public boolean isShowSetParamValues() {
		return showSetParamValues;
	}
	/**
	 * @param showSetParamValues the showSetParamValues to set
	 */
	public void setShowSetParamValues(boolean showSetParamValues) {
		this.showSetParamValues = showSetParamValues;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the reportDesc
	 */
	public String getReportDesc() {
		return reportDesc;
	}
	/**
	 * @param reportDesc the reportDesc to set
	 */
	public void setReportDesc(String reportDesc) {
		this.reportDesc = reportDesc;
	}
	/**
	 * @return the reportFileNameTobeGenerated
	 */
	public String getReportFileNameTobeGenerated() {
		return reportFileNameTobeGenerated;
	}
	/**
	 * 
	 * @return recursiveSchedule
	 */
	public boolean isRecursiveSchedule() {
		return recursiveSchedule;
	}
	/**
	 * 
	 * @param recursiveSchedule set to recursiveSchedule
	 */
	public void setRecursiveSchedule(boolean recursiveSchedule) {
		this.recursiveSchedule = recursiveSchedule;
	}
	
}

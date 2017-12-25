package com.synchronoss.saw.export.generate;

import com.synchronoss.saw.export.model.DataField;

public class ExportBean {
	
	private String fileName;
	private String [] columnHeader = new String []{};
	private String serverPathLocation;
	private DataField.Type[] columnDataType;
	private String reportName;
	private String reportDesc;
	private String publishDate;
	private String createdBy;
	
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
	 * @return the publishDate
	 */
	public String getPublishDate() {
		return publishDate;
	}


	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}


	public ExportBean() {
		this.serverPathLocation = System.getProperty("custom.pubReports.root");;
	}

	/**
	 *
	 * @return
	 */
	public DataField.Type[] getColumnDataType() {
		return columnDataType;
	}

	/**
	 *
	 * @param columnDataType
	 */
	public void setColumnDataType(DataField.Type[] columnDataType) {
		this.columnDataType = columnDataType;
	}


	/**
	 * @return the serverPathLocation
	 */
	public String getServerPathLocation() {
		return serverPathLocation;
	}
	
	/**
	 * @param serverPathLocation the serverPathLocation to set
	 */
	public void setServerPathLocation(String serverPathLocation) {
		this.serverPathLocation = serverPathLocation;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the columnHeader
	 */
	public String[] getColumnHeader() {
		return columnHeader;
	}
	/**
	 * @param columnHeader the columnHeader to set
	 */
	public void setColumnHeader(String[] columnHeader) {
		this.columnHeader = columnHeader;
	}

	/**
	 * Gets createdBy
	 *
	 * @return value of createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets createdBy
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}

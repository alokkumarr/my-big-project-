package com.synchronoss.saw.export.generate;

import java.util.ArrayList;
import java.util.List;

public class ExportExcelBean {
	
	private String fileName;
	private String [] columnHeader = new String []{};
	private List<StringBuffer> rowBufferList = new ArrayList<StringBuffer>();
	private String serverPathLocation;
	private String [] columnDataType = new String[]{};
	private String reportName;
	private String reportDesc;
	private String publishDate;
	
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


	public ExportExcelBean() {
		this.serverPathLocation = System.getProperty("custom.pubReports.root");;
	}
	
	
	public String[] getColumnDataType() {
		return columnDataType;
	}


	public void setColumnDataType(String[] columnDataType) {
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
	 * @return the rowBufferList
	 */
	public List<StringBuffer> getRowBufferList() {
		return rowBufferList;
	}
	/**
	 * @param rowBufferList the rowBufferList to set
	 */
	public void setRowBufferList(List<StringBuffer> rowBufferList) {
		this.rowBufferList = rowBufferList;
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
	
}

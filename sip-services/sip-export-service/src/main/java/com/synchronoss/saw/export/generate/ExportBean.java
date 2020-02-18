package com.synchronoss.saw.export.generate;

import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.export.model.DataField;
import com.synchronoss.saw.model.Field;

public class ExportBean {

	private String fileName;
	private String [] columnHeader = new String []{};
	private DataField.Type[] columnDataType;
	private String reportName;
	private String reportDesc;
	private String publishDate;
	private String createdBy;
	private String fileType;
  private Field.Type[] columnFieldDataType;
  private Analysis analysis;
  private Integer pageNo;
  private Long lastExportedSize;
  private Long lastExportLimit;

    /**
     *
     * @return the filetype as csv or xlsx
     */
	public String getFileType() {
	    return fileType;
    }

    /**
     *
     * @param fileType : filtype will be either csv or xlsx
     */
    public void setFileType(String fileType)   {
	    this.fileType = fileType;
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

	/**
	 * @return the columnFieldDataType
	 */
	public Field.Type[] getColumnFieldDataType() {
		return columnFieldDataType;
	}

	/**
	 * @param columnFieldDataType the columnFieldDataType to set
	 */
	public void setColumnFieldDataType(Field.Type[] columnFieldDataType) {
		this.columnFieldDataType = columnFieldDataType;
	}

    /**
     * @return the sipQuery
     */
    public Analysis getAnalysis() {
        return analysis;
    }

    /**
     * @param analysis  to set
     */
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }


    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Long getLastExportedSize() {
        return lastExportedSize;
    }

    public void setLastExportedSize(Long lastExportedSize) {
        this.lastExportedSize = lastExportedSize;
    }

    public Long getLastExportLimit() {
        return lastExportLimit;
    }

    public void setLastExportLimit(Long lastExportLimit) {
        this.lastExportLimit = lastExportLimit;
    }
}


package com.razor.raw.re.report.process;

import java.util.List;

import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.Report;



/**
 * 
 * @author surendra.rajaneni
 *
 */
public class ReportDefinationImpl {

	private String description;
	private String name;
	private List<ReportParameterViewImpl> queryParameters = null;
	private List<ReportColumnImpl> queryColumns = null;
	private Datasource datasource;
	private OutputFileDetails fileInformationFromQuery;
	private String type = "VIEW";
	private long reportCategoryId;
	private long reportId;
	private String categoryGroup = null;
	private String publishedBy = null;
	private String emailId = null;
	private boolean copyToFTP;
	private boolean attachReportToMail;
	private boolean scheduled;
	private String customerId;
	private String productId;
	private String query;
	private String reportType;
	private Report report;
	
	public boolean isScheduled() {
		return scheduled;
	}


	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}


	public Report getReport() {
		return report;
	}


	public void setReport(Report report) {
		this.report = report;
	}


	public String getReportType() {
		return reportType;
	}


	public void setReportType(String reportType) {
		this.reportType = reportType;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<ReportParameterViewImpl> getQueryParameters() {
		return queryParameters;
	}


	public void setQueryParameters(List<ReportParameterViewImpl> queryParameters) {
		this.queryParameters = queryParameters;
	}


	public List<ReportColumnImpl> getQueryColumns() {
		return queryColumns;
	}


	public void setQueryColumns(List<ReportColumnImpl> queryColumns) {
		this.queryColumns = queryColumns;
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	/**
	 * 
	 * @param datasource
	 * @param reportName
	 * @param description
	 * @param reportQuery
	 * @param queryParameters
	 * @param queryColumns
	 * @param fileInformationFromQuery
	 * @param type
	 * @param publishedBy
	 * @param reportCategoryId
	 * @param reportId
	 * @param categoryGroup
	 * @param emailId
	 * @param copyToFTP
	 * @param attachReportToMail
	 * @param custId
	 * @param prodId
	 */
	public ReportDefinationImpl(Datasource datasource, String reportName,String description,
			String reportQuery,
			List<ReportParameterViewImpl> queryParameters,
			List<ReportColumnImpl> queryColumns,
 OutputFileDetails fileInformationFromQuery, String type,
			String publishedBy, long reportCategoryId, long reportId, String categoryGroup, String emailId, boolean copyToFTP, boolean attachReportToMail, String custId, String prodId,Report report) {
		super();
		this.name = reportName;
		this.description = description;
		this.queryParameters = queryParameters;
		this.queryColumns = queryColumns;
		this.fileInformationFromQuery = fileInformationFromQuery;
		this.type = type; 
		this.publishedBy = publishedBy;
		this.reportCategoryId = reportCategoryId;
		this.reportId = reportId;
		this.categoryGroup = categoryGroup;
		this.emailId = emailId;
		this.copyToFTP = copyToFTP;
		this.attachReportToMail = attachReportToMail;
		this.customerId = custId;
		this.productId = prodId;
		this.datasource = datasource;
		this.query= reportQuery;
		this.report = report;
	}

	
	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	public boolean isAttachReportToMail() {
		return attachReportToMail;
	}


	public void setAttachReportToMail(boolean attachReportToMail) {
		this.attachReportToMail = attachReportToMail;
	}


	public boolean isCopyToFTP() {
		return copyToFTP;
	}

	public void setCopyToFTP(boolean copyToFTP) {
		this.copyToFTP = copyToFTP;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public List<ReportParameterViewImpl> getReportParameters() {

		return queryParameters;
	}

	public List<ReportColumnImpl> getReportColumns() {

		return queryColumns;
	}

	/*public IEngineDataSource getDataSource() {

		return dataSourceFromXML;
	}*/

	public OutputFileDetails getGenerateFileInformation() {

		return fileInformationFromQuery;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	/**
	 * @return the publishedBy
	 */
	public String getPublishedBy() {
		return publishedBy;
	}



	/**
	 * @param publishedBy the publishedBy to set
	 */
	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
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
	 * @return the categoryGroup
	 */
	public String getCategoryGroup() {
		return categoryGroup;
	}

	/**
	 * @param categoryGroup the categoryGroup to set
	 */
	public void setCategoryGroup(String categoryGroup) {
		this.categoryGroup = categoryGroup;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getProductId() {
		return productId;
	}


	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	

}

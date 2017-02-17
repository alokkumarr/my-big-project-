package com.razor.raw.core.dao.repository;

import java.util.List;

import com.razor.raw.core.pojo.PublishedReport;
/**
 * 
 * @author surendra.rajaneni
 *
 */
public interface PublishedReportRepository {
	/**
	 * 
	 * @param reportDefinationImpl
	 * @param recordLimit
	 * @param connection
	 * @return
	 */
	void insertPublishedReports(PublishedReport pubReport);
	/**
	 * 
	 * @param userId
	 * @param prodId
	 * @return
	 */
	
	public List<PublishedReport> getPublishedReports(String userId, String tetantId, String prodId);
	
	/**
	 * Get List of published reports by categoryId
	 * @param userId
	 * @param tetantId
	 * @param prodId
	 * @param catagoryId
	 * @return
	 */
	public List<PublishedReport> getPublishedReports(String userId, String tetantId, String prodId, String categoryId);
	
	/**
	 *The below method is used to retrieve the published
	 * report information from table <>
	 * 
	 * @param PublishedReport
	 *            object
	 * @return List<PublishedReport> : contains published report information
	 */
	public List<PublishedReport> getPublishedReports(PublishedReport publishedReport);
	
	/**
	 * check the given report exist in published report or not. 
	 * @param reportId
	 * @param tenantId
	 * @param productId
	 * @return
	 */
	boolean isPublishReportExist(String reportId, String tenantId, String productId);
	/**
	 * This method is used to delete the published report by publishedReportId
	 * @param rawPublishedReportsId
	 */
	void deletePublishReport(long rawPublishedReportsId);
	/**
	 * This method is used to delete the published report by published report name.
	 * @param pubReportName
	 */
	void deletePublishReport(String pubReportName);
	
	/**
	 * check the published record is exist or not by category, product, customer
	 * @param reportName
	 * @param tetantId
	 * @param prodId
	 * @param categoryId
	 * @return
	 */
	boolean isPublishedReportExist(String reportName, String tetantId, String prodId, String categoryId);
	
	/**
	 * 
	 * @param categoryId
	 * @return boolean whether restricted or not
	 */
	boolean getRestrictedStatusForCatId(long categoryId);
	/**
	 * @param
	 * @param
	 * @param
	 */
	boolean isPublishedReportFileExist(String reportLocation);
}

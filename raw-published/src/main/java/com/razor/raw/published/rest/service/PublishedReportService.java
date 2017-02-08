package com.razor.raw.published.rest.service;

import java.util.List;

import com.razor.raw.core.pojo.PublishedReport;

public interface PublishedReportService {
	
	/**
	 * 
	 * @param userId
	 * @param tetantId
	 * @param prodId
	 * @param categoryId
	 * @return
	 */
	public List<PublishedReport> getPublishedReports(String userId, String tetantId, String prodId, String categoryId);
	
	/**
	 * 
	 * @param reportName
	 * @param tetantId
	 * @param prodId
	 * @param categoryId
	 * @return
	 */
	public boolean isPublishedReportExist(String reportName, String tetantId, String prodId, String categoryId);
	
	/**
	 * 
	 * @param rawPublishedReportsId
	 */
	public void deletePublishReport(long rawPublishedReportsId);
	
	/**
	 * insert records into published reports table
	 * @param pubReport
	 */
	void insertPublishedReports(PublishedReport pubReport);

	/**
	 * @param
	 * @param
	 * @param
	 */
	public boolean isPublishedReportFileExist(String filename);
	
}

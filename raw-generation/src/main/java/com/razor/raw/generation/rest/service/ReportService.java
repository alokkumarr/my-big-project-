package com.razor.raw.generation.rest.service;

import java.util.List;

import com.razor.raw.core.pojo.Report;
import com.razor.raw.generation.rest.bean.ReportViewBean;
import com.razor.raw.generation.rest.bean.ReportViewReqBean;

public interface ReportService {
	/**
	 * 
	 * @param categoryId
	 * @param tenantId
	 * @return list of report
	 */
	List<Report> getReportByCategoryId(long categoryId, String tenantId,String userID, String productID);
	/**
	 * 
	 * @param reportViewReqBean
	 * @return list of CustomViewReport
	 */
	ReportViewBean getReportViewByReport(ReportViewReqBean reportViewReqBean);
	/**
	 * 
	 * @param reportName
	 * @param tenantId
	 * @param productId
	 * @return boolean value of published report
	 */
	boolean getPublishedReportStatus(String reportName, String tenantId, String productId );
	
	Report getReportByReportId(long reportId);
	
	void updateReportScheduledDate(long reportId);
}

package com.razor.raw.core.dao.repository;

import java.util.List;

import com.razor.raw.core.pojo.ScheduledReport;


/**
 * 
 * @author 
 *
 */
public interface ScheduledReportRepository {

	List<ScheduledReport> viewScheduledReportList(String productId,
			String tenantId, long reportCategoryId,String userId);

	List<String> getScheduledReports(String tenantId, String productId);


	
}

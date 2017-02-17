package com.razor.raw.core.dao.repository;

import com.razor.raw.core.pojo.ReportExecutionLog;

/**
 * 
 * @author ajay.kumar
 *
 */
public interface ReportExecutionLogRepository {
	void insertReportExecutionLog(ReportExecutionLog reportLog);

	void insertReportExecutionLog(long reportId, String description, String user);
}

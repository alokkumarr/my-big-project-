package com.razor.raw.core.dao.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.razor.raw.core.dao.repository.ReportExecutionLogRepository;
import com.razor.raw.core.pojo.ReportExecutionLog;

@Repository
public class ReportExecutionLogRepositoryImpl implements ReportExecutionLogRepository{
	private static final Logger logger = LoggerFactory
			.getLogger(PublishedReportRepositoryImpl.class);
	private final JdbcTemplate jdbcTemplate;
	@Autowired
	public ReportExecutionLogRepositoryImpl(JdbcTemplate jdbc) {
		jdbcTemplate = jdbc;
	}
	String insertReportExeLog = "INSERT INTO RAW_REPORT_EXECUTION_LOG (RAW_REPORTS_ID, DESCRIPTION, START_TIME, CREATED_USER) VALUES(?,?,SYSDATE(),?)";
	@Override
	public void insertReportExecutionLog(ReportExecutionLog reportLog) {
		logger.debug(this.getClass().getName() + " - insertReportExecutionLog - START");
		logger.debug(this.getClass().getName() + " - insertReportExecutionLog --- Query : " +insertReportExeLog);
		List<Object> params = new ArrayList<Object>();
		try { 
			params.add(reportLog.getReportsId());
			params.add(reportLog.getDescription());
			params.add(reportLog.getCreatedUser());
			jdbcTemplate.update(insertReportExeLog, params.toArray());
			logger.debug(this.getClass().getName() + " - insertReportExecutionLog - END");
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in insertReportExecutionLog - ", e);
		}
	}
	
	@Override
	public void insertReportExecutionLog(long reportId, String description, String user) {
		logger.debug(this.getClass().getName() + " - insertReportExecutionLog - START");
		logger.debug(this.getClass().getName() + " - insertReportExecutionLog --- Query : " +insertReportExeLog);
		List<Object> params = new ArrayList<Object>();
		try { 
			params.add(reportId);
			params.add(description);
			params.add(user);
			jdbcTemplate.update(insertReportExeLog, params.toArray());
			logger.debug(this.getClass().getName() + " - insertReportExecutionLog - END");
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in insertReportExecutionLog - ", e);
		}
	}

}

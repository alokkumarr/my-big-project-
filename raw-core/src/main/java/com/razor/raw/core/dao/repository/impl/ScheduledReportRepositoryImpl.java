package com.razor.raw.core.dao.repository.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.razor.raw.core.dao.mapper.ScheduledReportMapper;
import com.razor.raw.core.dao.repository.ScheduledReportRepository;
import com.razor.raw.core.pojo.ScheduledReport;

/**
 * 
 * @author girija.sankar
 *
 */
@Repository
public class ScheduledReportRepositoryImpl implements ScheduledReportRepository {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportRepositoryImpl.class);
	
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ScheduledReportRepositoryImpl(JdbcTemplate jdbc) {
		jdbcTemplate = jdbc;
	}
	private final String getRestrictedStatusByCatId = "SELECT RESTRICTED FROM RAW_REPORT_CATEGORY WHERE RAW_REPORT_CATEGORY_ID = ? ";

	private String scheduledReportsQuery = "SELECT RR.RAW_REPORTS_ID, RR.RAW_REPORT_CATEGORY_ID, RR.TENANT_ID,RR.PRODUCT_ID, RR.REPORT_NAME, RR.REPORT_DESCRIPTION, RR.REPORT_SCHEDULED_DT, QT.JOB_NAME, QT.JOB_GROUP "
			+ " FROM RAW_REPORTS RR, QRTZ_TRIGGERS QT WHERE  RR.DISPLAY_STATUS = 'Y' AND RAW_REPORT_CATEGORY_ID = ? AND TENANT_ID = ? AND PRODUCT_ID = ? AND RR.RAW_REPORTS_ID = QT.JOB_NAME ORDER BY RR.REPORT_SCHEDULED_DT DESC";
	
	private String scheduledReportsRestrictQuery = "SELECT RR.RAW_REPORTS_ID, RR.RAW_REPORT_CATEGORY_ID, RR.TENANT_ID,RR.PRODUCT_ID, RR.REPORT_NAME, RR.REPORT_DESCRIPTION, RR.REPORT_SCHEDULED_DT, QT.JOB_NAME, QT.JOB_GROUP "
			+ " FROM RAW_REPORTS RR, QRTZ_TRIGGERS QT, RAW_REPORT_CATEGORY RRC WHERE  "
			+ " RRC.RAW_REPORT_CATEGORY_ID = ? AND RR.DISPLAY_STATUS = 'Y'AND RR.TENANT_ID = ? AND RR.PRODUCT_ID = ? "
			+ " AND RR.RAW_REPORT_CATEGORY_ID = RRC.RAW_REPORT_CATEGORY_ID AND RR.RAW_REPORTS_ID = QT.JOB_NAME AND RRC.RESTRICTED = 'Y' AND RR.CREATED_USER= ? ORDER BY RR.REPORT_SCHEDULED_DT DESC";
	
	private String scheduledJobs = "SELECT JOB_NAME FROM QRTZ_JOB_DETAILS WHERE JOB_GROUP=?";
	
	@Override
	public List<ScheduledReport> viewScheduledReportList(String productId,
			String tenantId, long reportCategoryId,String userId) {
		logger.debug(this.getClass().getName() + " - getReportList - START");
		
		
		boolean isRestricted = getRestrictedStatusForCatId(reportCategoryId);
		
		List<ScheduledReport> scheduledReports = null;
		try {
			
			if(isRestricted){
				logger.debug(this.getClass().getName()
						+ " - getReportList --- Query : " + scheduledReportsRestrictQuery);
				scheduledReports = jdbcTemplate.query(scheduledReportsRestrictQuery,
						new ScheduledReportMapper(), reportCategoryId, tenantId, productId, userId );
			}
			else{
				logger.debug(this.getClass().getName()
						+ " - getReportList --- Query : " + scheduledReportsQuery);
				scheduledReports = jdbcTemplate.query(scheduledReportsQuery,
						new ScheduledReportMapper(), reportCategoryId, tenantId, productId);
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getReportList - ", e);
		}
		logger.debug(this.getClass().getName() + " - getReportList - END");
		return scheduledReports;
	}

	private boolean getRestrictedStatusForCatId(long categoryId){
		
		logger.info(this.getClass().getName() + " - getRestrictedStatusForCatId - START");
		logger.debug(this.getClass().getName()
				+ " - getRestrictedStatusForCatId --- Query : " + getRestrictedStatusByCatId);
		String restricted= null;
		try {
			restricted = jdbcTemplate.queryForObject(getRestrictedStatusByCatId,
					new Object[] { categoryId }, String.class);
			logger.info(this.getClass().getName()
					+ " - getRestrictedStatusForCatId - START");
		} catch (DataAccessException e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getRestrictedStatusForCatId - ", e);
		}
		
		if(restricted != null && restricted.equalsIgnoreCase("Y")){
			return true;
		}
		return false;
	}
	
	@Override
	public List<String> getScheduledReports(String tenantId,String productID) {
		String jobGroup = null;
		logger.debug(this.getClass().getName() + " - Provided TENANT ID : " +tenantId+" and PRODUCT ID : "+productID); 
		if(!StringUtils.isEmpty(productID) && !StringUtils.isEmpty(tenantId))
		{
			jobGroup=productID+"-"+tenantId;
			Object[] params = { jobGroup };
			return jdbcTemplate.queryForList(scheduledJobs, params, String.class);
		}
		logger.error(this.getClass().getName() + " - Provided TENANT ID and PRODUCT ID are NULL- END"); 
		return null;
	}

	}

package com.razor.raw.core.dao.repository.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.dao.mapper.PublishedReportMapper;
import com.razor.raw.core.dao.repository.PublishedReportRepository;
import com.razor.raw.core.pojo.PublishedReport;
@Repository
public class PublishedReportRepositoryImpl implements PublishedReportRepository{
	private static final Logger logger = LoggerFactory
			.getLogger(PublishedReportRepositoryImpl.class);
	
	private final JdbcTemplate jdbcTemplate;
	@Autowired
	public PublishedReportRepositoryImpl(JdbcTemplate jdbc) {
		jdbcTemplate = jdbc;
	}
	private String insertPublishReportQuery = "INSERT INTO RAW_PUBLISHED_REPORTS (RAW_REPORTS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, REPORT_DESCRIPTION, REPORT_FORMAT, "
			+ " REPORT_LOCATION, DISPLAY_STATUS, IS_SCHEDULED, CREATED_USER, CREATED_DATE) values (?,?,?,?,?,?,?,?,?,?,?,sysdate()) ";
	
	private String publishReportQuery = " SELECT RAW_PUBLISHED_REPORTS_ID, RAW_REPORTS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, "
			+ " REPORT_LOCATION, DISPLAY_STATUS, IS_SCHEDULED, CREATED_USER, CREATED_DATE FROM RAW_PUBLISHED_REPORTS WHERE CREATED_USER = ? AND TENANT_ID = ? AND PRODUCT_ID = ?";
	
	private String publishReportExist = "select count(*) from raw_published_reports where report_name = ? and tenant_id = ? and product_id = ?";
	
	private String publishReportByCatagoryQuery = "SELECT RAW_PUBLISHED_REPORTS_ID, RAW_REPORTS_ID,RAW_REPORT_CATEGORY_ID,TENANT_ID,PRODUCT_ID,REPORT_NAME,REPORT_DESCRIPTION,REPORT_FORMAT,REPORT_LOCATION,IS_SCHEDULED,CREATED_USER,CREATED_DATE "
			+ "FROM raw_published_reports WHERE DISPLAY_STATUS = 'Y' AND RAW_REPORT_CATEGORY_ID = ? and TENANT_ID = ? AND PRODUCT_ID = ? ORDER BY CREATED_DATE DESC ";
	
	private String publishReportByRestrictedCatagoryQuery = "SELECT RAW_PUBLISHED_REPORTS_ID, RAW_REPORTS_ID,RAW_REPORT_CATEGORY_ID,TENANT_ID,PRODUCT_ID,REPORT_NAME,REPORT_DESCRIPTION,REPORT_FORMAT,REPORT_LOCATION,IS_SCHEDULED,CREATED_USER,CREATED_DATE "
			+ "FROM raw_published_reports WHERE DISPLAY_STATUS = 'Y' AND RAW_REPORT_CATEGORY_ID = ? and TENANT_ID = ? AND PRODUCT_ID = ? AND CREATED_USER =? ORDER BY CREATED_DATE DESC  ";
	
	private String deletePublishReportByIdQuery = "DELETE FROM raw_published_reports WHERE RAW_PUBLISHED_REPORTS_ID = ?";
	
	private String deletePublishReportByNameQuery = "DELETE FROM raw_published_reports WHERE REPORT_NAME = ?";
	
	private String duplicatePublishedReportQuery = "SELECT COUNT(*) from raw_published_reports where RAW_REPORT_CATEGORY_ID = ? and TENANT_ID = ? AND PRODUCT_ID = ? and REPORT_NAME = ?";
	
	private String checkPublishedFileExists = "SELECT COUNT(*) from raw_published_reports where REPORT_LOCATION = ? ";
	
	private final String getRestrictedStatusByCatId = "SELECT RESTRICTED FROM RAW_REPORT_CATEGORY WHERE RAW_REPORT_CATEGORY_ID = ? ";
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonConstants.DATETIME_PATTERN);
	@Override
	public void insertPublishedReports(PublishedReport pubReport) {
		logger.debug(this.getClass().getName() + " - insertPublishedReports - START");
		List<Object> params = new ArrayList<Object>();
		try { 
			//params.add(pubReport.getPublishReportId());
			params.add(pubReport.getReportId());
			params.add(pubReport.getReportCategoryId());
			params.add(pubReport.getTenantId());
			params.add(pubReport.getProductId());
			params.add(pubReport.getReportName());
			params.add(pubReport.getReportDescription());
			params.add(pubReport.getFormat());
			params.add(pubReport.getReportLocaton());
			params.add(pubReport.isDisplayStatus() == true ? "Y" : "N");
			params.add(pubReport.isScheduled() == true ? "Y" : "N");
			params.add(pubReport.getCreatedUser());
			jdbcTemplate.update(insertPublishReportQuery, params.toArray());
			logger.debug(this.getClass().getName() + " - insertPublishedReports - END");
		} catch (Exception e) {
			logger.error("Exception occured at + "+ this.getClass().getName() + "in insertPublishedReports - ", e);
		}
	}

	@Override
	public List<PublishedReport> getPublishedReports(String userId, String tetantId, String prodId) {
		logger.debug(this.getClass().getName() + " - insertPublishedReports - START -- Query : " +publishReportQuery);
		List<PublishedReport> publishedReports = null; 
		try {
			publishedReports = jdbcTemplate.query(publishReportQuery, new PublishedReportMapper(), userId, tetantId, prodId);
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in getPublishedReports - ", e);
		}
		logger.debug(this.getClass().getName() + " - getPublishedReports - END");
		return publishedReports;
	}

	@Override
	public List<PublishedReport> getPublishedReports(
			PublishedReport publishedReport) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPublishReportExist(String reportId, String tenantId, String productId) {
		logger.debug(this.getClass().getName() + " - isPublishReportExist - START -- Query : " +publishReportExist);
		boolean flag = false;
		Object[] params = {reportId,tenantId,productId};
		int i = jdbcTemplate.queryForInt(publishReportExist, params);
		if(i > 0)
			flag =  true;
		logger.debug(this.getClass().getName() + " - isPublishReportExist - END");
		return flag;
	}

	@Override
	public List<PublishedReport> getPublishedReports(String userId,
			String tetantId, String prodId, String catagoryId) {
		logger.debug(this.getClass().getName() + " - getPublishedReports - START -- Query : " +publishReportByCatagoryQuery);
		List<PublishedReport> publishedReports = null; 
		
		boolean isRestricted = getRestrictedStatusForCatId(Long.parseLong(catagoryId));
		
		try {
			if(isRestricted){
				publishedReports = jdbcTemplate.query(publishReportByRestrictedCatagoryQuery, new PublishedReportMapper(), catagoryId, tetantId, prodId,userId);
			}
			else{
			publishedReports = jdbcTemplate.query(publishReportByCatagoryQuery, new PublishedReportMapper(), catagoryId, tetantId, prodId);
			}
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in getPublishedReports - ", e);
		}
		logger.debug(this.getClass().getName() + " - getPublishedReports - END");
		return publishedReports;
	}

	@Override
	public void deletePublishReport(long rawPublishedReportsId) {
		logger.debug(this.getClass().getName() + " - deletePublishReport - START -- Query : " +deletePublishReportByIdQuery);
		logger.debug(this.getClass().getName() + " - deletePublishReport - rawPublishedReportsId : " +rawPublishedReportsId);
		int i = jdbcTemplate.update(deletePublishReportByIdQuery,rawPublishedReportsId);
		logger.debug(this.getClass().getName() + " - number of records updated: : " +i);
	}
	
	@Override
	public void deletePublishReport(String pubReportName) {
		logger.debug(this.getClass().getName() + " - deletePublishReport - START -- Query : " +deletePublishReportByIdQuery);
		logger.debug(this.getClass().getName() + " - deletePublishReport - publishedReportName : " +pubReportName);
		int i = jdbcTemplate.update(deletePublishReportByNameQuery, pubReportName);
		logger.debug(this.getClass().getName() + " - number of records updated: : " +i);
	}
	
	@Override
	public boolean isPublishedReportExist(String reportName, String tetantId,
			String prodId, String categoryId) {
		logger.debug(this.getClass().getName() + " - isPublishedReportExist - START -- Query : " +duplicatePublishedReportQuery);
		logger.debug(this.getClass().getName() + " - isPublishedReportExist - START -- reportName : " +reportName + " categoryId "+categoryId);
		boolean flag = false;
		Integer count = jdbcTemplate.queryForObject(duplicatePublishedReportQuery, Integer.class,categoryId,tetantId,prodId,reportName);
		if(count != null && count > 0)
		{
			flag = true; 
		}
		return flag;
	}
	
	@Override
	public boolean isPublishedReportFileExist(String reportLocation) {
		logger.debug(this.getClass().getName() + " - isPublishedReportFileExist - START -- Query : " +checkPublishedFileExists);
		logger.debug(this.getClass().getName() + " - isPublishedReportFileExist - START -- reportLocation : " +reportLocation );
		boolean flag = false;
		Integer count = jdbcTemplate.queryForObject(checkPublishedFileExists, Integer.class,reportLocation);
		if(count != null && count > 0)
		{
			flag = true; 
		}
		return flag;
	}
	
	@Override
	public boolean getRestrictedStatusForCatId(long categoryId){
		
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

}

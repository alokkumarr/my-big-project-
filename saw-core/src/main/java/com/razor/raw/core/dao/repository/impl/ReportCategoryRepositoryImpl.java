package com.razor.raw.core.dao.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.dao.mapper.ReportCategoryMapper;
import com.razor.raw.core.dao.repository.ReportCategoryRepository;
import com.razor.raw.core.pojo.ReportCategory;

@Repository
public class ReportCategoryRepositoryImpl implements ReportCategoryRepository{
	private static final Logger logger = LoggerFactory
			.getLogger(ReportCategoryRepositoryImpl.class);
	private final JdbcTemplate jdbcTemplate;
	@Autowired
	public ReportCategoryRepositoryImpl(JdbcTemplate jdbc) {
		jdbcTemplate = jdbc;
	}
	
	private String reportCatListByIdQuery = "SELECT RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_CATEGORY_NAME, REPORT_CATEGORY_DESCRIPTION, REPORT_SUPER_CATEGORY_ID, DISPLAY_STATUS, "
			+ " RESTRICTED, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE FROM RAW_REPORT_CATEGORY WHERE TENANT_ID = ? AND PRODUCT_ID = ? AND DISPLAY_STATUS='Y' ORDER BY RESTRICTED DESC ";
	
	private String reportCatByIdQuery = "SELECT RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_CATEGORY_NAME, REPORT_CATEGORY_DESCRIPTION, REPORT_SUPER_CATEGORY_ID, DISPLAY_STATUS, "
			+ " RESTRICTED, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE FROM RAW_REPORT_CATEGORY WHERE RAW_REPORT_CATEGORY_ID = ?";
	
	private String reportNameByIdQuery = "SELECT REPORT_CATEGORY_NAME FROM RAW_REPORT_CATEGORY WHERE RAW_REPORT_CATEGORY_ID = ?";
	
	private String insertReportCatQuery = "INSERT INTO RAW_REPORT_CATEGORY (TENANT_ID, PRODUCT_ID, REPORT_CATEGORY_NAME, REPORT_CATEGORY_DESCRIPTION, REPORT_SUPER_CATEGORY_ID, "
			+ " DISPLAY_STATUS, RESTRICTED, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE) VALUES (?,?,?,?,?,?,?,?,?,SYSDATE(),SYSDATE())";
	
	private String getIdByNameQuery = "SELECT RAW_REPORT_CATEGORY_ID FROM RAW_REPORT_CATEGORY WHERE REPORT_CATEGORY_NAME = ?";
	
	private String getIdByNameAndSuperCategoryQuery = "SELECT RAW_REPORT_CATEGORY_ID FROM RAW_REPORT_CATEGORY WHERE REPORT_CATEGORY_NAME = ? AND REPORT_SUPER_CATEGORY_ID IN "
			+ "	(SELECT RAW_REPORT_CATEGORY_ID FROM RAW_REPORT_CATEGORY WHERE REPORT_CATEGORY_NAME = ?) AND TENANT_ID=? AND PRODUCT_ID=? ";
	
	private String getIdByNameAndSuperCategoryNullQuery = "SELECT RAW_REPORT_CATEGORY_ID FROM RAW_REPORT_CATEGORY WHERE REPORT_CATEGORY_NAME = ? AND REPORT_SUPER_CATEGORY_ID IS NULL AND TENANT_ID=? AND PRODUCT_ID=? ";
	
	private String getIdByNameAndSuperCategoryID = "SELECT RAW_REPORT_CATEGORY_ID FROM RAW_REPORT_CATEGORY WHERE REPORT_CATEGORY_NAME = ? AND REPORT_SUPER_CATEGORY_ID =? AND TENANT_ID=? AND PRODUCT_ID=? ";
	
	private String deleteCategoryByName = "DELETE FROM RAW_REPORT_CATEGORY WHERE REPORT_CATEGORY_NAME = ?";
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonConstants.DATETIME_PATTERN);
	/**
	 * 
	 * The below method is used to get list of Report Category List
	 */
	@Override
	public List<ReportCategory> getReportCategoryDetails(String tenantId,
			String productId) {
		logger.debug(this.getClass().getName() + " - getReportCategoryDetails - START");
		logger.debug(this.getClass().getName() + " - getReportCategoryDetails --- Query : " +reportCatByIdQuery);
		List<ReportCategory> reportCatList = null;
		try {
			reportCatList = jdbcTemplate.query(reportCatListByIdQuery, new ReportCategoryMapper(), tenantId, productId);
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in getReportCategoryDetails - ", e);
		}
		logger.debug(this.getClass().getName() + " - getReportCategoryDetails - END");
		return reportCatList;
	}

	@Override
	public ReportCategory getReportCategoryId(long reportCategoryId) {
		logger.debug(this.getClass().getName() + " - getReportCategoryId - START");
		logger.debug(this.getClass().getName() + " - getReportCategoryId -- Query : " +reportCatByIdQuery);
		ReportCategory reportCategory = null;
		try {
			List<ReportCategory> reportCategories = jdbcTemplate.query(reportCatByIdQuery, new ReportCategoryMapper(), reportCategoryId);
			if(reportCategories != null && reportCategories.size() > 0){
				reportCategory = reportCategories.get(0);
			}
			
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in getReportCategoryId - ", e);
		}
		logger.debug(this.getClass().getName() + " - getReportCategoryId - END");
		return reportCategory;
	}

	@Override
	public String getReportNameCategoryId(long reportCategoryId) {
		logger.debug(this.getClass().getName() + " - getReportNameCategoryId - START");
		logger.debug(this.getClass().getName() + " - getReportNameCategoryId -- Query : " +reportNameByIdQuery);
		String name = null;
		try {
			name = jdbcTemplate.queryForObject(reportNameByIdQuery, new Object[] { reportCategoryId }, String.class);
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in getReportNameCategoryId - ", e);
		}
		logger.debug(this.getClass().getName() + " - getReportNameCategoryId - END");
		return name;
	}

	@Override
	public Long insertReportCategory(ReportCategory reportCategory) {
		logger.debug(this.getClass().getName() + " - insertReportCategory - START");
		logger.debug(this.getClass().getName() + " - insertReportCategory -- Query : " +insertReportCatQuery);
		Long id = null;
		
		List<Object> params = new ArrayList<Object>();
		try {
			/*params.add(reportCategory.getTenantId());
			params.add(reportCategory.getProductId());
			params.add(reportCategory.getReportCategoryName());
			params.add(reportCategory.getReportCategoryDescription());
			params.add(reportCategory.getReportSuperCategoryId() != 0 ? reportCategory.getReportSuperCategoryId() : null);
			params.add(reportCategory.isDisplayStatus() == true ? "Y" : "N");
			params.add(reportCategory.isRestricted() == true ? "Y" : "N");
			params.add(reportCategory.getCreatedUser());
			params.add(reportCategory.getModifiedUser());*/
			
			//int i = jdbcTemplate.update(insertReportCatQuery, params.toArray());
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			int i = jdbcTemplate.update( new PreparedStatementCreator() {
		        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		            PreparedStatement ps =
		                connection.prepareStatement(insertReportCatQuery,new String[] {"RAW_REPORT_CATEGORY_ID"});
		            //ps.setString(1, name);
		            ps.setString(1,reportCategory.getTenantId());
					ps.setString(2,reportCategory.getProductId());
					ps.setString(3,reportCategory.getReportCategoryName());
					ps.setString(4,reportCategory.getReportCategoryDescription());
					ps.setString(5,reportCategory.getReportSuperCategoryId() != 0 ? ""+reportCategory.getReportSuperCategoryId() : null);
					ps.setString(6,reportCategory.isDisplayStatus() == true ? "Y" : "N");
					ps.setString(7,reportCategory.isRestricted() == true ? "Y" : "N");
					ps.setString(8,reportCategory.getCreatedUser());
					ps.setString(9,reportCategory.getModifiedUser());
		            return ps;
		        }
		    }, keyHolder);
			logger.debug("ReportCategoryRepositoryImpl - insertReportCategory - number of recordes effected : "+i);
			logger.debug(this.getClass().getName() + " - insertReportCategory - END");
			id = (Long) keyHolder.getKey();
			
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in insertReportCategory - ", e);
		}
		return id;
	}
	@Override
	public List<Long> categoryIdByName(String categoryName) {
		logger.info(this.getClass().getName() + " - categoryIdByName - START");
		logger.debug(this.getClass().getName() + " - categoryIdByName -- Query : " +getIdByNameQuery);
		List<Long> categoryId = null;
		try {
			categoryId = jdbcTemplate.queryForList(getIdByNameQuery, Long.class, categoryName);
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in categoryIdByName - ", e);
		}
		return categoryId;
	}
	
	@Override
	public Long categoryIdByNameAndSuperCategory(String categoryName,String superCategoryName,String tenantID, String prodCode) {
		logger.info(this.getClass().getName() + " - categoryIdByName - START");
		logger.debug(this.getClass().getName() + " - categoryIdByName -- Query : " +getIdByNameQuery);
		Long categoryId = null;
		List<Long> catIds =  null;
		try {
			if(superCategoryName == null){
				catIds = jdbcTemplate.queryForList(getIdByNameAndSuperCategoryNullQuery,Long.class, categoryName,tenantID,prodCode);
				
				if(catIds != null && catIds.size() > 0)
				{
					categoryId = catIds.get(0);
				}
			}
			else{
				catIds = jdbcTemplate.queryForList(getIdByNameAndSuperCategoryQuery,Long.class, categoryName,superCategoryName,tenantID,prodCode);
				if(catIds != null && catIds.size() > 0)
				{
					categoryId = catIds.get(0);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in categoryIdByName - ", e);
		}
		return categoryId;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Long categoryIdByNameAndSuperCategoryID(String categoryName,Long superCategoryID,String tenantID, String prodCode) {
		logger.info(this.getClass().getName() + " - categoryIdByName - START");
		logger.debug(this.getClass().getName() + " - categoryIdByName -- Query : " +getIdByNameQuery);
		Long categoryId = null;
		List<Long> catIds =  null;
		try {
			
			catIds=jdbcTemplate.queryForList(getIdByNameAndSuperCategoryID,Long.class, categoryName,superCategoryID,tenantID,prodCode);
			if(catIds != null && catIds.size() > 0)
			{
				categoryId = catIds.get(0);
			}
			
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in categoryIdByName - ", e);
		}
		return categoryId;
	}
	
	
	
	
	@Override
	public void deleteReportCategoryByName(String name) {
		logger.info(this.getClass().getName() + " - deleteReportCategoryByName - START");
		logger.debug(this.getClass().getName() + " - deleteReportCategoryByName -- Query : " +getIdByNameQuery);
		try {
			int raws = jdbcTemplate.update(deleteCategoryByName, new Object[] {name});
			logger.debug("Total no of ReportCategories(s) deleted-"+raws);
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in deleteReportCategoryByName - ", e);
		}
	}
}

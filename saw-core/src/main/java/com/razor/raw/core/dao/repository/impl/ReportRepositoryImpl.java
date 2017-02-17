package com.razor.raw.core.dao.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.dao.mapper.ColumnMapper;
import com.razor.raw.core.dao.mapper.ParameterMapper;
import com.razor.raw.core.dao.mapper.ReportMapper;
import com.razor.raw.core.dao.repository.ReportRepository;
import com.razor.raw.core.pojo.Column;
import com.razor.raw.core.pojo.Columns;
import com.razor.raw.core.pojo.Parameter;
import com.razor.raw.core.pojo.Parameters;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.core.pojo.ReportCategory;
import com.razor.raw.core.pojo.ReportParameter;
import com.razor.raw.core.pojo.ReportResult;
import com.razor.raw.core.pojo.ViewMetaDataEntity;

/**
 * 
 * @author surendra.rajaneni
 *
 */
@Repository
public class ReportRepositoryImpl implements ReportRepository {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportRepositoryImpl.class);
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ReportRepositoryImpl(JdbcTemplate jdbc) {
		jdbcTemplate = jdbc;
	}

	private final String reportByIdQuery = "SELECT RAW_REPORTS_ID, RAW_PRODUCT_VIEWS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, REPORT_DESCRIPTION, REPORT_QUERY, REPORT_QUERY_TYPE, DISPLAY_STATUS, DESIGNER_QUERY, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE FROM RAW_REPORTS WHERE RAW_REPORTS_ID = ?";

	private final String reportByCatIdQuery = "SELECT RAW_REPORTS_ID, RAW_PRODUCT_VIEWS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, REPORT_DESCRIPTION, REPORT_QUERY, REPORT_QUERY_TYPE, DISPLAY_STATUS, DESIGNER_QUERY, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE FROM RAW_REPORTS WHERE RAW_REPORT_CATEGORY_ID = ? AND DISPLAY_STATUS='Y' ORDER BY MODIFIED_DATE DESC ";
	
	private final String reportByRestrictedCatIdQuery = " SELECT 	RAW_REPORTS_ID, RAW_PRODUCT_VIEWS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, REPORT_DESCRIPTION, REPORT_QUERY,  "+ 
			" 		REPORT_QUERY_TYPE, DISPLAY_STATUS, DESIGNER_QUERY, CREATED_USER,  "+ 
			" 		MODIFIED_USER, CREATED_DATE, MODIFIED_DATE  "+ 
			" FROM 	RAW_REPORTS  "+ 
			" WHERE 	RAW_REPORT_CATEGORY_ID = ? AND DISPLAY_STATUS='Y' AND CREATED_USER= ? ORDER BY MODIFIED_DATE DESC ";


	private final String getRestrictedStatusByCatId = "SELECT RESTRICTED FROM RAW_REPORT_CATEGORY WHERE RAW_REPORT_CATEGORY_ID = ? ";
	
	private String reportColumnsByIdQuery = "SELECT RAW_REPORT_COLUMNS_ID , RAW_REPORTS_ID , ALIGN , LABEL , NAME , ALIAS , PATTERN , SIZE , TYPE , GROUPBY , RENDERCRITERIA , SELECTED , AGGREGATE , SORTTYPE , CRITERIA , VIEWNAME , CRITERIAOPTION FROM RAW_REPORT_COLUMNS WHERE RAW_REPORTS_ID = ? and SELECTED = 'Y'";

	private String reportParamByIdQuery = "SELECT RAW_REPORT_PARAMETERS_ID, RAW_REPORTS_ID, LOOKUPS, DEFAULTVALUE, NAME, TYPE, VALUE, DISPLAY, OPTIONAL,EXEC_ORDER_INDEX FROM RAW_REPORT_PARAMETERS WHERE RAW_REPORTS_ID = ?";

	private String scheduledJobs = "SELECT JOB_NAME FROM QRTZ_JOB_DETAILS WHERE JOB_GROUP=?";

	private String scheduledReportJob = "SELECT JOB_NAME FROM QRTZ_JOB_DETAILS WHERE JOB_GROUP=? AND JOB_NAME=? ";

	private String insertReportQuery = "INSERT INTO RAW_REPORTS(RAW_PRODUCT_VIEWS_ID, RAW_REPORT_CATEGORY_ID, TENANT_ID, PRODUCT_ID, REPORT_NAME, REPORT_DESCRIPTION, REPORT_QUERY, REPORT_QUERY_TYPE, DISPLAY_STATUS, DESIGNER_QUERY, CREATED_USER, MODIFIED_USER, CREATED_DATE, MODIFIED_DATE) values (?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),SYSDATE())";

	private String insertParameterQuery = "INSERT INTO RAW_REPORT_PARAMETERS(RAW_REPORTS_ID, LOOKUPS, DEFAULTVALUE, NAME, TYPE, VALUE, DISPLAY, OPTIONAL,EXEC_ORDER_INDEX) values(?,?,?,?,?,?,?,?,?)";

	private String insertColumnQuery = "INSERT INTO RAW_REPORT_COLUMNS(RAW_REPORTS_ID, ALIGN, LABEL, NAME, ALIAS, PATTERN, SIZE, TYPE, GROUPBY, RENDERCRITERIA, SELECTED, AGGREGATE, SORTTYPE, CRITERIA, VIEWNAME, CRITERIAOPTION) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private String isReportExistQuery = "SELECT RAW_REPORTS_ID FROM RAW_REPORTS WHERE REPORT_NAME = ? AND RAW_REPORT_CATEGORY_ID = ? AND DISPLAY_STATUS='Y' ";
	private String isMyReportExistQuery = "SELECT RAW_REPORTS_ID FROM RAW_REPORTS WHERE REPORT_NAME = ? AND RAW_REPORT_CATEGORY_ID = ? AND CREATED_USER = ? AND DISPLAY_STATUS='Y' ";
	
	private String deleteReportQuery = "DELETE FROM RAW_REPORTS WHERE RAW_REPORTS_ID = ?";
	
	private String deleteReportColumns = "DELETE FROM RAW_REPORT_COLUMNS WHERE RAW_REPORTS_ID = ?";
	
	private String deleteReportParameters = "DELETE FROM RAW_REPORT_PARAMETERS WHERE RAW_REPORTS_ID = ?";
	
	private String deleteReportPublishedvalues = "DELETE FROM RAW_PUBLISHED_REPORTS WHERE RAW_REPORTS_ID = ?";
	
	private String updateActiveByReportID="UPDATE RAW_REPORTS SET DISPLAY_STATUS =? WHERE RAW_REPORTS_ID = ?";
	
	private String updateScheduledDateByReportID="UPDATE RAW_REPORTS SET REPORT_SCHEDULED_DT = SYSDATE() WHERE RAW_REPORTS_ID = ?";
	
	private String inActiveByReportId="UPDATE RAW_REPORTS SET DISPLAY_STATUS =?, MODIFIED_DATE=SYSDATE(), MODIFIED_USER= ? WHERE RAW_REPORTS_ID = ?";
	
	
	private String updateReportQueryByReportID="UPDATE RAW_REPORTS SET REPORT_QUERY =?, MODIFIED_DATE=SYSDATE(), MODIFIED_USER= ? WHERE RAW_REPORTS_ID = ?";
	
	private String validateTenantIDProdCodeComb="SELECT RAW_DATA_SOURCE_ID FROM RAW_DATA_SOURCE  WHERE TENANT_ID= ? AND PRODUCT_ID=? "
			+ "AND SERVER_NAME IS NOT NULL AND SCHEMA_NAME IS NOT NULL AND PORT IS NOT NULL ";
			
 

	/**
	 * Get List of ReportCategory and Sub Category based on UserId
	 * 
	 * @param userId
	 * @return
	 */
	public List<ReportCategory> getReportCategory(String userId) {
		// return jdbcTemplate.query(reportByIdQuery,new ReportMapper(),userId);
		return null;
	}

	/**
	 * Get the List
	 * 
	 * @param categoryId
	 * @return
	 */
	public Report getReportByReportId(long reportId) {
		logger.debug(this.getClass().getName()
				+ " - getReportByReportId - START");
		logger.debug(this.getClass().getName()
				+ " - getReportByReportId --- Query : " + reportByIdQuery);
		Report report = null;
		List<Report> reports = jdbcTemplate.query(reportByIdQuery,
				new ReportMapper(), reportId);
		if (reports != null && reports.size() > 0) {
			report = reports.get(0);
			Columns columns = getReportSelectedColumns(reportId);
			Parameters parameters = getReportParameters(reportId);
			report.setParametarised((parameters == null) ? false : true);
			report.setScheduled(isScheduled(report.getReportId(),
					report.getTenantId(), report.getProductId()));
			report.setColumns(columns);
			report.setParameters(parameters);
		}
		logger.debug(this.getClass().getName() + " - getReportByReportId - END");
		return report;
	}

	private boolean isScheduled(long reportId, String tenantId,String productID) {
		logger.debug(this.getClass().getName() + " - isScheduled - START");
		logger.debug(this.getClass().getName() + " - isScheduled --- Query : "
				+ scheduledReportJob);
		String jobGroup = null;
		logger.debug(this.getClass().getName() + " - Provided TENANT ID : " +tenantId+" and PRODUCT ID : "+productID); 
		
		String reportJob = null;
		if(!StringUtils.isEmpty(productID) && !StringUtils.isEmpty(tenantId))
		{
			jobGroup=productID+"-"+tenantId;
			Object[] params = { jobGroup, reportId };
			
			try {
				reportJob = jdbcTemplate.queryForObject(scheduledReportJob, params,
						String.class);
			} catch (EmptyResultDataAccessException e) {
				reportJob = null;
			}
			logger.debug(this.getClass().getName() + " - isScheduled - END");
		}
		
		
		return (reportJob == null) ? false : true;
	}

	/**
	 * @param name
	 * @return id of report by name
	 */
	@Override
	public long getReportIdByName(String reportName, long reportCategoryId) {
		logger.info(this.getClass().getName() + " - getReportIdByName - START");
		logger.debug(this.getClass().getName()
				+ " - getReportIdByName --- Query : " + isReportExistQuery);
		long reportId = 0;
		try {
			reportId = jdbcTemplate.queryForObject(isReportExistQuery,
					new Object[] { reportName,  reportCategoryId}, Long.class);
			logger.info(this.getClass().getName()
					+ " - getReportIdByName - START");
		} catch (DataAccessException e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getReportIdByName - ", e);
		}
		return reportId;
	}

	/**
	 * @param name
	 * @return id of report by name
	 */
	@Override
	public long getMyReportIdByName(String reportName, long reportCategoryId, String createdUser) {
		logger.info(this.getClass().getName() + " - getMyReportIdByName - START");
		logger.debug(this.getClass().getName()
				+ " - getMyReportIdByName --- Query : " + isMyReportExistQuery);
		long reportId = 0;
		try {
			reportId = jdbcTemplate.queryForObject(isMyReportExistQuery,
					new Object[] { reportName,  reportCategoryId, createdUser}, Long.class);
			logger.info(this.getClass().getName()
					+ " - getMyReportIdByName - START");
		} catch (DataAccessException e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getMyReportIdByName - ", e);
		}
		return reportId;
	}
	
	/**
	 * @param report
	 * Method is used to store the Report in RAW metadata
	 */
	
	@Override
	public void saveReport(Report report) {
		logger.info(this.getClass().getName() + " - saveReport - START");
		logger.debug(this.getClass().getName() + " - saveReport --- Query : "
				+ insertReportQuery);
		List<Object> params = new ArrayList<Object>();
		try {
			params.add(1);// TODO: productViewId on desiner time to set from UI
			params.add(report.getReportCategoryId());
			params.add(report.getTenantId());
			params.add(report.getProductId());
			params.add(report.getReportName());
			params.add(report.getReportDescription());
			params.add(report.getReportQuery());
			params.add(report.getReportQueryType());
			params.add(CommonConstants.TRUE_IND);// display status
			params.add(report.isDesignerQuery() == true ? CommonConstants.TRUE_IND
					: CommonConstants.FALSE_IND);
			params.add(report.getCreatedUser());// created User
			params.add(report.getCreatedUser());// Modified user
			/*params.add(report.getParameters() != null ? CommonConstants.TRUE_IND
					: CommonConstants.FALSE_IND);
			params.add(CommonConstants.FALSE_IND);//*/	
			int result = jdbcTemplate.update(insertReportQuery, params.toArray());
			System.out.println("Total report inserted = "+result);
			/*long reportId = getReportIdByName(report.getReportName(), report.getReportCategoryId());
			int paramRelult;
			int colResult;
			if (report.getParameters() != null) {
				paramRelult = saveParameters(report.getParameters(), reportId);
				if(paramRelult == 0){
					deleteReport(reportId);
					reportId = 0;
				}
			}
			if (report.getColumns() != null && reportId != 0) {
				colResult = saveColumns(report.getColumns(), reportId);
				if(colResult == 0)
					deleteReport(reportId);
			}*/
			
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in saveReport - ", e);
		}
		logger.debug(this.getClass().getName() + " - saveReport - END");
	}

	/**
	 * @param parameters
	 *            Methos is uesd to save the parameter for RAW metadata
	 */
	@Override
	public int saveParameters(Parameters parameters, long reportId) {
		logger.info(this.getClass().getName() + " - saveParameters - START");
		logger.debug(this.getClass().getName()
				+ " - saveParameters --- Query : " + insertReportQuery);
		List<Object> params;
		int result = 0;
		try {
			if (parameters.getParameter() != null) {
				for (Parameter parameter : parameters.getParameter()) {
					params = new ArrayList<Object>();
					params.add(reportId);
					params.add(parameter.getLookupValue());
					params.add(parameter.getDefaultValue());
					params.add(parameter.getName());
					params.add(parameter.getType());
					params.add(parameter.getValue());
					params.add(parameter.getDisplay());
					params.add(parameter.isOptional() == true ? 1
							: 0);
					params.add(parameter.getIndex());
					result = jdbcTemplate.update(insertParameterQuery, params.toArray());
					logger.info(this.getClass().getName()
							+ " - saveParameters - END");
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in saveParameters - ", e);
		}
		return result;
	}

	/**
	 * @param columns
	 *            Save the column data into RAW
	 */
	@Override
	@Transactional
	public int saveColumns(Columns columns, long reportId) {
		logger.info(this.getClass().getName() + " - saveColumns - START");
		logger.debug(this.getClass().getName() + " - saveColumns --- Query : "
				+ insertColumnQuery);
		List<Object> params;
		int result = 0;
		try {
			if (columns.getColumn() != null) {
				for (Column column : columns.getColumn()) {
					params = new ArrayList<Object>();
					params.add(reportId);
					params.add(column.getAlign());
					params.add(column.getLabel());
					params.add(column.getName());
					params.add(column.getAlias());
					params.add(column.getPattern());
					params.add(column.getSize());
					params.add(column.getType());
					params.add(column.getGroupBy());
					params.add(column.getRenderCriteria());
					params.add(column.isSelected() == true ? CommonConstants.TRUE_IND
							: CommonConstants.FALSE_IND);
					params.add(column.getAggregate());
					params.add(column.getSortType());
					params.add(column.getCriteria());
					params.add(column.getViewName());
					params.add(column.getCriteriaOption());
					result = jdbcTemplate.update(insertColumnQuery, params.toArray());
					logger.info(this.getClass().getName()
							+ " - saveColumns - END");
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in saveColumns - ", e);
		}
		return result;
	};

	@Override
	public List<Report> getReportList(long categoryId, String tenantId, String userID,String productID) {
		logger.debug(this.getClass().getName() + " - getReportList - START");
		logger.debug(this.getClass().getName()
				+ " - getReportList --- Query : " + reportByCatIdQuery);
		
		boolean isRestricted = getRestrictedStatusForCatId(categoryId);
		
		List<String> reportJobs = getScheduledReports(tenantId,productID);
		List<Report> reports = null;
		try {
			
			if(isRestricted){
				reports = jdbcTemplate.query(reportByRestrictedCatIdQuery,
						new ReportMapper(), categoryId,userID );
			}
			else{
				reports = jdbcTemplate.query(reportByCatIdQuery,
						new ReportMapper(), categoryId);
			}
			if (reports != null && reports.size() > 0) {
				for (Report report : reports) {
					if (reportJobs.contains(report.getReportId() + "")) {
						report.setScheduled(true);
					}

					Columns columns = getReportSelectedColumns(report.getReportId());
					Parameters parameters = getReportParameters(report
							.getReportId());
					report.setParametarised((parameters == null) ? false : true);
					report.setColumns(columns);
					report.setParameters(parameters);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getReportList - ", e);
		}
		logger.debug(this.getClass().getName() + " - getReportList - END");
		return reports;
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
	

	@Override
	public ReportResult getReportData(String reportSQL,
			List<ReportParameter> parameters, AtomicInteger totalRecords,
			int pageNumber, int rowsPerPage, String queryType, boolean isPreview) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ViewMetaDataEntity> getCustomerViewNamesBasedOnPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteReport(long reportId, boolean isPublishedReports) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void deleteReport(long reportId) {
		logger.info(this.getClass().getName() + " - deleteReport - START");
		logger.debug(this.getClass().getName() + " - deleteReport -- Query : " +deleteReportQuery);
		try {
			jdbcTemplate.update(deleteReportQuery, new Object[] {reportId});
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in deleteReport - ", e);
		}
	}
	
	
	@Override
	public void deleteReportColumns(long reportId) {
		logger.info(this.getClass().getName() + " - deleteReport - START");
		logger.debug(this.getClass().getName() + " - deleteReport -- Query : " +deleteReportColumns);
		try {
			jdbcTemplate.update(deleteReportColumns, new Object[] {reportId});
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in deleteReport - ", e);
		}
	}
	
	@Override
	public void deleteReportParameters(long reportId) {
		logger.info(this.getClass().getName() + " - deleteReport - START");
		logger.debug(this.getClass().getName() + " - deleteReport -- Query : " +deleteReportParameters);
		try {
			jdbcTemplate.update(deleteReportParameters, new Object[] {reportId});
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in deleteReport - ", e);
		}
	}
	
	@Override
	public void deleteReportPublishedValues(long reportId) {
		logger.info(this.getClass().getName() + " - deleteReport - START");
		logger.debug(this.getClass().getName() + " - deleteReport -- Query : " +deleteReportPublishedvalues);
		try {
			jdbcTemplate.update(deleteReportPublishedvalues, new Object[] {reportId});
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in deleteReport - ", e);
		}
	}
	
	@Override
	public void activateReport(long reportId) {
		logger.info(this.getClass().getName() + " - deleteReport - START");
		logger.debug(this.getClass().getName() + " - deleteReport -- Query : " +updateActiveByReportID);
		try {
			jdbcTemplate.update(updateActiveByReportID, new Object[] {"Y",reportId});
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in deleteReport - ", e);
		}
	}
	
	@Override
	public void updateReportScheduledDate(long reportId) {
		logger.debug(this.getClass().getName() + " - updateReportScheduledDate -- Query : " +updateScheduledDateByReportID);
		try {
			jdbcTemplate.update(updateScheduledDateByReportID, new Object[] {reportId});
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in updateReportScheduledDate - ", e);
		}
	}
	
	
	
	
	@Override
	public void inActivateReport(long reportId, String modifiedUser) {
		logger.info(this.getClass().getName() + " - inActivateReport - START");
		logger.debug(this.getClass().getName() + " - inActivateReport -- Query : " +inActiveByReportId);
		try {
			jdbcTemplate.update(inActiveByReportId, new Object[] {"N", modifiedUser,reportId});
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in inActivateReport - ", e);
		}
	}
	
	@Override
	public void updateReportQueryByID(String reportQuery,long reportId, String modifiedUser) {
		logger.info(this.getClass().getName() + " - updateReportQueryByID - START");
		logger.debug(this.getClass().getName() + " - updateReportQueryByID -- Query : " +updateReportQueryByReportID);
		try {
			jdbcTemplate.update(updateReportQueryByReportID, new Object[] {reportQuery,modifiedUser, reportId });
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in updateReportQueryByID - ", e);
		}
	}
	
	

	@Override
	public boolean verifyReportName(Report reportVO) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFtpIsEnabled(String customerId, String productId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Parameters getReportParameters(long reportId) {
		logger.debug(this.getClass().getName() + " - getReportList - START");
		logger.debug(this.getClass().getName()
				+ " - getReportList --- Query : " + reportParamByIdQuery);
		Parameters parameters = null;
		try {
			List<Parameter> listParameters = jdbcTemplate.query(
					reportParamByIdQuery, new ParameterMapper(), reportId);
			if (listParameters != null && listParameters.size() > 0) {
				parameters = new Parameters();
				parameters.setParameter(listParameters);
			}

		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getReportParameters - ", e);
		}
		logger.debug(this.getClass().getName() + " - getReportParameters - END");
		return parameters;
	}

	@Override
	public boolean validateTenantIDProdIDComb(String tenantID,String prodID){
		logger.debug(this.getClass().getName() + " - validateTenantIDProdIDComb - START");
		logger.debug(this.getClass().getName()
				+ " - getReportColumns --- Query : " + validateTenantIDProdCodeComb);
		
		try {
			long reportId=0;
			reportId = jdbcTemplate.queryForObject(validateTenantIDProdCodeComb,
					new Object[] { tenantID,  prodID}, Long.class);
			
			if (reportId == 0) {
				return false;
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getReportColumns - ", e);
			return false;
		}
		logger.debug(this.getClass().getName() + " - validateTenantIDProdIDComb - END");
		return true;
	}
	
	
	@Override
	public Columns getReportSelectedColumns(long reportId) {
		logger.debug(this.getClass().getName() + " - getReportColumns - START");
		logger.debug(this.getClass().getName()
				+ " - getReportColumns --- Query : " + reportColumnsByIdQuery);
		Columns columns = null;
		try {
			List<Column> listColumns = jdbcTemplate.query(
					reportColumnsByIdQuery, new ColumnMapper(), reportId);
			if (listColumns != null && listColumns.size() > 0) {
				columns = new Columns();
				columns.setColumn(listColumns);
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getReportColumns - ", e);
		}
		logger.debug(this.getClass().getName() + " - getReportColumns - END");
		return columns;
	}

	private List<String> getScheduledReports(String tenantId,String productID) {
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
	/**
	 * Save the Report into RIR Meta data
	 * 
	 * @param report
	 */
	/*
	 * void saveReport(Report report);
	 *//**
	 * The below method is used to retrieve the published report information
	 * from table <>
	 * 
	 * @param PublishedReport
	 *            object
	 * @return List<PublishedReport> : contains published report information
	 */
	/*
	 * public List<PublishedReport> getPublishedReports(PublishedReport
	 * publishedReport);
	 *//**
	 * 
	 * @param reportSQL
	 * @param parameters
	 * @param totalRecords
	 * @param pageNumber
	 * @param rowsPerPage
	 * @param queryType
	 * @param isPreview
	 * @return
	 */
	/*
	 * ReportResult getReportData(String reportSQL, List<ReportParameter>
	 * parameters, AtomicInteger totalRecords, int pageNumber, int rowsPerPage,
	 * String queryType, boolean isPreview);
	 * 
	 * 
	 * void insertPublishedReports();
	 * 
	 * public List<PublishedReport> getPublishedReports(String userId, String
	 * prodId);
	 * 
	 * List<ViewMetaDataEntity> getCustomerViewNamesBasedOnPattern();
	 * 
	 * void deleteReport(long reportId, boolean isPublishedReports);
	 * 
	 * boolean verifyReportName(Report reportVO);
	 *//**
	 * 
	 * @param reportDefinationImpl
	 * @param recordLimit
	 * @param connection
	 * @return
	 */
	/*
	 * boolean isFtpIsEnabled(String customerId, String productId);
	 */
}

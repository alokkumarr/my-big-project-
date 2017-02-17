package com.razor.raw.core.dao.repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.razor.raw.core.pojo.Columns;
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
public interface ReportRepository {
	
	
	/** 
	 * Get List of ReportCategory and Sub Category based on UserId
	 * @param userId
	 * @return
	 */
	public List<ReportCategory> getReportCategory(String userId);
	
	/**
	 * Save the Report into RAW Meta data
	 * @param report
	 */
	void saveReport(Report report);
	/**
	 * 
	 * @param parameter
	 *  @param reportId
	 * Save the Parameter into RAW metadata
	 */
	int saveParameters(Parameters parameter, long reportId);
	/**
	 * 
	 * @param columns
	 * @param reportId
	 * Save the Column into RAW metadata
	 */
	int saveColumns(Columns columns,long reportId);
	
	/**
	 * Get the List 
	 * @param categoryId
	 * @param tenantId
	 * @return
	 */
	List<Report> getReportList(long categoryId, String tenantId, String userID, String productID);
	
	/**
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
	ReportResult getReportData(String reportSQL, List<ReportParameter> parameters, AtomicInteger totalRecords, int pageNumber, int rowsPerPage, String queryType, boolean isPreview);
	/**
	 * 
	 * @return list of VewMetaDataEntity
	 */
	List<ViewMetaDataEntity> getCustomerViewNamesBasedOnPattern();
	/**
	 * This is used to delete the report by reportId and isPublishedReports
	 * @param reportId
	 * @param isPublishedReports
	 */
	void deleteReport(long reportId, boolean isPublishedReports);
	/**
	 * This is used to delete the report by reportId and reportCategory
	 * @param reportId
	 * @param isPublishedReports
	 */
	void deleteReport(long reportId);
	/**
	 * This used to verify report is exist or not
	 * @param reportVO
	 * @return boolean
	 */
	boolean verifyReportName(Report reportVO);
	
	/**
	 * 
	 * @param reportDefinationImpl
	 * @param recordLimit
	 * @param connection
	 * @return
	 */
	boolean isFtpIsEnabled(String customerId, String productId);
	
	/**
	 * Get Report By ReportID
	 * 
	 * @param reportId
	 * @return
	 */
	Report getReportByReportId(long reportId);
	
	/**
	 * get List of report parameters.
	 * @param reportId
	 * @return
	 */
	Parameters getReportParameters(long reportId);
	
	/**
	 * Get List of report columns.
	 * @param reportId
	 * @return
	 */
	Columns  getReportSelectedColumns(long reportId);
	/**
	 * 
	 * @param name
	 * @return the id of report by name
	 */
	long getReportIdByName(String name, long reportCategoryId);
	
	/**
	 * 
	 * @param name
	 * 
	 */
	void deleteReportColumns(long reportId);
	
	/**
	 * 
	 * @param name
	 * @return the id of report by name
	 */
	void deleteReportParameters(long reportId);
	
	/**
	 * 
	 * @param name
	 * @return the id of report by name
	 */
	void deleteReportPublishedValues(long reportId);
	
	/**
	 * 
	 * @param name
	 * @return the id of report by name
	 */
	void activateReport(long reportId);

	
	/**
	 * 
	 * @param name
	 * @return the id of report by name
	 */
	void inActivateReport(long reportId, String modifiedUser);
	
	/**
	 * 
	 * @param name
	 * @return the id of report by name
	 */
	void updateReportQueryByID(String reportQuery,long reportId, String modifiedUser);
	
	
	/**
	 * 
	 * @param name
	 * @return the id of report by name
	 */
	boolean validateTenantIDProdIDComb(String tenantID,String prodID);
	
	/**
	 * 
	 * @param categoryId
	 * @return boolean whether restricted or not
	 */
	boolean getRestrictedStatusForCatId(long categoryId);

	long getMyReportIdByName(String reportName, long reportCategoryId,
			String createdUser);

	void updateReportScheduledDate(long reportId);	

	
	
}

package com.razor.raw.core.dao.repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.razor.raw.core.pojo.Columns;
import com.razor.raw.core.pojo.Parameters;
import com.razor.raw.core.pojo.PublishedReport;
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
public interface ReportCategoryRepository {
	
	
	/** 
	 * Get List of ReportCategory and Sub Category based on tenantId and Product Id
	 * @param userId
	 * @return
	 */
	List<ReportCategory> getReportCategoryDetails(String tenantId, String productId);
	
	/**
	 * 
	 * @param reportCategoryId
	 * @return
	 */
	ReportCategory getReportCategoryId(long reportCategoryId);
	
	/**
	 * Get Report Category by Category id
	 * @param reportCategoryId
	 * @return
	 */
	String getReportNameCategoryId(long reportCategoryId);	
	/**
	 * This is use to insert data in Report Category table
	 * @param reportCategory
	 * @return 
	 */
	Long insertReportCategory(ReportCategory reportCategory);
	/**
	 * 
	 * @param categoryName
	 * @return list category id by name
	 */
	List<Long> categoryIdByName(String categoryName);
	/**
	 * This use the delete report category
	 * @param name
	 */
	void deleteReportCategoryByName(String name);
	/**
	 * 
	 * @param categoryName
	 * @param superCategoryName
	 * 
	 * @return  category id by name
	 */
	Long categoryIdByNameAndSuperCategory(String categoryName,String superCategoryName,String tenantID, String prodCode);
	/**
	 * 
	 * @param categoryName
	 * @param superCategoryID
	 * 
	 * @return  category 
	 */
	
	public Long categoryIdByNameAndSuperCategoryID(String categoryName,Long superCategoryID,String tenantID, String prodCode);
}

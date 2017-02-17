package com.razor.raw.generation.rest.service.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.dao.repository.DataSourceRepository;
import com.razor.raw.core.dao.repository.PublishedReportRepository;
import com.razor.raw.core.dao.repository.ReportRepository;
import com.razor.raw.core.pojo.Column;
import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.drill.services.DrillServices;
import com.razor.raw.drill.services.impl.DrillServicesImpl;
import com.razor.raw.generation.rest.bean.ReportViewBean;
import com.razor.raw.generation.rest.bean.ReportViewReqBean;
import com.razor.raw.generation.rest.service.ReportService;
import com.razor.raw.logging.LogManager;

@Service
public class ReportServiceImpl implements ReportService{
	private static final Logger logger = LoggerFactory
			.getLogger(ReportServiceImpl.class);
	@Autowired
	public ReportRepository reportRepository;
	@Autowired
	public DataSourceRepository dataSourceRepository;
	
	@Autowired
	public PublishedReportRepository publishedReportRepository;
	/**
	 * @param categoryId
	 * @param tenantId
	 * @return list of Report
	 */
	@Override
	public List<Report> getReportByCategoryId(long categoryId, String tenantId,String userID,String productID) {
		logger.debug(this.getClass().getName() + " - getReportByCategoryId - START");
		List<Report> reportList = reportRepository.getReportList(categoryId,tenantId,userID,productID);
		logger.debug(this.getClass().getName() + " - getReportByCategoryId - END");
		return reportList;
	}
	/**
	 * @param reportViewReqBean
	 * return the list of report by report
	 */
	@Override
	public ReportViewBean getReportViewByReport(ReportViewReqBean reportViewReqBean) {
		logger.debug(this.getClass().getName() + " - getReportViewByReport - START-- reportId = "+reportViewReqBean.getReport().getReportId());
		Connection connection = null;
		ResultSet resultSet = null;
		ResultSet resultSet1 = null;
		ReportViewBean reportViews = new ReportViewBean();
		List<Map<String, String>> reportViewList = null;
		DrillServices drillServices = new DrillServicesImpl();
		Report report = reportViewReqBean.getReport();
		long totalCount = 0;
		Map<String,String> displayColumns = prepareDisplayColumnsSet(report);
		try {
			Datasource datasource = dataSourceRepository.getDataSourec(reportViewReqBean.getReport().getTenantId(), reportViewReqBean.getReport().getProductId());
			connection = drillServices.getConnection(datasource);
			totalCount = drillServices.getTotalCount(reportViewReqBean.getReport(), connection);
			reportViews.setTotalCount(totalCount);
			if(totalCount ==0){
				return reportViews;
			}
			resultSet = drillServices.executeQueryByReport(reportViewReqBean.getReport(),reportViewReqBean.getRecordLimit(), connection);
			reportViewList = new ArrayList<Map<String, String>>();
			if(resultSet != null ){
				ResultSetMetaData rsMetaData = resultSet.getMetaData();
				int numberOfColumns = rsMetaData.getColumnCount();
				while ( resultSet.next() ) {
					Map<String, String> map = new LinkedHashMap<String, String>();
					 for (int i = 1; i <= numberOfColumns; i++) {
						 String key = rsMetaData.getColumnName(i);
						  String value = null;
						  boolean displayColumn =  isDisplayColumn(key.toUpperCase(),displayColumns);
						 if(rsMetaData.getColumnTypeName(i)!= null && displayColumn){ 
						  if (CommonConstants.SQL_DATE_TYPE.equalsIgnoreCase(rsMetaData.getColumnTypeName(i))) {
							  if(resultSet.getDate(i) != null){
								Date date = resultSet.getDate(i);
								SimpleDateFormat dateFmt = new SimpleDateFormat(CommonConstants.DATE_PATTERN);
								value = dateFmt.format(date);
							  }
							} else {
								if(CommonConstants.SQL_NUMBER_TYPE.equalsIgnoreCase(rsMetaData.getColumnTypeName(i))){
									try{
										BigDecimal bigDecimal = resultSet.getBigDecimal(i);
										if(bigDecimal != null)
											value = String.valueOf(bigDecimal.longValueExact());
									}catch(Exception e){
										LogManager.log(LogManager.CATEGORY_DATABASE, LogManager.LEVEL_ERROR, "Exception occured at "+ this.getClass().getName() + " in getReportViewByReport"
												+ LogManager.printStackTrace(e));
									}
								}else {
									try {
										value = resultSet.getString(i) != null ? resultSet.getString(i) : "";
									} catch (NullPointerException e) {
										value = "";
									}
									
								}
							}
						  if(displayColumn)
							  map.put(displayColumns.get(key.toUpperCase()), value);
					 }
					 }
					 reportViewList.add(map);
				}
				
				reportViews.setReportViewList(reportViewList);
			}
			logger.debug(this.getClass().getName() + " - getReportViewByReport - END");
		}catch(Exception e){
			logger.error("Exception occured at "+ this.getClass().getName() + "in getReportViewByReport - ", e);
		}
		finally
		{
			drillServices.closeConnection(connection, null, resultSet);
		}
		return reportViews;
	}
	
	private Map<String,String> prepareDisplayColumnsSet(Report report) {
		
		Map<String,String> displayColumns = new HashMap<String,String>();
		List<Column> columns = report.getColumns().getColumn();
		if(columns != null && columns.size() > 0)
		{
			for(Column column : columns)
			{
				if(column.isSelected() && null != column.getName() ){
					String colName = column.getName().toUpperCase();
					String columnDisplayName = column.getName();
					if(!StringUtils.isEmpty(column.getAlias())){
						columnDisplayName= column.getAlias();
					}
					else if(!StringUtils.isEmpty(column.getLabel())){
						columnDisplayName= column.getLabel();
					}
						
					displayColumns.put(colName, columnDisplayName);	
				}
				
		}
			
		}
		return displayColumns;
	}
	
	private boolean isDisplayColumn(String key, Map<String,String> displayColumns) {
		
		if(displayColumns != null && displayColumns.size() > 0)
		{
			return displayColumns.keySet().contains(key.toUpperCase());
		}
		return false;
	}
	/**
	 * @param reportName
	 * @param tenantId
	 * @param productId
	 * return boolean get publish status
	 */
	@Override
	public boolean getPublishedReportStatus(String reportName, String tenantId, String productId ) {
		// TODO Auto-generated method stub
		return publishedReportRepository.isPublishReportExist(reportName, tenantId, productId);
	}
	
	@Override
	public Report getReportByReportId(long reportId) {
		return reportRepository.getReportByReportId(reportId);
	}
	
	@Override
	public void updateReportScheduledDate(long reportId) {
		reportRepository.updateReportScheduledDate(reportId);
		
	}
	
}

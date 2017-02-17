package com.razor.raw.generation.rest.service.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.dao.repository.DataSourceRepository;
import com.razor.raw.core.dao.repository.ReportRepository;
import com.razor.raw.core.pojo.Column;
import com.razor.raw.core.pojo.Columns;
import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.DrillView;
import com.razor.raw.core.pojo.Parameters;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.drill.services.DrillServices;
import com.razor.raw.drill.services.impl.DrillServicesImpl;
import com.razor.raw.generation.rest.bean.DesignerReportReqBean;
import com.razor.raw.generation.rest.bean.DesignerSaveReportBean;
import com.razor.raw.generation.rest.bean.ReportViewBean;
import com.razor.raw.generation.rest.bean.Valid;
import com.razor.raw.generation.rest.service.DesignerReportService;
import com.razor.raw.logging.LogManager;
import com.razor.raw.utility.JsonUtilService;
import com.razor.raw.utility.ReportValidatorUtility;
import com.razor.raw.utility.beans.DeleteInactivateReqPerTenProd;
import com.razor.raw.utility.beans.DeleteReportReq;
import com.razor.raw.utility.beans.ReportInsertUpdateReq;
import com.razor.raw.utility.beans.TenantIDProdID;



@Service
public class DesignerReportServiceImpl implements DesignerReportService{
	private static final Logger logger = LoggerFactory
			.getLogger(DesignerReportServiceImpl.class);

	@Autowired
	public DataSourceRepository dataSourceRepository;

	@Autowired
	public ReportRepository reportRepository;
	
	
	@Autowired
	JsonUtilService utilService;
	
	private String statusMessage;
	/**
	 * @param designerReportReqBean
	 * return the result list for designer report
	 */
	@Override
	public ReportViewBean getDesignerReport(DesignerReportReqBean designerReportReqBean) {
		logger.debug(this.getClass().getName() + " - getDesignerReport - START-- ");
		Connection connection = null;
		ResultSetMetaData rsMetaData = null;
		ResultSet resultSet =null;
		
		ReportViewBean reportViews = new ReportViewBean();
		List<Map<String, String>> reportViewList = null;
		DrillServices drillServices = new DrillServicesImpl();
		try {
			Datasource datasource = dataSourceRepository.getDataSourec(designerReportReqBean.getTenantId(), designerReportReqBean.getProductId());
			connection = drillServices.getConnection(datasource);
			Report report = new Report();
			report.setReportQuery(designerReportReqBean.getQuery());
			report.setTenantId(designerReportReqBean.getTenantId());
			report.setProductId(designerReportReqBean.getProductId());
			report.setReportQueryType(designerReportReqBean.getReportQueryType());
			Parameters parameters = new Parameters();
			parameters.setParameter(designerReportReqBean.getParameters());
			report.setParameters(parameters);
			report.setDesignerQuery(true);
			resultSet = drillServices.executeQueryByReport(report, 10, connection);
			reportViewList = new ArrayList<Map<String, String>>();
			if(resultSet != null ){
				 rsMetaData = resultSet.getMetaData();
				int numberOfColumns = rsMetaData.getColumnCount();
				while (resultSet != null && resultSet.next()) {
					 Map<String, String> map = new LinkedHashMap<String,String>();
					 for (int i = 1; i <= numberOfColumns; i++) {
						 String key = rsMetaData.getColumnName(i);
						  String value = null;
						 if(rsMetaData.getColumnTypeName(i)!= null ){ 
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
										LogManager.log(LogManager.CATEGORY_DATABASE, LogManager.LEVEL_ERROR, "Exception occured at "+ this.getClass().getName() + " in getDesignerReport"
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
						  map.put(key.toUpperCase(), value);
					 }
					 }
					 reportViewList.add(map);
				}
				reportViews.setReportViewList(reportViewList);
				reportViews.setStatusMessage(null);
				
			}
			logger.debug(this.getClass().getName() + " - getDesignerReport - END");
		}catch(Exception e){
			logger.error("Exception occured at "+ this.getClass().getName() + "in getDesignerReport - ", e);
			String errorMsg = e.getLocalizedMessage();
			if(errorMsg != null && errorMsg.contains("\n")){
				errorMsg = e.getLocalizedMessage().substring(0, e.getLocalizedMessage().indexOf("\n"));
			}
			reportViews.setStatusMessage(errorMsg);
		}
		finally{
			drillServices.closeConnection(connection, null, resultSet);
		}
		return reportViews;
	}

	@Override
	public List<DrillView> getViewList(String tenantId, String productID) {
		logger.debug(this.getClass().getName() + " - getViewList - START--  : "+tenantId+" : "+productID);
		Connection connection = null;
		List<DrillView> viewList = null;
		ResultSet resultSet = null;
		DrillServices drillServices = new DrillServicesImpl();
		try {
			Datasource datasource = dataSourceRepository.getDataSourec(tenantId, productID);
			connection = drillServices.getConnection(datasource);
			resultSet = drillServices.getViewList(datasource.getSchemaName(), connection);
			if (resultSet != null) {
				viewList = new ArrayList<DrillView>();
				while (resultSet != null && resultSet.next()) {
					DrillView drillView = new DrillView();
					drillView.setViewName(resultSet.getString("TABLE_NAME"));
					drillView.setTableSchema(resultSet.getString("TABLE_SCHEMA"));
					viewList.add(drillView);
				}
			}
			for(DrillView drillView : viewList){
				resultSet = drillServices.getViewColumns(drillView, connection);
				if (resultSet != null) {
					if(drillView.getColumnList() == null ){
						drillView.setColumnList( new ArrayList<Column>());
					}
					List<Column> columnList = drillView.getColumnList();
					while (resultSet != null && resultSet.next()) {
						Column column = new Column();
						column.setName(resultSet.getString("COLUMN_NAME"));
						column.setType(resultSet.getString("DATA_TYPE"));
						columnList.add(column);
					}
				}
			}
		}catch(Exception e){
			logger.error("Exception occured at "+ this.getClass().getName() + "in getViewList - ", e);
		}
		finally
		{
			drillServices.closeConnection(connection, null, resultSet);
		}

		logger.debug(this.getClass().getName() + " - getViewList - END");
		return viewList;
	}

	@Override
	public Valid insertDesignerReport(DesignerSaveReportBean designerSaveReportBean) {
		String output = null;
		Valid valid = new Valid();
		valid.setValid(false);
		ReportInsertUpdateReq reportInsertUpdateReq = new ReportInsertUpdateReq();
		
		reportInsertUpdateReq.setDisplayStatus(designerSaveReportBean.getDisplayStatus());
		reportInsertUpdateReq.setReportCategoryId(designerSaveReportBean.getReportCategoryId());
		reportInsertUpdateReq.setReportCategoryName(designerSaveReportBean.getReportCategoryName());
		reportInsertUpdateReq.setReportDescription(designerSaveReportBean.getReportDescription());
		reportInsertUpdateReq.setReportName(designerSaveReportBean.getReportName());
		reportInsertUpdateReq.setReportQuery(designerSaveReportBean.getReportQuery());
		reportInsertUpdateReq.setReportQueryType(designerSaveReportBean.getReportQueryType());
		reportInsertUpdateReq.setTenantIDProdIDs(designerSaveReportBean.getTenantIDProdIDs());
		reportInsertUpdateReq.setDesignerQuery(designerSaveReportBean.isDesignerQuery());
		if(designerSaveReportBean.getParameters().size() > 0) {
			Parameters parameters = new Parameters();
			parameters.setParameter(designerSaveReportBean.getParameters());
			reportInsertUpdateReq.setParameters(parameters);
		}		
		
		ReportValidatorUtility reportValidatorUtility = new ReportValidatorUtility(); 
		try {
			reportInsertUpdateReq = reportValidatorUtility.isValidReport(reportInsertUpdateReq);
			if (reportInsertUpdateReq != null) {
				if(utilService.verifyValidityOfTenantAndProductID(reportInsertUpdateReq.getTenantIDProdIDs())){
					for(TenantIDProdID tenantIDProdID:reportInsertUpdateReq.getTenantIDProdIDs() ){
						/*derive columns for the designer query*/
						//reportInsertUpdateReq.setColumns(new Columns());
						Report report=reportValidatorUtility.createReportForTenProdID(reportInsertUpdateReq, tenantIDProdID.getTenantID(), tenantIDProdID.getProductId(), tenantIDProdID.getCreatedUser());
						report.setColumns(getDesignerQueryColumns(report));
						System.out.println("Starting operation insert"
								+ " for report " + reportInsertUpdateReq.getReportName()+" for tenant "+tenantIDProdID.getTenantID()+" and product "+tenantIDProdID.getProductId());
						output = utilService.insertReport(report);
							if (output != null && output.equals("FAILURE")) {
								output = "Report name already exists in given category Hierarchy";
							} else if (output != null && output.equals("SUCCESS")) {
								valid.setValid(true);
								output = "Report Query is Saved Successfully";
							}
						}
				}
				else{
					System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
					logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
					output = "Issue with Tenant , Product mapping";
				}
			} else {
				output = "Report Query is Invalid/parameter list is incorrect";
			}
		} catch (Exception e) {
			logger.error("Exception occcured while saving the Query"+e.getMessage());
			String errorMsg = e.getLocalizedMessage();
			if(errorMsg != null && errorMsg.contains("\n")){
				errorMsg = errorMsg.substring(errorMsg.indexOf("\n"));
			}
			output = errorMsg;
		}
		valid.setValidityMessage(output);
		return valid;
	}

	private Columns getDesignerQueryColumns(Report report) throws Exception{
		logger.debug(this.getClass().getName()
				+ " - getDesignerQueryColumns - START--  : "
				+ report.getReportName());
		Connection connection = null;
		ResultSet resultSet = null;
		Columns columns = new Columns();
		List<Column> columnList = new ArrayList<Column>();
		DrillServices drillServices = new DrillServicesImpl();
		try {
			Datasource datasource = dataSourceRepository.getDataSourec(report.getTenantId(), report.getProductId());
			connection = drillServices.getConnection(datasource);
			resultSet = drillServices.executeQueryByReport(report, 1, connection);
			if (resultSet != null) {
				ResultSetMetaData rsMetaData = resultSet.getMetaData();
				int numberOfColumns = rsMetaData.getColumnCount();
				columnList = new ArrayList<Column>();
				for (int i = 0; i < numberOfColumns; i++) {
					Column column = new Column();
					column.setName(rsMetaData.getColumnName(i+1));
					if (rsMetaData.getColumnTypeName(i+1) != null) {
						column.setType(rsMetaData.getColumnTypeName(i+1));
						column.setAlign("LEFT");
						column.setAlias(rsMetaData.getColumnName(i+1));
						column.setLabel(rsMetaData.getColumnName(i+1));
						column.setSize("100");
						column.setSelected(true);
					}
					columnList.add(column);
				}
				columns.setColumn(columnList);
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName()
					+ "in getDesignerQueryColumns - ", e);
			throw e;
		}
		finally
		{
			drillServices.closeConnection(connection, null, resultSet);
		}

		logger.debug(this.getClass().getName()
				+ " - getDesignerQueryColumns - END");
		return columns;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Override
	public Valid updateDesignerReport(
			DesignerSaveReportBean designerSaveReportBean) {
		String output = null;
		Valid valid = new Valid();
		valid.setValid(false);
		ReportInsertUpdateReq reportInsertUpdateReq = new ReportInsertUpdateReq();
		
		reportInsertUpdateReq.setDisplayStatus(designerSaveReportBean.getDisplayStatus());
		reportInsertUpdateReq.setReportCategoryId(designerSaveReportBean.getReportCategoryId());
		reportInsertUpdateReq.setReportCategoryName(designerSaveReportBean.getReportCategoryName());
		reportInsertUpdateReq.setReportDescription(designerSaveReportBean.getReportDescription());
		reportInsertUpdateReq.setReportName(designerSaveReportBean.getReportName());
		reportInsertUpdateReq.setReportQuery(designerSaveReportBean.getReportQuery());
		reportInsertUpdateReq.setReportQueryType(designerSaveReportBean.getReportQueryType());
		reportInsertUpdateReq.setTenantIDProdIDs(designerSaveReportBean.getTenantIDProdIDs());
		reportInsertUpdateReq.setDesignerQuery(designerSaveReportBean.isDesignerQuery());
		if(designerSaveReportBean.getParameters().size() > 0) {
			Parameters parameters = new Parameters();
			parameters.setParameter(designerSaveReportBean.getParameters());
			reportInsertUpdateReq.setParameters(parameters);
		}		
		
		ReportValidatorUtility reportValidatorUtility = new ReportValidatorUtility(); 
		try {
			reportInsertUpdateReq = reportValidatorUtility.isValidReport(reportInsertUpdateReq);
			if (reportInsertUpdateReq != null) {
				if(utilService.verifyValidityOfTenantAndProductID(reportInsertUpdateReq.getTenantIDProdIDs())){
					for(TenantIDProdID tenantIDProdID:reportInsertUpdateReq.getTenantIDProdIDs() ){
						/*derive columns for the designer query*/
						//reportInsertUpdateReq.setColumns(new Columns());
						Report report=reportValidatorUtility.createReportForTenProdID(reportInsertUpdateReq, tenantIDProdID.getTenantID(), tenantIDProdID.getProductId(), tenantIDProdID.getCreatedUser());
						report.setColumns(getDesignerQueryColumns(report));
						System.out.println("Starting operation edit"
								+ " for report " + reportInsertUpdateReq.getReportName()+" for tenant "+tenantIDProdID.getTenantID()+" and product "+tenantIDProdID.getProductId());
						output = utilService.updateExistingReport(report);
							if (output != null && output.equals("FAILURE")) {
								output = "Invalid Report Details";
							} else if (output != null && output.equals("SUCCESS")) {
								valid.setValid(true);
								output = "Designer Report is Updated Successfully";
							}
						}
				}
				else{
					System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
					logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
					output = "Issue with Tenant , Product mapping";
				}
			} else {
				output = "Report Query is Invalid/parameter list is incorrect";
			}
		} catch (Exception e) {
			logger.error("Exception occcured while saving the designer Query"+e.getMessage());
			String errorMsg = e.getLocalizedMessage();
			if(errorMsg != null && errorMsg.contains("\n")){
				errorMsg = errorMsg.substring(errorMsg.indexOf("\n"));
			}
			output = errorMsg;
		}
		valid.setValidityMessage(output);
		return valid;
	}

	@Override
	public Valid inactivateDesignerReport(DeleteReportReq deleteReportReq) {
		String output = null;
		Valid valid = new Valid();
		valid.setValid(false);
		try{
			if(utilService.verifyValidityOfTenantAndProductID(deleteReportReq.getTenantIDProdIDs())){
				for(TenantIDProdID tenantIDProdID:deleteReportReq.getTenantIDProdIDs() ){
					DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd = new DeleteInactivateReqPerTenProd();
					deleteInactivateReqPerTenProd.setProductId(tenantIDProdID.getProductId());
					deleteInactivateReqPerTenProd.setReportCategoryName(deleteReportReq.getReportCategoryName());
					deleteInactivateReqPerTenProd.setReportId(deleteReportReq.getReportId());
					deleteInactivateReqPerTenProd.setReportName(deleteReportReq.getReportName());
					deleteInactivateReqPerTenProd.setReportSuperCategoryList(deleteReportReq.getReportSuperCategoryList());
					deleteInactivateReqPerTenProd.setTenantID(tenantIDProdID.getTenantID());
					deleteInactivateReqPerTenProd.setModifiedUser(deleteReportReq.getModifiedUser());
					
					System.out.println("Starting operation inactivate"
							+ " for report " + deleteReportReq.getReportName()+" for product "+tenantIDProdID.getTenantID()+" and tenant "+tenantIDProdID.getProductId());
					output = utilService.inactivateReport(deleteInactivateReqPerTenProd);
					if (output != null && output.equals("SUCCESS")) {
						valid.setValid(true);
						output = "Report is Deleted Successfully";
					}
				}
			}
			else{
				System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
				logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
				output = "TENANT ID, PRODUCT ID mapping is not valid";
			}
		} catch (Exception e) {
			logger.error("Exception occcured while inactivating the the designer report"+e.getMessage());
			String errorMsg = e.getLocalizedMessage();
			if(errorMsg != null && errorMsg.contains("\n")){
				errorMsg = errorMsg.substring(errorMsg.indexOf("\n"));
			}
			output = errorMsg;
		}
		valid.setValidityMessage(output);
		return valid;
	}
 


}

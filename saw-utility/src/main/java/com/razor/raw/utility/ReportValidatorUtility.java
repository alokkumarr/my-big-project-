package com.razor.raw.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.pojo.Column;
import com.razor.raw.core.pojo.Parameter;
import com.razor.raw.core.pojo.Parameters;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.utility.beans.DeleteInactivateReqPerTenProd;
import com.razor.raw.utility.beans.DeleteReportReq;
import com.razor.raw.utility.beans.ReportInsertUpdateReq;
import com.razor.raw.utility.common.CommonValidatorUtility;


public class ReportValidatorUtility {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportValidatorUtility.class);
	
	
	public DeleteReportReq isValidDeleteRequest(String jsonFile){

		logger.info(this.getClass().getName() + " - isValidReport - START");
		DeleteReportReq deleteReportReq = null;
		Gson gson = new Gson();
		try {
			BufferedReader br = new BufferedReader(new FileReader(jsonFile));
			deleteReportReq = gson.fromJson(br, DeleteReportReq.class);
			
			List<String> chkMandotaryField=isValidDeleteReq(deleteReportReq);
			if(chkMandotaryField.size() > 0){
				for(String msg : chkMandotaryField){
					logger.error(this.getClass().getName() + "- isValidReport- error in mandatory field  : " +msg);
				}
				logger.error(this.getClass().getName() + "- isValidReport  -- END");
				return null;
			}
			
			
		} catch (Exception e) {
			logger.error(this.getClass().getName() + "- isValidReport  -- Query : "+ e);
			deleteReportReq = null;
		}
		return deleteReportReq;
	
	}
	
	
	public ReportInsertUpdateReq isValidReport(String jsonFile){
		logger.info(this.getClass().getName() + " - isValidReport - START");
		ReportInsertUpdateReq report = null;
		boolean validParam = false;
		boolean validQuery = false;
		Gson gson = new Gson();
		try {
			BufferedReader br = new BufferedReader(new FileReader(jsonFile));
			report = gson.fromJson(br, ReportInsertUpdateReq.class);
			Parameters parameters = report.getParameters();
			List<String> chkMandotaryField = isValidMandatoryField(report);
			
			if(chkMandotaryField.size() == 0){
				if(report != null && report.getParameters() != null){
					validParam = isValidParameters(parameters);
					if(validParam == false) {
						logger.debug(this.getClass().getName() + "- isValidReport- provided parameter is not correct. ");
						report = null;
					}
						
				}
				if(report.getReportQuery() != null){
					int size=0;
					if(null != parameters && null != parameters.getParameter()){
						size=parameters.getParameter().size(); 
					}
					
					validQuery = isValidQuery(report.getReportQuery(),size);
					if(validQuery == false){
						logger.debug(this.getClass().getName() + "- isValidReport - report query param is not correct. ");
						report = null;
					}
				}
			}else{
				for(String msg : chkMandotaryField){
					logger.error(this.getClass().getName() + "- isValidReport- error in mandatory field  : " +msg);
					return null;
				}
			}
			logger.info(this.getClass().getName() + "- isValidReport  -- END");
		} catch (Exception e) {
			logger.error(this.getClass().getName() + "- isValidReport  -- Query : "+ e);
			report = null;
		}
		return report;
	}

	public ReportInsertUpdateReq isValidReport(ReportInsertUpdateReq reportInsertUpdateReq){
		logger.info(this.getClass().getName() + " - isValidReport - START");
		boolean validParam = false;
		boolean validQuery = false;
		try {
			Parameters parameters = reportInsertUpdateReq.getParameters();
			List<String> chkMandotaryField = isValidMandatoryField(reportInsertUpdateReq);
			
			if(chkMandotaryField.size() == 0){
				if(reportInsertUpdateReq != null && reportInsertUpdateReq.getParameters() != null){
					validParam = isValidParameters(parameters);
					if(validParam == false) {
						logger.debug(this.getClass().getName() + "- isValidReport- provided parameter is not correct. ");
						reportInsertUpdateReq = null;
					}
						
				}
				if(reportInsertUpdateReq.getReportQuery() != null){
					int size=0;
					if(null != parameters && null != parameters.getParameter()){
						size=parameters.getParameter().size(); 
					}
					
					validQuery = isValidQuery(reportInsertUpdateReq.getReportQuery(),size);
					if(validQuery == false){
						logger.debug(this.getClass().getName() + "- isValidReport - report query param is not correct. ");
						reportInsertUpdateReq = null;
					}
				}
			}else{
				for(String msg : chkMandotaryField){
					logger.error(this.getClass().getName() + "- isValidReport- error in mandatory field  : " +msg);
					return null;
				}
			}
			logger.info(this.getClass().getName() + "- isValidReport  -- END");
		} catch (Exception e) {
			logger.error(this.getClass().getName() + "- isValidReport  -- Query : "+ e);
			reportInsertUpdateReq = null;
		}
		return reportInsertUpdateReq;
	}

	
	public boolean isValidParameters(Parameters parameters){
		logger.info(this.getClass().getName() + " - isValidParameters - START");
		List<Parameter> paramList = parameters.getParameter();
		boolean successParam = false;
		for(Parameter param : paramList){
			boolean isLookups = false;
			if(CommonConstants.SQL_INTEGER_TYPE.equals(param.getType()))
				if(param.getLookupValue() != null && !StringUtils.isEmpty(param.getLookupValue())){
					isLookups = isValidLookup(param.getLookupValue(), param.getType());
					if(!isLookups)
						break;
				}
			if(param.getDefaultValue() != null &&  !StringUtils.isEmpty(param.getDefaultValue())){
				switch (param.getType()) {
				case CommonConstants.SQL_DATE_TYPE:
					successParam = CommonValidatorUtility.isValidDrilDate(param.getDefaultValue());
					break;
				case CommonConstants.SQL_NUMBER_TYPE:
					successParam = CommonValidatorUtility.isValidNumber(param.getDefaultValue());
					break;
				case CommonConstants.SQL_INTEGER_TYPE:
					successParam = CommonValidatorUtility.isValidInteger(param.getDefaultValue());
					break;
				case CommonConstants.SQL_DATE_RANGE_TYPE:
					successParam = CommonValidatorUtility.isValidDateRange(param.getDefaultValue());
					break;
				case CommonConstants.SQL_NUMBER_RANGE_TYPE:
					successParam = CommonValidatorUtility.isValidNumberRange(param.getDefaultValue());
					break;
				case CommonConstants.SQL_INTEGER_RANGE_TYPE:
					successParam = CommonValidatorUtility.isValidIntegerRange(param.getDefaultValue());
					break;
				case CommonConstants.SQL_STRING_TYPE:
					successParam = true;
					break;
				case CommonConstants.SQL_SIMPLE_DATE_TYPE:
					successParam = true;
					break;
				}
				if(successParam == false){
					System.out.println("Error in parameter "+param.getType()+" is not valid value "+param.getValue());
					logger.info(this.getClass().getName() + " - isValidParameters - Not valid "+param.getType());
					break;
				}
			}
			else{
				successParam=true;
			}
		}
		logger.info(this.getClass().getName() + " - isValidParameters - END");
		return successParam;
	}
	
	public List<String> isValidMandatoryField(ReportInsertUpdateReq report){
		logger.info(this.getClass().getName() + " - isValidMandatoryFile - START");
		List<String> errorMsg = new ArrayList<String>();
		if(report.getReportCategoryName() == null || StringUtils.isEmpty(report.getReportCategoryName().trim()))
			errorMsg.add("Report Catatory should not be null");
		if(report.getTenantIDProdIDs() == null || report.getTenantIDProdIDs().size()==0)
			errorMsg.add("Tanent Id , PROD code mapping should not be null");
		if(report.getReportName() == null || StringUtils.isEmpty(report.getReportName().trim()))
			errorMsg.add("Report name should not be null");
		if(report.getReportQuery() == null || StringUtils.isEmpty(report.getReportQuery().trim()))
			errorMsg.add("Report query should not be null");
		if(report.getParameters() != null){
			for(Parameter parameter : report.getParameters().getParameter()){
				if(parameter.getName() == null || StringUtils.isEmpty(parameter.getName().trim()))
					errorMsg.add("Report parameter name should not be null");
				if(parameter.getType() == null || StringUtils.isEmpty(parameter.getType().trim()))
					errorMsg.add("Report parameter type should not be null");
				if(parameter.getIndex() == 0)
					errorMsg.add("Report parameter index should be provided and should be greater than 0");
			}
		}
		if(report.getColumns() != null){
			for(Column column : report.getColumns().getColumn()){
				if(column.getName() == null || StringUtils.isEmpty(column.getName().trim())){
					errorMsg.add("Report column name should not be null");
				}
				if(column.getType() == null || StringUtils.isEmpty(column.getType().trim())){
					errorMsg.add("Report column type should not be null");
				}
				
			}
		}
		logger.info(this.getClass().getName() + " - isValidMandatoryFile - END");
		return errorMsg;
	}
	public boolean isValidQuery(String query, int paramSize){
		int noOfQuesMark = StringUtils.countOccurrencesOf(query, "?");
		if(noOfQuesMark == paramSize)
			return true;
		return false;
	}
	public boolean isValidLookup(String lookupValue,  String lookType){
		logger.info(this.getClass().getName() + " - isValidLookup - START");
		String[] lookupVal = lookupValue.split("\\|\\|\\|");
		boolean valid = false;
		if(lookType.equals(CommonConstants.SQL_INTEGER_TYPE) ){
			for(String lookup : lookupVal){
				valid = CommonValidatorUtility.isValidNumber(lookup);
				if(!valid)
					break;
			}
		}
		logger.info(this.getClass().getName() + " - isValidLookup - END");
		return valid;
	}
	
	public List<String> isValidDeleteReq(DeleteReportReq deleteReportReq){
		logger.info(this.getClass().getName() + " - isValidMandatoryFile - START");
		List<String> errorMsg = new ArrayList<String>();
		if(deleteReportReq.getReportCategoryName() == null)
			errorMsg.add("Report Catatory should not be null");
		if(deleteReportReq.getTenantIDProdIDs() == null)
			errorMsg.add("Tenant Id, PROD code should not be null");
		
		if(deleteReportReq.getReportName() == null)
			errorMsg.add("Report name should not be null");
		
		return errorMsg;
		
	}
	
	public Report createReportForTenProdID(ReportInsertUpdateReq reportUpReq,String tenantID, String prodCode, String createdUser){
		
		Report report= new Report();
		
		report.setColumns(reportUpReq.getColumns());
		report.setCreatedUser(createdUser);
		report.setDesignerQuery(reportUpReq.isDesignerQuery());
		report.setCreatedDate(reportUpReq.getCreatedDate());
		report.setDisplayStatus(reportUpReq.getDisplayStatus());
		report.setModifiedDate(reportUpReq.getModifiedDate());
		report.setModifiedUser(reportUpReq.getModifiedUser());
		report.setParametarised(reportUpReq.isParametarised());
		report.setScheduled(reportUpReq.isScheduled());
		report.setParameters(reportUpReq.getParameters());
		report.setProductId(prodCode);
		report.setTenantId(tenantID);
		report.setProductViewsId(reportUpReq.getProductViewsId());
		report.setReportCategoryId(reportUpReq.getReportCategoryId());
		report.setReportCategoryName(reportUpReq.getReportCategoryName());
		report.setReportDescription(reportUpReq.getReportDescription());
		report.setReportSuperCategoryList(reportUpReq.getReportSuperCategoryList());
		report.setReportId(reportUpReq.getReportId());
		report.setReportQuery(reportUpReq.getReportQuery());
		report.setReportQueryType(reportUpReq.getReportQueryType());
		report.setReportName(reportUpReq.getReportName());
		
		
		return report;
		
	}
	
	
	
public DeleteInactivateReqPerTenProd createReportForTenProdIDDel(DeleteReportReq deleteReportReq,String tenantID, String prodCode){
		
		DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd = new DeleteInactivateReqPerTenProd();
		
		deleteInactivateReqPerTenProd.setProductId(prodCode);
		deleteInactivateReqPerTenProd.setReportCategoryName(deleteReportReq.getReportCategoryName());
		deleteInactivateReqPerTenProd.setReportName(deleteReportReq.getReportName());
		deleteInactivateReqPerTenProd.setReportSuperCategoryList(deleteReportReq.getReportSuperCategoryList());
		deleteInactivateReqPerTenProd.setTenantID(tenantID);
		deleteInactivateReqPerTenProd.setModifiedUser(deleteReportReq.getModifiedUser());
		
		return deleteInactivateReqPerTenProd;
		
	}
	
}

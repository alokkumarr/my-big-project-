package com.razor.raw.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.razor.raw.core.dao.repository.ReportCategoryRepository;
import com.razor.raw.core.dao.repository.ReportRepository;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.core.pojo.ReportCategory;
import com.razor.raw.utility.beans.DeleteInactivateReqPerTenProd;
import com.razor.raw.utility.beans.TenantIDProdID;
import com.razor.raw.utility.common.RawUtilConstants;
import com.razor.scheduler.common.QuartzScheduleJobDetails;
import com.razor.scheduler.jobs.report.ReportDefinationJob;
import com.razor.scheduler.jobs.report.ReportDefinition;
import com.razor.scheduler.service.ScheduleService;



@Component
public class JsonUtilService {
	private static final Logger logger = LoggerFactory
			.getLogger(JsonUtilService.class);
	
	@Autowired
	ReportRepository reportRepository;
	
	@Autowired
	ReportCategoryRepository reportCategoryRepository;
	
	@Autowired
	ScheduleService scheduleService;
	
	
	public boolean verifyValidityOfTenantAndProductID(ArrayList<TenantIDProdID> tenantIDProdIDs){
		if(tenantIDProdIDs !=null){
			
			for(TenantIDProdID tenantIDProdID:tenantIDProdIDs ){
				boolean isValidTentProd=false;
				isValidTentProd=reportRepository.validateTenantIDProdIDComb(tenantIDProdID.getTenantID(),tenantIDProdID.getProductId());
				if(!isValidTentProd){
					return false;
				}
			}
		}
		else{
			logger.error(this.getClass().getName() + " - Tenant ID and Product ID combination is NULL , please check if entries are present in RAW_DATA_SOURCE ");
			System.out.println("ERROR - Tenant ID and Product ID combination is NULL , please check if entries are present in RAW_DATA_SOURCE  ");
			return false;
		}
		
		
		return true;
	}
	
	
	
	@Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	public String insertReport(Report report){
		
		logger.info(this.getClass().getName() + " INSERT-----------------------**************Report Name "+ report.getReportName()+" ****************---------------------Started");
		
		Long currentRootCategoryID = checkForReportCategoryHierarchyExists(report.getReportCategoryName(),report.getReportSuperCategoryList(),report.getTenantId(),report.getProductId());
		
		List<Long> categoryList=new ArrayList<Long>();
		if(currentRootCategoryID != null){
			categoryList.add(currentRootCategoryID);
		}
		else{
			Long categoryID=insertMissingHierarchyStructure(report);
			if(categoryID != null)
				categoryList.add(categoryID);
		}
		
		int paramRelult;
		int colResult;
		
		if(categoryList.size() != 0 && categoryList.size() == 1){
			report.setReportCategoryId(categoryList.get(0));
			
			long reportId = 0;
			if(report.getReportCategoryName() != null && report.getReportCategoryName().equals(RawUtilConstants.MY_REPORTS)){
				reportId = reportRepository.getMyReportIdByName(report.getReportName(), report.getReportCategoryId(), report.getCreatedUser());
			}else {
				reportId = reportRepository.getReportIdByName(report.getReportName(), report.getReportCategoryId());
			}
			if(reportId == 0){
				reportRepository.saveReport(report);
				reportId = reportRepository.getReportIdByName(report.getReportName(), report.getReportCategoryId());
				if (report.getParameters() != null) {
					paramRelult = reportRepository.saveParameters(report.getParameters(), reportId);
					logger.info(this.getClass().getName() + "-- total no of parameter inserted for report -"+report.getReportName()+" is "+paramRelult);
				}
				if (report.getColumns() != null) {
					colResult = reportRepository.saveColumns(report.getColumns(), reportId);
					logger.info(this.getClass().getName() + "-- total no of column inserted for report -"+report.getReportName()+" is "+colResult);
				}
				logger.debug(this.getClass().getName() + "- " +report.getReportName() +" inserted successfully.");
				logger.info(this.getClass().getName() + " - insertReport - END");
			}
			else{
				logger.error(this.getClass().getName() + " - Given Report name already exists in given category Hierarchy ");
				System.out.println("ERROR: Given Report name already exists in given category Hierarchy");
				return "FAILURE";
			}
			
			
		}else if(categoryList.size() > 1){
			logger.error(this.getClass().getName() + " - insertReport - Exception");
			logger.error(this.getClass().getName() + " - Report insertion can not perform because report category found more than 1 in DB.");
			return "Report insertion can not perform because report category found more than 1 in DB";
		} else if(categoryList.size() == 0){
			logger.error(this.getClass().getName() + " - insertReport - Exception categoryList is 0 ");
			return "Report insertion can not perform because report category not found";
		}
		logger.info(this.getClass().getName() + " INSERT-----------------------**************Report Name "+ report.getReportName()+" ****************---------------------End");
		return "SUCCESS";
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	public String deleteReport(DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd){
		logger.info(this.getClass().getName() + " DELETE-----------------------**************Report Name "+ deleteInactivateReqPerTenProd.getReportName()+" ****************---------------------Started");
		
		Long categoryID= checkForReportCategoryHierarchyExists(deleteInactivateReqPerTenProd.getReportCategoryName(),deleteInactivateReqPerTenProd.getReportSuperCategoryList(),deleteInactivateReqPerTenProd.getTenantID(),deleteInactivateReqPerTenProd.getProductId());
		
		if(categoryID !=null){
			Long reportId = reportRepository.getReportIdByName(deleteInactivateReqPerTenProd.getReportName(), categoryID);
			if(reportId != null && reportId >0){
				
				reportRepository.deleteReportColumns(reportId);
				reportRepository.deleteReportParameters(reportId);
				reportRepository.deleteReportPublishedValues(reportId);
				reportRepository.deleteReport(reportId);
				scheduleService.deleteJob(reportId+"", deleteInactivateReqPerTenProd.getTenantID());
				
			}
			else{
				logger.error("Provided report doesn't exists");
				return "FAILURE";
			}
		}
		
		else{
			logger.error("Provided report Category TREE doesn't exists");
			return "FAILURE";
		}
		
		logger.info(this.getClass().getName() + " DELETE-----------------------**************Report Name "+ deleteInactivateReqPerTenProd.getReportName()+" ****************---------------------END");
		return "SUCCESS";
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	public String activateReport(DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd){
		
		logger.info(this.getClass().getName() + " Activate-----------------------**************Report Name "+ deleteInactivateReqPerTenProd.getReportName()+" ****************---------------------Started");
		
		Long categoryID= checkForReportCategoryHierarchyExists(deleteInactivateReqPerTenProd.getReportCategoryName(),deleteInactivateReqPerTenProd.getReportSuperCategoryList(),deleteInactivateReqPerTenProd.getTenantID(),deleteInactivateReqPerTenProd.getProductId());
		
		if(categoryID !=null){
			Long reportId = reportRepository.getReportIdByName(deleteInactivateReqPerTenProd.getReportName(), categoryID);
			if(reportId != null && reportId >0){
				reportRepository.activateReport(reportId);
			}
			else{
				logger.error("Provided report doesn't exists");
				return "FAILURE";
			}
		}
		
		else{
			logger.error("Provided report Category TREE doesn't exists");
			return "FAILURE";
		}
		
		logger.info(this.getClass().getName() + " Activate-----------------------**************Report Name "+ deleteInactivateReqPerTenProd.getReportName()+" ****************---------------------END");
		return "SUCCESS";
		
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	public String inactivateReport(DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd){
		
		logger.info(this.getClass().getName() + " Inactivate Report Name "+ deleteInactivateReqPerTenProd.getReportName()+" ---Started");
		
		reportRepository.inActivateReport(deleteInactivateReqPerTenProd.getReportId(), deleteInactivateReqPerTenProd.getModifiedUser());
		scheduleService.deleteJob(deleteInactivateReqPerTenProd.getReportId()+"", deleteInactivateReqPerTenProd.getTenantID());
		
		/*Long categoryID= checkForReportCategoryHierarchyExists(deleteInactivateReqPerTenProd.getReportCategoryName(),deleteInactivateReqPerTenProd.getReportSuperCategoryList(),deleteInactivateReqPerTenProd.getTenantID(),deleteInactivateReqPerTenProd.getProductId());
		
		if(categoryID !=null){
			Long reportId = reportRepository.getReportIdByName(deleteInactivateReqPerTenProd.getReportName(), categoryID);
			if(reportId != null && reportId >0){
				
				reportRepository.inActivateReport(deleteInactivateReqPerTenProd.getReportId(), deleteInactivateReqPerTenProd.getModifiedUser());
				scheduleService.deleteJob(reportId+"", deleteInactivateReqPerTenProd.getTenantID());
				
			}else{
				logger.error("Provided report doesn't exists:"+deleteInactivateReqPerTenProd.getReportName());
				return "Provided report doesn't exists:"+deleteInactivateReqPerTenProd.getReportName();
			}
		}else{
			logger.error("Provided report Category TREE doesn't exists:"+deleteInactivateReqPerTenProd.getReportCategoryName());
			return "Provided report Category TREE doesn't exists:"+deleteInactivateReqPerTenProd.getReportCategoryName();
		}*/
		
		logger.info(this.getClass().getName() + " Inactivate Report Name: "+ deleteInactivateReqPerTenProd.getReportName()+" ---END");
		return "SUCCESS";
		
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW,isolation=Isolation.SERIALIZABLE)
	public String updateExistingReport(Report report){
		logger.info(this.getClass().getName() + " UPDATE Report Name "+ report.getReportName()+" ---Started");
		Long currentRootCategoryID = checkForReportCategoryHierarchyExists(report.getReportCategoryName(),report.getReportSuperCategoryList(),report.getTenantId(),report.getProductId());
		
		List<Long> categoryList=new ArrayList<Long>();
		if(currentRootCategoryID != null){
			categoryList.add(currentRootCategoryID);
		}
		else{
			logger.error(this.getClass().getName() + " - Report update cannot be performed as Report Category TREE doesn't exist.");
		}
		
		int paramRelult;
		int colResult;
		
		if(categoryList.size() != 0 && categoryList.size() == 1){
			report.setReportCategoryId(currentRootCategoryID);
			
			Long reportId = null;
			reportId = reportRepository.getReportIdByName(report.getReportName(), report.getReportCategoryId());
			
			if(reportId == null || reportId == 0){
				logger.error(this.getClass().getName() + " - updateExistingReport - Exception");
				return "FAILURE";
			}
			else{
				report.setReportId(reportId);
				reportRepository.updateReportQueryByID(report.getReportQuery(),report.getReportId(), report.getModifiedUser());
				reportRepository.deleteReportColumns(report.getReportId());
				reportRepository.deleteReportParameters(report.getReportId());


				if (report.getParameters() != null) {
					paramRelult = reportRepository.saveParameters(report.getParameters(), report.getReportId());
					logger.info(this.getClass().getName() + "-- total no of parameter inserted for report -"+report.getReportName()+" is "+paramRelult);
				}
				if (report.getColumns() != null) {
					colResult = reportRepository.saveColumns(report.getColumns(), report.getReportId());
					logger.info(this.getClass().getName() + "-- total no of column inserted for report -"+report.getReportName()+" is "+colResult);
				}
				logger.debug(this.getClass().getName() + "- " +report.getReportName() +" inserted successfully.");
				logger.info(this.getClass().getName() + " - insertReport - END");
				
				ReportDefinition reportDefinition = scheduleService.getReportDefinition(report.getReportId()+"", report.getProductId() +"-"+report.getTenantId());
				if(reportDefinition != null){
					logger.info(this.getClass().getName() + " - update schedular instance - Start");
					reportDefinition.setReport(report);
					QuartzScheduleJobDetails<ReportDefinition> quartzJobDetails = new QuartzScheduleJobDetails<ReportDefinition>(reportDefinition);
					quartzJobDetails.setJobclassName(ReportDefinationJob.class);
					quartzJobDetails.setJobGroup(report.getProductId() +"-"+report.getTenantId());
					quartzJobDetails.setJobName(report.getReportId()+"");
					quartzJobDetails.setQuartzCronExpression(reportDefinition.getCronExpressionGen());
					if (!reportDefinition.isRecursiveSchedule()) {
						long time = Long.parseLong(reportDefinition.getCronExpressionGen());
						quartzJobDetails.setSimpleTriggerDate(new Date(time));
						scheduleService.setSimpleTrigger(quartzJobDetails);
					} else {
						scheduleService.setCronTrigger(quartzJobDetails);
					}
					logger.info(this.getClass().getName() + " - update schedular instance - End");
				}
				
				
						}
		}else if(categoryList.size() > 1){
			logger.error(this.getClass().getName() + " - insertReport - Exception");
			logger.error(this.getClass().getName() + " - Report insertion can not perform because report category found more than 1 in DB.");
			return "Report update can not perform because report category found more than 1 in DB";
		}
		logger.info(this.getClass().getName() + " UPDATE Report Name "+ report.getReportName()+" ---END");
		return "SUCCESS";
	}
	
	@Transactional(propagation=Propagation.MANDATORY,isolation=Isolation.SERIALIZABLE)
	private Long insertMissingHierarchyStructure(Report report){
		boolean firstParent=true;
		Long currentRootCategoryID = null;
		boolean isRestricted=false;
		Long superCategoryId=null;
		
		if(null != report.getReportSuperCategoryList() && report.getReportSuperCategoryList().size() > (RawUtilConstants.MAX_CATEGORY_LIST_DEPTH-1)){
			logger.error(this.getClass().getName() + " Category depth exists maximum Limit of "+RawUtilConstants.MAX_CATEGORY_LIST_DEPTH);
			return null;
		}
		
		
		if(null != report.getReportSuperCategoryList() && report.getReportSuperCategoryList().size() > 0){
			
			for(String categoryName:report.getReportSuperCategoryList()){
				if(firstParent){
					
					firstParent=false;
					if(categoryName.equalsIgnoreCase(RawUtilConstants.MY_REPORTS)){
						isRestricted=true;
					}
					currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategory(categoryName,null,report.getTenantId(),report.getProductId());
					if(currentRootCategoryID == null){
						currentRootCategoryID = insertReportCategory(report,categoryName,null,isRestricted);
						//currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategory(categoryName,null,report.getTenantId(),report.getProductId());
					}
				}
				else if(currentRootCategoryID != null){
					superCategoryId = currentRootCategoryID;
					currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategoryID(categoryName,superCategoryId,report.getTenantId(),report.getProductId());
					if(currentRootCategoryID == null){
						currentRootCategoryID = insertReportCategory(report,categoryName,superCategoryId,isRestricted);
						//currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategoryID(categoryName,superCategoryId,report.getTenantId(),report.getProductId());
					}
				}
				
			}
			
			superCategoryId = currentRootCategoryID;
			currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategoryID(report.getReportCategoryName(),superCategoryId,report.getTenantId(),report.getProductId());
			if(currentRootCategoryID == null){
				currentRootCategoryID = insertReportCategory(report,report.getReportCategoryName(),superCategoryId,isRestricted);
				//currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategoryID(report.getReportCategoryName(),superCategoryId,report.getTenantId(),report.getProductId());
			}
			
		}
		else{
			currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategory(report.getReportCategoryName(),null,report.getTenantId(),report.getProductId());
			if(currentRootCategoryID == null){
				currentRootCategoryID = insertReportCategory(report,report.getReportCategoryName(),superCategoryId,isRestricted);
				//currentRootCategoryID=reportCategoryRepository.categoryIdByNameAndSuperCategoryID(report.getReportCategoryName(),superCategoryId,report.getTenantId(),report.getProductId());
			}
		}
				
		return currentRootCategoryID;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED,isolation=Isolation.SERIALIZABLE)
	public Long insertReportCategory(Report report,String reportName,Long superCategoryID, boolean isRestricted){
		logger.info(this.getClass().getName() + " - insertReportCategory - START");
		Long id = null;
		ReportCategory category = new ReportCategory();
		category.setCreatedUser("SYSTEM");
		category.setDisplayStatus(true);
		category.setModifiedUser("SYSTEM");
		category.setProductId(report.getProductId());
		String reportCategoryDes = reportName + " Description";
		category.setReportCategoryDescription(reportCategoryDes);
		category.setReportCategoryName(reportName);
		if(superCategoryID != null){
			category.setReportSuperCategoryId(superCategoryID);
		}
		else{
			category.setReportSuperCategoryId(0);
		}
		category.setRestricted(isRestricted);
		category.setTenantId(report.getTenantId());
		id = reportCategoryRepository.insertReportCategory(category);
		logger.info(this.getClass().getName() + " - insertReportCategory - END - id "+id);
		return id;
	}
	
	@Transactional(propagation=Propagation.MANDATORY,isolation=Isolation.SERIALIZABLE)
	private Long checkForReportCategoryHierarchyExists(
			String reportCategoryName,
			ArrayList<String> reportSuperCategoryList, String tenantID,
			String productID) {
		/*
		 * Check whether reportCategoryname exist , if YES check whether full
		 * super category list exists. If report Category doesn't exist Support
		 * only upto 4 levels
		 */

		boolean firstParent = true;
		Long currentRootCategoryID = null;

		if (null != reportSuperCategoryList
				&& reportSuperCategoryList.size() > 0) {

			for (String categoryName : reportSuperCategoryList) {
				if (firstParent) {
					currentRootCategoryID = reportCategoryRepository
							.categoryIdByNameAndSuperCategory(categoryName,
									null, tenantID, productID);
					firstParent = false;
				} else if (currentRootCategoryID != null) {
					currentRootCategoryID = reportCategoryRepository
							.categoryIdByNameAndSuperCategoryID(categoryName,
									currentRootCategoryID, tenantID, productID);
				} else {
					return null;
				}
			}

			currentRootCategoryID = reportCategoryRepository
					.categoryIdByNameAndSuperCategoryID(reportCategoryName,
							currentRootCategoryID, tenantID, productID);

		} else {
			currentRootCategoryID = reportCategoryRepository
					.categoryIdByNameAndSuperCategory(reportCategoryName, null,
							tenantID, productID);
		}

		return currentRootCategoryID;

	}
	
	
	
}

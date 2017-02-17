package com.razor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.razor.raw.core.pojo.Report;
import com.razor.raw.utility.JsonUtilService;
import com.razor.raw.utility.ReportValidatorUtility;
import com.razor.raw.utility.beans.DeleteInactivateReqPerTenProd;
import com.razor.raw.utility.beans.DeleteReportReq;
import com.razor.raw.utility.beans.ReportInsertUpdateReq;
import com.razor.raw.utility.beans.TenantIDProdID;
import com.razor.raw.utility.common.RawUtilConstants;
import com.razor.raw.utility.properties.RUProperties;

@SpringBootApplication
@SpringApplicationConfiguration(classes = {RUProperties.class})
public class RawUtilityApplication {
	private static final Logger logger = LoggerFactory
			.getLogger(RawUtilityApplication.class);

	public static void main(String[] args) {
		logger.debug("Saw utility - SawUtilityApplication - STARTED");
		if (args.length == 2) {
			for (String s : args) {
				System.out.println(s);
			}

			ConfigurableApplicationContext context = SpringApplication.run(
					RawUtilityApplication.class, args);
			JsonUtilService service = context.getBean(JsonUtilService.class);
			ReportValidatorUtility jsonUtil = new ReportValidatorUtility();

			try {

				System.out.println("Starting execution ");

				String crudType = args[1];
				String output = null;
				if (crudType != null) {
					if (crudType.equalsIgnoreCase(RawUtilConstants.UPDATE_REQ)) {
						ReportInsertUpdateReq reportUpReq = jsonUtil.isValidReport(args[0]);

						if (reportUpReq != null) {
							if(service.verifyValidityOfTenantAndProductID(reportUpReq.getTenantIDProdIDs())){
								
								for(TenantIDProdID tenantIDProdID:reportUpReq.getTenantIDProdIDs() ){
									Report report=jsonUtil.createReportForTenProdID(reportUpReq, tenantIDProdID.getTenantID(), tenantIDProdID.getProductId(),tenantIDProdID.getCreatedUser() );
									System.out.println("Starting operation " + crudType
											+ " for report " + reportUpReq.getReportName()+" for tenant "+tenantIDProdID.getTenantID()+" and product "+tenantIDProdID.getProductId());
									output = service.updateExistingReport(report);
									}
							}
							else{
								System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
								logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
								output = "FAILURE";
							}
							
						} else {
							output = "FAILURE";
						}

					}

					else if (crudType
							.equalsIgnoreCase(RawUtilConstants.INSERT_REQ)) {
						ReportInsertUpdateReq reportUpReq = jsonUtil.isValidReport(args[0]);
						if (reportUpReq != null) {
							if(service.verifyValidityOfTenantAndProductID(reportUpReq.getTenantIDProdIDs())){
								for(TenantIDProdID tenantIDProdID:reportUpReq.getTenantIDProdIDs() ){
									Report report=jsonUtil.createReportForTenProdID(reportUpReq, tenantIDProdID.getTenantID(), tenantIDProdID.getProductId(), tenantIDProdID.getCreatedUser());
									System.out.println("Starting operation " + crudType
											+ " for report " + reportUpReq.getReportName()+" for tenant "+tenantIDProdID.getTenantID()+" and product "+tenantIDProdID.getProductId());
									output = service.insertReport(report);
									}
							}
							else{
								System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
								logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
								output = "FAILURE";
							}
						} else {
							output = "FAILURE";
						}
					}

					else if (crudType
							.equalsIgnoreCase(RawUtilConstants.DELETE_REQ)) {
						DeleteReportReq deleteReportReq = jsonUtil
								.isValidDeleteRequest(args[0]);
						if (deleteReportReq != null) {
							if(service.verifyValidityOfTenantAndProductID(deleteReportReq.getTenantIDProdIDs())){
								for(TenantIDProdID tenantIDProdID:deleteReportReq.getTenantIDProdIDs() ){
									DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd=jsonUtil.createReportForTenProdIDDel(deleteReportReq, tenantIDProdID.getTenantID(), tenantIDProdID.getProductId());
									System.out.println("Starting operation " + crudType
											+ " for report " + deleteReportReq.getReportName()+" for tenant "+tenantIDProdID.getTenantID()+" and product "+tenantIDProdID.getProductId());
									output = service.deleteReport(deleteInactivateReqPerTenProd);
									}
							}
							else{
								System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
								logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
								output = "FAILURE";
							}
							
						} else {
							output = "FAILURE";
						}

					}

					else if (crudType
							.equalsIgnoreCase(RawUtilConstants.ACTIVATE_REPORT)) {
						DeleteReportReq deleteReportReq = jsonUtil
								.isValidDeleteRequest(args[0]);
						
						if (deleteReportReq != null) {
							if(service.verifyValidityOfTenantAndProductID(deleteReportReq.getTenantIDProdIDs())){
								for(TenantIDProdID tenantIDProdID:deleteReportReq.getTenantIDProdIDs() ){
									DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd=jsonUtil.createReportForTenProdIDDel(deleteReportReq, tenantIDProdID.getTenantID(), tenantIDProdID.getProductId());
									System.out.println("Starting operation " + crudType
											+ " for report " + deleteReportReq.getReportName()+" for tenant "+tenantIDProdID.getTenantID()+" and product "+tenantIDProdID.getProductId());
									output = service.activateReport(deleteInactivateReqPerTenProd);
									}
							}
							else{
								System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
								logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
								output = "FAILURE";
							}
							
						}
						
						
						
						else {
							output = "FAILURE";
						}
					}

					else if (crudType
							.equalsIgnoreCase(RawUtilConstants.INACTIVATE_REPORT)) {
						DeleteReportReq deleteReportReq = jsonUtil
								.isValidDeleteRequest(args[0]);
						
						if (deleteReportReq != null) {
							if(service.verifyValidityOfTenantAndProductID(deleteReportReq.getTenantIDProdIDs())){
								for(TenantIDProdID tenantIDProdID:deleteReportReq.getTenantIDProdIDs() ){
									DeleteInactivateReqPerTenProd deleteInactivateReqPerTenProd=jsonUtil.createReportForTenProdIDDel(deleteReportReq, tenantIDProdID.getTenantID(), tenantIDProdID.getProductId());
									System.out.println("Starting operation " + crudType
											+ " for report " + deleteReportReq.getReportName()+" for product "+tenantIDProdID.getTenantID()+" and tenant "+tenantIDProdID.getProductId());
									output = service.inactivateReport(deleteInactivateReqPerTenProd);
									}
							}
							else{
								System.out.println("ERROR: Issue with TENANT _ID , PRODUCT ID mapping");
								logger.error("Issue with TENANT _ID , PRODUCT ID mapping");
								output = "FAILURE";
							}
							
						}
						
						else {
							
							output = "FAILURE";
						}

					}

					else {
						System.out
						.println("ERROR:Please provide proper CRUD operation type");
						logger.error("Saw utility - Error: Please provide proper CRUD operation type ");
						
					}

				}

				if (output.equalsIgnoreCase("SUCCESS")) {
					logger.info("Saw utility - executed - successfully");
					System.out.println("Saw utility - executed - successfully");
				} else {
					logger.error("Saw utility - FAILED, please check logs");
					System.out
							.println("ERROR:Saw utility - FAILED, please check logs");
				}

			} catch (Exception e) {
				System.out
				.println("ERROR:Exception occured during execution, run FAILED, please check logs for more details "+e);
				logger.error("Saw utility - FAILED " + e);
				
			} finally {
				context.close();
			}

		} else {
			logger.error("Provide the file location.");
			System.out.println("ERROR:Provide the file location");
		}

	}

}

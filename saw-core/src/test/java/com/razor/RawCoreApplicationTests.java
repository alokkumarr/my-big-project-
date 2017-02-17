package com.razor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.razor.raw.core.common.exception.RAWDSException;
import com.razor.raw.core.dao.repository.CustomerFTPRepository;
import com.razor.raw.core.dao.repository.DataSourceRepository;
import com.razor.raw.core.dao.repository.PublishedReportRepository;
import com.razor.raw.core.dao.repository.ReportCategoryRepository;
import com.razor.raw.core.dao.repository.ReportExecutionLogRepository;
import com.razor.raw.core.dao.repository.ReportRepository;
import com.razor.raw.core.pojo.Columns;
import com.razor.raw.core.pojo.CustomerFTP;
import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.Parameters;
import com.razor.raw.core.pojo.PublishedReport;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.core.pojo.ReportCategory;
import com.razor.raw.core.pojo.ReportExecutionLog;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RawCoreApplication.class)
@WebAppConfiguration
public class RawCoreApplicationTests {

	@Autowired
	ReportRepository reportRepository;
	
	@Autowired
	DataSourceRepository datasourceRepository;
	
	@Autowired
	CustomerFTPRepository customerFtpRepository;
	
	@Autowired
	PublishedReportRepository  publishedReportRepository;
	
	@Autowired
	ReportExecutionLogRepository reportExeLogRepository;	
	
	@Autowired
	ReportCategoryRepository reportCategoryRepository;	
	
	@Test
	public void contextLoads() {
	}
	

	@Test
	public void getReportList()
	{
		List<Report> reportList = reportRepository.getReportList(1, "PGI", "sysadmin@t-mobilenew","UA_RRM"); 
		if(reportList != null && reportList.size() > 0){
			assertTrue(reportList.get(0).getReportCategoryId() == 1);
			assertTrue(reportList.get(0).getTenantId().equalsIgnoreCase("PGI"));
		}
			
	}
	
	@Test
	public void getAllDataSources() throws RAWDSException
	{
		List<Datasource> listDatasource = datasourceRepository.getAllDataSources();
		if(listDatasource != null && listDatasource.size() > 0)
			assertTrue(listDatasource.get(0).getDataSourceId() != 0);
	}
	
	@Test
	public void getDataSourec() throws RAWDSException{
		Datasource datasource = datasourceRepository.getDataSourec("PGI", "DEV");
		if(datasource != null)
		{
		assertTrue(datasource.getProductID().equalsIgnoreCase("DEV"));
		assertTrue(datasource.getTenantID().equalsIgnoreCase("PGI"));
		}
	}
	
	@Test
	public void getCustomerSftpData() throws RAWDSException{
		List<CustomerFTP> listSftpDtos = customerFtpRepository.getCustomerSftpData("PGI", "DEV");
		if(listSftpDtos != null && listSftpDtos.size() > 0){
			assertTrue(listSftpDtos.get(0).getTenantId().equalsIgnoreCase("PGI"));
			assertTrue(listSftpDtos.get(0).getProductId().equalsIgnoreCase("DEV"));
			
		}
			
	}
	
	@Test
	public void getReportParameters() {
		Parameters parameters = reportRepository.getReportParameters(2);
		if(parameters != null){
			assertTrue(parameters.getParameter().get(0).getRawReportsId() == 2);
		}
	}
	
	@Test
	public void getReportColumns() {
		Columns colList = reportRepository.getReportSelectedColumns(1);
		if(colList != null){
			assertTrue(colList.getColumn().get(0).getRawReportsId() == 1);
		}
	}
	
	@Test
	public void deletePublishReport() {
		publishedReportRepository.deletePublishReport("TestReport");
	}
	
	@Test
	public void insertPublishedReports() {
		PublishedReport pubReport = new PublishedReport();
		pubReport.setCreatedUser("Ajay");
		pubReport.setTenantId("PGI");
		pubReport.setDisplayStatus(true);
		pubReport.setProductId("DEV");
		pubReport.setReportCategoryId(1);
		pubReport.setReportId(1);
		pubReport.setReportLocaton("Ban");
		pubReport.setReportName("TestReport");
		pubReport.setScheduled(true);
		
		publishedReportRepository.insertPublishedReports(pubReport);
	}
	
	
	@Test
	public void getReportListByCategoryId(){
		List<Report> reports = reportRepository.getReportList(1,"PGI","sysadmin@t-mobilenew","UA_RRM");
		if(reports != null && reports.size() >0){
			assertTrue(reports.get(0).getReportCategoryId() == 1);
		}
	}

	@Test
	public void insertReportExecutionLog(){
		ReportExecutionLog log = new ReportExecutionLog();
		log.setReportsId(1);
		log.setDescription("rep_desc");
		log.setCreatedUser("ajay");
		reportExeLogRepository.insertReportExecutionLog(log);
	}
	
	@Test
	public void getReportNameCategoryId() {
		String name = reportCategoryRepository.getReportNameCategoryId(1);
		if(name != null){
			assertNotNull(name);
		}
	}
	
	@Test
	public void getReportCategoryId() {
		ReportCategory reportCat = reportCategoryRepository.getReportCategoryId(1);
		if(reportCat != null){
			assertTrue(reportCat.getReportCategoryId() == 1);
		}
	}
	
	@Test
	public void getReportCategoryDetails() {
		List<ReportCategory> reportCatList = reportCategoryRepository.getReportCategoryDetails("PGI","DEV");
		if(reportCatList != null && reportCatList.size() > 0){
			assertTrue(reportCatList.get(0).getTenantId().equalsIgnoreCase("PGI"));
			assertTrue(reportCatList.get(0).getProductId().equalsIgnoreCase("DEV"));
			
		}
	}
	
	@Test
	public void insertReportCategory() {
		ReportCategory category = new ReportCategory();
		category.setCreatedUser("TestRportCategory");
		category.setDisplayStatus(true);
		category.setModifiedUser("ajay");
		category.setProductId("DEV");
		category.setReportCategoryDescription("PGI desc");
		category.setReportCategoryName("TestCategoryInsert");
		category.setReportSuperCategoryId(1);
		category.setRestricted(false);
		category.setTenantId("PGI");
		reportCategoryRepository.insertReportCategory(category);
	}
	

	@Test
	public void deleteReportCategoryByName() {
		reportCategoryRepository.deleteReportCategoryByName("TestCategoryInsert");
	}
	
	@Test
	public void getPublishedReports() {
		List<PublishedReport> pubReportList = publishedReportRepository.getPublishedReports("Ajay","PGI","DEV");
		if(pubReportList != null && pubReportList.size() > 0){
			assertTrue(pubReportList.get(0).getCreatedUser().equalsIgnoreCase("Ajay"));
			assertTrue(pubReportList.get(0).getTenantId().equalsIgnoreCase("PGI"));
			assertTrue(pubReportList.get(0).getProductId().equalsIgnoreCase("DEV"));
		}
	}
}

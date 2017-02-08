/**
 * 
 */
package com.razor.raw.re.report.generate;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.razor.distribution.UploadUtility;
import com.razor.distribution.email.MailSender;
import com.razor.distribution.email.MailUtility;
import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.dao.repository.CustomerFTPRepository;
import com.razor.raw.core.dao.repository.PublishedReportRepository;
import com.razor.raw.core.dao.repository.ReportCategoryRepository;
import com.razor.raw.core.dao.repository.ReportExecutionLogRepository;
import com.razor.raw.core.dao.repository.ReportRepository;
import com.razor.raw.core.pojo.CustomerFTP;
import com.razor.raw.core.pojo.PublishedReport;
import com.razor.raw.drill.services.DrillServices;
import com.razor.raw.drill.services.impl.DrillServicesImpl;
import com.razor.raw.re.properties.REProperties;
import com.razor.raw.re.report.interfaces.IFileExporter;
import com.razor.raw.re.report.process.ReportDefinationImpl;



/**
 * 
 * @author surendra.rajaneni
 *
 */
@Component
public class ReportFileGenerator {

	private static final Logger logger = LoggerFactory.getLogger(ReportFileGenerator.class);
	
	@Autowired
	ReportRepository reportRepository;
	@Autowired
	ReportCategoryRepository reportCategoryRepository;
	@Autowired
	CustomerFTPRepository customerFTPRepository;
	@Autowired
	PublishedReportRepository publishedReportRepository;
	@Autowired
	ReportExecutionLogRepository reportExecutionLogRepository;
	@Autowired
	REProperties reProperties;
	@Autowired
	MailSender mailSender;
	@Autowired
	MailUtility mailUtility;
	
	DrillServices drillServices = new DrillServicesImpl();
	

	public void fileGenerator(ReportDefinationImpl reportDefinationImpl) {
		logger.debug("User activity started here:"+ this.getClass().getName() + " excelFileGenerator method");
		String fileFormat = reportDefinationImpl.getGenerateFileInformation().getOutputFileFormat();
		String recordLimit = null;
		String msg = "Report <REPORT NAME> has executed however the output has exceeded the upper row limit for Excel and/or CSV output. Please consider revisiting your report query to optimize it and reduce the volume of rows in the output.";
		boolean copyToFTP = reportDefinationImpl.isCopyToFTP();
		
		if(CommonConstants.CSV_FILE_FORMAT.equalsIgnoreCase(fileFormat)){
			recordLimit = reProperties.getCsvRowFetchSize();
		}else{
			recordLimit = reProperties.getXlsRowFetchSize();
		}
		logger.debug("recordLimit : "+recordLimit);
		if (recordLimit == null || (recordLimit != null && recordLimit.trim().length() == 0)) {
			recordLimit = "65000";
		}
		List<StringBuffer> bufferList = new ArrayList<StringBuffer>();
		StringBuffer rowBuffer = null;
		ExportExcelBean exportExcelBean = new ExportExcelBean();
		exportExcelBean.setServerPathLocation(reProperties.getPublishedReportPath());
		String[] EXPORT_COLUMN_HEADER = {};
		String[] exportColumnHeaderDataType = {};
		String reportType = null;
		Connection connection = null;
		ResultSet resultSet =null;
		String value ="";
		long noOfRecords = 0;
		int rcdLimit = (recordLimit != null && recordLimit.trim().length() != 0)?Integer.parseInt(recordLimit) : 0;
		try {
			connection = drillServices.getConnection(reportDefinationImpl.getDatasource());
			// Removing the records count logic as (query running two times) SAW - . 
			//noOfRecords = drillServices.getTotalCount(reportDefinationImpl.getReport(), connection);
			//logger.debug("Total Count:"+noOfRecords);
		//	if(noOfRecords <= rcdLimit || rcdLimit == 0 || noOfRecords == 0)
			//	msg = "";
			resultSet = drillServices.executeQueryByReport(reportDefinationImpl.getReport(), rcdLimit, connection);
			reportExecutionLogRepository.insertReportExecutionLog(reportDefinationImpl.getReportId(), "Got the results from Drill", reportDefinationImpl.getPublishedBy());
			if(resultSet != null){
				ResultSetMetaData rsMetaData = resultSet.getMetaData();
				int numberOfColumns = rsMetaData.getColumnCount();
				// Retrieving the each row values from the output of the query
				EXPORT_COLUMN_HEADER = new String[numberOfColumns];
				exportColumnHeaderDataType = new String[numberOfColumns];
				for (int i = 1; i <= numberOfColumns; i++) {
					EXPORT_COLUMN_HEADER[i-1] = rsMetaData.getColumnName(i).toUpperCase();
					String columnType = rsMetaData.getColumnTypeName(i).toUpperCase();
					if(columnType.equalsIgnoreCase("DOUBLE") || columnType.equalsIgnoreCase("INT") || columnType.equalsIgnoreCase("SMALLINT") || columnType.equalsIgnoreCase("TINYINT") || columnType.equalsIgnoreCase("BIT")
							|| columnType.equalsIgnoreCase("DECIMAL") || columnType.equalsIgnoreCase("NUMERIC") || columnType.equalsIgnoreCase("MONEY") || columnType.equalsIgnoreCase("FLOAT") || columnType.equalsIgnoreCase("REAL")
							|| columnType.equalsIgnoreCase("BINARY") || columnType.equalsIgnoreCase("VARBINARY") || columnType.equalsIgnoreCase("DOUBLE") || columnType.equalsIgnoreCase("PRECISION") || columnType.equalsIgnoreCase("INTEGER")
							|| columnType.equalsIgnoreCase("SMALLINT") || columnType.equalsIgnoreCase("DEC") || columnType.equalsIgnoreCase("BIGINT"))
					{
						columnType = "NUMARIC";
					}
					exportColumnHeaderDataType[i-1] = columnType;
				}
				logger.debug("Generating File Format:"+fileFormat);
				
				IFileExporter fileExporter = null;
				if(CommonConstants.CSV_FILE_FORMAT.equalsIgnoreCase(fileFormat)){
					reportType = "CSV";
					fileExporter = new CSVReportDataExporter();
				}else{
					reportType = "EXCEL";
					fileExporter = new  XlsxExporter();
				}
				while (resultSet != null && resultSet.next()) {
					rowBuffer = new StringBuffer();
					int k = 1;
					for (int i = 0; i < numberOfColumns; i++) {
						// Handling Date column Type
						if (CommonConstants.SQL_DATE_TYPE.equalsIgnoreCase(rsMetaData.getColumnTypeName(k))) {
							Date date = resultSet.getDate(k);
							SimpleDateFormat dateFmt = new SimpleDateFormat(CommonConstants.DATE_PATTERN);
							rowBuffer = fileExporter.rowMaker(date != null ? dateFmt.format(date): null, rowBuffer);
						} else {
							if(CommonConstants.SQL_NUMBER_TYPE.equalsIgnoreCase(rsMetaData.getColumnTypeName(k))){
								try{
									BigDecimal bigDecimal = resultSet.getBigDecimal(k);
									rowBuffer = fileExporter.rowMaker(bigDecimal.longValueExact()+"", rowBuffer);
								}catch(Exception e){
									try {
										value = resultSet.getString(k) != null ? resultSet.getString(k) : "";
									} catch (NullPointerException ne) {
										value = "";
									}
									rowBuffer = fileExporter.rowMaker(value, rowBuffer);
								}
							}else {
								// this code has been addded to handle drill different datatype exceptions .
								try {
									value = resultSet.getString(k) != null ? resultSet.getString(k) : "";
								} catch (NullPointerException ne) {
									value = "";
								}
								rowBuffer = fileExporter.rowMaker(value, rowBuffer);
							}
						}
						if(CommonConstants.CSV_FILE_FORMAT.equalsIgnoreCase(fileFormat) && i < numberOfColumns-1){
							rowBuffer.append(",");
						}
						k = k + 1;
					}
					bufferList.add(rowBuffer);
					noOfRecords++;
				}
				if(noOfRecords <= rcdLimit || rcdLimit == 0 || noOfRecords == 0)
				{
				msg = "";
				}
				SimpleDateFormat dateFrmt = new SimpleDateFormat("MM/dd/yyyy hh:mm aa z");
				String currentDate = dateFrmt.format(new Date());
				msg = msg.replaceAll("<REPORT NAME>", reportDefinationImpl.getName());
				List<String> attachmentList = new ArrayList<String>();
				String attachMsg = "";
				boolean attachReport = false;
				String maxAttachmentSize = reProperties.getMaxAttachementSize();
				int maxAttachSize = -1;
				if( maxAttachmentSize == null || maxAttachmentSize.trim().equals(""))
				{
					maxAttachSize = 5;
				}
				else
				{
					maxAttachSize = Integer.parseInt( maxAttachmentSize );
				}
				logger.debug("MAX_ATTACHMENT_SIZE configured for report attachmet : " + maxAttachSize + "Mb.");
				if(CommonConstants.CSV_FILE_FORMAT.equalsIgnoreCase(fileFormat)){
					logger.debug("User activity started here :"	+ this.getClass().getName() + " excelFileGenerator method for CSV format  - copyToFTP - "	+ copyToFTP);
					List<StringBuffer> tempBufferList = new ArrayList<StringBuffer>();
					tempBufferList.add(new StringBuffer("Report Name : "+reportDefinationImpl.getName()));
					tempBufferList.add(new StringBuffer("Report Description : "+reportDefinationImpl.getDescription()));
					tempBufferList.add(new StringBuffer("Published On : "+currentDate));
					tempBufferList.add(new StringBuffer(""));
					tempBufferList.add(fileExporter.appendHeader(EXPORT_COLUMN_HEADER));
					tempBufferList.addAll(bufferList);
					String fileName  = reportDefinationImpl.getGenerateFileInformation().customFileNameWithName();
					exportExcelBean.setFileName(fileName.replaceAll("(?i).csv", ".zip"));
					fileExporter.generateFile(exportExcelBean, exportExcelBean.getServerPathLocation() + File.separator
							+ fileName, tempBufferList);
					logger.debug("CSV generated :"+ this.getClass().getName() + exportExcelBean.getServerPathLocation() + File.separator+ fileName);
					String zipFileName = exportExcelBean.getServerPathLocation() + File.separator
							+ fileName;

					ZipFileWriter.writeZip(new File(zipFileName));

					logger.debug("ZIP generated :" + this.getClass().getName() + exportExcelBean.getServerPathLocation() + File.separator + (fileName.replaceAll("(?i).csv", ".zip")));

					zipFileName = zipFileName.replaceAll("(?i).csv", ".zip");
					if (copyToFTP) {
						uploadtoFTP(zipFileName, reportDefinationImpl.getCustomerId(),
								reportDefinationImpl.getProductId());
						reportExecutionLogRepository.insertReportExecutionLog(reportDefinationImpl.getReportId(), "File Uploaded to FTP/SFTP", reportDefinationImpl.getPublishedBy());	
						
					}
					if(reportDefinationImpl.isAttachReportToMail())
					{
						File file = new File(zipFileName);
						double fileSize = file.length();
						double fileSizeMb = ((double)(fileSize / 1024) ) / 1024 ;
						if(maxAttachSize < fileSizeMb )
						{
							logger.debug("File "+file.getName()+" cannot be attached. File size exceeds max file size limit for attachment.");
							attachMsg = "\n File "+file.getName()+" cannot be attached. File size exceeds max file size limit for attachment.";
						}
						else
						{
							attachmentList.add( zipFileName );
							attachReport = true;
						}
					}
					CSVReportDataExporter.deleteCSVFile(
							new File(exportExcelBean.getServerPathLocation() + File.separator + fileName), true);
					logger.debug("User activity ends here :"+ this.getClass().getName() + " excelFileGenerator method for CSV format");
					
				} else {
					logger.debug("User activity started here :"	+ this.getClass().getName() + " excelFileGenerator method for EXCEL format - copyToFTP - "
							+ copyToFTP);
					exportExcelBean.setReportName(reportDefinationImpl.getName());
					exportExcelBean.setReportDesc(reportDefinationImpl.getDescription());
					exportExcelBean.setPublishDate(currentDate);
					exportExcelBean.setColumnHeader(EXPORT_COLUMN_HEADER);
					exportExcelBean.setColumnDataType(exportColumnHeaderDataType);
					exportExcelBean.setRowBufferList(bufferList);
					exportExcelBean.setFileName(reportDefinationImpl.getGenerateFileInformation().customFileNameWithName());
					//new JExcelDataExporter(exportExcelBean).exportEntireDataInList();
					XlsxExporter xlsxExporter = new XlsxExporter();
					xlsxExporter.exportJsonData(exportExcelBean);
					String fileName = exportExcelBean.getServerPathLocation() + File.separator
							+ exportExcelBean.getFileName();
					if (copyToFTP) {
						uploadtoFTP(fileName, reportDefinationImpl.getCustomerId(),
								reportDefinationImpl.getProductId());
						reportExecutionLogRepository.insertReportExecutionLog(reportDefinationImpl.getReportId(), "File Uploaded to FTP/SFTP", reportDefinationImpl.getPublishedBy());	
					}
					if(reportDefinationImpl.isAttachReportToMail())
					{
						File file = new File( fileName );
						double fileSize = file.length();
						double fileSizeMb = ((double)(fileSize / 1024) ) / 1024 ;
						if(maxAttachSize < fileSizeMb )
						{
							logger.debug("File "+file.getName()+" cannot be attached. File size exceeds max file size limit for attachment.");
							attachMsg = "\n File "+file.getName()+" cannot be attached. File size exceeds max file size limit for attachment.";
						}
						else
						{
							attachReport = true;
							attachmentList.add( fileName );
						}
					}
					logger.debug("User activity ends here :"+ this.getClass().getName() + " excelFileGenerator method for EXCEL format");
				}
				reportDefinationImpl.setReportType(reportType);
				reportExecutionLogRepository.insertReportExecutionLog(reportDefinationImpl.getReportId(), "File Generation Completed", reportDefinationImpl.getPublishedBy());
				PublishedReport publishedReport = preparePublishedReport(reportDefinationImpl, exportExcelBean.getFileName());
				publishedReportRepository.insertPublishedReports(publishedReport);
				reportExecutionLogRepository.insertReportExecutionLog(reportDefinationImpl.getReportId(), "Stored into Published Reports", reportDefinationImpl.getPublishedBy());
				if(reportDefinationImpl.getEmailId() != null && reportDefinationImpl.getEmailId().trim().length() > 0){
					String categoryName = reportCategoryRepository.getReportNameCategoryId(reportDefinationImpl.getReport().getReportCategoryId());
					if(categoryName != null){
						categoryName = "Report Category     -  " +categoryName;
					}else{
						categoryName= "";
					}
					mailSender.sendMail(reportDefinationImpl.getEmailId(), mailUtility.getSubject()+categoryName+"  - "+reportDefinationImpl.getName(), categoryName +"<br>"+"Report Name        -  "+reportDefinationImpl.getName()+" <br> "+"Report Description  -  "+reportDefinationImpl.getDescription()+" <br> "+"Report Execution or Publish Time:"+currentDate+"<BR><BR>"+msg+"<BR><BR>"+attachMsg, attachReport ? attachmentList : null);
					reportExecutionLogRepository.insertReportExecutionLog(reportDefinationImpl.getReportId(), "Email Sent", reportDefinationImpl.getPublishedBy());	
					logger.debug("Email sent success");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured at "+ this.getClass().getName(),e);
		} 
		finally {
			bufferList.clear();
			drillServices.closeConnection(connection, null, resultSet);
		}
		logger.debug("User activity ends here:"+ this.getClass().getName() + " excelFileGenerator method");
	}

	private PublishedReport preparePublishedReport(
			ReportDefinationImpl reportDefinationImpl, String fileName) {
		PublishedReport publishedReport = new PublishedReport();
		publishedReport.setCreatedUser(reportDefinationImpl.getPublishedBy());
		publishedReport.setTenantId(reportDefinationImpl.getCustomerId());
		publishedReport.setDisplayStatus(true);
		publishedReport.setProductId(reportDefinationImpl.getProductId());
		publishedReport.setReportCategoryId(reportDefinationImpl.getReportCategoryId());
		publishedReport.setReportId(reportDefinationImpl.getReportId());
		publishedReport.setReportLocaton(fileName);
		publishedReport.setReportName(reportDefinationImpl.getName());
		publishedReport.setReportDescription(reportDefinationImpl.getDescription());
		publishedReport.setScheduled(reportDefinationImpl.isScheduled());
		publishedReport.setFormat(reportDefinationImpl.getReportType());
		return publishedReport;
	}

	private void uploadtoFTP(String file, String customerId, String productId)
			throws Exception {
		logger.debug("User activity Starts here:"+ this.getClass().getName() + " uploadtoFTP method");
		
		try {
			//CustomerFTPDao customerFTPDao = new CustomerFTPDaoImpl();
			List<CustomerFTP> custftpList = customerFTPRepository.getCustomerSftpData(customerId, productId);
			if (custftpList != null && custftpList.size() > 0) {
				CustomerFTP customerFTP = custftpList.get(0);
				if (isValidSftp(customerFTP)) {
					UploadUtility utility = new UploadUtility();
					ArrayList<String> files = new ArrayList<String>();
					files.add(file);
					utility.uploadFile(files, customerFTP);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured at " + this.getClass().getName(),e);
		}
		logger.debug("User activity Ends here:"	+ this.getClass().getName() + " uploadtoFTP method");
	}

	private boolean isValidSftp(CustomerFTP customerFTP) throws Exception {
		boolean flag = true;
		if (customerFTP.isFtpEnable()) {
			if (isEmpty(customerFTP.getHost()) || customerFTP.getPort() == 0 || isEmpty(customerFTP.getuName())
					|| isEmpty(customerFTP.getPassword()) || isEmpty(customerFTP.getFolder())) {
				throw new Exception("provided SFTP/FTP credentials are not correct.");
			}
		}
		return flag;
	}

	public boolean isEmpty(String s) {
		if (s == null || s != null && s.trim().length() == 0)
			return true;
		return false;
	}
}

package com.razor.raw.published.rest.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.razor.raw.core.common.CommonConstants;
import com.razor.raw.core.pojo.PublishedReport;
import com.razor.raw.download.common.util.FileUploadService;
import com.razor.raw.published.rest.bean.PublishManualFileBean;
import com.razor.raw.published.rest.properties.RPProperties;
import com.razor.raw.published.rest.service.PublishedReportService;

/**
 * 
 * @author AJAY.KUMAR This class is used to
 */
@EnableOAuth2Resource
@RestController
public class FileUploadController {

	private static final Logger logger = LoggerFactory
			.getLogger(FileUploadController.class);
	@Autowired
	FileUploadService fileUploadService;
	@Autowired
	PublishedReportService publishedReportService;
	@Autowired
	RPProperties rpProperties;

	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	public void fileUpload(@RequestParam MultipartFile file,HttpServletResponse response) {
		logger.debug("FileUploadController - fileUpload - START");

		if (file.getOriginalFilename().toLowerCase().endsWith(CommonConstants.CSV_FILE_FORMAT)
				|| file.getOriginalFilename().toLowerCase().endsWith(CommonConstants.XLSX_FILE_FORMAT) || file.getOriginalFilename().toLowerCase().endsWith(CommonConstants.XLS_FILE_FORMAT)) {
			
			if(file.getSize() > ( rpProperties.getPublishedReportMaxSize() * 1024 *1024 )){
				try {
					response.sendError(700);
				} catch (IOException e) {
					logger.error("FileUploadController - fileUpload - Exception", e);
				}
				return;
			}
			
			String name = rpProperties.getPublishedReportPath()
					+ File.separator + file.getOriginalFilename();
			File destFile = new File(name);
			try {
				file.transferTo(destFile);
			} catch (IllegalStateException e) {
				try {
					response.sendError(600);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					logger.error("FileUploadController - fileUpload - Exception", e1);
				}
				logger.error("FileUploadController - fileUpload - Exception", e);
			} catch (IOException e) {
				try {
					response.sendError(600);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					logger.error("FileUploadController - fileUpload - Exception", e1);
				}
				logger.error("FileUploadController - fileUpload - Exception", e);
			}
			logger.debug("FileUploadController - fileUpload - END");

		}
		else{
			try {
				response.sendError(600);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.error("FileUploadController - fileUpload - Exception; Not valid format");
		}

	}
	
	@RequestMapping(value = "/publishManualFile", method = RequestMethod.POST)
	public boolean publishManualFile(
			@RequestBody PublishManualFileBean publishManualFileBean) {
		logger.debug("publishManualFile - fileUpload - START");
		boolean status = false;

		boolean flag = publishedReportService.isPublishedReportExist(
				publishManualFileBean.getReportName(),
				publishManualFileBean.getTetantId(),
				publishManualFileBean.getProdId(),
				publishManualFileBean.getDestCategoryId());
		
		boolean reportFileExists = publishedReportService.isPublishedReportFileExist(
				publishManualFileBean.getFilename());
		
		logger.debug("FileUploadController - publishManualFile - isPublishedReportExist - "
				+ flag);

		if (!flag) {
			PublishedReport pubReport = new PublishedReport();

			pubReport.setReportName(publishManualFileBean.getReportName());
			pubReport.setReportDescription(publishManualFileBean
					.getReportDescription());
			pubReport.setReportCategoryId(Long.parseLong(publishManualFileBean
					.getDestCategoryId()));
			pubReport.setProductId(publishManualFileBean.getProdId());
			pubReport.setReportLocaton(publishManualFileBean.getFilename());
			pubReport.setCreatedUser(publishManualFileBean.getUserId());
			pubReport.setTenantId(publishManualFileBean.getTetantId());

			pubReport.setDisplayStatus(true);
			pubReport.setScheduled(false);

			if (publishManualFileBean.getFilename() != null) {
				if (publishManualFileBean.getFilename().toLowerCase().endsWith(
						CommonConstants.CSV_FILE_FORMAT))
					pubReport.setFormat(CommonConstants.CSV_DB_FORMAT);
				else if (publishManualFileBean.getFilename().toLowerCase().endsWith(
						CommonConstants.XLSX_FILE_FORMAT) || publishManualFileBean.getFilename().toLowerCase().endsWith(CommonConstants.XLS_FILE_FORMAT))
					pubReport.setFormat(CommonConstants.XLSX_DB_FORMAT);
			}

			publishedReportService.insertPublishedReports(pubReport);
			status = true;
		}
		else if(flag && !reportFileExists)
		{
			logger.debug("FileUploadController - publishManualFile - Fail");
			String name = rpProperties.getPublishedReportPath()
					+ File.separator + publishManualFileBean.getFilename();
			File destFile = new File(name);
			boolean isDeleted = destFile.delete();
			logger.debug("FileUploadController - publishManualFile - File delete is - "+isDeleted);
		}
		
		logger.debug("PublishedReportController - copyPublishedReport - END - success");
		return status;
	}
	
	
	
	
	
	
}

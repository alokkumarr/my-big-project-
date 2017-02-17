package com.razor.raw.generation.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razor.raw.generation.rest.bean.ReportCategoryBean;
import com.razor.raw.generation.rest.service.ReportCategoryService;

@RestController
@EnableOAuth2Resource
@EnableConfigurationProperties
public class ReportCategoryController {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportCategoryController.class);
	@Autowired
	ReportCategoryService reportCategoryService;
	
	@RequestMapping(value = "/getListOfReportCategories", method = RequestMethod.GET)
	public List<ReportCategoryBean> getReportDetails(@RequestParam("tenantId") String tenantId, @RequestParam("productId") String productId){
		logger.debug(this.getClass().getName() + " - getReportDetails - START");
		List<ReportCategoryBean> reportCategories = reportCategoryService.getReportCategoryDetails(tenantId, productId);
		logger.debug(this.getClass().getName() + " - getReportDetails - END");
		return reportCategories;
	}

	
	@RequestMapping(value = "/getCurrentDateTime", method = RequestMethod.GET)
	public String getCurrentDateTime(){
		logger.debug(this.getClass().getName() + " - getCurrentDate - START");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    logger.debug(this.getClass().getName() + " - getCurrentDate - END");
		return sdfDate.format(new Date());
	}

	
	
}

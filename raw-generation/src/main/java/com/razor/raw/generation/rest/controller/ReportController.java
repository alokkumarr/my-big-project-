package com.razor.raw.generation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.razor.raw.core.jms.IQueueSender;
import com.razor.raw.core.req.ReportReq;
import com.razor.raw.generation.rest.bean.ReportViewBean;
import com.razor.raw.generation.rest.bean.ReportViewReqBean;
import com.razor.raw.generation.rest.properties.RGProperties;
import com.razor.raw.generation.rest.service.ReportService;

@EnableOAuth2Resource
@RestController
public class ReportController {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportController.class);
	@Autowired
	public ReportService reportService;

	
	@Autowired
	@Qualifier("reportRequestSender")
	IQueueSender iQueueSender;
	
	@Autowired
	RGProperties rgProperties;

	/*@RequestMapping(value = "/getReport", method = RequestMethod.POST)
	public List<Report> getReportDetails(@RequestBody ReportParamBean param){
		List<Report> reportList = reportService.getReportByCategoryId(param.getCategoryId(), param.getTenantId());
		return reportList;
	}*/
	
	
	@RequestMapping(value = "/publishReport", method = RequestMethod.POST)
	public boolean publishReport(@RequestBody ReportReq reportReq){
		logger.debug(this.getClass().getName() + " - publishReport - START");
		boolean status  = false;
		if(!reportService.getPublishedReportStatus(reportReq.getName(), reportReq.getTenantId(), reportReq.getProductId()))
		{
			iQueueSender.sendMessage(reportReq);
			status= true;
		}
		logger.debug(this.getClass().getName() + " - publishReport - END");
		return status;
	}

	/**
	 * 
	 * @param report
	 * return the report views of Report
	 */
	@RequestMapping(value = "/viewReport", method = RequestMethod.POST)
	public ReportViewBean viewReport(@RequestBody ReportViewReqBean reportViewReqBean) {
		logger.debug(this.getClass().getName() + " - viewReport - START");
		ReportViewBean reportViews = reportService.getReportViewByReport(reportViewReqBean);
		logger.debug(this.getClass().getName() + " - viewReport - END");	
		return reportViews;
	}

	
	
	
}

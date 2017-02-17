package com.razor.raw.generation.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razor.raw.core.pojo.DrillView;
import com.razor.raw.generation.rest.bean.DesignerReportReqBean;
import com.razor.raw.generation.rest.bean.DesignerSaveReportBean;
import com.razor.raw.generation.rest.bean.ReportViewBean;
import com.razor.raw.generation.rest.bean.Valid;
import com.razor.raw.generation.rest.properties.RGProperties;
import com.razor.raw.generation.rest.service.DesignerReportService;
import com.razor.raw.utility.beans.DeleteReportReq;

@EnableOAuth2Resource
@RestController
public class DesignerReportController {
	private static final Logger logger = LoggerFactory
			.getLogger(DesignerReportController.class);
	@Autowired
	public DesignerReportService designerReportService;
	
	@Autowired
	RGProperties rgProperties;
	
	/**
	 * 
	 * @param report
	 * return the report views of Report
	 */
	@RequestMapping(value = "/executeDesignerReport", method = RequestMethod.POST)
	public ReportViewBean executeDesignerReport(@RequestBody DesignerReportReqBean designerReportReqBean) {
		logger.debug(this.getClass().getName() + " - viewReport - START");
		ReportViewBean reportViews = designerReportService.getDesignerReport(designerReportReqBean);
		logger.debug(this.getClass().getName() + " - viewReport - END");	
		return reportViews;
	}
	/**
	 * 
	 * @param tenantId
	 * @param productId
	 * @return list of view
	 */
	@RequestMapping(value = "/getViewList", method = RequestMethod.GET)
	public List<DrillView> getViewList(@RequestParam("tenantId") String tenantId,  @RequestParam("productId") String productId){
		List<DrillView> reportList = designerReportService.getViewList(tenantId, productId);
		return reportList;
	}
	
	/**
	 * 
	 * @param tenantId
	 * @param productId
	 * @return list of view
	 */
	@RequestMapping(value = "/updateDesignerReport", method = RequestMethod.POST)
	public Valid updateDesignerReport(@RequestBody DesignerSaveReportBean designerSaveReportBean){
		logger.debug(this.getClass().getName() + " - updateDesignerReport - START");
		Valid saveStatus = designerReportService.updateDesignerReport(designerSaveReportBean);
		logger.debug(this.getClass().getName() + " - updateDesignerReport - END");
		return saveStatus;
	}
	
	/**
	 * 
	 * @param tenantId
	 * @param productId
	 * @return list of view
	 */
	@RequestMapping(value = "/inactivateDesignerReport", method = RequestMethod.POST)
	public Valid inactivateReport(@RequestBody DeleteReportReq deleteReportReq){
		logger.debug(this.getClass().getName() + " - inactivateDesignerReport - START");
		Valid saveStatus = designerReportService.inactivateDesignerReport(deleteReportReq);
		logger.debug(this.getClass().getName() + " - inactivateDesignerReport - END");
		return saveStatus;
	}
	
	/**
	 * 
	 * @param tenantId
	 * @param productId
	 * @return list of view
	 */
	@RequestMapping(value = "/insertDesignerReport", method = RequestMethod.POST)
	public Valid insertDesignerReport(@RequestBody DesignerSaveReportBean designerSaveReportBean){
		logger.debug(this.getClass().getName() + " - insertDesignerReport - START");
		Valid saveStatus = designerReportService.insertDesignerReport(designerSaveReportBean);
		logger.debug(this.getClass().getName() + " - insertDesignerReport - END");
		return saveStatus;
	}
	
}

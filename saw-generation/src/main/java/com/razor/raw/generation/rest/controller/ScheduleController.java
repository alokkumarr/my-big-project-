package com.razor.raw.generation.rest.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razor.raw.core.pojo.Report;
import com.razor.raw.generation.rest.properties.RGProperties;
import com.razor.raw.generation.rest.service.ReportService;
import com.razor.scheduler.common.GeneratedScheduleCron;
import com.razor.scheduler.common.QuartzScheduleJobDetails;
import com.razor.scheduler.common.ScheduleReportRespBean;
import com.razor.scheduler.common.ScheduledReportReqBean;
import com.razor.scheduler.jobs.report.ReportDefinationJob;
import com.razor.scheduler.jobs.report.ReportDefinition;
import com.razor.scheduler.service.ScheduleService;

@EnableOAuth2Resource
@RestController
public class ScheduleController {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportController.class);
	@Autowired
	ScheduleService scheduleService;
	
	@Autowired
	ReportService reportService;
	
	@Autowired
	RGProperties rgProperties;

	/**
	 * @param reportDefinition
	 * @param jobGroup
	 * @param jobName
	 * Method is used to schedule the report 
	 */
	@RequestMapping(value = "/scheduleReport", method = RequestMethod.POST)
	public void scheduleReport(@RequestBody ReportDefinition reportDefinition, @RequestParam("jobGroup") String jobGroup, @RequestParam("jobName") String jobName) {
		logger.debug(this.getClass().getName() + " - scheduleReport - START");
		QuartzScheduleJobDetails<ReportDefinition> quartzJobDetails = new QuartzScheduleJobDetails<ReportDefinition>(
				reportDefinition);
		Report report = reportDefinition.getReport();
		// validate is the save schedule report request from edit schedule screen
		if(report != null && (report.getReportQuery() == null || report.getReportQuery().isEmpty())){
			long reportId = report.getReportId();
			report = reportService.getReportByReportId(reportId);
			if(report == null){
				logger.debug(this.getClass().getName() + "Unable to fetch the report details for Id:"+reportId+" - edit scheduledReport");
			}else{
				reportDefinition.setReport(report);
			}
		}
		//reportDefinition.setLastModDate(new Date());
		String cronExpressionGen = reportDefinition.getCronExpressionGen();
		if (cronExpressionGen != null && !cronExpressionGen.equals("")) {
			quartzJobDetails.setQuartzCronExpression(cronExpressionGen);
			quartzJobDetails.setJobclassName(ReportDefinationJob.class);
			quartzJobDetails.setJobGroup(jobGroup);
			quartzJobDetails.setJobName(jobName);
			
			if (!reportDefinition.isRecursiveSchedule()) {
				long time = Long.parseLong(reportDefinition.getCronExpressionGen());
				quartzJobDetails.setSimpleTriggerDate(new Date(time));
				scheduleService.setSimpleTrigger(quartzJobDetails);
			} else {
				scheduleService.setCronTrigger(quartzJobDetails);
			}
			reportService.updateReportScheduledDate(report.getReportId());
			logger.debug(this.getClass().getName() + " - scheduleReport - END");
		}
		
	}
	
	@RequestMapping(value = "/viewScheduledReportList", method = RequestMethod.POST)
	public ScheduleReportRespBean viewScheduledReportList(@RequestBody ScheduledReportReqBean scheduleReqBean){
		logger.debug(this.getClass().getName() + " - showSummary - START");
		
		if(!StringUtils.isEmpty(scheduleReqBean.getTenantId()) && !StringUtils.isEmpty(scheduleReqBean.getProductId())){
			return scheduleService.viewScheduledReportList(scheduleReqBean);
		}
		return null;
	}
	
	
	@RequestMapping(value = "/showScheduleSummary", method = RequestMethod.GET)
	public List<GeneratedScheduleCron> showSummary(@RequestParam String reportId, @RequestParam String tenantId, @RequestParam String productID) 
	{
		logger.debug(this.getClass().getName() + " - showSummary - START");
		
		if(!StringUtils.isEmpty(tenantId) && !StringUtils.isEmpty(productID)){
			String jobGroup = productID+"-"+tenantId;
			return scheduleService.showSummary(reportId, jobGroup, ((rgProperties.getNoOfScheduleUpcommingEvents() == null )? 5 : Integer.parseInt(rgProperties.getNoOfScheduleUpcommingEvents())) );
		}
		return null;
	}
	
	
	@RequestMapping(value = "/deleteScheduledTask", method = RequestMethod.GET)
	public void deleteScheduleTask(@RequestParam String reportId, @RequestParam String tenantId) 
	{
		logger.debug(this.getClass().getName() + " - deleteScheduleTask - START");
		scheduleService.deleteJob(reportId, tenantId);
	}
	
}

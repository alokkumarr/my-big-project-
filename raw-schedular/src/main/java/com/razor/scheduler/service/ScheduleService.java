package com.razor.scheduler.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.razor.raw.core.dao.repository.ScheduledReportRepository;
import com.razor.raw.core.pojo.ScheduledReport;
import com.razor.scheduler.common.GeneratedScheduleCron;
import com.razor.scheduler.common.QuartzScheduleJobDetails;
import com.razor.scheduler.common.ScheduleReportRespBean;
import com.razor.scheduler.common.ScheduledReportReqBean;
import com.razor.scheduler.jobs.report.ReportDefinition;

@Service
public class ScheduleService {

	private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);
	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;
	
	@Autowired
	public ScheduledReportRepository scheduledReportRepository;
	
	public final static String SCHEDULER_DATE_FORMAT = "MM/dd/yyyy hh:mm aa";
	public final static String JOB_DATA_MAP = "JOB_DATA_MAP";
	
	public void setSimpleTrigger(QuartzScheduleJobDetails<?> quartzJobDetails)
	{
		logger.debug(this.getClass().getName() + " setSimpleTrigger - JobName - "+quartzJobDetails.getJobName() + " - JobGroup -"+quartzJobDetails.getJobGroup());
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.deleteJob(new JobKey(quartzJobDetails.getJobName(), quartzJobDetails.getJobGroup()));
			scheduler.scheduleJob(quartzJobDetails.retrieveJobDetails(),
					quartzJobDetails.retrieveSimpleTriggerImpl());
		} catch (SchedulerException e) {
			logger.error(this.getClass().getName() + " setSimpleTrigger - ",e);
			e.printStackTrace();
		}
		logger.debug(this.getClass().getName() + " setSimpleTrigger - ends");
	}
	
	public void setCronTrigger(QuartzScheduleJobDetails<?> quartzJobDetails)
	{
		logger.debug(this.getClass().getName() + " setCronTrigger - JobName - "+quartzJobDetails.getJobName() + " - JobGroup -"+quartzJobDetails.getJobGroup());
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.deleteJob(new JobKey(quartzJobDetails.getJobName(), quartzJobDetails.getJobGroup()));
			scheduler.scheduleJob(quartzJobDetails.retrieveJobDetails(),
					quartzJobDetails.retrieveCronTriggerImpl());
		} catch (SchedulerException e) {
			logger.error(this.getClass().getName() + " setCronTrigger - ",e);
			e.printStackTrace();
		}
		logger.debug(this.getClass().getName() + " setCronTrigger - ends");
	}
	
	public void deleteJob(String name, String group)
	{
		logger.debug(this.getClass().getName() + " deleteJob - JobName - "+name + " - JobGroup -"+group);
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.deleteJob(new JobKey(name, group));
		} catch (SchedulerException e) {
			e.printStackTrace();
			logger.debug(this.getClass().getName() + " deleteJob - error",e);
		}
		logger.debug(this.getClass().getName() + " deleteJob - ends");
	}
	
	
	public List<GeneratedScheduleCron> showSummary(String jobName, String groupName, int NO_UPCOMING_EVENTS) {
		logger.debug(	"User activity started here:" + " showSummary method");
		SimpleDateFormat simpleDate = new SimpleDateFormat(SCHEDULER_DATE_FORMAT);
		String generatedCronExpression = null;
		List<GeneratedScheduleCron> upcomingEvents = null;
		try {
			if (!StringUtils.isEmpty(jobName) && !StringUtils.isEmpty(groupName)) {
				List<? extends Trigger> triggers = schedulerFactoryBean.getScheduler().getTriggersOfJob( new JobKey(jobName, groupName));
				if (triggers.size() > 0) {
					for (Trigger trigger : triggers) {
						if (trigger instanceof CronTrigger) {
							CronTrigger cronTrigger = (CronTrigger) trigger;
							generatedCronExpression = cronTrigger.getCronExpression();
							CronExpression cronExpression = new CronExpression(generatedCronExpression);
							Date date = new Date();
							upcomingEvents = new ArrayList<GeneratedScheduleCron>();
							GeneratedScheduleCron generated = null;
							for (int i = 0; i < NO_UPCOMING_EVENTS; i++) {
								try {
									date = cronExpression.getNextValidTimeAfter(date);
									generated = new GeneratedScheduleCron();
									generated.setSerailNum(i + 1);
									generated
											.setGeneratedCronExpressionDate((simpleDate
													.format(date)).toUpperCase());
									upcomingEvents.add(generated);
								} catch (Exception e) {
									logger.error("Exception occured at " + this.getClass().getName(),e);
									break;
								}
							}
							logger.debug("cron************"+generatedCronExpression);
						}else if(trigger instanceof SimpleTrigger){
							SimpleTrigger simpleTrigger = (SimpleTrigger)trigger;
							upcomingEvents = new ArrayList<GeneratedScheduleCron>();
							try{
							String startDateString = simpleDate.format(new Date());
							GeneratedScheduleCron generated = new GeneratedScheduleCron();
							logger.debug(""+simpleTrigger.getNextFireTime());
							generated.setSerailNum(1);
							generated.setGeneratedCronExpressionDate((simpleDate
									.format(simpleTrigger.getNextFireTime())).toUpperCase());
							upcomingEvents.add(generated);
							}catch(Exception e){
								logger.error("Exception occured at Simple Trigger " + this.getClass().getName(),e);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName(),e);
		}
		return upcomingEvents;
	}

	public ScheduleReportRespBean viewScheduledReportList(
			ScheduledReportReqBean scheduleReqBean) {
		ScheduleReportRespBean scheduleReportRespBean = new ScheduleReportRespBean();
		List<ScheduledReport> scheduledReportList = scheduledReportRepository.viewScheduledReportList(scheduleReqBean.getProductId(),
				scheduleReqBean.getTenantId(),scheduleReqBean.getReportCategoryId(),scheduleReqBean.getUserId());
		try {
			for(ScheduledReport scheduledReport : scheduledReportList){
				
				ReportDefinition reportDefinition = (ReportDefinition) schedulerFactoryBean
						.getScheduler()
						.getJobDetail(new JobKey(scheduledReport.getJobName(),
										scheduledReport.getJobGroup()))
						.getJobDataMap().get(JOB_DATA_MAP);
				Boolean isRecursive = reportDefinition.isRecursiveSchedule();
				scheduledReport.setEmailId(reportDefinition.getEmailId());
				scheduledReport.setFileNameFormat(reportDefinition.getFileNameFormat());
				scheduledReport.setScheduleType(isRecursive ? "Recurring" : "One Time"  );
				scheduledReport.setCronExpression(reportDefinition.getCronExpressionGen());
				scheduledReport.setDistribution((!reportDefinition.isCopyToFTP()) ? "E-MAIL" 
						: ((!reportDefinition.isAttachReportToMail()) ? "FTP" : "E-MAIL & FTP"));
				
				scheduledReport.setDistribution((!reportDefinition.isCopyToFTP() 
						&& !reportDefinition.isAttachReportToMail()) ? null : (scheduledReport.getDistribution()));
				
				scheduledReport.setCopyToFTP(reportDefinition.isCopyToFTP());
				scheduledReport.setAttachReportToMail(reportDefinition.isAttachReportToMail());
				//scheduledReport.setLastModDate(reportDefinition.getLastModDate());
			}
			scheduleReportRespBean.setScheduledReportList(scheduledReportList);
			
			scheduleReportRespBean.setTotalCount(scheduledReportList.size());
			scheduleReportRespBean.setStatusMessage("SUCCESS");
		} catch (SchedulerException e) {
			logger.error("Exception occured while fetching the scheduler details " + this.getClass().getName(),e);
		}
		return scheduleReportRespBean;
	}
	
	public ReportDefinition getReportDefinition( String jobName, String JobGroup) {
		ReportDefinition reportDefinition = null;
		try {
			JobDetail jobDetail = schedulerFactoryBean
				.getScheduler()
				.getJobDetail(new JobKey(jobName,JobGroup)) ;
			if(jobDetail != null){
				reportDefinition = (ReportDefinition) jobDetail.getJobDataMap().get(JOB_DATA_MAP);
			}
		} catch (SchedulerException e) {
			logger.error("Exception occured while fetching the scheduler details, Job Name: "+jobName + ", "+ this.getClass().getName(),e);
		}
		return reportDefinition;
	}

	/*public Boolean getScheduledReports( String tenantId, String productId) {
		List<String> scheduleList = scheduledReportRepository.getScheduledReports(tenantId, productId);
		return scheduledReportRepository.getScheduledReports(tenantId, productId);
	}*/
	
	
}

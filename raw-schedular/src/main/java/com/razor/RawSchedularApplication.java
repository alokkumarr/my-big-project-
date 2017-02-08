package com.razor;

import java.util.Date;

import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.razor.raw.core.pojo.OutputFile;
import com.razor.raw.core.pojo.Report;
import com.razor.scheduler.common.QuartzScheduleJobDetails;
import com.razor.scheduler.jobs.report.ReportDefinationJob;
import com.razor.scheduler.jobs.report.ReportDefinition;
import com.razor.scheduler.service.ScheduleService;

@SpringBootApplication
public class RawSchedularApplication {

    public static void main(String[] args) throws SchedulerException {
    	ConfigurableApplicationContext applicationContext =    SpringApplication.run(RawSchedularApplication.class, args);
    	ScheduleService service = applicationContext.getBean(ScheduleService.class);
        simpleTest(service);
    	//service.deleteJob("2", "PGI");
/*        try {
        	config.startSimpleJob(null, 1000L,schedulerFactoryBean);
			//scheduler.deleteJob(new JobKey("sampleJobDetail", "DEFAULT"));
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/         }
    
    
    public static void simpleTest(ScheduleService service) throws SchedulerException
    {
    	ReportDefinition reportDefinition = new ReportDefinition(); 
    	QuartzScheduleJobDetails<ReportDefinition> quartzJobDetails = new QuartzScheduleJobDetails<ReportDefinition>(reportDefinition);

		String cronExpressionGen = "0 0/1 * * * ?";//new CronExpressionBuilder().getCronExpression(this);
		//LogManager.log(LogManager.CATEGORY_DEFAULT,LogManager.LEVEL_DEBUG,"cronExpressionGen:"+cronExpressionGen);
		//LogManager.log(LogManager.CATEGORY_DEFAULT,LogManager.LEVEL_DEBUG,"Report ID:"+viewReportModel.getMdReportMainId());
		if (cronExpressionGen != null && !cronExpressionGen.equals("")) {
			quartzJobDetails.setQuartzCronExpression(cronExpressionGen);
			quartzJobDetails.setJobclassName(ReportDefinationJob.class);
			quartzJobDetails.setJobGroup("PGI"); 
			quartzJobDetails.setJobName("4"); 
			reportDefinition.setFileNameFormat(".xlxs");
			reportDefinition.setReportFileNameTobeGenerated("ReportName");
			reportDefinition.setReportDesc("getDescription");
			
			reportDefinition.setTenantId("PGI");
			reportDefinition.setProductID("DEV");
			reportDefinition.setUsername("SURE");
			reportDefinition.setEmailId("surendra.rajaneni@razorsight.com");
			reportDefinition.setCopyToFTP(true);
			reportDefinition.setAttachReportToMail(true);
			
			
			//viewReportModel.getReportDefinition().setScheduleType(getSchedulerType());
			//quartzJobDetails.setOtherRequiredInfo(otherInformation);
			if("once".equalsIgnoreCase("ONCE")){
				quartzJobDetails.setSimpleTriggerDate(new Date());
				//Scheduler scheduler = FacesUtils.getQuartzSchedular();
				//LogManager.log(LogManager.CATEGORY_DEFAULT, LogManager.LEVEL_DEBUG, "Scheduler :" + scheduler);
				/*service.scheduleJob(quartzJobDetails.retrieveJobDetails(),
						quartzJobDetails.retrieveSimpleTriggerImpl());*/
				service.setSimpleTrigger(quartzJobDetails);
			} else {
				//Scheduler scheduler = FacesUtils.getQuartzSchedular();
				//LogManager.log(LogManager.CATEGORY_DEFAULT, LogManager.LEVEL_DEBUG, "Scheduler :" + scheduler);
				/*service.scheduleJob(quartzJobDetails.retrieveJobDetails(),
						quartzJobDetails.retrieveCronTriggerImpl());*/
				service.setCronTrigger(quartzJobDetails);
			}
    }
    }

	
}

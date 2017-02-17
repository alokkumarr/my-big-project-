package com.razor;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.razor.scheduler.common.GeneratedScheduleCron;
import com.razor.scheduler.common.QuartzScheduleJobDetails;
import com.razor.scheduler.jobs.report.ReportDefinationJob;
import com.razor.scheduler.jobs.report.ReportDefinition;
import com.razor.scheduler.service.ScheduleService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RawSchedularApplication.class)
public class RawSchedularApplicationTests {
	@Autowired
	ScheduleService scheduleService;
	/*@Test
	public void contextLoads() {
	}*/
	
	@Test
	public void showSummary()
	{
		try {
			simpleTest();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<GeneratedScheduleCron> crons = scheduleService.showSummary("5", "PGI", 10);
		for(GeneratedScheduleCron cron : crons)
		{
			System.out.println(cron.getGeneratedCronExpressionDate());
		}
		//assertTrue(crons.size() == 10);
	}
	
	
	
	public void simpleTest() throws SchedulerException
    {
    	ReportDefinition reportDefinition = new ReportDefinition(); 
    	QuartzScheduleJobDetails<ReportDefinition> quartzJobDetails = new QuartzScheduleJobDetails<ReportDefinition>(reportDefinition);

		String cronExpressionGen = "0 0/10 * * * ?";//new CronExpressionBuilder().getCronExpression(this);
		if (cronExpressionGen != null && !cronExpressionGen.equals("")) {
			quartzJobDetails.setQuartzCronExpression(cronExpressionGen);
			quartzJobDetails.setJobclassName(ReportDefinationJob.class);
			quartzJobDetails.setJobGroup("PGI"); 
			quartzJobDetails.setJobName("5"); 
			reportDefinition.setFileNameFormat(".xlxs");
			reportDefinition.setReportFileNameTobeGenerated("ReportName");
			reportDefinition.setReportDesc("getDescription");
			
			reportDefinition.setTenantId("PGI");
			reportDefinition.setProductID("DEV");
			reportDefinition.setUsername("SURE");
			reportDefinition.setEmailId("surendra.rajaneni@razorsight.com");
			reportDefinition.setCopyToFTP(true);
			reportDefinition.setAttachReportToMail(true);
			reportDefinition.setRecursiveSchedule(false);
			if(!reportDefinition.isRecursiveSchedule()){
				quartzJobDetails.setSimpleTriggerDate(new Date());
				scheduleService.setSimpleTrigger(quartzJobDetails);
			} else {
				scheduleService.setCronTrigger(quartzJobDetails);
			}
    }
    }

}

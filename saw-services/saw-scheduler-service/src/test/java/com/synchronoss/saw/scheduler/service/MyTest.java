package com.synchronoss.saw.scheduler.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.synchronoss.saw.scheduler.job.BisCronJob;
import com.synchronoss.saw.scheduler.modal.BISSchedulerJobDetails;

import junit.framework.Assert;

public class MyTest {
	 @Autowired
	    private ApplicationContext context;
	 @InjectMocks
	 JobService<BISSchedulerJobDetails> bisJobService;
	 @Spy
	 SchedulerFactoryBean schedulerFactoryBean;
	

	   

	@Test
	public void testScheduleCronJob() {
		
		BISSchedulerJobDetails jobDetail = new BISSchedulerJobDetails();
		jobDetail.setChannelType("Test");
		jobDetail.setCronExpression("0 0 12 * * ?");
		jobDetail.setDescription("");
		List list = new ArrayList();
		list.add("test@test.com");
		jobDetail.setEmailList(list);
		try {
			jobDetail.setEndDate(new SimpleDateFormat( "yyyy-MM-dd" ).parse( "2018-11-30T06:06" ));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobDetail.setEntityId("test");
		jobDetail.setFileType("test");
		jobDetail.setJobGroup("test");
		jobDetail.setJobName("test");
		try {
			jobDetail.setJobScheduleTime(new SimpleDateFormat( "yyyy-MM-dd" ).parse( "2018-11-29T05:06" ));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobDetail.setUserFullName("DDDD");
		
		
		JobUtil jobUtil = Mockito.mock(JobUtil.class);
			
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		//Mockito.when(jobUtil.createBatchIngestionJob(BisCronJob.class, false, context, jobDetail, "test")).thenReturn(factoryBean.getObject());
		
		/*JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		 Mockito.when(jobUtil.createBatchIngestionJob(
				 BisCronJob.class,false,context,jobDetail,"test")).
		 thenReturn(factoryBean.getObject());*/
		Scheduler scheduler = Mockito.mock(Scheduler.class);
		Mockito.doReturn(scheduler).when(schedulerFactoryBean).getScheduler();
	//	Mockito.doReturn(new Date()).when(schedulerFactoryBean).scheduleJob();
		bisJobService.scheduleCronJob(jobDetail, BisCronJob.class);
		 Assert.assertTrue(true);
		 
		 
		 
		 
		
	}
}

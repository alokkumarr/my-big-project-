package com.synchronoss.saw.scheduler.job;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;

import com.synchronoss.saw.scheduler.modal.BISSchedulerJobDetails;

public class BisCronJobTest {
	
	@Test
	public void testExecuteInternal(){
		BisCronJob cronJob = new BisCronJob();
		JobExecutionContext context = Mockito.mock(JobExecutionContext.class);
		JobDetail jobDetail = Mockito.mock(JobDetail.class);
		JobKey jobKey = new JobKey("test");
		JobDataMap jobDataMap = Mockito.mock(JobDataMap.class);
		BISSchedulerJobDetails bisJobDetails = Mockito.mock(BISSchedulerJobDetails.class);
		
		
		
		when(context.getJobDetail()).thenReturn(jobDetail);
		when(jobDetail.getKey()).thenReturn(jobKey);
		when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.get("JOB_DATA_MAP")).thenReturn(bisJobDetails);
		when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
		when(jobDataMap.getString("myKey")).thenReturn("test");
		try {
			cronJob.executeInternal(context);
			
			verify(context).getJobDetail();
			verify(jobDetail).getKey();
			verify(jobDetail).getJobDataMap();
			verify(jobDataMap).get("JOB_DATA_MAP");
			verify(context).getMergedJobDataMap();
			verify(jobDataMap).getString("myKey");
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	@Test
	public void testInterrupt(){
		
		BisCronJob cronJob = new BisCronJob();
		
		try {
			cronJob.interrupt();
		} catch (UnableToInterruptJobException e) {
			Assert.assertEquals(e.getMessage(),"UnableToInterruptJobException");
			
		}
	}

}

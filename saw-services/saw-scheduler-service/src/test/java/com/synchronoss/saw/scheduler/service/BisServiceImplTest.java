package com.synchronoss.saw.scheduler.service;

import static org.hamcrest.CoreMatchers.any;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.synchronoss.saw.scheduler.job.BisCronJob;
import com.synchronoss.saw.scheduler.job.BisSimpleJob;
import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;
import com.synchronoss.saw.scheduler.modal.ScheduleKeys;

@RunWith(SpringRunner.class)
public class BisServiceImplTest {

	private BisJobServiceImpl bisJobService;
	
	private BisSchedulerJobDetails schJobDetail ;
	private ScheduleKeys scheduleKeys;
	private JobKey jobKey;
	private JobDetail jobDetail;
	private JobDataMap jobDataMap;
	private Scheduler scheduler ;
	private JobDetailFactoryBean factoryBean;
	private JobExecutionContext jobContext;

	@Before
	public void setUp() throws ParseException{
		/**
		 * Prepare request object
		 */
		schJobDetail = new BisSchedulerJobDetails();
		schJobDetail.setChannelType("Test");
		schJobDetail.setCronExpression("0 0 12 * * ?");
		schJobDetail.setDescription("");
		List<String> emails = new ArrayList<String>();
		emails.add("test@test.com");
		schJobDetail.setEmailList(emails);
		schJobDetail.setEntityId("test");
		schJobDetail.setFileType("test");
		schJobDetail.setJobGroup("test");
		schJobDetail.setJobName("test");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		schJobDetail.setEndDate(calendar.getTime());
		schJobDetail.setJobScheduleTime(new Date());
		schJobDetail.setUserFullName("DDDD");
		schJobDetail.setChannelId("123");
		schJobDetail.setTimezone("UTC");

		
		scheduleKeys = new ScheduleKeys();
		scheduleKeys.setCategoryId("test");
		scheduleKeys.setGroupName("test");
		scheduleKeys.setJobName("test");
		
		
		jobKey = new JobKey("test", "test");
		
		bisJobService = Mockito.spy(BisJobServiceImpl.class);
		
	}
	
	private void mockScheduleJob() throws SchedulerException {
		SchedulerFactoryBean schedulerFactoryBean = Mockito.mock(SchedulerFactoryBean.class);
		bisJobService.schedulerFactoryBean = schedulerFactoryBean;
	    scheduler = Mockito.mock(Scheduler.class);
		Mockito.when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
	    factoryBean = new JobDetailFactoryBean();
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenReturn(new Date());
	}
	
	private void mockJob() throws SchedulerException  {
		
	    jobDetail =Mockito.mock(JobDetail.class);
	    jobDataMap = Mockito.mock(JobDataMap.class);
	    jobKey = new JobKey("test", "test");
	    
		Mockito.when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
		Mockito.when( jobDetail.getJobDataMap()).thenReturn(jobDataMap);
		
	}
	
	private void mockJobContext() throws SchedulerException  {
		List<JobExecutionContext> list = new ArrayList<JobExecutionContext>();
		 jobContext = Mockito.mock(JobExecutionContext.class);
		Mockito.when(jobContext.getJobDetail()).thenReturn(jobDetail);
		list.add(jobContext);
		Mockito.when(scheduler.getCurrentlyExecutingJobs()).thenReturn(list);
		Mockito.when(jobDetail.getKey()).thenReturn(jobKey);
	}
	
	private void mockTriggers() throws SchedulerException  {
		final List triggers = new ArrayList();
		Trigger trigger = Mockito.mock(Trigger.class);
		triggers.add(trigger);
		Mockito.when(trigger.getStartTime()).thenReturn(schJobDetail.getJobScheduleTime());
		Mockito.when(trigger.getNextFireTime()).thenReturn(schJobDetail.getEndDate());
		Mockito.when(trigger.getPreviousFireTime()).thenReturn(schJobDetail.getJobScheduleTime());
		
		Mockito.when(scheduler.getTriggersOfJob(jobKey)).thenReturn(triggers);
	}
	
	private void mockJobGroupNames() throws SchedulerException {
		Set<JobKey> set = new HashSet<JobKey>();
		set.add(jobKey);
		Mockito.when(scheduler.getJobKeys(Mockito.any())).thenReturn(set);
		List<String> list = new ArrayList<String>();
		list.add("test");
		Mockito.when(scheduler.getJobGroupNames()).thenReturn(list);
		Mockito.when(jobDataMap.get(JobUtil.JOB_DATA_MAP_ID)).thenReturn(schJobDetail);
	}
	 
	private void mockJobContextList() throws SchedulerException {
		List<JobExecutionContext> jobContextList = new ArrayList<JobExecutionContext>();
		jobContextList.add(jobContext);
		Mockito.when(scheduler.getCurrentlyExecutingJobs()).thenReturn(jobContextList);
		Mockito.when(jobDetail.getKey()).thenReturn(jobKey);
	}
	
	@Test
	public void testScheduleCronJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		Boolean value = bisJobService.scheduleCronJob(schJobDetail, BisCronJob.class);
		Assert.assertTrue(value);

	}
	
	@Test
	public void testScheduleOneTimeJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		Boolean value = bisJobService.scheduleOneTimeJob(schJobDetail, BisSimpleJob.class);
		Assert.assertTrue(value);

	}
	
	@Test
	public void testUpdateOneTimeJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		Boolean value = bisJobService.updateOneTimeJob(schJobDetail);
		Assert.assertTrue(value);

	}
	@Test
	public void testUpdateCronJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenReturn(new Date());
		Boolean value = bisJobService.updateCronJob(schJobDetail);
		Assert.assertTrue(value);

	}
	
	
	@Test
	public void testDeleteJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		Mockito.when(scheduler.deleteJob(jobKey)).thenReturn(true);
		Mockito.when(scheduler.scheduleJob(Mockito.any(), Mockito.any())).thenReturn(new Date());
		Boolean value = bisJobService.deleteJob(scheduleKeys);
		Assert.assertTrue(value);

	}
	
	@Test
	public void testPauseJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		Boolean value = bisJobService.pauseJob(scheduleKeys);
		Assert.assertTrue(value);

	}
	
	
	@Test
	public void testResumeJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		Boolean value = bisJobService.resumeJob(scheduleKeys);
		Assert.assertTrue(value);

	}
	
	
	@Test
	public void testStartJobNow() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		Boolean value = bisJobService.startJobNow(scheduleKeys);
		
		Assert.assertTrue(value);

	}
	
	@Test
	public void testIsJobRunning() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		mockJobContext();
		Boolean value = bisJobService.isJobRunning(scheduleKeys);
		Assert.assertTrue(value);

	}
	
	@Test
	public void testGetAllJobs() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		mockJobContext();
		mockJobGroupNames();
		mockJobContextList();
		mockTriggers();
		
		
		List<Map<String, Object>> value = bisJobService.getAllJobs("test","123");
		Assert.assertEquals(value.size(), 1);
		Map<String, Object> resultMap = value.get(0);
		Assert.assertEquals(resultMap.size(), 5);
		Assert.assertEquals(resultMap.get("jobStatus"), "RUNNING");
		Assert.assertEquals(resultMap.get("scheduleTime"), schJobDetail.getJobScheduleTime().getTime());
		Assert.assertEquals(resultMap.get("lastFiredTime"),schJobDetail.getJobScheduleTime().getTime());
		Assert.assertEquals(resultMap.get("nextFireTime"), schJobDetail.getEndDate().getTime());
	}
	
	
	
	@Test
	public void testGetJobDetails() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockJob();
		mockJobContext();
		mockJobGroupNames();
		mockJobContextList();
		mockTriggers();
		
		Map<String, Object> resultMap = bisJobService.getJobDetails(scheduleKeys);
		
		Assert.assertEquals(resultMap.size(), 5);
		Assert.assertEquals(resultMap.get("jobStatus"), "RUNNING");
		Assert.assertEquals(resultMap.get("scheduleTime"), schJobDetail.getJobScheduleTime().getTime());
		Assert.assertEquals(resultMap.get("lastFiredTime"),schJobDetail.getJobScheduleTime().getTime());
		Assert.assertEquals(resultMap.get("nextFireTime"), schJobDetail.getEndDate().getTime());
	}
	
	
	
	@Test
	public void testIsJobWithNamePresent() throws SchedulerException, ParseException{
		mockScheduleJob();
		Mockito.when(scheduler.checkExists(jobKey)).thenReturn(true);
		Boolean value = bisJobService.isJobWithNamePresent(scheduleKeys);
		Assert.assertTrue(value);

	}
	
	
	@Test
	public void testGetJobState() throws SchedulerException, ParseException{
		mockScheduleJob();
		mockTriggers();
		mockJob();
		mockJobContextList();
		
		JobDetail jobDetail =Mockito.mock(JobDetail.class);
		Mockito.when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
		Mockito.when(jobDetail.getKey()).thenReturn(jobKey);
		Mockito.when(scheduler.getTriggerState(Mockito.any())).thenReturn(TriggerState.NORMAL);
		
		String value = bisJobService.getJobState(scheduleKeys);
		Assert.assertEquals(value,"SCHEDULED");

	}
	
	
	@Test
	public void testStopJob() throws SchedulerException, ParseException{
		mockScheduleJob();
		Mockito.when(scheduler.interrupt(jobKey)).thenReturn(true);
		Boolean value = bisJobService.stopJob(scheduleKeys);
		Assert.assertTrue(value);

	}
}

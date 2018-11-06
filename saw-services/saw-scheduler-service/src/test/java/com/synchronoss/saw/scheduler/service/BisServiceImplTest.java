package com.synchronoss.saw.scheduler.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.synchronoss.saw.scheduler.SAWSchedulerServiceApplication;
import com.synchronoss.saw.scheduler.job.BisCronJob;
import com.synchronoss.saw.scheduler.modal.BISSchedulerJobDetails;

import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BisServiceImplTest.class)
@ContextConfiguration(classes = SAWSchedulerServiceApplication.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class BisServiceImplTest {

	@Autowired
	@Lazy
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private ApplicationContext context;

	@Autowired
	JobService<BISSchedulerJobDetails> bisJobService;

	@Value("${saw-analysis-service-url}")
	private String analysisUrl;

	@Ignore
	public void testScheduleOneTimeJob() {

		/*
		 * BISSchedulerJobDetails jobDetail = new BISSchedulerJobDetails();
		 * jobDetail.setChannelType("Test"); jobDetail.setCronExpression("");
		 * jobDetail.setDescription(""); List list = new ArrayList();
		 * list.add("test@test.com"); jobDetail.setEmailList(list);
		 * jobDetail.setEndDate(new Date("\"2018-11-30T06:06\""));
		 * jobDetail.setEntityId("test"); jobDetail.setFileType("test");
		 * jobDetail.setJobGroup("test"); jobDetail.setJobName("test");
		 * jobDetail.setJobScheduleTime(new Date("2018-11-29T05:06"));
		 * jobDetail.setUserFullName("DDDD");
		 */
	}

	@org.junit.Test
	public void testScheduleCronJob() {
		BISSchedulerJobDetails jobDetail = new BISSchedulerJobDetails();
		jobDetail.setChannelType("Test");
		jobDetail.setCronExpression("0 0 12 * * ?");
		jobDetail.setDescription("");
		List list = new ArrayList();
		list.add("test@test.com");
		jobDetail.setEmailList(list);
		try {
			jobDetail.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2018-11-30T06:06"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobDetail.setEntityId("test");
		jobDetail.setFileType("test");
		jobDetail.setJobGroup("test");
		jobDetail.setJobName("test");
		try {
			jobDetail.setJobScheduleTime(new SimpleDateFormat("yyyy-MM-dd").parse("2018-11-29T05:06"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobDetail.setUserFullName("DDDD");

		bisJobService.scheduleCronJob(jobDetail, BisCronJob.class);
	}

	/*
	 * @org.junit.Test public void testUpdateOneTimeJob() {
	 * fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testUpdateCronJob() {
	 * fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testUnScheduleJob() {
	 * fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testDeleteJob() { fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testPauseJob() { fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testResumeJob() { fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testStartJobNow() { fail("Not yet implemented");
	 * }
	 * 
	 * @org.junit.Test public void testIsJobRunning() { fail("Not yet implemented");
	 * }
	 * 
	 * @org.junit.Test public void testGetAllJobs() { fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testGetJobDetails() {
	 * fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testIsJobWithNamePresent() {
	 * fail("Not yet implemented"); }
	 * 
	 * @org.junit.Test public void testGetJobState() { fail("Not yet implemented");
	 * }
	 * 
	 * @org.junit.Test public void testStopJob() { fail("Not yet implemented"); }
	 */

}

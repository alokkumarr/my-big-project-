package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface JobService {

	boolean scheduleOneTimeJob(SchedulerJobDetail job, Class<? extends QuartzJobBean> jobClass);

	boolean scheduleCronJob(SchedulerJobDetail job, Class<? extends QuartzJobBean> jobClass);

    boolean updateOneTimeJob(String jobName, Date date);
	boolean updateCronJob(String jobName, Date date, String cronExpression);
	
	boolean unScheduleJob(String jobName);
	boolean deleteJob(String jobName);
	boolean pauseJob(String jobName);
	boolean resumeJob(String jobName);
	boolean startJobNow(String jobName);
	boolean isJobRunning(String jobName);
	List<Map<String, Object>> getAllJobs();

	Map<String, Object> getJobDetails(String jobName);

	boolean isJobWithNamePresent(String jobName);
	String getJobState(String jobName);
	boolean stopJob(String jobName);
}

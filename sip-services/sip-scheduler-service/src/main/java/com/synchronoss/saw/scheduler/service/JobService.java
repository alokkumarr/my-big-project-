package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.scheduler.modal.ScheduleKeys;

import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;
import java.util.Map;

public interface JobService<T> {

	boolean scheduleOneTimeJob(T job, Class<? extends QuartzJobBean> jobClass);

	boolean scheduleCronJob(T job, Class<? extends QuartzJobBean> jobClass);

    boolean updateOneTimeJob(T schedulerJobDetail);
	boolean updateCronJob(T schedulerJobDetail);
	
	boolean unScheduleJob(ScheduleKeys jobName);
	boolean deleteJob(ScheduleKeys scheduleKeys);
	boolean pauseJob(ScheduleKeys scheduleKeys);
	boolean resumeJob(ScheduleKeys scheduleKeys);
	boolean startJobNow(ScheduleKeys scheduleKeys);
	boolean isJobRunning(ScheduleKeys scheduleKeys);
	List<Map<String, Object>> getAllJobs(String groupkey , String categoryID);

	Map<String, Object> getJobDetails(ScheduleKeys scheduleKeys);

	boolean isJobWithNamePresent(ScheduleKeys scheduleKeys);

	String getJobState(ScheduleKeys scheduleKeys);
	boolean stopJob(ScheduleKeys scheduleKeys);
}

package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.scheduler.modal.FetchByCategoryBean;
import com.synchronoss.saw.scheduler.modal.ScheduleKeys;
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

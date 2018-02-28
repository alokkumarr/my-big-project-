package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.scheduler.modal.ScheduleKeys;
import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobServiceImpl implements JobService{

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

	@Autowired
	@Lazy
    SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private ApplicationContext context;

	/**
	 * Schedule a job by jobName at given date.
     * @param job
     * @param jobClass
     * @return
	 */
	@Override
	public boolean scheduleOneTimeJob(SchedulerJobDetail job, Class<? extends QuartzJobBean> jobClass) {
        logger.info("Request received to scheduleJob");

		String jobKey = job.getJobName();
		String groupKey = job.getJobGroup();
		String triggerKey = job.getJobName();

		JobDetail jobDetail = JobUtil.createJob(jobClass, false, context, job, groupKey);

		logger.debug("creating trigger for key :"+jobKey + " at date :"+job.getJobScheduleTime());
		Trigger cronTriggerBean = JobUtil.createSingleTrigger(triggerKey, job.getJobScheduleTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
            logger.debug("Job with key jobKey :"+jobKey+ " and group :"+groupKey+ " scheduled successfully for date :"+dt);
			return true;
		} catch (SchedulerException e) {
            logger.error("SchedulerException while scheduling job with key :"+jobKey + " message :"+e.getMessage());
		}

		return false;
	}
	
	/**
	 * Schedule a job by jobName at given date.
     * @param job
     * @param jobClass
     * @return
	 */

	@Override
	public boolean scheduleCronJob(SchedulerJobDetail job, Class<? extends QuartzJobBean> jobClass) {
		logger.info("Request received to scheduleJob");

		String jobKey = job.getJobName();
		String groupKey = job.getJobGroup();
		String triggerKey = job.getJobName();

		JobDetail jobDetail = JobUtil.createJob(jobClass, false, context, job, groupKey);

        logger.debug("creating trigger for key :"+jobKey + " at date :"+job.getJobScheduleTime());
		Trigger cronTriggerBean = JobUtil.createCronTrigger(triggerKey, job.getJobScheduleTime(),job.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
			logger.debug("Job with key jobKey :"+jobKey+ " and group :"+groupKey+ " scheduled successfully for date :"+dt);
			return true;
		} catch (SchedulerException e) {
            logger.error("SchedulerException while scheduling job with key :"+jobKey + " message :"+e.getMessage());
		}

		return false;
	}

	/**
	 * Update one time scheduled job.
     * @param jobName
     * @param date
     * @return
	 */

	@Override
	public boolean updateOneTimeJob(String jobName, Date date) {
        logger.info("Request received for updating one time job.");

		String jobKey = jobName;

        logger.debug("Parameters received for updating one time job : jobKey :"+jobKey + ", date: "+date);
		try {
			//Trigger newTrigger = JobUtil.createSingleTrigger(jobKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
			Trigger newTrigger = JobUtil.createSingleTrigger(jobKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

			Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
            logger.debug("Trigger associated with jobKey :"+jobKey+ " rescheduled successfully for date :"+dt);
			return true;
		} catch ( Exception e ) {
            logger.error("SchedulerException while updating one time job with key :"+jobKey + " message :"+e.getMessage());
			return false;
		}
	}

	/**
	 * Update scheduled cron job.
     * @param jobName
     * @param date
     * @param cronExpression
     * @return
	 */
	@Override
	public boolean updateCronJob(String jobName, Date date, String cronExpression) {
        logger.info("Request received for updating cron job.");

		String jobKey = jobName;

        logger.debug("Parameters received for updating cron job : jobKey :"+jobKey + ", date: "+date);
		try {
			Trigger newTrigger = JobUtil.createCronTrigger(jobKey, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

			Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
            logger.debug("Trigger associated with jobKey :"+jobKey+ " rescheduled successfully for date :"+dt);
			return true;
		} catch ( Exception e ) {
            logger.error("SchedulerException while updating cron job with key :"+jobKey + " message :"+e.getMessage());

			return false;
		}
	}
	
	/**
	 * Remove the indicated Trigger from the scheduler. 
	 * If the related job does not have any other triggers, and the job is not durable, then the job will also be deleted.
     * @param scheduleKeys
     * @return
	 */
	@Override
	public boolean unScheduleJob(ScheduleKeys scheduleKeys) {
		logger.info("Request received for Unscheduleding job.");

		String jobKey = scheduleKeys.getJobName();

		TriggerKey tkey = new TriggerKey(jobKey);
		logger.debug("Parameters received for unscheduling job : tkey :"+jobKey);
		try {
			boolean status = schedulerFactoryBean.getScheduler().unscheduleJob(tkey);
			logger.debug("Trigger associated with jobKey :"+jobKey+ " unscheduled with status :"+status);
			return status;
		} catch (SchedulerException e) {
			logger.error("SchedulerException while unscheduling job with key :"+jobKey + " message :"+e.getMessage());
			return false;
		}
	}

	/**
	 * Delete the identified Job from the Scheduler - and any associated Triggers.
     * @param scheduleKeys
     * @return
	 */

	@Override
	public boolean deleteJob(ScheduleKeys scheduleKeys) {
		logger.info("Request received for deleting job.");

		String jobKey = scheduleKeys.getJobName();
		String groupKey = scheduleKeys.getGroupName();

		JobKey jkey = new JobKey(jobKey, groupKey); 
		logger.debug("Parameters received for deleting job : jobKey :"+jobKey);

		try {
			boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
            logger.debug("Job with jobKey :"+jobKey+ " deleted with status :"+status);
			return status;
		} catch (SchedulerException e) {
			logger.error("SchedulerException while deleting job with key :"+jobKey + " message :"+e.getMessage());
			return false;
		}
	}

	/**
	 * Pause a job
     * @param scheduleKeys
     * @return
	 */
	@Override
	public boolean pauseJob(ScheduleKeys scheduleKeys) {
        logger.info("Request received for pausing job.");

		String jobKey = scheduleKeys.getJobName();
		String groupKey =scheduleKeys.getGroupName();
		JobKey jkey = new JobKey(jobKey, groupKey); 
		logger.debug("Parameters received for pausing job : jobKey :"+jobKey+ ", groupKey :"+groupKey);

		try {
			schedulerFactoryBean.getScheduler().pauseJob(jkey);
			logger.debug("Job with jobKey :"+jobKey+ " paused succesfully.");
			return true;
		} catch (SchedulerException e) {
            logger.error("SchedulerException while pausing job with key :"+ scheduleKeys + " message :"+e.getMessage());
			return false;
		}
	}

	/**
	 * Resume paused job
     * @param scheduleKeys
     * @return
	 */
	@Override
	public boolean resumeJob(ScheduleKeys scheduleKeys) {
		logger.info("Request received for resuming job.");

		String jobKey = scheduleKeys.getJobName();
		String groupKey = scheduleKeys.getGroupName();

		JobKey jKey = new JobKey(jobKey, groupKey);
        logger.debug("Parameters received for resuming job : jobKey :"+jobKey);
		try {
			schedulerFactoryBean.getScheduler().resumeJob(jKey);
            logger.debug("Job with jobKey :"+jobKey+ " resumed succesfully.");
			return true;
		} catch (SchedulerException e) {
			logger.error("SchedulerException while resuming job with key :"+jobKey+ " message :"+e.getMessage());
			return false;
		}
	}

	/**
	 * Start a job now
     * @param scheduleKeys
     * @return
	 */
	@Override
	public boolean startJobNow(ScheduleKeys scheduleKeys) {
        logger.info("Request received for starting job now.");

		String jobKey = scheduleKeys.getJobName();
		String groupKey = scheduleKeys.getGroupName();

		JobKey jKey = new JobKey(jobKey, groupKey);
        logger.debug("Parameters received for starting job now : jobKey :"+jobKey);
		try {
			schedulerFactoryBean.getScheduler().triggerJob(jKey);
            logger.debug("Job with jobKey :"+jobKey+ " started now succesfully.");
			return true;
		} catch (SchedulerException e) {
            logger.error("SchedulerException while starting job now with key :"+jobKey+ " message :"+e.getMessage());
			return false;
		}		
	}

	/**
	 * Check if job is already running
     * @param scheduleKeys
     * @return
	 */
	@Override
	public boolean isJobRunning(ScheduleKeys scheduleKeys) {
        logger.info("Request received to check if job is running");

		String jobKey = scheduleKeys.getJobName();
		String groupKey = scheduleKeys.getGroupName();

        logger.debug("Parameters received for checking job is running now : jobKey :"+jobKey);
		try {

			List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
			if(currentJobs!=null){
				for (JobExecutionContext jobCtx : currentJobs) {
					String jobNameDB = jobCtx.getJobDetail().getKey().getName();
					String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
					if (jobKey.equalsIgnoreCase(jobNameDB) && groupKey.equalsIgnoreCase(groupNameDB)) {
						return true;
					}
				}
			}
		} catch (SchedulerException e) {
            logger.error("SchedulerException while checking job with key :"+jobKey+ " is running. error message :"+e.getMessage());
			return false;
		}
		return false;
	}

	/**
	 * Get all jobs
     * @return
	 * @param groupkey
	 * @param categoryID
	 */
	@Override
	public List<Map<String, Object>> getAllJobs(String groupkey , String categoryID) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			for (String groupName : scheduler.getJobGroupNames()) {
				if (groupName.equalsIgnoreCase(groupkey)) {
					for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
						String jobName = jobKey.getName();
						JobDetail jobDetail = scheduler.getJobDetail(jobKey);
						SchedulerJobDetail job = (SchedulerJobDetail) jobDetail.getJobDataMap().get(JobUtil.JOB_DATA_MAP_ID);
						if (job.getCategoryID().equalsIgnoreCase(categoryID)) {
							//get job's trigger
							List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
							Date scheduleTime = triggers.get(0).getStartTime();
							Date nextFireTime = triggers.get(0).getNextFireTime();
							Date lastFiredTime = triggers.get(0).getPreviousFireTime();


							Map<String, Object> map = new HashMap<String, Object>();
							map.put("jobDetails", job);
							map.put("scheduleTime", scheduleTime);
							map.put("lastFiredTime", lastFiredTime);
							map.put("nextFireTime", nextFireTime);
							ScheduleKeys scheduleKeys = new ScheduleKeys();
							scheduleKeys.setJobName(jobName);
							scheduleKeys.setGroupName(groupName);

							if (isJobRunning(scheduleKeys)) {
								map.put("jobStatus", "RUNNING");
							} else {
								String jobState = getJobState(scheduleKeys);
								map.put("jobStatus", jobState);
							}

							list.add(map);
							logger.info("Job details:");
							logger.debug("Job Name:" + jobName + ", Group Name:" + groupName + ", Schedule Time:" + scheduleTime);
						}
					}
				}
			}
		} catch (SchedulerException e) {
            logger.error("SchedulerException while fetching all jobs. error message :"+e.getMessage());

		}
		return list;
	}


	/**
	 * Get all jobs
	 * @return
	 * @param scheduleKeys
	 */
	@Override
	public Map<String, Object> getJobDetails(ScheduleKeys scheduleKeys) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			for (String groupName : scheduler.getJobGroupNames()) {
				if (groupName.equalsIgnoreCase(scheduleKeys.getGroupName())){
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					if (scheduleKeys.getJobName().equalsIgnoreCase(jobKey.getName())) {
						JobDetail jobDetail = scheduler.getJobDetail(jobKey);
						SchedulerJobDetail job = (SchedulerJobDetail) jobDetail.getJobDataMap().get(JobUtil.JOB_DATA_MAP_ID);
						//get job's trigger
						if (job.getCategoryID().equalsIgnoreCase(scheduleKeys.getCategoryId())) {
							List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
							Date scheduleTime = triggers.get(0).getStartTime();
							Date nextFireTime = triggers.get(0).getNextFireTime();
							Date lastFiredTime = triggers.get(0).getPreviousFireTime();

							map.put("jobDetails", job);
							map.put("scheduleTime", scheduleTime);
							map.put("lastFiredTime", lastFiredTime);
							map.put("nextFireTime", nextFireTime);

							if (isJobRunning(scheduleKeys)) {
								map.put("jobStatus", "RUNNING");
							} else {
								String jobState = getJobState(scheduleKeys);
								map.put("jobStatus", jobState);
							}
							logger.info("Job details:");
							logger.debug("Job Name:" + scheduleKeys + ", Group Name:" + groupName + ", Schedule Time:" + scheduleTime);
						}
					}
				}
				}
			}
		} catch (SchedulerException e) {
			logger.error("SchedulerException while fetching jobs details. error message :"+e.getMessage());

		}
		return map;
	}

	/**
	 * Check job exist with given name
     * @param scheduleKeys
     * @return
	 */
	@Override
	public boolean isJobWithNamePresent(ScheduleKeys scheduleKeys) {
		try {
			String groupKey = scheduleKeys.getGroupName();
			JobKey jobKey = new JobKey(scheduleKeys.getJobName(), groupKey);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(jobKey)){
				return true;
			}
		} catch (SchedulerException e) {
			logger.error("SchedulerException while checking job with name and group exist:"+e.getMessage());
		}
		return false;
	}

	/**
	 * Get the current state of job
     * @param scheduleKeys
     * @return
	 */
	public String getJobState(ScheduleKeys scheduleKeys) {
        logger.info("JobServiceImpl.getJobState()");

		try {
			String groupKey = scheduleKeys.getGroupName();
			JobKey jobKey = new JobKey(scheduleKeys.getJobName(), groupKey);

			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);

			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
			if(triggers != null && triggers.size() > 0){
				for (Trigger trigger : triggers) {
					TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

					if (TriggerState.PAUSED.equals(triggerState)) {
						return "PAUSED";
					}else if (TriggerState.BLOCKED.equals(triggerState)) {
						return "BLOCKED";
					}else if (TriggerState.COMPLETE.equals(triggerState)) {
						return "COMPLETE";
					}else if (TriggerState.ERROR.equals(triggerState)) {
						return "ERROR";
					}else if (TriggerState.NONE.equals(triggerState)) {
						return "NONE";
					}else if (TriggerState.NORMAL.equals(triggerState)) {
						return "SCHEDULED";
					}
				}
			}
		} catch (SchedulerException e) {
            logger.error("SchedulerException while checking job with name and group exist:"+e.getMessage());
		}
		return null;
	}

	/**
	 * Stop a job
     * @param scheduleKeys
     * @return
	 */
	@Override
	public boolean stopJob(ScheduleKeys scheduleKeys) {
        logger.info("JobServiceImpl.stopJob()");
		try{	
			String jobKey = scheduleKeys.getJobName();
			String groupKey = scheduleKeys.getGroupName();

			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jkey = new JobKey(jobKey, groupKey);

			return scheduler.interrupt(jkey);

		} catch (SchedulerException e) {
            logger.error("SchedulerException while stopping job. error message :"+e.getMessage());
		}
		return false;
	}
}


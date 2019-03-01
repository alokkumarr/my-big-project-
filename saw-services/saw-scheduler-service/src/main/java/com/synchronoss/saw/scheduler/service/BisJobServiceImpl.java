package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.scheduler.entities.QrtzTriggers;
import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;
import com.synchronoss.saw.scheduler.modal.ScheduleKeys;
import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import com.synchronoss.saw.scheduler.repository.QuartzRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;






@Service
public class BisJobServiceImpl implements JobService<BisSchedulerJobDetails> {

  private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

  @Autowired
  @Lazy
  SchedulerFactoryBean schedulerFactoryBean;

  @Autowired
  private ApplicationContext context;
  
  
  @Autowired
  QuartzRepository quartzRepository;

  /**
   * Schedule a job by jobName at given date.
   * 
   * @param job job details
   * @param jobClass class of job
   * @return
   */
  @Override
  public boolean scheduleOneTimeJob(BisSchedulerJobDetails job,
      Class<? extends QuartzJobBean> jobClass) {
    logger.info("Request received to scheduleJob");

    String jobKey = job.getJobName();
    String groupKey = job.getJobGroup();
    String triggerKey = job.getJobName();

    JobDetail jobDetail = JobUtil.createBatchIngestionJob(jobClass, false, context, job, groupKey);

    logger.info("creating trigger for key :" + jobKey + " at date :" + job.getJobScheduleTime());
    Trigger cronTriggerBean = JobUtil.createSingleTrigger(triggerKey, job.getJobScheduleTime(),
        SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
      logger.info("Job with key jobKey :" + jobKey + " and group :" + groupKey
          + " scheduled successfully for date :" + dt);
      return true;
    } catch (SchedulerException e) {
      logger.error("SchedulerException while scheduling job with key :" + jobKey + " message :"
          + e.getMessage());
    }

    return false;
  }

  /**
   * Schedule a job by jobName at given date.
   * 
   * @param job job details
   * @param jobClass class of job
   * @return scheduled or not
   */

  @Override
  public boolean scheduleCronJob(BisSchedulerJobDetails job,
      Class<? extends QuartzJobBean> jobClass) {
    logger.info("Request received to scheduleJob");

    String jobKey = job.getJobName();
    String groupKey = job.getJobGroup();

    String timezone = job.getTimezone();
    logger.trace("Timezone :" + timezone);

    JobDetail jobDetail = JobUtil.createBatchIngestionJob(jobClass, false, context, job, groupKey);

    logger.info("creating trigger for key :" + jobKey + " at date :" + job.getJobScheduleTime());
    Trigger cronTriggerBean = JobUtil.createCronTrigger(jobKey, job.getJobScheduleTime(),
        job.getEndDate(), job.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW,
        timezone);

    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
      logger.info("Job with key jobKey :" + jobKey + " and group :" + groupKey
          + " scheduled successfully for date :" + dt);
      return true;
    } catch (SchedulerException e) {
      logger.error("SchedulerException while scheduling job with key :" + jobKey + " message :"
          + e.getMessage());
    }

    return false;
  }

  /**
   * Update one time scheduled job.
   * 
   * @param schedulerJobDetail schedule details
   * @return updated or not
   */

  @Override
  public boolean updateOneTimeJob(BisSchedulerJobDetails schedulerJobDetail) {
    logger.info("Request received for updating one time job.");

    String jobName = schedulerJobDetail.getJobName();
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    JobKey jobKey = new JobKey(jobName, schedulerJobDetail.getJobGroup());

    logger.info("Parameters received for updating one time job : jobKey :" + jobKey + ", date: "
        + schedulerJobDetail.getJobScheduleTime());
    try {
      Trigger newTrigger = JobUtil.createSingleTrigger(jobName,
          schedulerJobDetail.getJobScheduleTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);
      jobDetail.getJobDataMap().replace(JobUtil.JOB_DATA_MAP_ID, schedulerJobDetail);
      scheduler.addJob(jobDetail, true, true);
      Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobName),
          newTrigger);
      logger.info("Trigger associated with jobKey :" + jobName
          + " rescheduled successfully for date :" + dt);
      return true;
    } catch (Exception e) {
      logger.error("SchedulerException while updating one time job with key :" + jobKey
          + " message :" + e.getMessage());
      return false;
    }
  }

  /**
   * Update scheduled cron job.
   * 
   * @param schedulerJobDetail scheduler details
   * @return updated or not
   */
  @Override
  public boolean updateCronJob(BisSchedulerJobDetails schedulerJobDetail) {
    logger.info("Request received for updating cron job.");

    String jobName = schedulerJobDetail.getJobName();
    Scheduler scheduler = schedulerFactoryBean.getScheduler();

    String timezone = schedulerJobDetail.getTimezone();
    JobKey jobKey = new JobKey(jobName, schedulerJobDetail.getJobGroup());
    logger.info("Parameters received for updating cron job : jobKey :" + jobKey + ", date: "
        + schedulerJobDetail.getJobScheduleTime());
    try {
      Trigger newTrigger = JobUtil.createCronTrigger(jobName,
          schedulerJobDetail.getJobScheduleTime(), schedulerJobDetail.getEndDate(),
          schedulerJobDetail.getCronExpression(),
          SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, timezone);
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);
      jobDetail.getJobDataMap().replace(JobUtil.JOB_DATA_MAP_ID, schedulerJobDetail);
      scheduler.addJob(jobDetail, true, true);
      Date dt = scheduler.rescheduleJob(TriggerKey.triggerKey(jobName), newTrigger);
      logger.info("Trigger associated with jobKey :" + jobName
          + " rescheduled successfully for date :" + dt);
      return true;
    } catch (Exception e) {
      logger.error("SchedulerException while updating cron job with key :" + jobKey + " message :"
          + e.getMessage());
      return false;
    }
  }

  /**
   * Remove the indicated Trigger from the scheduler. If the related job does not have any other
   * triggers, and the job is not durable, then the job will also be deleted.
   * 
   * @param scheduleKeys schedule key..etc
   * @return unscheduled or not
   */
  @Override
  public boolean unScheduleJob(ScheduleKeys scheduleKeys) {
    logger.info("Request received for Unscheduleding job.");

    String jobKey = scheduleKeys.getJobName();

    TriggerKey tkey = new TriggerKey(jobKey);
    logger.info("Parameters received for unscheduling job : tkey :" + jobKey);
    try {
      boolean status = schedulerFactoryBean.getScheduler().unscheduleJob(tkey);
      logger.info(
          "Trigger associated with jobKey :" + jobKey + " unscheduled with status :" + status);
      return status;
    } catch (SchedulerException e) {
      logger.error("SchedulerException while unscheduling job with key :" + jobKey + " message :"
          + e.getMessage());
      return false;
    }
  }

  /**
   * Delete the identified Job from the Scheduler - and any associated Triggers.
   * 
   * @param scheduleKeys schedule key..etc
   * @return deleted or not
   */

  @Override
  public boolean deleteJob(ScheduleKeys scheduleKeys) {
    logger.info("Request received for deleting job.");

    String jobKey = scheduleKeys.getJobName();
    String groupKey = scheduleKeys.getGroupName();

    JobKey jkey = new JobKey(jobKey, groupKey);
    logger.info("Parameters received for deleting job : jobKey :" + jobKey);

    try {
      boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
      logger.info("Job with jobKey :" + jobKey + " deleted with status :" + status);
      return status;
    } catch (SchedulerException e) {
      logger.error("SchedulerException while deleting job with key :" + jobKey + " message :"
          + e.getMessage());
      return false;
    }
  }

  /**
   * Pause a job.
   * 
   * @param scheduleKeys schedule key..etc
   * @return paused or not
   */
  @Override
  public boolean pauseJob(ScheduleKeys scheduleKeys) {
    logger.info("Request received for pausing job.");

    String jobKey = scheduleKeys.getJobName();
    String groupKey = scheduleKeys.getGroupName();
    JobKey jkey = new JobKey(jobKey, groupKey);
    logger.info(
        "Parameters received for pausing job : jobKey :" + jobKey + ", groupKey :" + groupKey);

    try {
      schedulerFactoryBean.getScheduler().pauseJob(jkey);
      
      
      /**
      * Below lines are added to avoid quartz misfire during
      * resume. Currently no way to avoid misfire
      * through quartz api for one schedule. 
      */
      QrtzTriggers cronTriggers = this.quartzRepository.findByJobName(jobKey);
      cronTriggers.setNextFireTime(-1);
      this.quartzRepository.save(cronTriggers);
      
      
      logger.info("Job with jobKey :" + jobKey + " paused succesfully.");
      return true;
    } catch (SchedulerException e) {
      logger.error("SchedulerException while pausing job with key :" + scheduleKeys + " message :"
          + e.getMessage());
      return false;
    }
  }

  /**
   * Resume paused job.
   * 
   * @param scheduleKeys @return‰
   */
  @Override
  public boolean resumeJob(ScheduleKeys scheduleKeys) {
    logger.info("Request received for resuming job.");

    String jobKey = scheduleKeys.getJobName();
    String groupKey = scheduleKeys.getGroupName();

    JobKey key = new JobKey(jobKey, groupKey);
    logger.info("Parameters received for resuming job : jobKey :" + jobKey);
    try {
      TriggerKey triggeKey = TriggerKey.triggerKey(jobKey);

      logger.info("Trigger key:::" + triggeKey);
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggeKey);
     
      
      /**
      * Below lines are added to avoid misfire during
      * resume for a paused trigger. If any approach
      * with api is found in future this can be replaced.
      */
      QrtzTriggers cronTriggers = this.quartzRepository.findByJobName(jobKey);
      cronTriggers.setNextFireTime(trigger.getFireTimeAfter(new Date()).getTime());
      this.quartzRepository.save(cronTriggers);
      
      
      schedulerFactoryBean.getScheduler().resumeJob(key);
      logger.info("Next fire time after now::: " + trigger.getFireTimeAfter(new Date()));
      logger.info("Job with jobKey :" + jobKey + " resumed succesfully.");
      return true;
    } catch (SchedulerException e) {
      logger.error("SchedulerException while resuming job with key :" + jobKey + " message :"
          + e.getMessage());
      return false;
    }
  }

  /**
   * Start a job now.
   * 
   * @param scheduleKeys schedule key..etc
   * @return started job or not
   */
  @Override
  public boolean startJobNow(ScheduleKeys scheduleKeys) {
    logger.info("Request received for starting job now.");

    String jobKey = scheduleKeys.getJobName();
    String groupKey = scheduleKeys.getGroupName();

    JobKey key = new JobKey(jobKey, groupKey);
    logger.info("Parameters received for starting job now : jobKey :" + jobKey);
    try {
      schedulerFactoryBean.getScheduler().triggerJob(key);
      logger.info("Job with jobKey :" + jobKey + " started now succesfully.");
      return true;
    } catch (SchedulerException e) {
      logger.error("SchedulerException while starting job now with key :" + jobKey + " message :"
          + e.getMessage());
      return false;
    }
  }

  /**
   * Check if job is already running.
   * 
   * @param scheduleKeys job name, group name..etc
   * @return is job running or not
   */
  @Override
  public boolean isJobRunning(ScheduleKeys scheduleKeys) {
    logger.info("Request received to check if job is running");

    String jobKey = scheduleKeys.getJobName();
    String groupKey = scheduleKeys.getGroupName();

    logger.info("Parameters received for checking job is running now : jobKey :" + jobKey);
    try {

      List<JobExecutionContext> currentJobs =
          schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
      if (currentJobs != null) {
        for (JobExecutionContext jobCtx : currentJobs) {
          String jobNameDb = jobCtx.getJobDetail().getKey().getName();
          String groupNameDb = jobCtx.getJobDetail().getKey().getGroup();
          if (jobKey.equalsIgnoreCase(jobNameDb) && groupKey.equalsIgnoreCase(groupNameDb)) {
            return true;
          }
        }
      }
    } catch (SchedulerException e) {
      logger.error("SchedulerException while checking job with key :" + jobKey
          + " is running. error message :" + e.getMessage());
      return false;
    }
    return false;
  }

  /**
   * Retrive all jobs.
   */
  @Override
  public List<Map<String, Object>> getAllJobs(String groupkey, String channelId) { 
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();

      for (String groupName : scheduler.getJobGroupNames()) {
        if (groupName.equalsIgnoreCase(groupkey)) {
          for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
            String jobName = jobKey.getName();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            BisSchedulerJobDetails job =
                (BisSchedulerJobDetails) jobDetail.getJobDataMap().get(JobUtil.JOB_DATA_MAP_ID);
              
            if (job.getChannelId().equalsIgnoreCase(channelId) 
                && !(job.getCronExpression() == null 
                || job.getCronExpression().trim().equals(""))) {
              // get job's trigger
              List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
              Map<String, Object> map = new HashMap<String, Object>();
              map.put("jobDetails", job);
              Date scheduleTime = triggers.get(0).getStartTime();
              Date nextFireTime = triggers.get(0).getNextFireTime();
              Date lastFiredTime = triggers.get(0).getPreviousFireTime();
              map.put("scheduleTime", scheduleTime.getTime());
              map.put("lastFiredTime", lastFiredTime != null ? lastFiredTime.getTime() : -1);
              long nextFt = (nextFireTime == null) ? -1 : nextFireTime.getTime();
              map.put("nextFireTime", nextFt);
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
              logger.info("Job Name:" + jobName + ", Group Name:" + groupName + ", Schedule Time:"
                  + scheduleTime + " nextFireTime :" + nextFireTime + ": lastFiredTime :"
                  + lastFiredTime);
            }
          }
        }
      }
    } catch (SchedulerException e) {
      logger.error("SchedulerException while fetching all jobs. error message :" + e.getMessage());

    }
    return list;
  }

  /**
   * Retrieve job details.
   */

  @Override
  public Map<String, Object> getJobDetails(ScheduleKeys scheduleKeys) {
    Map<String, Object> map = new HashMap<String, Object>();
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();

      for (String groupName : scheduler.getJobGroupNames()) {
        if (groupName.equalsIgnoreCase(scheduleKeys.getGroupName())) {
          for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
            if (scheduleKeys.getJobName().equalsIgnoreCase(jobKey.getName())) {
              JobDetail jobDetail = scheduler.getJobDetail(jobKey);
              BisSchedulerJobDetails job =
                  (BisSchedulerJobDetails) jobDetail.getJobDataMap().get(JobUtil.JOB_DATA_MAP_ID);
              // get job's trigger
              if (job.getChannelType().equalsIgnoreCase(scheduleKeys.getCategoryId())) {
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                logger.trace("Actual nextFireTime :" + triggers.get(0).getNextFireTime().getTime());
                logger.trace(
                    "Actual lastFiredTime :" + triggers.get(0).getPreviousFireTime().getTime());
                map.put("jobDetails", job);
                Date scheduleTime = triggers.get(0).getStartTime();
                Date nextFireTime = triggers.get(0).getNextFireTime();
                Date lastFiredTime = triggers.get(0).getPreviousFireTime();
                map.put("scheduleTime", scheduleTime.getTime());
                map.put("lastFiredTime", lastFiredTime != null ? lastFiredTime.getTime() : -1);
                map.put("nextFireTime", nextFireTime.getTime());

                if (isJobRunning(scheduleKeys)) {
                  map.put("jobStatus", "RUNNING");
                } else {
                  String jobState = getJobState(scheduleKeys);
                  map.put("jobStatus", jobState);
                }
                logger.info("Job details:");
                logger.info("Job Name:" + scheduleKeys + ", Group Name:" + groupName
                    + ", Schedule Time:" + scheduleTime);
              }
            }
          }
        }
      }
    } catch (SchedulerException e) {
      logger.error(
          "SchedulerException while fetching jobs details. error message :" + e.getMessage());

    }
    return map;
  }

  /**
   * Check job exist with given name.
   * 
   * @param scheduleKeys job name, group name..etc
   * @return is job with name present or not
   */
  @Override
  public boolean isJobWithNamePresent(ScheduleKeys scheduleKeys) {
    try {
      String groupKey = scheduleKeys.getGroupName();
      JobKey jobKey = new JobKey(scheduleKeys.getJobName(), groupKey);
     
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      logger.info("Checking job with details" + jobKey + " exists?:  " 
              + scheduler.checkExists(jobKey));
      if (scheduler.checkExists(jobKey)) {
        return true;
      }
    } catch (SchedulerException e) {
      logger.error(
          "SchedulerException while checking job with name and group exist:" + e.getMessage());
    }
    return false;
  }

  /**
   * Get the current state of job.
   * 
   * @param scheduleKeys job name, group name..etc
   * @return state of job such as scheduled, paused..etc
   */
  public String getJobState(ScheduleKeys scheduleKeys) {
    logger.info("JobServiceImpl.getJobState()");

    try {
      String groupKey = scheduleKeys.getGroupName();
      JobKey jobKey = new JobKey(scheduleKeys.getJobName(), groupKey);

      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);

      List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
      if (triggers != null && triggers.size() > 0) {
        for (Trigger trigger : triggers) {
          TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

          if (TriggerState.PAUSED.equals(triggerState)) {
            return "PAUSED";
          } else if (TriggerState.BLOCKED.equals(triggerState)) {
            return "BLOCKED";
          } else if (TriggerState.COMPLETE.equals(triggerState)) {
            return "COMPLETE";
          } else if (TriggerState.ERROR.equals(triggerState)) {
            return "ERROR";
          } else if (TriggerState.NONE.equals(triggerState)) {
            return "NONE";
          } else if (TriggerState.NORMAL.equals(triggerState)) {
            return "SCHEDULED";
          }
        }
      }
    } catch (SchedulerException e) {
      logger.error(
          "SchedulerException while checking job with name and group exist:" + e.getMessage());
    }
    return null;
  }

  /**
   * Stop a job.
   * 
   * @param scheduleKeys job name, group name..etc
   * @return job is stopped or not
   */
  @Override
  public boolean stopJob(ScheduleKeys scheduleKeys) {
    logger.info("JobServiceImpl.stopJob()");
    try {
      String jobKey = scheduleKeys.getJobName();
      String groupKey = scheduleKeys.getGroupName();

      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      JobKey jkey = new JobKey(jobKey, groupKey);

      return scheduler.interrupt(jkey);

    } catch (SchedulerException e) {
      logger.error("SchedulerException while stopping job. error message :" + e.getMessage());
    }
    return false;
  }

  /*
   *
   * @param startDate starting date of job
   * @param endDate ending date of job
   * @return job is active or not
   */
  private static boolean isActiveSchedule(Date startDate, Date endDate) {
    Date date = new Date();
    if (endDate != null) {
      if (startDate.compareTo(date) <= 0 && endDate.compareTo(date) >= 0) {
        return true;
      } else {
        return false;
      }
        
    } else {
      // In case their are no end date the then schedule will be always
      // active.
      return true;
    }
  }
}

package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.scheduler.config.PersistableCronTriggerFactoryBean;
import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;
import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class JobUtil {

    private static Logger logger = LoggerFactory.getLogger(JobUtil.class);
    protected final static String JOB_DATA_MAP_ID="JOB_DATA_MAP";

    private final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	/**
	 * Create Quartz Job.
	 * 
	 * @param jobClass Class whose executeInternal() method needs to be called. 
	 * @param isDurable Job needs to be persisted even after completion. if true, job will be persisted, not otherwise. 
	 * @param context Spring application context.
	 * @param job job data.
	 * @param jobGroup Job group.
	 * 
	 * @return JobDetail object
	 */
	protected static JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable,
                                         ApplicationContext context, SchedulerJobDetail job, String jobGroup){
	    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
	    factoryBean.setJobClass(jobClass);
	    factoryBean.setDurability(isDurable);
	    factoryBean.setApplicationContext(context);
	    factoryBean.setName(job.getJobName());
	    factoryBean.setGroup(jobGroup);
        logger.debug(job.toString());

	    // set job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_DATA_MAP_ID,job);
        factoryBean.setJobDataMap(jobDataMap);
        factoryBean.afterPropertiesSet();
        
	    return factoryBean.getObject();
	}
	
	
	
	/**
     * Create Quartz Job.
     * 
     * @param jobClass Class whose executeInternal() method needs to be called. 
     * @param isDurable Job needs to be persisted even after completion. if true, job will be persisted, not otherwise. 
     * @param context Spring application context.
     * @param job job data.
     * @param jobGroup Job group.
     * 
     * @return JobDetail object
     */
    protected static JobDetail createBatchIngestionJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable,
                                         ApplicationContext context, BisSchedulerJobDetails job, String jobGroup){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(job.getJobName());
        factoryBean.setGroup(jobGroup);
        
        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_DATA_MAP_ID,job);
        factoryBean.setJobDataMap(jobDataMap);
        factoryBean.afterPropertiesSet();
        
        return factoryBean.getObject();
    }


	/**
	 * Create cron trigger. 
	 * 
	 * @param triggerName Trigger name.
	 * @param startTime Trigger start time.
	 * @param cronExpression Cron expression.
	 * @param misFireInstruction Misfire instruction (what to do in case of misfire happens).
     * @param timezone Timezone value in simple format. (E.g.: PST, JST)
	 *  
	 * @return Trigger
	 */
	protected static Trigger createCronTrigger(String triggerName, Date startTime, Date endTime,
                                               String cronExpression, int misFireInstruction,
                                               String timezone){
		PersistableCronTriggerFactoryBean factoryBean = new PersistableCronTriggerFactoryBean();
	    factoryBean.setName(triggerName);
	    factoryBean.setStartTime(startTime);
	    factoryBean.setCronExpression(cronExpression);
	    factoryBean.setMisfireInstruction(misFireInstruction);

        logger.debug("Cron Expression = " + cronExpression);

        logger.debug("TimeZone = " + timezone);

	    TimeZone t = TimeZone.getTimeZone(timezone);

        factoryBean.setTimeZone(t);

        try {
	        factoryBean.afterPropertiesSet();
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    if(endTime != null)
	        return factoryBean.getObject().getTriggerBuilder().endAt(endTime).build();
	    else
	        return factoryBean.getObject();
	}
	
	/**
	 * Create a Single trigger.
	 * 
	 * @param triggerName Trigger name.
	 * @param startTime Trigger start time.
	 * @param misFireInstruction Misfire instruction (what to do in case of misfire happens).
	 * 
	 * @return Trigger
	 */
	protected static Trigger createSingleTrigger(String triggerName, Date startTime, int misFireInstruction){
		SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
	    factoryBean.setName(triggerName);
	    factoryBean.setStartTime(startTime);
	    factoryBean.setMisfireInstruction(misFireInstruction);
	    factoryBean.setRepeatCount(0);
	    factoryBean.afterPropertiesSet();
	    return factoryBean.getObject();
	}
}

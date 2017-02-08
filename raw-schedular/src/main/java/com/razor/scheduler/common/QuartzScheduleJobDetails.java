/**
 * 
 */
package com.razor.scheduler.common;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.SimpleTrigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.razor.raw.logging.LogManager;


/**
 * 
 * @author surendra.rajaneni
 *
 * @param <T>
 */
public class QuartzScheduleJobDetails<T> implements QuartzJobRS {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130473824586296357L;
	private T t;
	private String quartzCronExpression;
	private String jobID;
	private String jobName;
	private String jobGroup;
	private Class<? extends Job> jobclassName;
	//private Object otherRequiredInfo;
	private Date simpleTriggerDate;
	

	
	/**
	 * @return the simpleTriggerDate
	 */
	public Date getSimpleTriggerDate() {
		return simpleTriggerDate;
	}
	/**
	 * @param simpleTriggerDate the simpleTriggerDate to set
	 */
	public void setSimpleTriggerDate(Date simpleTriggerDate) {
		this.simpleTriggerDate = simpleTriggerDate;
	}
	
	public QuartzScheduleJobDetails(T t) {
		this.t = t;
	}
	
	/**
	 * @param t the t to set
	 */
	public void setT(T t) {
		this.t = t;
	}
	/**
	 * @return the jobclassName
	 */
	public Class<? extends Job> getJobclassName() {
		return jobclassName;
	}

	/**
	 * @param jobclassName the jobclassName to set
	 */
	public void setJobclassName(Class<? extends Job> jobclassName) {
		this.jobclassName = jobclassName;
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return the jobGroup
	 */
	public String getJobGroup() {
		return jobGroup;
	}

	/**
	 * @param jobGroup the jobGroup to set
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	/**
	 * @return the jobID
	 */
	public String getJobID() {
		return jobID;
	}

	/**
	 * @param jobID the jobID to set
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * @return the t
	 */
	public T getT() {
		return t;
	}

	/**
	 * @return the quartzCronExpression
	 */
	public String getQuartzCronExpression() {
		return quartzCronExpression;
	}

	/**
	 * @param quartzCronExpression
	 *            the quartzCronExpression to set
	 */
	public void setQuartzCronExpression(String quartzCronExpression) {
		this.quartzCronExpression = quartzCronExpression;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public JobDetailImpl retrieveJobDetails() {
		JobDetailImpl jobDetails = new JobDetailImpl();
		jobDetails.setName(getJobName());
		jobDetails.setGroup(getJobGroup());
		jobDetails.setJobClass(getJobclassName());
		jobDetails.setRequestsRecovery(true);
		Map jobDataMap = jobDetails.getJobDataMap();
		jobDataMap.put(SchedularConstants.JOB_DATA_MAP_ID.toString(), getT());
		return jobDetails;
	}

	@Override
	public CronTriggerImpl retrieveCronTriggerImpl() {
		CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
		try {
			cronTriggerImpl.setCronExpression(getQuartzCronExpression());
			cronTriggerImpl.setJobGroup(getJobGroup());
			cronTriggerImpl.setJobName(getJobName());
			cronTriggerImpl.setName(getJobName());
			cronTriggerImpl.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY);
			CronExpression cronExpression = new CronExpression(
					getQuartzCronExpression());
			cronTriggerImpl.setStartTime(cronExpression.getNextValidTimeAfter(new Date()));
			} catch (ParseException e) {
			LogManager.log(LogManager.CATEGORY_DEFAULT,
					LogManager.LEVEL_ERROR,
					"Exception occured at " + this.getClass().getName()
							+ LogManager.printStackTrace(e));
		}
		return cronTriggerImpl;
	}
	
	public SimpleTriggerImpl retrieveSimpleTriggerImpl() {
		SimpleTriggerImpl simpleTriggerImpl= new SimpleTriggerImpl();
		simpleTriggerImpl.setJobGroup(getJobGroup());
		simpleTriggerImpl.setJobName(getJobName());
		simpleTriggerImpl.setName(getJobName());
		simpleTriggerImpl.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		simpleTriggerImpl.setRepeatCount(0);
		simpleTriggerImpl.setStartTime(getSimpleTriggerDate());
		return simpleTriggerImpl;
	}


}

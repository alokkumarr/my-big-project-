package com.synchronoss.saw.scheduler.job;

import com.synchronoss.saw.scheduler.service.JobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CronJob extends QuartzJobBean implements InterruptableJob{

	private static final Logger logger = LoggerFactory.getLogger(CronJob.class);
	private volatile boolean toStopFlag = true;
	
	@Autowired
	JobService jobService;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobKey key = jobExecutionContext.getJobDetail().getKey();
		logger.info("Cron Job started with key :" + key.getName() + ", Group :"+key.getGroup()
				+ " , Thread Name :"+Thread.currentThread().getName() + " ,Time now :"+new Date());

	/*	List<Map<String, Object>> list = jobService.getAllJobs(schedule);
		logger.debug("Job list :"+list);*/
		
		/**
         *  For retrieving stored key-value pairs
         */
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
		String myValue = dataMap.getString("myKey");
		logger.debug("Value:" + myValue);

		logger.info("Thread: "+ Thread.currentThread().getName() +" stopped.");
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		logger.info("Stopping thread... ");
		toStopFlag = false;
	}

}
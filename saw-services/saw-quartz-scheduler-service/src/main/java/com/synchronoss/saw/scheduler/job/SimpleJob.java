package com.synchronoss.saw.scheduler.job;

import com.synchronoss.saw.scheduler.service.JobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;
import java.util.Map;

public class SimpleJob extends QuartzJobBean implements InterruptableJob{

	private static final Logger logger = LoggerFactory.getLogger(SimpleJob.class);
	private volatile boolean toStopFlag = true;
	
	@Autowired
	JobService jobService;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobKey key = jobExecutionContext.getJobDetail().getKey();
		logger.info("Simple Job started with key :" + key.getName() + ", Group :"+key.getGroup()
				+ " , Thread Name :"+Thread.currentThread().getName());

		List<Map<String, Object>> list = jobService.getAllJobs();
		logger.debug("Job list :"+list);
		
		/**
         *  For retrieving stored key-value pairs
         */
		JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
		String myValue = dataMap.getString("myKey");
		logger.debug("Value:" + myValue);

		while(toStopFlag){
			try {
				logger.debug(" Job Running... Thread Name :"+Thread.currentThread().getName());
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error("Interrupted Exception for simple job ", e.getMessage());
			}
		}
		logger.info("Thread: "+ Thread.currentThread().getName() +" stopped.");
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		logger.info("Stopping thread... ");
		toStopFlag = false;
	}

}
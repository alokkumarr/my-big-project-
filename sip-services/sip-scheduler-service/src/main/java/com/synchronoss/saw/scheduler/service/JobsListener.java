package com.synchronoss.saw.scheduler.service;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JobsListener implements JobListener{

	private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

	/**
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return "globalJob";
	}

	/**
	 *
	 * @param context
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		logger.info("JobsListener.jobToBeExecuted()");
	}

	/**
	 *
	 * @param context
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		logger.info("JobsListener.jobExecutionVetoed()");
	}

	/**
	 *
	 * @param context
	 * @param jobException
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		logger.info("JobsListener.jobWasExecuted()");
	}

}

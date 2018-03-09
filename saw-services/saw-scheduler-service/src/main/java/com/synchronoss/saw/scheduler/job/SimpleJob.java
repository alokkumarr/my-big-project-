package com.synchronoss.saw.scheduler.job;

import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import com.synchronoss.saw.scheduler.service.AnalysisService;
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

    protected final static String JOB_DATA_MAP_ID="JOB_DATA_MAP";
	
	@Autowired
	JobService jobService;

	@Autowired
	AnalysisService analysisService;


	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDetail jobDetail =
				jobExecutionContext.getJobDetail();
		JobKey key = jobDetail.getKey();
		logger.info("Simple Job started with key :" + key.getName() + ", Group :"+key.getGroup()
				+ " , Thread Name :"+Thread.currentThread().getName());

        SchedulerJobDetail job = (SchedulerJobDetail) jobDetail.getJobDataMap().get(JOB_DATA_MAP_ID);

        analysisService.executeAnalysis(job.getAnalysisID());
        analysisService.scheduleDispatch(job);

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
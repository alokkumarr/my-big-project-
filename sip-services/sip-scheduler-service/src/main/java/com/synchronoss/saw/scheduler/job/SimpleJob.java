package com.synchronoss.saw.scheduler.job;

import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import com.synchronoss.saw.scheduler.service.AnalysisService;
import com.synchronoss.saw.scheduler.service.JobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SimpleJob extends QuartzJobBean implements InterruptableJob {

  private static final Logger logger = LoggerFactory.getLogger(SimpleJob.class);
  private volatile boolean toStopFlag = true;

  protected static final String JOB_DATA_MAP_ID = "JOB_DATA_MAP";

  @Autowired JobService jobService;

  @Autowired AnalysisService analysisService;

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    JobDetail jobDetail = jobExecutionContext.getJobDetail();
    JobKey key = jobDetail.getKey();
    logger.info(
        "Simple Job started with key :"
            + key.getName()
            + ", Group :"
            + key.getGroup()
            + " , Thread Name :"
            + Thread.currentThread().getName());

    SchedulerJobDetail job = (SchedulerJobDetail) jobDetail.getJobDataMap().get(JOB_DATA_MAP_ID);
    boolean isDslScheduled =  job.getType() != null && (job.getType().equalsIgnoreCase("pivot")
        || job.getType().equalsIgnoreCase("chart"));
    if (isDslScheduled) {
      analysisService.executeDslAnalysis(job.getAnalysisID());
    } else {
      analysisService.executeAnalysis(job.getAnalysisID());
    }
    analysisService.scheduleDispatch(job);

    logger.info("Thread: " + Thread.currentThread().getName() + " stopped.");
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    logger.info("Stopping thread... ");
    toStopFlag = false;
  }
}

package com.synchronoss.saw.scheduler.job;

import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;

import java.util.Date;

import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;



public class BisCronJob extends QuartzJobBean implements InterruptableJob {

  private static final Logger logger = LoggerFactory.getLogger(CronJob.class);
  private volatile boolean toStopFlag = true;

  protected static final String JOB_DATA_MAP_ID = "JOB_DATA_MAP";

  @Value("${bis-transfer-url}")
  private String bisTransferUrl;

  RestTemplate restTemplate = new RestTemplate();


  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    JobDetail jobDetail = jobExecutionContext.getJobDetail();
    JobKey key = jobDetail.getKey();
    
    logger.info("Cron Job started with key :" + key.getName() + ", Group :" + key.getGroup()
        + " , Thread Name :" + Thread.currentThread().getName() + " ,Time now :" + new Date());
    BisSchedulerJobDetails jobRequest =
        (BisSchedulerJobDetails) jobDetail.getJobDataMap().get(JOB_DATA_MAP_ID);

    try {
      restTemplate.postForLocation(bisTransferUrl, jobRequest);
    } catch (Exception exception) {
      logger.error("Error during file transfer for the schedule. "
          + "Refer Batch Ingestion logs for more details", 
          exception.getMessage());
    }
    
    logger.info("Thread: " + Thread.currentThread().getName() + " stopped.");
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    logger.info("Stopping thread... ");
    toStopFlag = false;
  }

}

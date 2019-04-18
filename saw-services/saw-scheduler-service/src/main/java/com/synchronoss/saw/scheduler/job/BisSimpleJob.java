package com.synchronoss.saw.scheduler.job;

import com.synchronoss.bda.util.RestUtil;
import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;
import javax.annotation.PostConstruct;
import org.quartz.InterruptableJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.client.RestTemplate;




public class BisSimpleJob extends QuartzJobBean implements InterruptableJob {

  private static final Logger logger = LoggerFactory.getLogger(SimpleJob.class);
  private volatile boolean toStopFlag = true;

  @Value("${bis-transfer-url}")
  private String bisTransferUrl;

  @Autowired
  private RestUtil restUtil;

  RestTemplate restTemplate = null;

  @PostConstruct
  public void init() throws Exception {
    restTemplate = restUtil.restTemplate();
        
  }

  protected static final String JOB_DATA_MAP_ID = "JOB_DATA_MAP";

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    JobDetail jobDetail = jobExecutionContext.getJobDetail();
    JobKey key = jobDetail.getKey();
    logger.info("Simple Job started with key :" + key.getName() + ", Group :" + key.getGroup()
        + " , Thread Name :" + Thread.currentThread().getName());

    BisSchedulerJobDetails jobDetails =
        (BisSchedulerJobDetails) jobDetail.getJobDataMap().get(JOB_DATA_MAP_ID);
    
    try {
      restTemplate.postForLocation(bisTransferUrl, jobDetails);
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
    setToStopFlag(false);
  }

  public boolean isToStopFlag() {
    return toStopFlag;
  }

  public void setToStopFlag(boolean toStopFlag) {
    this.toStopFlag = toStopFlag;
  }

}

package com.synchronoss.saw.scheduler.job;

import com.synchronoss.saw.scheduler.modal.BisSchedulerJobDetails;
import com.synchronoss.sip.utils.RestUtil;
import java.util.Date;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class BisCronJob extends QuartzJobBean implements InterruptableJob {

  private static final Logger logger = LoggerFactory.getLogger(CronJob.class);
  private volatile boolean toStopFlag = true;

  protected static final String JOB_DATA_MAP_ID = "JOB_DATA_MAP";

  @Value("${bis-transfer-url}")
  private String bisTransferUrl;

  @Autowired
  private RestUtil restUtil;

  
  @Override
  // @Transactional
  protected void executeInternal(JobExecutionContext jobExecutionContext)
      throws JobExecutionException {
    JobDetail jobDetail = jobExecutionContext.getJobDetail();
    JobKey key = jobDetail.getKey();
    logger.info("Cron Job started with key after :" + key.getName() + ", Group :" + key.getGroup()
        + " , Thread Name :" + Thread.currentThread().getName() + " ,Time now :" + new Date());
    BisSchedulerJobDetails jobRequest =
        (BisSchedulerJobDetails) jobDetail.getJobDataMap().get(JOB_DATA_MAP_ID);
    RestTemplate restTemplate = null;
    try {
      restTemplate = restUtil.restTemplate(restUtil.getKeyStore(), restUtil.getKeyStorePassword(),
          restUtil.getTrustStore(), restUtil.getTrustStorePassword());
      logger.trace("restUtil.getKeyStore(): " + restUtil.getKeyStore());
      logger.trace("restUtil.getTrustStore(): " + restUtil.getTrustStore());
      restTemplate.postForLocation(bisTransferUrl, jobRequest);
      
    } catch (Exception exception) {
      /**
       * As BIS is async process for larger files async timesout. 
       * This can be ignored.
       */
      logger.info("Async BIS transfer still running "
          + exception);
    }
    
    logger.info("Thread: " + Thread.currentThread().getName() + " stopped.");
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    logger.info("Stopping thread... ");
    this.setToStopFlag(false);
  }

  public boolean isToStopFlag() {
    return toStopFlag;
  }

  public void setToStopFlag(boolean toStopFlag) {
    this.toStopFlag = toStopFlag;
  }

}

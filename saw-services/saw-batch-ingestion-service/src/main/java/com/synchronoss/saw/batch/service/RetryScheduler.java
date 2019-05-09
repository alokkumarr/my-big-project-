package com.synchronoss.saw.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RetryScheduler {
  @Autowired
  private RetryExecutorService retryExecutor;
  
  private static final Logger logger = LoggerFactory
      .getLogger(TransferExecutorService.class);
  
  /**
   * Scheduler for retry execution.
   */
  @Scheduled(fixedDelayString = "${sip.service.retry.delay}")
  public void recoverFromInconsistentState() {
    logger.info("Retry polling starts here");
    retryExecutor.recoverFromInconsistentState();
    
    
  }
}

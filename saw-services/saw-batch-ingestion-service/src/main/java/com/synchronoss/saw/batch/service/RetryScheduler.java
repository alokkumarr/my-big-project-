package com.synchronoss.saw.batch.service;

import org.springframework.scheduling.annotation.Scheduled;

public class RetryScheduler {
  
  private RetryExecutorService retryExecutor;
  
  /**
   * Scheduler for retry execution.
   */
  @Scheduled(fixedDelayString = "${sip.service.retry.delay}")
  public void recoverFromInconsistentState() {
    
    retryExecutor.recoverFromInconsistentState();
    
    
  }
}

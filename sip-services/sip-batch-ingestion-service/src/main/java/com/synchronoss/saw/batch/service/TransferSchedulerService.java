package com.synchronoss.saw.batch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TransferSchedulerService {
  
  @Autowired
  private TransferExecutorService transferService;
  
  /**
   * scheduler for file transfer processing.
   */
  @Scheduled(fixedDelayString = "${sip.service.worker.delay}")
  public void processFileTransfer() {
    
    transferService.processFileTransfer();
    
  }

}

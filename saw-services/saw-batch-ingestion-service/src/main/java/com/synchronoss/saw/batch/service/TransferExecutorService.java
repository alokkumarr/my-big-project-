package com.synchronoss.saw.batch.service;

import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.plugin.SipIngestionPluginFactory;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.service.SipLogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TransferExecutorService {

  @Autowired
  private SipIngestionPluginFactory factory;

  @Autowired
  SipLogging logService;
  
  private static final Logger logger = LoggerFactory
      .getLogger(TransferExecutorService.class);
  
  /**
   * executes the file transfer logic.
   */
  @Scheduled(fixedDelayString = "${sip.service.worker.delay}")
  public void processFileTransfer() {

    // factory.getInstance(ingestionType)

    BisFileLog bisFileLog = logService.retreiveOpenLogs();
    
    if (bisFileLog == null) {
      logger.info("No records found to process");
      
    } else {
      logger.info("One record found to process" + bisFileLog.getFileName());
      SipPluginContract sipTransferService = factory
          .getInstance(bisFileLog.getBisChannelType());
      

      sipTransferService.executeFileTransfer(bisFileLog.getPid(),
          bisFileLog.getJob().getJobId(), bisFileLog.getBisChannelSysId(),
          bisFileLog.getRouteSysId(), bisFileLog.getFileName());
    }
    

  }

}

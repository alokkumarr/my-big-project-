package com.synchronoss.saw.batch.service;

import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.plugin.SipIngestionPluginFactory;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.service.SipLogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
   * Invokes file transfer logic based on
   * channel type.
   */
  @Async("transferWorkerExecutor")
  public void processFileTransfer() {


    // factory.getInstance(ingestionType)

    BisFileLog bisFileLog = logService.retreiveOpenLogs();
    
    if (bisFileLog == null) {
      logger.trace("No records found to process");
      
    } else {
      logger.trace("BIS File log = " + bisFileLog);
      logger.trace("One record found to process" + bisFileLog.getFileName());
      SipPluginContract sipTransferService = factory
          .getInstance(bisFileLog.getBisChannelType());
      

      sipTransferService.executeFileTransfer(bisFileLog.getPid(),
          bisFileLog.getJob().getJobId(), bisFileLog.getBisChannelSysId(),
          bisFileLog.getRouteSysId(), bisFileLog.getFileName(), bisFileLog.getRecdFileName());
    }
    

  
  }

}

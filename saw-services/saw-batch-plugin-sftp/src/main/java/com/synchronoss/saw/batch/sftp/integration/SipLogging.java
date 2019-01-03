package com.synchronoss.saw.batch.sftp.integration;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SipLogging {

  private static final Logger logger = LoggerFactory.getLogger(SipLogging.class);

  @Autowired
  private BisFileLogsRepository bisFileLogsRepository;

  /**
   * To make an entry to a log table. 
   */
  public void upsert(BisDataMetaInfo entity, String pid) throws SipNestedRuntimeException {
    logger.trace("Integrate with logging API to update with a status start here : "
        + entity.getProcessState());
    BisFileLog bisLog = new BisFileLog();
    bisLog.setPid(pid);
    bisLog.setBisChannelSysId(Long.valueOf(entity.getChannelId()));
    bisLog.setRouteSysId(Long.valueOf(entity.getRouteId()));
    bisLog.setFilePattern(entity.getFilePattern());
    bisLog.setFileName(entity.getActualDataName());
    bisLog.setRecdFileSize(entity.getDataSizeInBytes());
    bisLog.setRecdFileName(entity.getReceivedDataName());
    bisLog.setBisChannelType(entity.getChannelType().value());
    bisLog.setMflFileStatus(entity.getProcessState());
    bisLog.setActualFileRecDate(entity.getActualReceiveDate());
    bisLog.setBisProcessState(entity.getComponentState());
    if (bisFileLogsRepository.existsById(pid)) {
      bisLog.setCreatedDate(new Date());
      bisLog.setModifiedDate(new Date());
      bisFileLogsRepository.deleteById(pid);
      bisFileLogsRepository.save(bisLog);
    } else {
      bisLog.setCreatedDate(new Date());
      bisFileLogsRepository.save(bisLog);
    }
    logger.trace("Integrate with logging API to update with a status ends here : " 
        + entity.getProcessState()
        + " with an process Id " + bisLog.getPid());
  }

  public boolean checkDuplicateFile(String fileName) throws SipNestedRuntimeException {
    logger.trace("Integrate with logging API & checking for the duplicate files : " + fileName);
    return bisFileLogsRepository.isFileNameExists(fileName);
  }

  public void deleteLog(String pid) throws SipNestedRuntimeException {
    logger.trace("Delete an entry with logging API :" + pid);
    bisFileLogsRepository.deleteById(pid);
  }
  
  public  void updateLogs(Long channelId, Long routeId, String reasonCode) {
    
    BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
    bisDataMetaInfo
        .setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
    bisDataMetaInfo.setDataSizeInBytes(0L);
    bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
    bisDataMetaInfo.setProcessState(reasonCode);
    bisDataMetaInfo.setComponentState(reasonCode);
    bisDataMetaInfo.setActualReceiveDate(new Date());
    bisDataMetaInfo.setChannelId(channelId);
    bisDataMetaInfo.setRouteId(routeId);
    this.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
  }

}


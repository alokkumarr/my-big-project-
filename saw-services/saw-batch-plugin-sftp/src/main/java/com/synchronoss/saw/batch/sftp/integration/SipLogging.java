package com.synchronoss.saw.batch.sftp.integration;

import com.jcraft.jsch.ChannelSftp;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;

import java.io.File;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SipLogging {

  private static final Logger logger = LoggerFactory.getLogger(SipLogging.class);

  @Autowired
  private BisFileLogsRepository bisFileLogsRepository;
  

  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;
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
  
  public boolean duplicateCheck(boolean isDisableDuplicate, String sourcelocation, ChannelSftp.LsEntry entry ) {
    return (!isDisableDuplicate && !checkDuplicateFile(sourcelocation + File.separator 
        + entry.getFilename())) || isDisableDuplicate;
    
  }

}


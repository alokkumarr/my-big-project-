package com.synchronoss.saw.batch.sftp.integration;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.jcraft.jsch.ChannelSftp;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class SipLogging {

  private static final Logger logger = LoggerFactory.getLogger(SipLogging.class);

  @Autowired
  private BisFileLogsRepository bisFileLogsRepository;


  /**
   * To make an entry to a log table.
   */
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
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
    bisLog.setTransferStartTime(entity.getFileTransferStartTime());
    bisLog.setTransferEndTime(entity.getFileTransferEndTime());
    bisLog.setTransferDuration(entity.getFileTransferDuration());
    bisLog.setCheckpointDate(new Date());
    bisLog.setCreatedDate(new Date());
    if (bisFileLogsRepository.existsById(pid)) {
      bisLog.setModifiedDate(new Date());
      bisFileLogsRepository.deleteById(pid);
      bisFileLogsRepository.save(bisLog);
    } else {
      bisFileLogsRepository.save(bisLog);
    }
    logger.trace("Integrate with logging API to update with a status ends here : "
        + entity.getProcessState() + " with an process Id " + bisLog.getPid());
  }

  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public boolean checkDuplicateFile(String fileName) throws SipNestedRuntimeException {
    logger.trace("Integrate with logging API & checking for the duplicate files : " + fileName);
    return bisFileLogsRepository.isFileNameExists(fileName);
  }

  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public void deleteLog(String pid) throws SipNestedRuntimeException {
    logger.trace("Delete an entry with logging API :" + pid);
    bisFileLogsRepository.deleteById(pid);
  }


  /**
   * Adds entry to log table with given status.
   */
  public  void updateLogs(Long channelId, Long routeId, String reasonCode) {

    BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
    bisDataMetaInfo
        .setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
    bisDataMetaInfo.setDataSizeInBytes(0L);
    bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
    bisDataMetaInfo.setProcessState(BisProcessState.FAILED.value());
    bisDataMetaInfo.setComponentState(reasonCode);
    bisDataMetaInfo.setActualReceiveDate(new Date());
    bisDataMetaInfo.setChannelId(channelId);
    bisDataMetaInfo.setRouteId(routeId);
    this.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
  }


  /**
   * verify duplicate check enabled and is duplicate
   * or if duplicate check disabled.
   *
   * @param isDisableDuplicate disabled duplicate check flag
   * @param sourcelocation source path
   * @param entry file entry
   * @return true or false
   */
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public boolean duplicateCheck(boolean isDisableDuplicate,
      String sourcelocation, ChannelSftp.LsEntry entry) {
    return (!isDisableDuplicate && !checkDuplicateFile(sourcelocation + File.separator
        + entry.getFilename())) || isDisableDuplicate;

  }


  /**
   * verify the routeId & channelId exists with
   * data received & success.
   * @param routeId route id to be validated
   * @param channelId channel id to be validated
   * @return true or false
   */
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public boolean isRouteAndChannelExists(Long routeId, Long channelId) {
    return bisFileLogsRepository.isChannelAndRouteIdExists(routeId, channelId);

  }

  /**
   * This method is used retry id.
   *
   * @param numberOfMinutes in minutes
   * @param pageNumber page number
   * @param pageSize page size
   * @param column column to operate
   * @return BisFileLog
   */
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public List<BisFileLog> listOfRetryIds(int numberOfMinutes, int pageNumber, int pageSize,
      String column) {
    Page<BisFileLog> logs = bisFileLogsRepository.retryIds(numberOfMinutes,
        PageRequest.of(pageNumber, pageSize, Direction.DESC, column));
    return logs.getContent();
  }

  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public Integer countRetryIds(int numberOfMinutes) {
    Integer countOfRows = bisFileLogsRepository.countOfRetries(numberOfMinutes);
    return countOfRows;
  }

  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public Integer updateStatusFailed(String fileStatus, String processStatus, String pid) {
    Integer countOfRows = bisFileLogsRepository.updateBislogsStatus(fileStatus, processStatus, pid);
    return countOfRows;
  }

}

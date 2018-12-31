package com.synchronoss.saw.batch.sftp.integration;

import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;
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
    if (bisFileLogsRepository.existsById(pid)) {
      bisLog.setCreatedDate(new Date());
      bisLog.setModifiedDate(new Date());
      bisLog.setCheckpointDate(new Date());
      bisFileLogsRepository.deleteById(pid);
      bisFileLogsRepository.save(bisLog);
    } else {
      bisLog.setCreatedDate(new Date());
      bisLog.setCheckpointDate(new Date());
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
  public Integer updateStatusFailed(String status, String pid) {
    Integer countOfRows = bisFileLogsRepository.updateBislogsStatus(status, pid);
    return countOfRows;
  }

}


package com.synchronoss.saw.logs.service;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.jcraft.jsch.ChannelSftp;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisComponentState;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.entities.BisJobEntity;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;
import com.synchronoss.saw.logs.repository.SipJobDataRepository;

import java.io.File;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

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
  
  @Autowired
  private SipJobDataRepository sipJobDataRepository;


  /**
   * To make an entry to a log table.
   */
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  @Transactional(TxType.REQUIRED)
  public void upsert(BisDataMetaInfo entity, String pid) throws SipNestedRuntimeException {
    logger.trace("Integrate with logging API to update with a status start here : "
        + entity.getProcessState());
    BisFileLog bisLog = null;
    if (bisFileLogsRepository.existsById(pid)) {
      logger.trace("updating logs when process Id is found :" + pid);
      bisLog = bisFileLogsRepository.findByPid(pid);
      bisLog.setPid(pid);
      bisLog.setModifiedDate(new Date());
      bisLog.setBisChannelSysId(Long.valueOf(entity.getChannelId()));
      bisLog.setRouteSysId(Long.valueOf(entity.getRouteId()));
      bisLog.setMflFileStatus(entity.getProcessState());
      bisLog.setBisProcessState(entity.getComponentState());
      bisLog.setTransferDuration(entity.getFileTransferDuration());
      bisLog.setTransferEndTime(entity.getFileTransferEndTime());
      bisLog.setTransferStartTime(entity.getFileTransferStartTime());
      bisLog.setSource(entity.getSource());
      bisLog.getJob().setJobId(entity.getJobId());
      bisFileLogsRepository.save(bisLog);
    } else {
      logger.trace("inserting logs when process Id is not found :" + pid);
      bisLog = new BisFileLog();
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
      bisLog.setSource(entity.getSource());
      BisJobEntity jobEntity = null;
      if (entity.getJobId() != null) {
        jobEntity = this.retriveJobById(entity.getJobId());
      }
      
      bisLog.setJob(jobEntity);
      bisFileLogsRepository.save(bisLog);
    }
    logger.trace("Integrate with logging "
        + "API to update with a status ends here : "
        + entity.getProcessState() + " with an process Id " 
        + bisLog.getPid());
  }
  
  
  /**
   * To make an entry to a log table.
   */
  @Transactional(TxType.REQUIRED)
  public void upsertInProgressStatus(String pid, 
      String recdFilePath, Date startTime)
      throws SipNestedRuntimeException {
    BisFileLog bisLog = null;
    if (bisFileLogsRepository.existsById(pid)) {
      logger.trace("updating logs when process Id is found :" + pid);
      bisLog = bisFileLogsRepository.findByPid(pid);
      bisLog.setRecdFileName(recdFilePath);
      bisLog.setTransferStartTime(startTime);
      bisLog.setBisProcessState(BisComponentState.DATA_INPROGRESS.value());
      bisLog.setMflFileStatus(BisProcessState.INPROGRESS.value());
      this.bisFileLogsRepository.saveAndFlush(bisLog);
    }
  }
  
  /**
   * To make an entry to a log table.
   */
  @Transactional(TxType.REQUIRED)
  public void upsertInProgressStatus(String pid)
      throws SipNestedRuntimeException {
      
    updateLogStatus(BisProcessState.INPROGRESS.value(),
          BisComponentState.DATA_INPROGRESS.value(), pid);
  }
    
  /**
   * To make an entry to a log table.
   */
  @Transactional(TxType.REQUIRED)
  public void upsertFailedStatus(String pid) throws SipNestedRuntimeException {
    updateLogStatus(BisProcessState.FAILED.value(),
            BisComponentState.FAILED.value(), pid);
    
    
  }
  
  /**
   * To make an entry to a log table.
   */
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  @Transactional(TxType.REQUIRED)
  public void upsertSuccessStatus(String pid, BisDataMetaInfo metaInfo) 
      throws SipNestedRuntimeException {
    Optional<BisFileLog> bisFileLog = this.bisFileLogsRepository.findById(pid);
    if (bisFileLog.isPresent()) {
      BisFileLog log = bisFileLog.get();
      log.setMflFileStatus(BisProcessState.SUCCESS.value());
      log.setBisProcessState(BisComponentState.DATA_RECEIVED.value());
      log.setModifiedDate(new Date());
      log.setTransferStartTime(metaInfo.getFileTransferStartTime());
      log.setTransferEndTime(metaInfo.getFileTransferEndTime());
      log.setTransferDuration(metaInfo.getFileTransferDuration());
      log.setRecdFileName(metaInfo.getReceivedDataName());
      bisFileLogsRepository.saveAndFlush(log);
    }
  }

  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  @Transactional(TxType.REQUIRED)
  public boolean checkDuplicateFile(String fileName) throws SipNestedRuntimeException {
    logger.trace("Integrate with logging API & checking for the duplicate files : " + fileName);
    return bisFileLogsRepository.isFileNameExists(fileName);
  }

  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  @Transactional(TxType.REQUIRED)
  public void deleteLog(String pid) throws SipNestedRuntimeException {
    logger.trace("Delete an entry with logging API :" + pid);
    bisFileLogsRepository.deleteById(pid);
  }


  /**
   * Adds entry to log table with given status.
   */
  @Transactional(TxType.REQUIRED)
  public  void updateLogs(Long channelId, Long routeId, String reasonCode) {

    BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
    bisDataMetaInfo.setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
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
   * verify duplicate check enabled and is duplicate or if duplicate check disabled.
   *
   * @param isDisableDuplicate disabled duplicate check flag
   * @param sourcelocation source path
   * @param entry file entry
   * @return true or false
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public boolean duplicateCheck(boolean isDisableDuplicate,
      String sourcelocation, ChannelSftp.LsEntry entry) {

    ZonedDateTime duplicateCheckStartTime = ZonedDateTime.now();
    logger.trace("Duplicate check starting now :: ");
    
   

    boolean isDuplicate =  (!isDisableDuplicate
        &&  !checkDuplicateFile(sourcelocation + File.separator
        + entry.getFilename())) || isDisableDuplicate;
    
    ZonedDateTime duplicateCheckEndTime = ZonedDateTime.now();

    if (isDisableDuplicate) {
      logger.trace("Duplicate check disabled. Duration to check flag in milliseconds :: " + Duration
                .between(duplicateCheckStartTime, duplicateCheckEndTime).toMillis());
    } else {
      logger.trace("Total time for duplicate check in milliseconds :: " + Duration
                .between(duplicateCheckStartTime, duplicateCheckEndTime).toMillis());
    }



    return isDuplicate;

  }


  /**
   * verify the routeId & channelId exists with data received & success.
   *
   * @param routeId route id to be validated
   * @param channelId channel id to be validated
   * @return true or false
   */
  @Transactional(TxType.REQUIRED)
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
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public List<BisFileLog> listOfRetryIds(int numberOfMinutes, int pageNumber, int pageSize,
      String column) {
    Page<BisFileLog> logs = bisFileLogsRepository.retryIds(numberOfMinutes,
        PageRequest.of(pageNumber, pageSize, Direction.DESC, column));
    return logs.getContent();
  }

  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public Integer countRetryIds(int numberOfMinutes) {
    Integer countOfRows = bisFileLogsRepository.countOfRetries(numberOfMinutes);
    return countOfRows;
  }

  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public void updateStatusFailed(String fileStatus, String processStatus, String pid) {
    updateLogStatus(fileStatus, processStatus, pid);
  }
  
  /**
   * update log status.
   * 
   * @param fileStatus file Status
   * @param processStatus process status
   * @param pid process id
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public void updateLogStatus(String fileStatus, String processStatus, String pid) {
    Optional<BisFileLog> bisFileLog = this.bisFileLogsRepository.findById(pid);
    if (bisFileLog.isPresent()) {
      BisFileLog log = bisFileLog.get();
      log.setMflFileStatus(fileStatus);
      log.setBisProcessState(processStatus);
      log.setModifiedDate(new Date());
      bisFileLogsRepository.saveAndFlush(log);
    }
    
  }

  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public Page<BisFileLog> statusExistsForProcess(Long channelId, Long routeId,
      String processStatus) {
    return bisFileLogsRepository.isStatusExistsForProcess(processStatus, channelId, routeId,
        BisChannelType.SFTP.value(), PageRequest.of(0, 1, Direction.DESC, "modifiedDate"));
  }

  /**
   * verify pid exists before deleting it.
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public boolean checkAndDeleteLog(String pid) throws Exception {
    logger.trace("Delete and check process id :" + pid + "starts here");
    boolean result = false;
    if (bisFileLogsRepository.existsById(pid)) {
      try {
        deleteLog(pid);
        result = true;
      } catch (Exception ex) {
        throw new Exception("Exception occurred while deleting pid :" + pid);
      }
    }
    logger.trace("Delete and check process id :" + pid + "ends here");
    return result;
  }

  /**
  * check if any long running process exists
  * and update if any.
  * @param minutesToCheck maxInProgress minutes
  * @return number of updated records
  */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public Integer updateLongRunningTransfers(Integer minutesToCheck) {
    int updatedRecords = 0;
    int longCount = bisFileLogsRepository
        .countOfLongRunningTransfers(minutesToCheck);
    logger.trace("Long running process count: " +    longCount);
    if (longCount > 0) {
      logger.trace("Updating long running transfers to failed");
      List<BisFileLog> inProgLogs = bisFileLogsRepository
          .selectLongRunningTranfers(minutesToCheck);
      logger.trace("long running transfer update completed");
      
      inProgLogs.stream().map(bisFileLog -> {
        bisFileLog.setMflFileStatus("FAILED");
        bisFileLog.setBisProcessState("FAILED");
        return bisFileLog;
      }).forEach(bisFileLogsRepository::saveAndFlush);

      Map<BisJobEntity, Long> jobs = inProgLogs.stream().collect(
          Collectors.groupingBy(BisFileLog::getJob, Collectors.counting()));
      
      jobs.forEach((job, count) -> this.updateJobStatus(job.getJobId()));
      updatedRecords = inProgLogs.size();
    }
    return updatedRecords;
  }
  
  
  /**
   * check if any long running process exists
   * and update if any.
   * @param minutesToCheck maxInProgress minutes
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
   public void updateLongRunningJobs(Integer minutesToCheck) {
    int longCount = bisFileLogsRepository
         .countOfLongRunningJobs(minutesToCheck);
    logger.info("Long running process count: " +    longCount);
    if (longCount > 0) {
      logger.info("Updating long running transfers to failed");
      bisFileLogsRepository
           .updateBisJob("FAILED", minutesToCheck);
      logger.info("long running jobs update completed");
       
    }
  }
  
  /**
   * Update existing log process status.
   * 
   * @param channelId channel identifier
   * @param routeId route id entifier
   * @param processStatus status
   * @param fileStatus file status
   * @param source source
   */
  @Transactional(TxType.REQUIRED)
  public void upSertLogForExistingProcessStatus(Long channelId, Long routeId, String processStatus,
      String fileStatus, String source, Long jobId) {
    logger.trace(
        "upSertLogForExistingProcessStatus :" + channelId + " routeId " + routeId + "starts here");
    Page<BisFileLog> statuslog = statusExistsForProcess(channelId, routeId, processStatus);
    BisFileLog fileLog = null;
    if (statuslog != null
        && (statuslog.getContent() != null && statuslog.getContent().size() > 0)) {
      // It will have latest one by modifiedDate
      fileLog = statuslog.getContent().get(0);
      updateStatusFailed(BisProcessState.FAILED.value(),
          BisComponentState.HOST_NOT_REACHABLE.value(), fileLog.getPid());
    } else {
      BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
      bisDataMetaInfo.setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
      bisDataMetaInfo.setChannelId(channelId);
      bisDataMetaInfo.setRouteId(routeId);
      bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
      bisDataMetaInfo.setComponentState(processStatus);
      bisDataMetaInfo.setProcessState(fileStatus);
      bisDataMetaInfo.setSource(source);
      bisDataMetaInfo.setJobId(jobId);
      upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
    }
    logger.trace(
        "upSertLogForExistingProcessStatus :" + channelId + " routeId " + routeId + "ends here");
  }

  /**
   * verify duplicate check enabled and is duplicate or if duplicate check disabled.
   *
   * @param isDisableDuplicate disabled duplicate check flag
   * @param location source path
   * @return true or false
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public boolean duplicateCheckFilename(boolean isDisableDuplicate, String location) {
    return (!isDisableDuplicate && !checkDuplicateFile(location)) || isDisableDuplicate;

  }
  
  /**
   * check if any regular file running for the route.
   *
   * @param routeId Route identifier
   * @return true or false
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public boolean checkIfAlreadyRunning(Long routeId) {
    boolean isInProgress =   bisFileLogsRepository.countOfInProgress(routeId) > 0;
    logger.info("Any Inprogress regular jobs for  " + routeId + "? :: " + isInProgress);
    return isInProgress;
  }

  /**
   * Adds entry to job log.
   * 
   * @param channelId channel identifier.
   * @param routeId route identifier
   * @param filePattern pattern expression
   * @return job log created or not
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public BisJobEntity createJobLog(Long channelId, Long routeId, String filePattern) {
    BisJobEntity sipJob = new BisJobEntity();
    sipJob.setFilePattern(filePattern);
    sipJob.setJobName(channelId + "-" +  routeId.toString());
    sipJob.setJobStatus("OPEN");
    sipJob.setJobType("SFTP");
    sipJob.setStartTime(new Date());
    sipJob.setTotalCount(0L);
    sipJob.setSuccessCount(0L);
    sipJob.setCreatedBy("system");
    sipJob.setUpdatedBy("system");
    sipJob.setCreatedDate(new Date());
    sipJob.setUpdatedDate(new Date());
    sipJobDataRepository.save(sipJob);
    return sipJob;
    
  }
  
  /**
   * Adds entry to job log.
   * 
   * @param status job status.
   * @param successCnt number of files succssfully transferred
   * @param totalCnt total number of files to be processed
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public void  updateJobLog(long jobId, String status, long successCnt, long totalCnt) {
    Optional<BisJobEntity> sipJob = sipJobDataRepository.findById(jobId);
    if (sipJob.isPresent()) {
      BisJobEntity jobEntity = sipJob.get();
      jobEntity.setJobStatus(status);
      jobEntity.setTotalCount(totalCnt);
      jobEntity.setSuccessCount(successCnt);
      jobEntity.setCreatedBy("system");
      jobEntity.setUpdatedBy("system");
      jobEntity.setCreatedDate(new Date()); 
      jobEntity.setUpdatedDate(new Date());
      sipJobDataRepository.saveAndFlush(jobEntity);
    }
    
    
  }
  
  /**
   * Adds entry to job log.
   * 
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public void  updateSuccessCnt(long jobId) {
    Optional<BisJobEntity> sipJob = sipJobDataRepository.findById(jobId);
    if (sipJob.isPresent()) {
      Long successCnt = bisFileLogsRepository.getSuccessCntForJob(jobId);
      BisJobEntity jobEntity = sipJob.get();
      jobEntity.setSuccessCount(successCnt);
      jobEntity.setUpdatedDate(new Date());
      sipJobDataRepository.saveAndFlush(jobEntity);
    }
    
    
  }
  
  /**
   * Adds entry to job log.
   */
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {
      RuntimeException.class }, maxAttemptsExpression = 
          "#{${sip.service.max.attempts}}", backoff = 
          @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  public void updateJobStatus(long jobId) {
    logger.info("Updating job status:: ");
    Optional<BisJobEntity> sipJob = sipJobDataRepository.findById(jobId);
    if (sipJob.isPresent()) {
      BisJobEntity jobEntity = sipJob.get();
      logger.info("Job status::" + jobEntity.getJobStatus());
      logger.info("Job total count::" + jobEntity.getTotalCount());
      logger.info("Job success count::" + jobEntity.getSuccessCount());
      if (!jobEntity.getJobStatus().equals("OPEN")
          && jobEntity.getTotalCount() == jobEntity.getSuccessCount()) {
        logger.info("Count matched success");
        jobEntity.setJobStatus("SUCCESS");
        jobEntity.setEndTime(new Date());
       
      } else if (!jobEntity.getJobStatus().equals("OPEN") 
          && isJobPartiallyCompleted(jobId, jobEntity.getTotalCount())) {
        logger.info("Count matched partial");
        jobEntity.setJobStatus("PARTIALLY_COMPLETED");
      } else if (!jobEntity.getJobStatus().equals("OPEN") 
          && isJobFailed(jobId, jobEntity.getTotalCount())) {
        logger.info("Count matched failed");
        jobEntity.setJobStatus("FAILED");
        jobEntity.setEndTime(new Date());
      } else {
        logger.info("update job status none matched");
      }
      
      
      jobEntity.setUpdatedDate(new Date());
      sipJobDataRepository.saveAndFlush(jobEntity);

    }

  }
  
  @Transactional(TxType.REQUIRED)
  @Retryable(value = {
      RuntimeException.class }, maxAttemptsExpression = 
          "#{${sip.service.max.attempts}}", backoff = 
          @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  private boolean isJobFailed(long jobId, Long total) {
    return bisFileLogsRepository
        .countByMflFileStatusAndBisProcessStateAndJob_JobId("FAILED",
            "FAILED", jobId) == total;
  }

  @Transactional(TxType.REQUIRED)
  @Retryable(value = {
      RuntimeException.class }, maxAttemptsExpression = 
          "#{${sip.service.max.attempts}}", backoff = 
          @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  private boolean isJobPartiallyCompleted(Long jobId, Long total) {
    Long failedCnt = bisFileLogsRepository
        .countByMflFileStatusAndBisProcessStateAndJob_JobId("FAILED",
        "FAILED", jobId);
    logger.info("failed count" + failedCnt);
    Long successCnt =  bisFileLogsRepository.getSuccessCntForJob(jobId);
    logger.info("success count" + successCnt);
    
    boolean totalProcessed = (total == (failedCnt + successCnt));
   
    boolean isPartial = (successCnt > 0) && totalProcessed;
    logger.info("is partially complted?::" + isPartial);
    
    return (successCnt > 0) && (total == failedCnt + successCnt); 
  }
  
  /**
   * Returns Job entity by Id.
   * 
   * @param jobId job identifier
   * @return JobEntity
   */
  @Retryable(value = {RuntimeException.class},
      maxAttemptsExpression = "#{${sip.service.max.attempts}}",
      backoff = @Backoff(delayExpression = "#{${sip.service.retry.delay}}"))
  @Transactional(TxType.REQUIRED)
  public BisJobEntity retriveJobById(long jobId) {
    
    Optional<BisJobEntity> sipJob = sipJobDataRepository.findById(jobId);
    BisJobEntity jobEntity = null;
    if (sipJob.isPresent()) {
      jobEntity =   sipJob.get();
    }
    
    return jobEntity;
    
  }
  
  
  
  
  
  /**
   * Returns Job entity by Id.
   * 
   */
  @Transactional(TxType.REQUIRED)
  public void saveJob(BisJobEntity jobEntity) {
    sipJobDataRepository.saveAndFlush(jobEntity);
    
  }
  
  /**
   * Returns Job entity by Id.
   * 
   */
  @Transactional(TxType.REQUIRED)
  public BisFileLog retreiveOpenLogs() {
    List<BisFileLog> logs = bisFileLogsRepository.findFirstOpenLog();
    BisFileLog log = null;
    if (!logs.isEmpty()) {
      log = logs.get(0);
    } 
    return log;
  }
  


}

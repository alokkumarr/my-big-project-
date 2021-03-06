package com.synchronoss.saw.batch.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.extensions.SipRetryContract;
import com.synchronoss.saw.batch.model.BisComponentState;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.batch.plugin.SipRetryPluginFactory;
import com.synchronoss.saw.logs.constants.SourceType;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.service.SipLogging;
import com.synchronoss.sip.utils.IntegrationUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javassist.NotFoundException;
import javax.annotation.PostConstruct;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import sncr.bda.core.file.FileProcessor;
import sncr.bda.core.file.FileProcessorFactory;



@Service
public class RetryExecutorService {

  private static final Logger logger = LoggerFactory
      .getLogger(RetryExecutorService.class);


  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;
  


  @Autowired
  private SipLogging sipLogService;

  @Value("${bis.modified-retries}")
  @NotNull
  private int retries;

  @Value("${bis.transfer-batch-size}")
  @NotNull
  private Integer batchSize;

  @Value("${bis.default-data-drop-location}")
  @NotNull
  private String defaultDestinationLocation;

  @Value("${sip.service.retry.diff.mins}")
  @NotNull
  private Integer retryDiff;

  @Value("${sip.service.retry.page.size}")
  @NotNull
  private Integer retryPageSize;

  @Value("${bis.recheck-file-modified}")
  @NotNull
  private Boolean recheckFileModified;

  private final Integer pageStart = 0;

  private final String fileStatus = "FAILED";
  private final String procesStatus = "DATA_REMOVED";

  @Value("${bis.destination-fs-user}")
  @NotNull
  private String mapRfsUser;

  @Value("${bis.duplicate-entry}")
  @NotNull
  private Boolean duplicateEntry;

  FileSystem fs;
  Configuration conf;

  @Value("${sip.service.max.inprogress.mins}")
  @NotNull
  private Integer maxInprogressMins = 45;
  
  
  @Value("${sip.service.max.inprogress.job.mins}")
  @NotNull
  private Integer maxInprogressJobMins;

  private static final int LAST_MODIFIED_DEFAUTL_VAL = 0;

  @Autowired
  ChannelTypeService channelTypeService;

  @Autowired
  private SipRetryPluginFactory factory;
  
  FileProcessor processor;
  
  
  @PostConstruct
  private void init() throws Exception {
    
    processor = FileProcessorFactory.getFileProcessor(defaultDestinationLocation);

  }

  /**
   * This is method to handle inconsistency during failure. Step1: Check if any
   * long running process with 'InProgress' and mark them as failed. Step2:
   * Retrive all 'Failed' or 'HOST_NOT_REACHABLE' entries and cleans up
   * destination and update logs with 'Data_removed' Step3: Triggers transfer
   * call as part of retry
   */
  @Async("retryExecutor")
  public void recoverFromInconsistentState() {

    // Mark long running 'InProgress to 'Failed'
    sipLogService.updateLongRunningTransfers(maxInprogressMins);
    sipLogService.updateLongRunningJobs(maxInprogressJobMins);

    logger.trace("recoverFromInconsistentState execution starts here");
    int countOfRecords = sipLogService.countRetryIds(retryDiff);
    logger.trace("Count listOfRetryIds :" + countOfRecords);
    int totalNoOfPages = IntegrationUtils.calculatePages(countOfRecords,
        retryPageSize);
    logger.trace("totalNoOfPages :" + totalNoOfPages);
    for (int i = pageStart; i < totalNoOfPages; i++) {
      List<BisFileLog> logs = sipLogService.listOfRetryIds(retryDiff, i,
          retryPageSize, "checkpointDate");
      logger.trace("Data listOfRetryIds :" + logs);
      for (BisFileLog log : logs) {
        logger
            .trace("Process Id which is in inconsistent state: " + log.getPid());
        long routeId = log.getRouteSysId();
        logger.trace("Route Id which is in inconsistent state: " + routeId);
        long channelId = log.getBisChannelSysId();
        logger.trace("Channel Id which is in inconsistent state: " + channelId);
        Optional<BisRouteEntity> bisRouteEntityPresent = this
            .findRouteById(routeId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
            true);
        objectMapper
            .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        JsonNode nodeEntity = null;
        ObjectNode rootNode = null;
        try {
          BisRouteEntity bisRouteEntity;
          if (bisRouteEntityPresent.isPresent()) {
            bisRouteEntity = bisRouteEntityPresent.get();

            String routeMetadatStr = bisRouteEntity.getRouteMetadata();
            nodeEntity = objectMapper
                .readTree(routeMetadatStr);
            rootNode = (ObjectNode) nodeEntity;
            String channelType = channelTypeService
                .findChannelTypeFromRouteId(bisRouteEntity.getBisRouteSysId());
            SipRetryContract sipRetryContract = factory
                .getInstance(channelType);
            if (rootNode.get("disableDuplicate") != null
                && !rootNode.get("disableDuplicate").isNull()) {
              String disableDupFlag = rootNode.get("disableDuplicate").asText();
              Boolean isDisable = Boolean.valueOf(disableDupFlag);
              if (isDisable) {
                logger.trace("Inside isDisable starts here");
                if (sipLogService.isRouteAndChannelExists(routeId, channelId)) {
                  updateAndDeleteCorruptFiles(log, fileStatus, procesStatus);
                  // To retry only specific file instead of downloading all
                  // files
                  // in the source folder
                  if (log.getFileName() != null) {
                    logger.trace("Inside isDisable transferData starts here");
                    // SIP-6094 : this flow is related when user is set
                    // disableDuplicate as true
                    // and to update the process status as DATA_REMOVED when
                    // there is a file
                    // associated with it.
                    updateAndDeleteCorruptFiles(log,
                        BisProcessState.FAILED.value(),
                        BisComponentState.DATA_REMOVED.value());
                    sipRetryContract.retryFailedFileTransfer(channelId, routeId,
                        FilenameUtils.getName(log.getFileName()), isDisable,
                        SourceType.RETRY.name(), log.getJob().getJobId(), channelType);
                    // transferData(channelId, routeId,
                    // FilenameUtils.getName(log.getFileName()),
                    // isDisable, SourceType.RETRY.name());
                  } else {
                    logger
                        .trace("Inside isDisable transferData when starts here "
                            + "log.getFileName() is null");
                    // This transfer initiates when it is likely to be
                    // HOST_NOT_REACHABLE
                    // SIP-6094 : if HOST_NOT_REACHABLE then update the existing
                    // on
                    // instead of inserting new one
                    sipLogService.updateStatusFailed(
                        BisProcessState.FAILED.value(),
                        BisComponentState.HOST_NOT_REACHABLE.value(),
                        log.getPid());
                    logger.trace(
                        "Inside the block of retry when process status is "
                            + " inside disable block :"
                            + BisComponentState.HOST_NOT_REACHABLE.value());
                    logger.trace("Channel Id with :"
                        + BisComponentState.HOST_NOT_REACHABLE.value()
                        + " will be triggered by retry in case of isDisable duplicate "
                        + isDisable + " : " + channelId);
                    sipRetryContract.retryFailedJob(channelId, routeId,
                        log.getBisChannelType(), isDisable, log.getPid(),
                        BisComponentState.HOST_NOT_REACHABLE.value(), log.getJob().getJobId());
                    // transferRetry(channelId, routeId,
                    // log.getBisChannelType(), isDisable,
                    // log.getPid(),
                    // BisComponentState.HOST_NOT_REACHABLE.value());
                  }
                }
                logger.trace("Inside isDisable ends here");
              } else {
                // To retry only specific file instead of downloading all files
                // in
                // the in source folder
                logger.trace("Inside the block of retry when disable is not "
                    + "checked for route Id :" + routeId);
                // SIP-6094 : duplicate check has been introduced; no need to
                // retry if file
                // has been identified has duplicate
                // and to update the process status as DATA_REMOVED when there
                // is a file
                // associated with it.
                if (log.getFileName() != null && (sipLogService
                    .duplicateCheckFilename(isDisable, log.getFileName()))) {
                  updateAndDeleteCorruptFiles(log,
                      BisProcessState.FAILED.value(),
                      BisComponentState.DATA_REMOVED.value());
                  logger.trace(
                      "Inside the block of retry when file is not duplicate :"
                          + log.getPid());
                  sipRetryContract.retryFailedFileTransfer(channelId, routeId,
                      FilenameUtils.getName(log.getFileName()), isDisable,
                      SourceType.RETRY.name(),log.getJob().getJobId(), null);
                  // transferData(channelId, routeId,
                  // FilenameUtils.getName(log.getFileName()),
                  // isDisable, SourceType.RETRY.name());
                } else {
                  // This transfer initiates when it is likely to be
                  // HOST_NOT_REACHABLE
                  // SIP-6094 : if HOST_NOT_REACHABLE then update the existing
                  // on
                  // instead of inserting new one
                  sipLogService.updateStatusFailed(
                      BisProcessState.FAILED.value(),
                      BisComponentState.HOST_NOT_REACHABLE.value(),
                      log.getPid());
                  logger.trace(
                      "Inside the block of retry when process status is :"
                          + BisComponentState.HOST_NOT_REACHABLE.value());
                  // log.pid() has been added as part of SIP-6292
                  logger.trace("Channel Id with :"
                      + BisComponentState.HOST_NOT_REACHABLE.value()
                      + " will be triggered by retry in case of isDisable duplicate "
                      + isDisable + " : " + channelId);
                  sipRetryContract.retryFailedJob(channelId, routeId,
                      log.getBisChannelType(), isDisable, log.getPid(),
                      BisComponentState.HOST_NOT_REACHABLE.value(), null);
                  // transferRetry(channelId, routeId, log.getBisChannelType(),
                  // isDisable,
                  // log.getPid(),
                  // BisComponentState.HOST_NOT_REACHABLE.value());
                }
              }
            }
          } else {
            logger.trace("No route present with channelId: " + channelId
                + " routeID: " + routeId);
          }
        } catch (NotFoundException | IOException e) {
          logger.error("Exception occurred while reading duplicate attribute ",
              e);
        }
      } // end of second for loop
    } // end of first for loop
    logger.trace("recoverFromInconsistentState execution ends here");
  }

  @Transactional(TxType.REQUIRED)
  private Optional<BisRouteEntity> findRouteById(Long routeId) {
    return bisRouteDataRestRepository.findById(routeId);
  }

  /**
   * This is a common method to update the status.
   *
   * @param log          log instance which has the details.
   * @param fileStatus   file status to be entered
   * @param procesStatus component status to be updated
   */
  private void updateAndDeleteCorruptFiles(BisFileLog log, String fileStatus,
      String procesStatus) {
    sipLogService.updateStatusFailed(fileStatus, procesStatus,
        log.getPid());
    logger.info("rowId updateAndDeleteCorruptFiles: " + log.getPid());
    // The below code fix which will be part of
    // TODO : SIP-6148
    // This is known issue with this feature branch
    String fileName = null;
    if (log.getRecdFileName() != null) {
      fileName = log.getRecdFileName();
      logger.trace("Delete the corrupted file :" + fileName);
      File fileDelete = new File(fileName);
      if (fileDelete != null && fileDelete
          .getParentFile() != null) {
        logger.trace("Parent Directory deleted : " + fileDelete);
        try {
          processor.deleteFile(fileDelete.getPath(), 
              defaultDestinationLocation, mapRfsUser);
        } catch (IOException ex) {
          logger.error("Error during delete of currupted file:" 
                + ex.getMessage());
        }
      }
    } else {
      logger.trace("Corrupted file does not exist.");
    }
  }
}

package com.synchronoss.saw.apipull.plugin.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.extensions.SipRetryContract;
import com.synchronoss.saw.batch.model.BisComponentState;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.logs.constants.SourceType;
import com.synchronoss.saw.logs.entities.BisJobEntity;
import com.synchronoss.saw.logs.service.SipLogging;
import java.util.Optional;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * Used to retry failed API ingestion jobs.
 *
 * @author alok.kumarr
 * @since 3.4.0
 */
@Service("ApiPullRetryServiceImpl")
public class ApiPullRetryServiceImpl implements SipRetryContract {

  private static final Logger logger = LoggerFactory.getLogger(ApiPullRetryServiceImpl.class);

  @Autowired
  @Qualifier("apipullService")
  private ApiPullServiceImpl apiPullService;

  @Autowired private SipLogging sipLogService;

  @Override
  public void retryFailedJob(
      Long channelId,
      Long routeId,
      String channelType,
      boolean isDisable,
      String pid,
      String status,
      Long jobId)
      throws NotFoundException {
    logger.trace(
        "inside transfer retry block for channel type {} : channelId {} starts here",
        channelType,
        channelId);
    logger.trace("transferRetry with the process Id {}:", pid);

    try {
      Optional<BisRouteEntity> routeEntity = apiPullService.findRouteById(routeId);
      if (routeEntity.isPresent()) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        BisRouteEntity bisRouteEntity = routeEntity.get();

        if (bisRouteEntity.getStatus() > 0) {
          ObjectNode rootNode =
              (ObjectNode) objectMapper.readTree(bisRouteEntity.getRouteMetadata());

          BisJobEntity job = sipLogService.retriveJobById(jobId);
          job.setJobStatus("INPROGRESS");
          sipLogService.saveJob(job);

          String destinationLocation = rootNode.get("destinationLocation").asText();
          String sourceLocation = rootNode.get("sourceLocation").asText();
          logger.trace("sourceLocation inside transferRetry :" + sourceLocation);
          logger.trace("destinationLocation inside transferRetry :" + destinationLocation);
        }
      }
      if (sipLogService.checkAndDeleteLog(pid)) {
        logger.info(
            "deleted successfully the pid which had {} with pid {}",
            BisComponentState.HOST_NOT_REACHABLE,
            pid);
      }
    } catch (Exception ex) {
      logger.error(
          "Exception occurred while connecting to channel"
              + " with the channel Id: {} and with process id {}",
          channelId,
          pid,
          ex);
      sipLogService.upSertLogForExistingProcessStatus(
          channelId,
          routeId,
          BisComponentState.HOST_NOT_REACHABLE.value(),
          BisProcessState.FAILED.value(),
          SourceType.RETRY.name(),
          jobId);
    }

    logger.info(
        "inside transfer retry block for channel type : {} channelId : {} ends here.",
        channelType,
        channelId);
  }

  @Override
  public void retryFailedFileTransfer(
      Long channelId,
      Long routeId,
      String fileName,
      boolean isDisable,
      String source,
      Long jobId,
      String channelType) {
    this.apiPullService.scanFilesForPattern(
        channelId,
        routeId,
        fileName,
        isDisable,
        SourceType.RETRY.name(),
        Optional.of(jobId),
        channelType);
  }
}

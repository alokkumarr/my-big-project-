package com.synchronoss.saw.batch.plugin.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.extensions.SipRetryContract;
import com.synchronoss.saw.batch.model.BisComponentState;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.batch.sftp.integration.RuntimeSessionFactoryLocator;
import com.synchronoss.saw.batch.sftp.integration.SipLogging;
import com.synchronoss.saw.logs.constants.SourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javassist.NotFoundException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Service;


@Service
public class SftpRetryServiceImpl implements SipRetryContract {

  private static final Logger logger = LoggerFactory.getLogger(SftpRetryServiceImpl.class);
  
  @Autowired
  private RuntimeSessionFactoryLocator delegatingSessionFactory;
  
  @Autowired
  @Qualifier("sftpService")
  private SftpServiceImpl sftpServiceImpl;
  

  @Autowired
  private SipLogging sipLogService;
  
  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;

  @Override
  public void retryFailedJob(Long channelId, Long routeId, String channelType, boolean isDisable,
      String pid, String status) throws NotFoundException {

    logger.info("inside transfer retry block for channel type " + channelType + ": channelId "
        + channelId + " starts here");
    logger.info("transferRetry with the process Id :" + pid);
    final List<BisDataMetaInfo>  filesInfo = new ArrayList<>();
    // This block needs to improved in future with appropriate design pattern like
    // Abstract factory or switch block when more channel type will be added
    switch (channelType) {
      case "sftp":
        SessionFactory<LsEntry> sesionFactory =
            delegatingSessionFactory.getSessionFactory(channelId);
        try (Session<?> session = sesionFactory.getSession()) {
          if (session != null & session.isOpen()) {
            Optional<BisRouteEntity> routeEntity = this.findRouteById(routeId);
            if (routeEntity.isPresent()) {
              ObjectMapper objectMapper = new ObjectMapper();
              objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
              objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
              BisRouteEntity bisRouteEntity = routeEntity.get();
              final BisDataMetaInfo metaInfo = new BisDataMetaInfo();
              if (bisRouteEntity.getStatus() > 0) {
                JsonNode nodeEntity = null;
                ObjectNode rootNode = null;
                nodeEntity = objectMapper.readTree(bisRouteEntity.getRouteMetadata());
                rootNode = (ObjectNode) nodeEntity;
                // The below change has been made for the task SIP-6292
                
                
                String fileExclusions = null;
                if (rootNode.get("fileExclusions") != null) {
                  fileExclusions = rootNode.get("fileExclusions").asText();
                }
                metaInfo.setFilePattern(rootNode.get("filePattern").asText());
                String destinationLocation = rootNode.get("destinationLocation").asText();
                int lastModifiedHoursLmt = SftpServiceImpl.LAST_MODIFIED_DEFAUTL_VAL;
                String sourceLocation = rootNode.get("sourceLocation").asText();
                SftpRemoteFileTemplate template = new SftpRemoteFileTemplate(sesionFactory);
                if (rootNode.get("lastModifiedLimitHours") != null && !rootNode.get("lastModifiedLimitHours").equals("")) {
                  String lastModifiedLimitHours = rootNode.get("lastModifiedLimitHours").asText();
                  lastModifiedHoursLmt = Integer.valueOf(lastModifiedLimitHours);
                  logger.trace("Last modified hours limit configured:" 
                        + lastModifiedHoursLmt);
                }
                filesInfo.addAll(
                    sftpServiceImpl.transferDataFromChannel(
                        template, sourceLocation, metaInfo.getFilePattern(),
                        destinationLocation, channelId, routeId, 
                        fileExclusions, isDisable, SourceType.RETRY.name(), lastModifiedHoursLmt));
                
                logger.info("sourceLocation inside transferRetry :" + sourceLocation);
                logger.info("destinationLocation inside transferRetry :" + destinationLocation);
                logger.info(
                    "metaInfo.getFilePattern() inside transferRetry :" + metaInfo.getFilePattern());

              }
            }
          }
          // This has been added as a part of SIP-6292 change
          // when HOST_NOT_REACHABLE host is successfully connected
          // this will rectify irrespective of route active or not
          // and date are available on the source or not
          // all it make sure it host got back online
          // and after checking for existence then
          // removes the entry from the store
          if (sipLogService.checkAndDeleteLog(pid)) {
            logger.info("deleted successfully the pid which had "
                + BisComponentState.HOST_NOT_REACHABLE + " with pid " + pid);
          }
        } catch (Exception ex) {
          logger.error(
              "Exception occurred while connecting to channel with the channel Id:" + channelId
              + " and with process id " + pid,
              ex);
          sipLogService.upSertLogForExistingProcessStatus(channelId, routeId,
              BisComponentState.HOST_NOT_REACHABLE.value(), 
              BisProcessState.FAILED.value(), SourceType.RETRY.name());
        }
        break;
      case "jdbc":
        break;
      default:
        throw new NotFoundException("channelType does not support");
    }
    logger.info("inside transfer retry block for channel type " + channelType + ": channelId "
        + channelId + " ends here");
  

  }

  @Override
  public void retryFailedFileTransfer(Long channelId, Long routeId, String fileName,
      boolean isDisable, String source) {
    this.sftpServiceImpl.transferData(channelId, routeId, fileName,
        isDisable, SourceType.RETRY.name());
  }


  @Transactional(TxType.REQUIRED)
  private  Optional<BisRouteEntity>  findRouteById(Long routeId) {
    return bisRouteDataRestRepository.findById(routeId);
  }
  

}

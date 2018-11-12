package com.synchronoss.saw.batch.plugin.controllers;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisIngestionPayload;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.batch.sftp.integration.RuntimeSessionFactoryLocator;
import com.synchronoss.saw.batch.sftp.integration.SipFileFilterOnLastModifiedTime;
import com.synchronoss.saw.batch.sftp.integration.SipSftpFilter;
import com.synchronoss.saw.batch.utils.IntegrationUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowRegistration;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.remote.InputStreamCallback;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.sftp.session.SftpSession;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;



@Service("sftpService")
public class SftpServiceImpl extends SipPluginContract {

  private static final Logger logger = LoggerFactory.getLogger(SftpPluginController.class);
  
  @Autowired
  private RuntimeSessionFactoryLocator delegatingSessionFactory;

  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;

  @Autowired
  private IntegrationFlowContext flowContext;
  
  private Long timeDifference = 1000L;
  
  // TODO: It has to be enhanced to stream the logs to user interface
  //TODO: SIP-4613
  @Override
  public HttpStatus connectRoute(Long entityId) throws SftpProcessorException {
    logger.trace("connection test for the route with entity id :" + entityId);
    HttpStatus status = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    Optional<BisRouteEntity> bisRouteEntity = bisRouteDataRestRepository.findById(entityId);
    JsonNode nodeEntity = null;
    ObjectNode rootNode = null;
    if (bisRouteEntity.isPresent()) {
      BisRouteEntity entity = bisRouteEntity.get();
      try {
        nodeEntity = objectMapper.readTree(entity.getRouteMetadata());
        rootNode = (ObjectNode) nodeEntity;
        String destinationLocation = rootNode.get("destinationLocation").asText();
        File destinationPath = new File(destinationLocation);
        Paths.get(destinationLocation);
        if (destinationPath.exists()) {
          if ((destinationPath.canRead() && destinationPath.canWrite()) 
              && destinationPath.canExecute()) {
            status = HttpStatus.OK;
          }
        } else {
          status = HttpStatus.BAD_REQUEST;
          throw new SftpProcessorException("destination path does not exists");
        }
      } catch (IOException e) {
        status = HttpStatus.BAD_REQUEST;
        throw new SftpProcessorException("Exception occurred during " + entityId, e);
      } catch (InvalidPathException | NullPointerException ex) {
        status = HttpStatus.BAD_REQUEST;
        throw new SftpProcessorException("Invalid directory path " + entityId, ex);
      }
    } else {
      throw new SftpProcessorException(entityId + "does not exists");
    }
    return status;
  }

  // TODO: It has to be enhanced to stream the logs to user interface
  // TODO: SIP-4613
  @Override
   public HttpStatus connectChannel(Long entityId) throws SftpProcessorException {
    logger.trace("checking connectivity for the source id :" + entityId);
    HttpStatus  status = null;
    try {
      if (delegatingSessionFactory.getSessionFactory(entityId).getSession().isOpen()) {
        logger.info("connected successfully " + entityId);
        status = HttpStatus.OK;
        delegatingSessionFactory.getSessionFactory(entityId).getSession().close();
        delegatingSessionFactory.invalidateSessionFactoryMap();
      } else {
        status = HttpStatus.BAD_REQUEST;
      }
    } catch (Exception ex) {
      logger.info("Exception :", ex);
      status = HttpStatus.BAD_REQUEST;
    }
    return status;
  }

  //TODO : This transfer data has to be enhanced while integrating with Scheduler
  //TODO : to download a batch of files instead all files then initiate the downstream the process
  //TODO: Transfer needs some more work & and the same entity Id will not work. 
  @Override
 public HttpStatus transferData(BisIngestionPayload input) throws JsonProcessingException {
    logger.trace("transferring file from remote channel starts here");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    logger.trace("bisIngestionPayload :" + objectMapper.writeValueAsString(input));
    HttpStatus  status = null;
    SftpSession  sftp = null;
    try {
      if (connectChannel(input.getEntityId()).equals(HttpStatus.OK) 
            && connectRoute(input.getEntityId()).equals(HttpStatus.OK)) {
        logger.trace("connected successfully " + input.getEntityId());
        // getting channel details
        Optional<BisRouteEntity> entity = bisRouteDataRestRepository
              .findById(input.getEntityId());
        if (entity.isPresent()) {
          JsonNode nodeEntity = null;
          ObjectNode rootNode = null;
          nodeEntity = objectMapper.readTree(entity.get().getRouteMetadata());
          rootNode = (ObjectNode) nodeEntity;
          String filePattern = rootNode.get("filePattern").asText();

          // Creating chain file list
          ChainFileListFilter<LsEntry> list = new ChainFileListFilter<LsEntry>();
          SipSftpFilter sftpFilter = new SipSftpFilter();
          sftpFilter.setFilePatterns(filePattern);
          list.addFilter(sftpFilter);
          SipFileFilterOnLastModifiedTime  lastModifiedTime = 
                new SipFileFilterOnLastModifiedTime();
          lastModifiedTime.setTimeDifference(10000L);
          list.addFilter(lastModifiedTime);
          // Creating an integration using DSL
          QueueChannel out = new QueueChannel();
          String destinationLocation = rootNode.get("destinationLocation").asText();          
          IntegrationFlow flow = f -> f
              .handle(Sftp.outboundGateway(delegatingSessionFactory
              .getSessionFactory(input.getEntityId()), 
                AbstractRemoteFileOutboundGateway.Command.MGET,
                "payload")
                .options(AbstractRemoteFileOutboundGateway.Option.RECURSIVE, 
                AbstractRemoteFileOutboundGateway.Option.PRESERVE_TIMESTAMP)
                .filter(list)
                .localDirectory(new File(destinationLocation 
                + File.pathSeparator + getBatchId() + File.pathSeparator))
                .autoCreateLocalDirectory(true)
                .temporaryFileSuffix(".downloading")
                .localFilenameExpression("T(org.apache.commons.io.FilenameUtils).getBaseName"
                + "(#remoteFileName)+'.'+ new java.text.SimpleDateFormat("
                + "T(razorsight.mito.integration.IntegrationUtils)"
                + ".getRenameDateFormat()).format(new java.util.Date()) "
                + "+'.'+T(org.apache.commons.io.FilenameUtils).getExtension(#remoteFileName)"))
                .channel(out).log(LoggingHandler.Level.INFO.name())
                .channel(MessageChannels.direct("dynamicSftpLoggingChannel"));
          String sourceLocation = rootNode.get("sourceLocation").asText();          
          IntegrationFlowRegistration registration = this.flowContext
                .registration(flow).register();
          registration.getInputChannel().send(new GenericMessage<>(sourceLocation + "*"));   
          Message<?> result = out.receive(10_000); // will wait for 10 seconds\
          input.setMessageSource(result);
          registration.destroy();
        
          input.setChannelType(BisChannelType.SFTP);
          pullContent(input);
          status = HttpStatus.OK;
        } else {
          status = HttpStatus.BAD_REQUEST;
          throw new SftpProcessorException("There is a problem connecting either "
            + "channel or route. "
 + "Please check with system administration about the connectivity."); 
        }
      } else {
        status = HttpStatus.BAD_REQUEST;
        throw new SftpProcessorException("Entity does not exist");
      }
    // } // end of first if
    } catch (Exception ex) {
      logger.error("Exception occured while transferring file or there are no files available", ex);
      status = HttpStatus.BAD_REQUEST;
    } finally {
      if (sftp != null) {
        sftp.close();
      }
    }
    return status;
  }
  
  // TODO: It has to be enhanced to stream the logs to user interface
  // TODO: SIP-4613
  @Override
  public HttpStatus immediateConnectRoute(BisConnectionTestPayload payload) 
      throws SipNestedRuntimeException {
    logger.trace("Test connection to route starts here");
    HttpStatus status = null;
    File destinationPath = new File(payload.getDestinationLocation());
    Paths.get(payload.getDestinationLocation());
    if (destinationPath.exists()) {
      if ((destinationPath.canRead() && destinationPath.canWrite()) 
          && destinationPath.canExecute()) {
        status = HttpStatus.OK;
      }
    } else {
      status = HttpStatus.BAD_REQUEST;
      throw new SftpProcessorException("destination path does not exists");
    }
    logger.trace("Test connection to route ends here");
    return status;
  }

  
  // TODO: It has to be enhanced to stream the logs to user interface
  // TODO: SIP-4613
  @Override
 public HttpStatus immediateConnectChannel(BisConnectionTestPayload payload) 
      throws SipNestedRuntimeException {
    logger.trace("Test connection to channel starts here");
    HttpStatus status = null;
    DefaultSftpSessionFactory defaultSftpSessionFactory = null;
    try {
      defaultSftpSessionFactory = new DefaultSftpSessionFactory(true);
      defaultSftpSessionFactory.setHost(payload.getHost());
      defaultSftpSessionFactory.setPort(payload.getPort());
      defaultSftpSessionFactory.setUser(payload.getUsername());
      defaultSftpSessionFactory.setAllowUnknownKeys(true);
      Properties prop = new Properties();
      prop.setProperty("StrictHostKeyChecking", "no");
      defaultSftpSessionFactory.setSessionConfig(prop);
      defaultSftpSessionFactory.setPassword(payload.getPassword());
      if (defaultSftpSessionFactory.getSession().isOpen()) {
        status = HttpStatus.OK;
        defaultSftpSessionFactory.getSession().close();
      }
    } catch (Exception ex) {
      status = HttpStatus.BAD_REQUEST;
    } finally {
      if (defaultSftpSessionFactory != null && defaultSftpSessionFactory.getSession().isOpen()) {
        defaultSftpSessionFactory.getSession().close();
      }
    }
    return status;
  }
  
  @Override
  public List<BisDataMetaInfo> immediateTransfer(BisConnectionTestPayload payload) 
      throws SipNestedRuntimeException {
    logger.trace("Immediate Transfer file starts here");
    List<BisDataMetaInfo> transferredFiles = new ArrayList<>();
    DefaultSftpSessionFactory defaultSftpSessionFactory = null;
    try {
      defaultSftpSessionFactory = new DefaultSftpSessionFactory(true);
      defaultSftpSessionFactory.setHost(payload.getHost());
      defaultSftpSessionFactory.setPort(payload.getPort());
      defaultSftpSessionFactory.setUser(payload.getUsername());
      defaultSftpSessionFactory.setPassword(payload.getPassword());
      defaultSftpSessionFactory.setAllowUnknownKeys(true);
      Properties prop = new Properties();
      prop.setProperty("StrictHostKeyChecking", "no");
      defaultSftpSessionFactory.setSessionConfig(prop);
      if (defaultSftpSessionFactory.getSession().isOpen()) {
        SftpRemoteFileTemplate template = new SftpRemoteFileTemplate(defaultSftpSessionFactory);
        transferredFiles = listOfAll(template, payload.getSourceLocation(), 
                payload.getPattern(), payload);
        defaultSftpSessionFactory.getSession().close();
      }
    } catch (Exception ex) {
      logger.error("Exception triggered while transferring the file", ex);
      throw new SftpProcessorException("Exception triggered while transferring the file", ex);
    } finally {
      if (defaultSftpSessionFactory != null && defaultSftpSessionFactory.getSession().isOpen()) {
        defaultSftpSessionFactory.getSession().close();
      }
    }
    return transferredFiles;
  }
  
  private List<BisDataMetaInfo>  listOfAll(SftpRemoteFileTemplate template, String location, 
      String pattern, BisConnectionTestPayload payload) throws IOException {
    File localDirectory = new File(payload.getDestinationLocation() 
             + File.separator + getBatchId() + File.separator);
    if (!localDirectory.exists()) {
      localDirectory.mkdirs();
    }
    List<BisDataMetaInfo> list = new ArrayList<>(payload.getBatchSize());
    LsEntry [] files = template.list(location + File.separator + pattern);
    BisDataMetaInfo bisDataMetaInfo = null;
    for (LsEntry entry : files) {
      long lastModified = entry.getAttrs().getMTime();
      long currentTime = System.currentTimeMillis();
      if ((currentTime - lastModified) > timeDifference) {
        if (entry.getAttrs().isDir()) {
          listOfAll(template, location + File.separator + entry.getFilename(), pattern, payload);
        } else {
          if (list.size() <= payload.getBatchSize()) {
            File localFile = new File(localDirectory.getPath()
                + File.separator + FilenameUtils.getBaseName(entry.getFilename()) + "."
                + IntegrationUtils.renameFileAppender() + "." 
                + FilenameUtils.getExtension(entry.getFilename()));
            template.get(payload.getSourceLocation() + File.separator 
                + entry.getFilename(), new InputStreamCallback() {
                  @Override
                  public void doWithInputStream(InputStream stream) throws IOException {
                    FileCopyUtils.copy(StreamUtils.copyToByteArray(stream), localFile);
                  } 
                  });
            bisDataMetaInfo = new BisDataMetaInfo();
            bisDataMetaInfo.setProcessId(new UUIDGenerator()
                .generateId(bisDataMetaInfo).toString());
            bisDataMetaInfo.setDataSizeInBytes(new Long(entry.getAttrs().getSize()).doubleValue());
            bisDataMetaInfo.setActualDataName(payload.getSourceLocation() + File.separator 
                + entry.getFilename());
            bisDataMetaInfo.setReceivedDataName(localFile.getPath());
            bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
            bisDataMetaInfo.setProcessState(BisProcessState.SUCCESS.value());
            list.add(bisDataMetaInfo); 
          } else {
            break;
          }
        }
      }
    }
    return list;
  }
}

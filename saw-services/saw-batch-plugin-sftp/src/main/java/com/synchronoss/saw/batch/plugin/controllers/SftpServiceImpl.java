package com.synchronoss.saw.batch.plugin.controllers;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisComponentState;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisIngestionPayload;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.batch.sftp.integration.RuntimeSessionFactoryLocator;
import com.synchronoss.saw.batch.sftp.integration.SipFileFilterOnLastModifiedTime;
import com.synchronoss.saw.batch.sftp.integration.SipLogging;
import com.synchronoss.saw.batch.sftp.integration.SipSftpFilter;
import com.synchronoss.saw.batch.utils.IntegrationUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  private static final Logger logger = LoggerFactory.getLogger(SftpServiceImpl.class);

  @Autowired
  private RuntimeSessionFactoryLocator delegatingSessionFactory;

  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;

  @Autowired
  private SipLogging sipLogService;

  @Autowired
  private IntegrationFlowContext flowContext;

  @Value("${bis.partial-file-timeDifference}")
  @NotNull
  private Long timeDifference;

  @Value("${bis.transfer-batch-size}")
  @NotNull
  private Integer batchSize;

  @Value("${bis.default-data-drop-location}")
  @NotNull
  private String defaultDestinationLocation;

  // TODO: It has to be enhanced to stream the logs to user interface
  // TODO: SIP-4613
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
        String destinationLocation = (rootNode.get("destinationLocation").asText() != null
            ? defaultDestinationLocation + rootNode.get("destinationLocation").asText()
            : defaultDestinationLocation);
        File destinationPath = new File(destinationLocation);
        if (destinationPath.exists()) {
          if ((destinationPath.canRead() && destinationPath.canWrite())
              && destinationPath.canExecute()) {
            String sourceLocation = (rootNode.get("sourceLocation").asText());
            if (delegatingSessionFactory.getSessionFactory(entity.getBisChannelSysId()).getSession()
                .exists(sourceLocation)) {
              status = HttpStatus.OK;
            } else {
              status = HttpStatus.BAD_REQUEST;
              throw new SftpProcessorException(
                  "either path at destination " + "or source does not exists");
            }
          }
        } else {
          Files.createDirectories(Paths.get(destinationLocation));
          status = HttpStatus.OK;
        }
      } catch (AccessDeniedException e) {
        status = HttpStatus.UNAUTHORIZED;
        throw new SftpProcessorException(
            "Path does not exist or access denied for the entity" + entityId, e);
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
    HttpStatus status = null;
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

  /**
   * TODO : This transfer data has to be enhanced while integrating with Scheduler TODO : to
   * download a batch of files instead all files then initiate the downstream the process TODO:
   * Transfer needs some more work & and the same entity Id will not work.
   */
  public HttpStatus transferDataFlow(BisIngestionPayload input) throws JsonProcessingException {
    logger.trace("transferring file from remote channel starts here");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    logger.trace("bisIngestionPayload :" + objectMapper.writeValueAsString(input));
    HttpStatus status = null;
    SftpSession sftp = null;
    try {
      if (connectChannel(input.getEntityId()).equals(HttpStatus.OK)
          && connectRoute(input.getEntityId()).equals(HttpStatus.OK)) {
        logger.trace("connected successfully " + input.getEntityId());
        // getting channel details
        Optional<BisRouteEntity> entity = bisRouteDataRestRepository.findById(input.getEntityId());
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
          SipFileFilterOnLastModifiedTime lastModifiedTime = new SipFileFilterOnLastModifiedTime();
          lastModifiedTime.setTimeDifference(10000L);
          list.addFilter(lastModifiedTime);
          // Creating an integration using DSL
          QueueChannel out = new QueueChannel();
          String destinationLocation = rootNode.get("destinationLocation").asText();
          IntegrationFlow flow = f -> f
              .handle(Sftp
                  .outboundGateway(delegatingSessionFactory.getSessionFactory(input.getEntityId()),
                      AbstractRemoteFileOutboundGateway.Command.MGET, "payload")
                  .options(AbstractRemoteFileOutboundGateway.Option.RECURSIVE,
                      AbstractRemoteFileOutboundGateway.Option.PRESERVE_TIMESTAMP)
                  .filter(list)
                  .localDirectory(new File(
                      destinationLocation + File.pathSeparator + getBatchId() + File.pathSeparator))
                  .autoCreateLocalDirectory(true).temporaryFileSuffix(".downloading")
                  .localFilenameExpression("T(org.apache.commons.io.FilenameUtils).getBaseName"
                      + "(#remoteFileName)+'.'+ new java.text.SimpleDateFormat("
                      + "T(razorsight.mito.integration.IntegrationUtils)"
                      + ".getRenameDateFormat()).format(new java.util.Date()) "
                      + "+'.'+T(org.apache.commons.io.FilenameUtils)"
                      + ".getExtension(#remoteFileName)"))
              .channel(out).log(LoggingHandler.Level.INFO.name())
              .channel(MessageChannels.direct("dynamicSftpLoggingChannel"));
          String sourceLocation = rootNode.get("sourceLocation").asText();
          IntegrationFlowRegistration registration = this.flowContext.registration(flow).register();
          registration.getInputChannel().send(new GenericMessage<>(sourceLocation + "*"));
          Message<?> result = out.receive(10_000); // will wait for 10 seconds\
          input.setMessageSource(result);
          registration.destroy();

          input.setChannelType(BisChannelType.SFTP);
          pullContent(input);
          status = HttpStatus.OK;
        } else {
          status = HttpStatus.BAD_REQUEST;
          throw new SftpProcessorException(
              "There is a problem connecting either " + "channel or route. "
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
      throws SipNestedRuntimeException, IOException {
    logger.trace("Test connection to route starts here");
    HttpStatus status = null;
    String dataPath = payload.getDestinationLocation() != null
        ? defaultDestinationLocation + payload.getDestinationLocation()
        : defaultDestinationLocation;
    File destinationPath = new File(dataPath);
    if (destinationPath.exists()) {
      if ((destinationPath.canRead() && destinationPath.canWrite())
          && destinationPath.canExecute()) {
        delegatingSessionFactory.getSessionFactory(payload.getChannelId()).getSession().isOpen();
        if (!delegatingSessionFactory.getSessionFactory(payload.getChannelId()).getSession()
            .exists(payload.getSourceLocation())) {
          status = HttpStatus.BAD_REQUEST;
        } else {
          status = HttpStatus.OK;
        }
        delegatingSessionFactory.invalidateSessionFactoryMap();
      }
    } else {
      try {
        Files.createDirectories(Paths.get(dataPath));
      } catch (Exception ex) {
        status = HttpStatus.BAD_REQUEST;
        throw new SftpProcessorException(
            "Excpetion occurred while creating the directory " + "for destination", ex);
      }
      status = HttpStatus.OK;
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
      defaultSftpSessionFactory.setHost(payload.getHostName());
      defaultSftpSessionFactory.setPort(payload.getPortNo());
      defaultSftpSessionFactory.setUser(payload.getUserName());
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
    Preconditions.checkNotNull(payload.getChannelId() != null,
        "payload.getChannelId() cannot be null");
    Preconditions.checkNotNull(payload.getRouteId() != null, "payload.getRouteId() cannot be null");
    logger.trace("Immediate Transfer file starts here with the channel id " + payload.getChannelId()
        + "& route Id " + payload.getRouteId());
    List<BisDataMetaInfo> transferredFiles = new ArrayList<>();
    DefaultSftpSessionFactory defaultSftpSessionFactory = null;
    try {
      defaultSftpSessionFactory = new DefaultSftpSessionFactory(true);
      defaultSftpSessionFactory.setHost(payload.getHostName());
      defaultSftpSessionFactory.setPort(payload.getPortNo());
      defaultSftpSessionFactory.setUser(payload.getUserName());
      defaultSftpSessionFactory.setPassword(payload.getPassword());
      defaultSftpSessionFactory.setAllowUnknownKeys(true);
      Properties prop = new Properties();
      prop.setProperty("StrictHostKeyChecking", "no");
      defaultSftpSessionFactory.setSessionConfig(prop);
      if (defaultSftpSessionFactory.getSession().isOpen()) {
        logger.trace("session opened starts here ");
        SftpRemoteFileTemplate template = new SftpRemoteFileTemplate(defaultSftpSessionFactory);
        logger.trace("invocation of method immediatelistOfAll with location starts here "
            + payload.getSourceLocation() + " & file pattern " + payload.getFilePattern());
        transferredFiles = immediatelistOfAll(template, payload.getSourceLocation(),
            payload.getFilePattern(), payload);
        logger.trace("invocation of method immediatelistOfAll with location ends here "
            + payload.getSourceLocation() + " & file pattern " + payload.getFilePattern());
        defaultSftpSessionFactory.getSession().close();
        logger.trace("session opened closes here ");
      }
    } catch (Exception ex) {
      logger.error("Exception triggered while transferring the file", ex);
      throw new SftpProcessorException("Exception triggered while transferring the file", ex);
    } finally {
      if (defaultSftpSessionFactory != null && defaultSftpSessionFactory.getSession().isOpen()) {
        defaultSftpSessionFactory.getSession().close();
      }
    }
    logger.trace("Immediate Transfer file ends here with the channel id " + payload.getChannelId()
        + "& route Id " + payload.getRouteId());
    return transferredFiles;
  }

  private List<BisDataMetaInfo> immediatelistOfAll(SftpRemoteFileTemplate template, String location,
      String pattern, BisConnectionTestPayload payload) throws IOException, ParseException {
    List<BisDataMetaInfo> list = new ArrayList<>(payload.getBatchSize());
    LsEntry[] files = template.list(location + File.separator + pattern);
    BisDataMetaInfo bisDataMetaInfo = null;
    for (LsEntry entry : files) {
      long lastModified = entry.getAttrs().getMTime();
      long currentTime = System.currentTimeMillis();
      if ((currentTime - lastModified) > timeDifference) {
        if (entry.getAttrs().isDir()) {
          logger.trace("invocation of method immediatelistOfAll when directory "
              + "is availble in destination with location starts here "
              + payload.getSourceLocation() + " & file pattern " + payload.getFilePattern());
          immediatelistOfAll(template, location + File.separator + entry.getFilename(), pattern,
              payload);
          logger.trace("invocation of method immediatelistOfAll when directory "
              + "is availble in destination with location ends here " + payload.getSourceLocation()
              + " & file pattern " + payload.getFilePattern());
        } else {
          if (list.size() <= batchSize && entry.getAttrs().getSize() != 0) {
            String destination = payload.getDestinationLocation() != null
                ? defaultDestinationLocation + payload.getDestinationLocation()
                : defaultDestinationLocation;
            logger.trace("file from the source is downnloaded in the location :" + destination);
            File localDirectory =
                new File(destination + File.separator + getBatchId() + File.separator);
            logger.trace("directory where the file will be downnloaded  :"
                + localDirectory.getAbsolutePath());
            if (!localDirectory.exists()) {
              logger.trace("directory where the file will be downnloaded "
                  + "does not exist so it will be created :" + localDirectory.getAbsolutePath());
              localDirectory.mkdirs();
            }
            File localFile = new File(localDirectory.getPath() + File.separator
                + FilenameUtils.getBaseName(entry.getFilename()) + "."
                + IntegrationUtils.renameFileAppender() + "."
                + FilenameUtils.getExtension(entry.getFilename()));
            logger.trace("Actual file name after downloaded in the  :"
                + localDirectory.getAbsolutePath() + " file name " + localFile.getName());
            template.get(payload.getSourceLocation() + File.separator + entry.getFilename(),
                new InputStreamCallback() {
                  @Override
                  public void doWithInputStream(InputStream stream) throws IOException {
                    logger.trace("Streaming the content of the file in the directory starts here "
                        + entry.getFilename());
                    FileCopyUtils.copy(StreamUtils.copyToByteArray(stream), localFile);
                    logger.trace("Streaming the content of the file in the directory ends here "
                        + entry.getFilename());
                  }
                });
            bisDataMetaInfo = new BisDataMetaInfo();
            bisDataMetaInfo
                .setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
            bisDataMetaInfo.setDataSizeInBytes(entry.getAttrs().getSize());
            bisDataMetaInfo.setActualDataName(
                payload.getSourceLocation() + File.separator + entry.getFilename());
            bisDataMetaInfo.setReceivedDataName(localFile.getPath());
            bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
            bisDataMetaInfo.setProcessState(BisProcessState.SUCCESS.value());
            bisDataMetaInfo
                .setActualReceiveDate(new Date(((long) entry.getAttrs().getATime()) * 1000L));
            list.add(bisDataMetaInfo);
          } else {
            break;
          }
        }
      }
    }
    logger.trace("list of the files downloaded using immediatelistOfAll()  " + list);
    return list;
  }

  @Override
  public List<BisDataMetaInfo> transferData(Long channelId, Long routeId)
      throws SipNestedRuntimeException {
    Preconditions.checkNotNull(channelId != null, "payload.getChannelId() cannot be null");
    Preconditions.checkNotNull(routeId != null, "payload.getRouteId() cannot be null");
    logger.trace("Immediate Transfer file starts here with the channel id " + channelId
        + "& route Id " + routeId);
    logger.trace("Transfer starts here with an channel" + channelId + "and routeId " + routeId);
    List<BisDataMetaInfo> listOfFiles = new ArrayList<>();
    try {
      if (delegatingSessionFactory.getSessionFactory(channelId).getSession().isOpen()) {
        logger.info("connected successfully " + channelId);
        logger.trace("session opened starts here ");
        Optional<BisRouteEntity> channelEntity = bisRouteDataRestRepository.findById(routeId);
        if (channelEntity.isPresent()) {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
          objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
          BisRouteEntity bisChannelEntity = channelEntity.get();
          JsonNode nodeEntity = null;
          ObjectNode rootNode = null;
          nodeEntity = objectMapper.readTree(bisChannelEntity.getRouteMetadata());
          rootNode = (ObjectNode) nodeEntity;
          String sourceLocation = rootNode.get("sourceLocation").asText();
          logger.trace("sourceLocation from routeId " + routeId + " location " + sourceLocation);
          String destinationLocation = rootNode.get("destinationLocation").asText();
          logger.trace(
              "destinationLocation from routeId " + routeId + " location " + destinationLocation);
          String filePattern = rootNode.get("filePattern").asText();
          logger.trace("filePattern from routeId " + routeId + " filePattern " + filePattern);
          SftpRemoteFileTemplate template =
              new SftpRemoteFileTemplate(delegatingSessionFactory.getSessionFactory(channelId));
          logger.trace("invocation of method transferData when "
              + "directory is availble in destination with location starts here " + sourceLocation
              + " & file pattern " + filePattern);
          listOfFiles = transferDataFromChannel(template, sourceLocation, filePattern,
              destinationLocation, channelId, routeId);
          logger.trace("invocation of method transferData when "
              + "directory is availble in destination with location ends here " + sourceLocation
              + " & file pattern " + filePattern);
          delegatingSessionFactory.getSessionFactory(channelId).getSession().close();
          delegatingSessionFactory.invalidateSessionFactoryMap();
          logger.trace("session opened closes here ");
        } else {
          throw new SftpProcessorException("Exception occurred while connecting to channel");
        }
      }
    } catch (Exception ex) {
      logger.info("Exception occurred while connecting to channel :", ex);
      throw new SftpProcessorException("Exception occurred while connecting to channel", ex);
    } finally {
      if (delegatingSessionFactory.getSessionFactory(channelId).getSession().isOpen()) {
        logger.trace("session opened closes here in final block ");
        delegatingSessionFactory.getSessionFactory(channelId).getSession().close();
        delegatingSessionFactory.invalidateSessionFactoryMap();
      }
    }
    logger.trace("Transfer ends here with an channel " + channelId + " and routeId " + routeId);
    return listOfFiles;
  }

  private List<BisDataMetaInfo> transferDataFromChannel(SftpRemoteFileTemplate template,
      String sourcelocation, String pattern, String destinationLocation, Long channelId,
      Long routeId) throws IOException, ParseException {
    List<BisDataMetaInfo> list = new ArrayList<>(batchSize);
    LsEntry[] files = template.list(sourcelocation + File.separator + pattern);
    BisDataMetaInfo bisDataMetaInfo = null;
    for (LsEntry entry : files) {
      long lastModified = entry.getAttrs().getMTime();
      long currentTime = System.currentTimeMillis();
      if ((currentTime - lastModified) > timeDifference) {
        if (entry.getAttrs().isDir()) {
          logger.trace("invocation of method transferDataFromChannel "
              + "when directory is availble in destination with location starts here "
              + sourcelocation + " & file pattern " + pattern + " with channel Id " + channelId
              + " & route Id" + routeId);
          transferDataFromChannel(template, sourcelocation + File.separator + entry.getFilename(),
              pattern, destinationLocation, channelId, routeId);
          logger.trace("invocation of method transferDataFromChannel"
              + " when directory is availble in destination with location ends here "
              + sourcelocation + " & file pattern " + pattern + " with channel Id " + channelId
              + " & route Id" + routeId);
        } else {
          File fileTobeDeleted = null;
          try {
            if ((list.size() <= batchSize && entry.getAttrs().getSize() != 0) && !sipLogService
                .checkDuplicateFile(sourcelocation + File.separator + entry.getFilename())) {
              logger.trace("file duplication completed " + sourcelocation + File.separator
                  + entry.getFilename() + " batchSize " + batchSize);
              logger.trace(
                  "file from the source is downnloaded in the location :" + destinationLocation);
              File localDirectory = new File(defaultDestinationLocation + destinationLocation
                  + File.separator + getBatchId() + File.separator);
              logger.trace("directory where the file will be downnloaded  :"
                  + localDirectory.getAbsolutePath());
              if (!localDirectory.exists()) {
                logger.trace("directory where the file will be"
                    + " downnloaded does not exist so it will be created :"
                    + localDirectory.getAbsolutePath());
                localDirectory.mkdirs();
              }
              final File localFile = new File(localDirectory.getPath() + File.separator
                  + FilenameUtils.getBaseName(entry.getFilename()) + "."
                  + IntegrationUtils.renameFileAppender() + "."
                  + FilenameUtils.getExtension(entry.getFilename()));
              fileTobeDeleted = localDirectory;
              bisDataMetaInfo = new BisDataMetaInfo();
              bisDataMetaInfo
                  .setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
              bisDataMetaInfo.setReceivedDataName(localFile.getPath());
              bisDataMetaInfo.setDataSizeInBytes(entry.getAttrs().getSize());
              bisDataMetaInfo
                  .setActualDataName(sourcelocation + File.separator + entry.getFilename());
              bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
              bisDataMetaInfo.setProcessState(BisProcessState.INPROGRESS.value());
              bisDataMetaInfo
                  .setActualReceiveDate(new Date(((long) entry.getAttrs().getATime()) * 1000L));
              bisDataMetaInfo.setChannelId(channelId);
              bisDataMetaInfo.setRouteId(channelId);
              sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
              logger.trace("Actual file name after downloaded in the  :"
                  + localDirectory.getAbsolutePath() + " file name " + localFile.getName());
              template.get(sourcelocation + File.separator + entry.getFilename(),
                  new InputStreamCallback() {
                    @Override
                    public void doWithInputStream(InputStream stream) throws IOException {
                      logger.trace("Streaming the content of the file in the directory starts here "
                          + entry.getFilename());
                      FileCopyUtils.copy(StreamUtils.copyToByteArray(stream), localFile);
                      logger.trace("Streaming the content of the file in the directory ends here "
                          + entry.getFilename());
                    }
                  });
              boolean userHasPermissionsToWriteFile =
                  entry.getAttrs() != null && ((entry.getAttrs().getPermissions() & 00200) != 0)
                      && entry.getAttrs().getUId() != 0;
              if (userHasPermissionsToWriteFile) {
                logger.trace("the current user session have privileges to write on the location :"
                    + userHasPermissionsToWriteFile);
              } else {
                logger.trace(
                    "the current user session does not have privileges to write on the location :"
                        + userHasPermissionsToWriteFile);
              }
              bisDataMetaInfo.setProcessState(BisProcessState.SUCCESS.value());
              bisDataMetaInfo.setComponentState(BisComponentState.DATA_RECEIVED.value());
              sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
              list.add(bisDataMetaInfo);
            } else {
              if (list.size() == batchSize) {
                logger.trace("list of the files downloaded using transferDataFromChannel()  " + list
                    + "reached the limit of batcSize " + batchSize);
                break;
              } else {
                if (sipLogService
                    .checkDuplicateFile(sourcelocation + File.separator + entry.getFilename())) {
                  bisDataMetaInfo = new BisDataMetaInfo();
                  bisDataMetaInfo
                      .setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
                  bisDataMetaInfo.setDataSizeInBytes(entry.getAttrs().getSize());
                  bisDataMetaInfo
                      .setActualDataName(sourcelocation + File.separator + entry.getFilename());
                  bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
                  bisDataMetaInfo.setProcessState(BisProcessState.INPROGRESS.value());
                  bisDataMetaInfo
                      .setActualReceiveDate(new Date(((long) entry.getAttrs().getATime()) * 1000L));
                  bisDataMetaInfo.setChannelId(channelId);
                  bisDataMetaInfo.setRouteId(channelId);
                  bisDataMetaInfo.setProcessState(BisProcessState.FAILED.value());
                  bisDataMetaInfo.setReasonCode(BisProcessState.DUPLICATE.value());
                  list.add(bisDataMetaInfo);
                }
              }
            }
          } catch (Exception ex) {
            logger.error("Exception occurred while transferring the file from channel", ex);
            if (fileTobeDeleted.exists()) {
              logger.trace(" files or directory to be deleted on exception " + fileTobeDeleted);
              fileTobeDeleted.delete();
            }
            bisDataMetaInfo.setComponentState(BisComponentState.DATA_REMOVED.value());
            bisDataMetaInfo.setProcessState(BisProcessState.FAILED.value());
            sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
          }
        }
      }
    }
    return list;
  }
}

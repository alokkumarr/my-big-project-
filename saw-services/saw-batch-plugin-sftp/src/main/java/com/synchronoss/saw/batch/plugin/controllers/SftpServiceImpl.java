package com.synchronoss.saw.batch.plugin.controllers;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.fasterxml.jackson.core.JsonParser;
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
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.batch.sftp.integration.RuntimeSessionFactoryLocator;
import com.synchronoss.saw.batch.sftp.integration.SipLogging;
import com.synchronoss.saw.batch.utils.IntegrationUtils;
import com.synchronoss.saw.logs.entities.BisFileLog;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.integration.file.remote.InputStreamCallback;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.sftp.session.SftpSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import sncr.bda.core.file.FileProcessor;
import sncr.bda.core.file.FileProcessorFactory;
import sncr.bda.core.file.HFileOperations;

@Service("sftpService")
public class SftpServiceImpl extends SipPluginContract {

  private static final Logger logger = LoggerFactory.getLogger(SftpServiceImpl.class);

  @Autowired
  private RuntimeSessionFactoryLocator delegatingSessionFactory;

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
  
  
  private  FileProcessor processor;
  FileSystem fs;
  Configuration conf;
  
  @PostConstruct
  private void init() throws Exception {
    
    processor = FileProcessorFactory.getFileProcessor(defaultDestinationLocation);

    if (!processor.isDestinationExists(defaultDestinationLocation)) {
      logger.info("Defautl drop location not found");
      logger.info("Creating folders for default drop location :: " 
          + defaultDestinationLocation);
      
      processor.createDestination(defaultDestinationLocation, new StringBuffer());
      
      logger.info("Default drop location folders created? :: " 
          + processor.isDestinationExists(defaultDestinationLocation));
    }
    
    String location = defaultDestinationLocation
        .replace(processor.maprFsPrefix, "");
    conf = new Configuration();
    conf.set("hadoop.job.ugi", mapRfsUser);
    fs = FileSystem.get(URI.create(location), conf);

    
  }

  @Override
  public String connectRoute(Long entityId) throws SftpProcessorException {
    logger.trace("connection test for the route with entity id starts here :" + entityId);
    StringBuffer connectionLogs = new StringBuffer();
    String newLineChar = System.getProperty("line.separator");
    HttpStatus status = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    Optional<BisRouteEntity> bisRouteEntity = bisRouteDataRestRepository.findById(entityId);
    JsonNode nodeEntity = null;
    ObjectNode rootNode = null;
    if (bisRouteEntity.isPresent()) {
      BisRouteEntity entity = bisRouteEntity.get();
      SessionFactory<LsEntry> sessionFactory =
          delegatingSessionFactory.getSessionFactory(entity.getBisChannelSysId());
      try (Session<?> session = sessionFactory.getSession()) {
        nodeEntity = objectMapper.readTree(entity.getRouteMetadata());
        rootNode = (ObjectNode) nodeEntity;
        String destinationLoc = this.constructDestinationPath(
            rootNode.get("destinationLocation").asText());
        logger.trace("destination location configured:: " + destinationLoc);
        if (destinationLoc != null) {
          if (!destinationLoc.startsWith(File.separator)) {
            destinationLoc = File.separator + destinationLoc;
          }
        }
        logger.trace("destination location resolved:: " + destinationLoc);
        connectionLogs.append("Starting Test connectivity....");
        connectionLogs.append(newLineChar);
        connectionLogs.append("Establishing connection to host");
        String destinationLocation = this.processor.getFilePath(
            this.defaultDestinationLocation, destinationLoc, "");
        logger.info("Is destination directories exists?:: " 
            + processor.isDestinationExists(destinationLocation));
        if (!processor.isDestinationExists(destinationLocation)) {
          connectionLogs.append(newLineChar);
          logger.info("Destination directories doesnt exists. Creating...");
          connectionLogs.append("Destination directories doesnt exists. Creating...");
          processor.createDestination(destinationLocation,  connectionLogs);
          connectionLogs.append(newLineChar);
          connectionLogs.append("Destination directories created scucessfully!!");
          logger.info("Destination directories created scucessfully!!");
        }
        if (processor.isDestinationExists(destinationLocation)) {
          if (processor.isFileExistsWithPermissions(destinationLocation)) {
            String sourceLocation = (rootNode.get("sourceLocation").asText());
            connectionLogs.append(newLineChar);
            connectionLogs.append("Connecting to source location " + sourceLocation);
            logger.info("Connecting to source location " + sourceLocation);
            connectionLogs.append(newLineChar);
            logger.info("Connecting to destination location " + destinationLocation);
            connectionLogs.append("Connecting to destination location " + destinationLocation);
            connectionLogs.append(newLineChar);
            connectionLogs.append("Connecting...");
            if (session.exists(sourceLocation)) {
              connectionLogs.append("Connection successful!!");
              logger.info("Connection successful!!");
              status = HttpStatus.OK;
              connectionLogs.append(newLineChar);
              connectionLogs.append(status);
            } else {
              connectionLogs.append(newLineChar);
              connectionLogs.append("Unable to establish connection");
              status = HttpStatus.UNAUTHORIZED;
              connectionLogs.append(newLineChar);
              connectionLogs.append(status);
            }
            if (session.isOpen()) {
              session.close();
            }
          }
        }
      } catch (AccessDeniedException e) {
        status = HttpStatus.UNAUTHORIZED;
        connectionLogs.append(newLineChar);
        connectionLogs.append("Path does not exists or Access Denied.");
        connectionLogs.append(newLineChar);
        connectionLogs.append(status);
        logger.error("Path does not exist or access denied for the entity" + entityId, e);
      } catch (IOException e) {
        status = HttpStatus.UNAUTHORIZED;
        connectionLogs.append(newLineChar);
        connectionLogs.append("Exception occured");
        connectionLogs.append(newLineChar);
        connectionLogs.append(status);
        logger.error("Exception occurred during " + entityId, e);
      } catch (InvalidPathException | NullPointerException ex) {
        status = HttpStatus.UNAUTHORIZED;
        connectionLogs.append(newLineChar);
        connectionLogs.append("Invalid path or some exception");
        connectionLogs.append(newLineChar);
        connectionLogs.append(status);
        logger.error("Invalid directory path " + entityId, ex);
      } catch (Exception ex) {
        status = HttpStatus.UNAUTHORIZED;
        connectionLogs.append(newLineChar);
        connectionLogs.append("Exception");
        connectionLogs.append(newLineChar);
        connectionLogs.append(status);
        logger.error("Exception: " + ex.getMessage());
      }
    } else {
      throw new SftpProcessorException(entityId + " does not exists");
    }
    logger.trace("connection test for the route with entity id ends here :" + entityId);
    return connectionLogs.toString();
  }

  @Override
  public String connectChannel(Long entityId) {
    logger.trace("checking connectivity for the source id :" + entityId);

    HttpStatus status = null;
    StringBuffer connectionLogs = new StringBuffer();
    String newLineChar = System.getProperty("line.separator");
    connectionLogs.append("Starting Test connectivity....");
    connectionLogs.append(newLineChar);
    connectionLogs.append("Establishing connection to host");
    connectionLogs.append(newLineChar);
    connectionLogs.append("Connecting...");
    SessionFactory<LsEntry> sessionFactory = delegatingSessionFactory.getSessionFactory(entityId);
    try (Session<?> session = sessionFactory.getSession()) {
      if (session.isOpen()) {
        logger.info("connected successfully " + entityId);
        connectionLogs.append("Connection successful!!");
        status = HttpStatus.OK;
      } else {
        status = HttpStatus.UNAUTHORIZED;
        connectionLogs.append(newLineChar);
        connectionLogs.append("Connection Failed!!");
        connectionLogs.append(newLineChar);
        connectionLogs.append("Unable to establish connection");
        connectionLogs.append(newLineChar);
        connectionLogs.append(status);
      }
    } catch (Exception ex) {
      logger.info("Exception :", ex);
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      ex.printStackTrace(pw);
      connectionLogs.append(newLineChar);
      connectionLogs.append("Connection Failed!!");
      connectionLogs.append(newLineChar);
      connectionLogs.append("Unable to establish connection with below exception");
      connectionLogs.append(newLineChar);
      connectionLogs.append(ex.getMessage());
      connectionLogs.append(newLineChar);
      connectionLogs.append("Make sure host is valid and reachable");
      if (sw.toString().contains("java.net.UnknownHostException")) {
        connectionLogs.append(newLineChar);
        connectionLogs.append("java.net.UnknownHostException. Invalid host name");
      }
      if (sw.toString().contains("Too many authentication failures")) {
        connectionLogs.append(newLineChar);
        connectionLogs.append("Invalid user name or password");
      }
      status = HttpStatus.UNAUTHORIZED;
    }
    return connectionLogs.toString();
  }

  @Override
  public String immediateConnectRoute(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException, IOException {
    logger.trace("Test connection to route starts here");
    StringBuffer connectionLogs = new StringBuffer();
    String newLineChar = System.getProperty("line.separator");
    connectionLogs.append("Starting Test connectivity....");
    connectionLogs.append(newLineChar);
    connectionLogs.append("Establishing connection to host");
    connectionLogs.append(newLineChar);
    connectionLogs.append("Connecting...");
    HttpStatus status = null;
    String destinationLoc = this.constructDestinationPath(
        payload.getDestinationLocation());
  
    String dataPath = this.processor.getFilePath(
        this.defaultDestinationLocation, destinationLoc, "");
    logger.trace("Destination path: " + dataPath);
    logger.trace("Checking permissions for destination path: " + dataPath);
    try {
      connectionLogs.append(newLineChar);
      connectionLogs.append("Destination directory exists? :: " 
          + processor.isDestinationExists(dataPath));
      if (!processor.isDestinationExists(dataPath)) {
        connectionLogs.append(newLineChar);
       
        connectionLogs.append("Creating directories");
        processor.createDestination(dataPath, connectionLogs);
        connectionLogs.append(newLineChar);
        connectionLogs.append("Created directories");
      }
    
      if (processor.isDestinationExists(dataPath)) {
        if (processor.isFileExistsWithPermissions(dataPath)) {
          SessionFactory<LsEntry> sessionFactory = delegatingSessionFactory
              .getSessionFactory(payload.getChannelId());
          Session session = sessionFactory.getSession();
          if (!session
              .exists(payload.getSourceLocation())) {
            status = HttpStatus.UNAUTHORIZED;
            connectionLogs.append(newLineChar);
            connectionLogs
              .append("Source or destination location may not " + "exists!! or no permissions!!");
            connectionLogs.append(newLineChar);
            connectionLogs.append(status);
          } else {
            status = HttpStatus.OK;
            connectionLogs.append(newLineChar);
            connectionLogs.append("Connection successful!!");
            connectionLogs.append(newLineChar);
            connectionLogs.append(status);
          }
          if (session.isOpen()) {
            session.close();
          }
        } else {
          connectionLogs.append(newLineChar);
          connectionLogs.append("Destination directories "
              + "exists but no permission to `Read/Write/Execute'");
          logger.info("Destination directories "
              + "exists but no permission to `Read/Write/Execute'");
        }
      } else {
        connectionLogs.append(newLineChar);
        connectionLogs.append(
            "Destination directories " + "exists but no permission to `Read/Write/Execute'");
        logger
            .info("Destination directories " + "exists but no permission to `Read/Write/Execute'");
      }
    } catch (Exception ex) {
      status = HttpStatus.UNAUTHORIZED;
      logger.error("Excpetion occurred while creating the directory " + "for destination", ex);
      connectionLogs.append(newLineChar);
      connectionLogs.append("Exception occured while creating directories");
    }
    status = HttpStatus.OK;
    logger.trace("Test connection to route ends here");
    return connectionLogs.toString();
  }


  @Override
  public String immediateConnectChannel(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException {
    logger.trace("Test connection to channel starts here.");
    HttpStatus status = null;

    DefaultSftpSessionFactory defaultSftpSessionFactory = null;
    SftpSession sftpSession = null;
    StringBuffer connectionLogs = new StringBuffer();
    String newLineChar = System.getProperty("line.separator");
    connectionLogs.append("Starting Test connectivity....");
    connectionLogs.append(newLineChar);
    connectionLogs.append("Establishing connection to host: " + payload.getHostName() + " at port: "
        + payload.getPortNo());
    connectionLogs.append(newLineChar);
    connectionLogs.append("user name: " + payload.getUserName());
    connectionLogs.append(newLineChar);
    connectionLogs.append("Connecting...");
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
      sftpSession = defaultSftpSessionFactory.getSession();
      if (sftpSession != null && sftpSession.isOpen()) {
        status = HttpStatus.OK;
        connectionLogs.append(newLineChar);
        connectionLogs.append("Connection successful!!");
        connectionLogs.append(newLineChar);
        connectionLogs.append("closing connection");
        connectionLogs.append(newLineChar);
        connectionLogs.append(status);
        defaultSftpSessionFactory.getSession().close();
      }
    } catch (Exception ex) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      ex.printStackTrace(pw);
      status = HttpStatus.UNAUTHORIZED;
      connectionLogs.append(newLineChar);
      connectionLogs.append("Connection Failed!!");
      connectionLogs.append(newLineChar);
      connectionLogs.append("Unable to establish connection with below exception");
      connectionLogs.append(newLineChar);
      connectionLogs.append(ex.getMessage());
      connectionLogs.append(newLineChar);
      connectionLogs.append("Make sure host is valid and reachable");
      if (sw.toString().contains("java.net.UnknownHostException")) {
        connectionLogs.append(newLineChar);
        connectionLogs
            .append("java.net.UnknownHostException. Invalid host name" + payload.getHostName());
      }
      if (sw.toString().contains("Too many authentication failures")) {
        connectionLogs.append(newLineChar);
        connectionLogs.append("Invalid user name or password");
      }
      connectionLogs.append(newLineChar);
      connectionLogs.append(status);
      logger.error("Exception occurred while using immediateConnectChannel method ", ex);
    } finally {
      if (sftpSession != null && sftpSession.isOpen()) {
        logger.trace("closing connection from finally block");
        connectionLogs.append(newLineChar);
        connectionLogs.append("Disconnected....");
        sftpSession.close();
      }
    }
    logger.trace("Test connection to channel ends here.");
    return connectionLogs.toString();

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
    defaultSftpSessionFactory = new DefaultSftpSessionFactory(true);
    defaultSftpSessionFactory.setHost(payload.getHostName());
    defaultSftpSessionFactory.setPort(payload.getPortNo());
    defaultSftpSessionFactory.setUser(payload.getUserName());
    defaultSftpSessionFactory.setPassword(payload.getPassword());
    defaultSftpSessionFactory.setAllowUnknownKeys(true);
    Properties prop = new Properties();
    prop.setProperty("StrictHostKeyChecking", "no");
    defaultSftpSessionFactory.setSessionConfig(prop);
    try (Session<?> session = defaultSftpSessionFactory.getSession()) {
      logger.trace("session opened starts here ");
      SftpRemoteFileTemplate template = new SftpRemoteFileTemplate(defaultSftpSessionFactory);
      logger.trace("invocation of method immediatelistOfAll with location starts here "
          + payload.getSourceLocation() + " & file pattern " + payload.getFilePattern());
      transferredFiles = immediatelistOfAll(template, payload.getSourceLocation(),
          payload.getFilePattern(), payload);
      logger.trace("invocation of method immediatelistOfAll with location ends here "
          + payload.getSourceLocation() + " & file pattern " + payload.getFilePattern());
      session.close();
      template.getSession().close();
      logger.trace("session opened closes here ");
    } catch (Exception ex) {
      logger.error("Exception triggered while transferring the file", ex);
      sipLogService.updateLogs(Long.valueOf(payload.getChannelId()),
          Long.valueOf(payload.getRouteId()), BisComponentState.HOST_NOT_REACHABLE.value());
      throw new SftpProcessorException("Exception triggered while transferring the file", ex);
    } finally {
      if (defaultSftpSessionFactory != null && defaultSftpSessionFactory.getSession() != null) {
        if (defaultSftpSessionFactory.getSession().isOpen()) {
          defaultSftpSessionFactory.getSession().close();
        }
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
    long lastModifiedDate = 0L;
    for (LsEntry entry : files) {
      logger.trace("entry :" + entry.getFilename());
      long modifiedDate = new Date(entry.getAttrs().getMTime() * 1000L).getTime();
      logger.trace("modifiedDate :" + modifiedDate);
      for (int i = 0; i < retries; i++) {
        lastModifiedDate = new Date(
            template.list(location + File.separator + entry.getFilename())[0].getAttrs().getMTime()
                * 1000L).getTime();
      }
      logger.trace("lastModifiedDate :" + lastModifiedDate);
      logger.trace("lastModifiedDate - modifiedDate :" + (modifiedDate - lastModifiedDate));
      if ((lastModifiedDate - modifiedDate) == 0) {
        logger.trace("invocation of method immediatelistOfAll when directory "
            + "is availble in destination with location starts here " + payload.getSourceLocation()
            + " & file pattern " + payload.getFilePattern());
        immediatelistOfAll(template, location + File.separator + entry.getFilename(), pattern,
            payload);
        logger.trace("invocation of method immediatelistOfAll when directory "
            + "is availble in destination with location ends here " + payload.getSourceLocation()
            + " & file pattern " + payload.getFilePattern());
      } else {
        if (list.size() <= batchSize && entry.getAttrs().getSize() != 0) {
          
          String destination = this.constructDestinationPath(payload.getDestinationLocation());
          String path = processor.getFilePath(defaultDestinationLocation,
                   destination, File.separator + getBatchId());
          File localDirectory  = new File(path);
          logger.trace(
              "directory where the file will be downnloaded  :" + localDirectory.getAbsolutePath());
          try {
            if (!this.processor.isDestinationExists(localDirectory.getPath())) {
              logger.trace("directory where the file will be downnloaded "
                  + "does not exist so it will be created :" + localDirectory.getAbsolutePath());
              this.processor.createDestination(localDirectory.getPath(), new StringBuffer());
            }
          } catch (Exception e) {
            logger.error("Exception while checking destination location" + e.getMessage());
          }
          File localFile = createTargetFile(localDirectory, entry);
          logger.trace("Actual file name after downloaded in the  :"
              + localDirectory.getAbsolutePath() + " file name " + localFile.getName());
          template.get(payload.getSourceLocation() + File.separator + entry.getFilename(),
              new InputStreamCallback() {
                @Override
                public void doWithInputStream(InputStream stream) throws IOException {
                  try {
                    if (stream != null) {
                      
                      logger.trace("Streaming the content of the file in the directory starts here "
                          + entry.getFilename());
                      processor.transferFile(stream, localFile, 
                          defaultDestinationLocation, mapRfsUser);
                      logger.trace("Streaming the content of the file in the directory ends here "
                          + entry.getFilename());
                    }
                  } catch (Exception ex) {
                    logger.error("Exception occurred while writing file to the file system", ex);
                  } finally {
                      logger.trace("in finally block closing the stream");
                  }
                }
              });
          bisDataMetaInfo = new BisDataMetaInfo();
          bisDataMetaInfo.setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
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
    logger.trace("list of the files downloaded using immediatelistOfAll()  " + list);
    return list;
  }


  @Override
  public List<BisDataMetaInfo> transferData(Long channelId, Long routeId, String filePattern,
      boolean isDisable) throws SipNestedRuntimeException {
    Preconditions.checkNotNull(channelId != null, "payload.getChannelId() cannot be null");
    Preconditions.checkNotNull(routeId != null, "payload.getRouteId() cannot be null");
    logger.trace(
        "transferData file starts here with the channel id " + channelId + "& route Id " + routeId);
    logger.trace("Transfer starts here with an channel" + channelId + "and routeId " + routeId);
    List<BisDataMetaInfo> listOfFiles = new ArrayList<>();
    SessionFactory<LsEntry> sesionFactory = delegatingSessionFactory.getSessionFactory(channelId);
    try (Session<?> session = sesionFactory.getSession()) {
      if (session != null & session.isOpen()) {
        logger.info("connected successfully " + channelId);
        logger.trace("session opened starts here ");
        Optional<BisRouteEntity> routeEntity = bisRouteDataRestRepository.findById(routeId);
        if (routeEntity.isPresent()) {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
          objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
          BisRouteEntity bisRouteEntity = routeEntity.get();
          if (bisRouteEntity.getStatus() > 0) {
            JsonNode nodeEntity = null;
            ObjectNode rootNode = null;
            nodeEntity = objectMapper.readTree(bisRouteEntity.getRouteMetadata());
            rootNode = (ObjectNode) nodeEntity;
            String sourceLocation = rootNode.get("sourceLocation").asText();
            if (rootNode.get("batchSize") != null) {
              Integer batchSize = rootNode.get("batchSize").asInt();
              if (batchSize > 0) {
                logger.trace("batchSize sent as part of route metadata :" + batchSize);
                setBatchSize(batchSize);
              }
            }
            logger.trace("sourceLocation from routeId " + routeId + " location " + sourceLocation);
            String destinationLocation = rootNode.get("destinationLocation").asText();
            logger.trace(
                "destinationLocation from routeId " + routeId + " location " + destinationLocation);
            if (filePattern == null) {
              filePattern = rootNode.get("filePattern").asText();
            }
            logger.trace("filePattern from routeId " + routeId + " filePattern " + filePattern);
            logger.trace("session factory before connecting" + session);
            String fileExclusions = null;
            if (rootNode.get("fileExclusions") != null) {
              fileExclusions = rootNode.get("fileExclusions").asText();
              logger.trace("File exclusions configured for  route: " + fileExclusions);
            }
            if (rootNode.get("disableDuplicate") != null && !isDisable) {
              String disableDupFlag = rootNode.get("disableDuplicate").asText();
              isDisable = Boolean.valueOf(disableDupFlag);
              logger.trace("Duplicate has been disabled :" + isDisable);
            }
            SftpRemoteFileTemplate template = new SftpRemoteFileTemplate(sesionFactory);
            logger.trace("invocation of method transferData when "
                + "directory is availble in destination with location starts here " + sourceLocation
                + " & file pattern " + filePattern);
            listOfFiles = transferDataFromChannel(template, sourceLocation, filePattern,
                destinationLocation, channelId, routeId, fileExclusions, isDisable);
            logger.trace("invocation of method transferData when "
                + "directory is availble in destination with location ends here " + sourceLocation
                + " & file pattern " + filePattern);
            // if (session.isOpen()) {
            //  session.close();
            // }
          } else {
            logger.trace(bisRouteEntity.getBisRouteSysId() + " has been deactivated");
          }
          logger.trace("opened session has been closed here.");
        } else {
          throw new SftpProcessorException("Exception occurred while connecting to channel from "
              + "factory of connections because channel is not present with the Id " + channelId
              + " & routeId " + routeId);
        }
      }
    } catch (Exception ex) {
      logger.error(
          "Exception occurred while connecting to channel with the channel Id:" + channelId, ex);
      sipLogService.updateLogs(channelId, routeId, BisComponentState.HOST_NOT_REACHABLE.value());
    }
    logger.trace("Transfer ends here with an channel " + channelId + " and routeId " + routeId);
    return listOfFiles;
  }

  /**
   * Transfer files from given directory, recursing into each subdirectory.
   */
  private List<BisDataMetaInfo> transferDataFromChannel(SftpRemoteFileTemplate template,
      String sourcelocation, String pattern, String destinationLocation, Long channelId,
      Long routeId, String exclusions, boolean isDisableDuplicate)
      throws IOException, ParseException {
    logger.debug("Transfer files from directory recursively: {}, {}", sourcelocation, pattern);
    List<BisDataMetaInfo> list = new ArrayList<>();
    /* First transfer the files from the directory */
    list.addAll(transferDataFromChannelDirectory(template, sourcelocation, pattern,
        destinationLocation, channelId, routeId, exclusions, getBatchId(), isDisableDuplicate));
    /* Then iterate through directory looking for subdirectories */
    LsEntry[] entries = template.list(sourcelocation);
    for (LsEntry entry : entries) {
      logger.trace("Directory entry: " + entry.getFilename());
      /* Skip non-directory entries as they cannot be recursed into */
      if (!entry.getAttrs().isDir()) {
        logger.trace("Skip non-directory entry: " + entry.getFilename());
        continue;
      }
      /* Skip dot files, including special entries "." and ".." */
      if (entry.getFilename().startsWith(".")) {
        continue;
      }
      /* Transfer files from subdirectory */
      String sourcelocationDirectory = sourcelocation + File.separator + entry.getFilename();
      list.addAll(transferDataFromChannel(template, sourcelocationDirectory, pattern,
          destinationLocation, channelId, routeId, exclusions, isDisableDuplicate));
    }
    return list;
  }

  private List<BisDataMetaInfo> transferDataFromChannelDirectory(SftpRemoteFileTemplate template,
      String sourcelocation, String pattern, String destinationLocation, Long channelId,
      Long routeId, String exclusions, String batchId, boolean isDisableDuplicate)
      throws IOException, ParseException {
    ZonedDateTime startTime = ZonedDateTime.now();
    logger.trace("Starting transfer data......start time::" + startTime);
    List<BisDataMetaInfo> list = new ArrayList<>();
    LsEntry[] files = null;
    try {
      logger.info("checking recursive in transferDataFromChannelDirectory");
      if (template.list(sourcelocation + File.separator + pattern) != null) {
        files = template.list(sourcelocation + File.separator + pattern);
        logger.trace("Total files matching pattern " + pattern + " at source location "
            + sourcelocation + " are :: " + files.length);
        LsEntry[] filteredFiles = null;
        if (exclusions.isEmpty()) {
          filteredFiles = Arrays.copyOf(files, files.length);
        } else {
          if (exclusions != null) {
            filteredFiles =
                Arrays.stream(files).filter(file -> !file.getFilename().endsWith("." + exclusions))
                    .toArray(LsEntry[]::new);
          }
        }
        if (filteredFiles.length > 0) {

          logger.trace("Total files after filtering exclusions " + exclusions
              + " at source location " + sourcelocation + " are :: " + files.length);


          int sizeOfFileInPath = filteredFiles.length;
          batchSize = getBatchSize() > 0 ? getBatchSize() : batchSize;
          int iterationOfBatches = ((batchSize > sizeOfFileInPath) ? (batchSize / sizeOfFileInPath)
              : (sizeOfFileInPath / batchSize));
          logger.trace("iterationOfBatches :" + iterationOfBatches);
          logger.trace("batchSize :" + batchSize);
          logger.trace("filteredFiles.size :" + filteredFiles.length);
          final int partitionSize =
              (filteredFiles.length + iterationOfBatches - 1) / iterationOfBatches;

          logger.trace("partitionSize :" + partitionSize);
          List<LsEntry> filesArray = Arrays.asList(filteredFiles);
          logger.trace("number of files on this pull :" + filesArray.size());
          List<List<LsEntry>> result = IntStream.range(0, partitionSize)
              .mapToObj(i -> filesArray.subList(iterationOfBatches * i,
                  Math.min(iterationOfBatches * i + iterationOfBatches, filesArray.size())))
              .collect(Collectors.toList());
          logger.trace("number of files on this pull :" + filesArray.size());
          logger.trace("size of partitions :" + result.size());
          logger
              .trace("file from the source is downnloaded in the location :" + destinationLocation);
          File localDirectory = null;
          long lastModifiedDate = 0L;
          
          for (List<LsEntry> entries : result) {
            for (LsEntry entry : entries) {

              logger.trace("entry :" + entry.getFilename());
              long modifiedDate = new Date(entry.getAttrs().getMTime() * 1000L).getTime();
              logger.trace("modifiedDate :" + modifiedDate);
              if (recheckFileModified) {
                for (int i = 0; i < retries; i++) {
                  lastModifiedDate = new Date(
                      template.list(sourcelocation + File.separator + entry.getFilename())[0]
                          .getAttrs().getMTime() * 1000L).getTime();
                }
              } else {
                /*
                 * Recheck not requested, so use modified time provided by listing
                 */
                lastModifiedDate = modifiedDate;
              }
              logger.trace("lastModifiedDate :" + lastModifiedDate);
              logger.trace("lastModifiedDate - modifiedDate :" + (modifiedDate - lastModifiedDate));
              if ((lastModifiedDate - modifiedDate) == 0) {
                if (entry.getAttrs().isDir()) {
                  logger.trace("invocation of method transferDataFromChannel "
                      + "when directory is availble in destination with location starts here "
                      + sourcelocation + " & file pattern " + pattern + " with channel Id "
                      + channelId + " & route Id" + routeId);
                  transferDataFromChannel(template,
                      sourcelocation + File.separator + entry.getFilename(), pattern,
                      destinationLocation, channelId, routeId, exclusions, isDisableDuplicate);
                  logger.trace("invocation of method transferDataFromChannel"
                      + " when directory is availble in destination with location ends here "
                      + sourcelocation + " & file pattern " + pattern + " with channel Id "
                      + channelId + " & route Id" + routeId);
                } else {
                  File fileTobeDeleted = null;
                  logger.trace("Default drop location: " + defaultDestinationLocation);
                  
                  String destination = constructDestinationPath(destinationLocation);
                  String path = processor.getFilePath(defaultDestinationLocation,
                       destination, batchId);
                  logger.trace("File location at destination:: " + path);
                  localDirectory = new File(path);
                  try {
                    if (entry.getAttrs().getSize() != 0 && sipLogService
                        .duplicateCheck(isDisableDuplicate,sourcelocation,entry)) {
                      if (localDirectory != null && !this.processor
                          .isDestinationExists(localDirectory.getPath())) {

                        logger.trace("directory where the file will be"
                            + " downnloaded does not exist so it will be created :"
                            + localDirectory.getAbsolutePath());
                        logger.trace("directory where the file will be downnloaded  :"
                            + localDirectory.getAbsolutePath());
                        this.processor.createDestination(localDirectory
                            .getPath(), new StringBuffer());
                      }
                      logger.trace("file duplication completed " + sourcelocation + File.separator
                          + entry.getFilename() + " batchSize " + batchSize);
                      final File localFile = createTargetFile(localDirectory, entry);
                      fileTobeDeleted = localFile;
                      BisDataMetaInfo bisDataMetaInfo = prepareLogInfo(pattern,
                          getFilePath(localDirectory, entry),
                          getActualRecDate(entry), entry.getAttrs().getSize(),
                          sourcelocation + File.separator + entry.getFilename(), channelId, routeId,
                          localDirectory.getPath());

                      sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
                      logger.trace("Actual file name after downloaded in the  :"
                          + localDirectory.getAbsolutePath() + " file name " + localFile.getName());
                      FSDataOutputStream fos = fs
                          .create(new Path(localFile.getPath()));
                      template.get(sourcelocation + File.separator + entry.getFilename(),
                          new InputStreamCallback() {
                            @Override
                            public void doWithInputStream(InputStream stream) throws IOException {
                              

                              
                              logger.trace(
                                  "Streaming the content of the file in the directory starts here "
                                      + entry.getFilename());
                              ZonedDateTime fileTransStartTime = ZonedDateTime.now();
                              logger.trace("File" + entry.getFilename() + " transfer strat time:: "
                                  + fileTransStartTime);
                              try {
                                if (stream != null) {
                                  if (processor.isDestinationMapR(defaultDestinationLocation)) {
                                      logger.info("COPY BYTES STARTS HERE 8");
                                      org.apache.hadoop.io.IOUtils
                                        .copyBytes(stream, fos, 5120, false);
                                      
                                      logger.info("COPY BYTES COMPLETES HERE 8");
                                      
                                    } else {
                                      processor.transferFile(stream, localFile,
                                          defaultDestinationLocation, mapRfsUser);
                                      if (!processor.isDestinationMapR(
                                           defaultDestinationLocation)) {
                                        processor.closeStream(stream);
                                      }
                                    }
                                      
                                    logger.trace("Streaming the content of "
                                        + "the file in the directory "
                                        + "ends here " + entry.getFilename());
                                    ZonedDateTime fileTransEndTime = ZonedDateTime.now();
                                    logger.trace("File" + entry.getFilename()
                                        + "transfer end time:: "
                                        + fileTransEndTime);
                                    logger.trace(
                                         "closing the stream for the file " 
                                        + entry.getFilename());
                                    bisDataMetaInfo.setProcessState(
                                          BisProcessState.SUCCESS.value());
                                    bisDataMetaInfo.setComponentState(
                                        BisComponentState.DATA_RECEIVED.value());
                                    bisDataMetaInfo.setFileTransferStartTime(
                                        Date.from(fileTransStartTime.toInstant()));
                                    bisDataMetaInfo.setFileTransferEndTime(
                                        Date.from(fileTransEndTime.toInstant()));
                                    bisDataMetaInfo.setFileTransferDuration(
                                        Duration.between(fileTransStartTime,
                                            fileTransEndTime).toMillis());
                                    logger.trace("File transfer duration :: "
                                            + bisDataMetaInfo.getFileTransferDuration());
                                    sipLogService.upsert(
                                        bisDataMetaInfo, bisDataMetaInfo.getProcessId());
                                    list.add(bisDataMetaInfo);
                                }
                                
                              } catch (Exception ex) {
                                logger.error("Exception occurred while writting to file system "
                                    + entry.getFilename(),
                                    ex);
                                throw new SftpProcessorException(
                                    "Exception throw while streaming the file "
                                        + entry.getFilename() + " : ",
                                    ex);
                              } finally {
                                if (stream != null) {
                                  logger.trace("closing the stream for the file in finally block "
                                      + entry.getFilename());
                                  if (!processor.isDestinationMapR(defaultDestinationLocation)) {
                                   // processor.closeStream(stream);
                                  } else {
                                    fos.close();
                                    //stream.close();
                                    //stream = null;
                                    //org.apache.hadoop.io.IOUtils.cleanup(null, stream);
                                  }
                                   
                                }
                              }
                            }
                          });
                      boolean userHasPermissionsToWriteFile = entry.getAttrs() != null
                          && ((entry.getAttrs().getPermissions() & 00200) != 0)
                          && entry.getAttrs().getUId() != 0;
                      if (userHasPermissionsToWriteFile) {
                        logger.trace(
                            "the current user session have privileges to write on the location :"
                                + userHasPermissionsToWriteFile);
                      } else {
                        logger.trace("the current user session does not have "
                            + "privileges to write on the location :"
                            + userHasPermissionsToWriteFile);
                      }
                      bisDataMetaInfo.setProcessState(BisProcessState.SUCCESS.value());
                      bisDataMetaInfo.setComponentState(BisComponentState.DATA_RECEIVED.value());
                      sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
                      list.add(bisDataMetaInfo);

                    } else {
                      if (!isDisableDuplicate && sipLogService.checkDuplicateFile(
                          sourcelocation + File.separator + entry.getFilename())) {
                        logger.info("local Directory before calling getPath() ::" + localDirectory);
                        BisDataMetaInfo bisDataMetaInfo = prepareLogInfo(
                            pattern, getFilePath(localDirectory, entry),
                            getActualRecDate(entry), entry.getAttrs().getSize(),
                            sourcelocation + File.separator
                            +  entry.getFilename(), channelId, routeId,
                            localDirectory.getPath());
                        bisDataMetaInfo.setProcessId(
                            new UUIDGenerator().generateId(bisDataMetaInfo).toString());
                        bisDataMetaInfo.setProcessState(BisProcessState.FAILED.value());
                        bisDataMetaInfo.setComponentState(BisComponentState.DUPLICATE.value());
                        sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
                        list.add(bisDataMetaInfo);
                      }
                    }
                  } catch (Exception ex) {

                    logger.error("Exception occurred while transferring the file from channel", ex);
                    if (this.processor.isDestinationExists(fileTobeDeleted.getPath())) {
                      BisDataMetaInfo bisDataMetaInfo = prepareLogInfo(
                          pattern, getFilePath(localDirectory, entry),
                          getActualRecDate(entry), entry.getAttrs().getSize(),
                          sourcelocation + File.separator + entry.getFilename(), channelId, routeId,
                          localDirectory.getPath());

                      logger.trace(" files or directory to be deleted on exception "
                          + fileTobeDeleted);
                      bisDataMetaInfo.setComponentState(BisComponentState.DATA_REMOVED.value());
                      bisDataMetaInfo.setProcessState(BisProcessState.FAILED.value());
                      sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
                      sipLogService.deleteLog(bisDataMetaInfo.getProcessId());
                    }
                    this.processor.deleteFile(fileTobeDeleted.getPath(), 
                        this.defaultDestinationLocation, this.mapRfsUser);
                    if (template.getSession() != null) {
                      template.getSession().close();
                    }
                  }
                }
              }
            } // end of loop for the number of files to be download at each batch
          } // time it should iterate
          
          
        } else {
          logger.info("On this current pull no data found on the source.");

        }
      } else {
        logger.info(
            "there is no directory path available " + sourcelocation + File.separator + pattern);
      }
      ZonedDateTime endTime = ZonedDateTime.now();
      logger.trace("Ending transfer data......end time:: " + endTime);

      long durationInMillis = Duration.between(startTime, endTime).toMillis();
      logger.trace("End of data tranfer.....Total time in milliseconds::: " + durationInMillis);
    } catch (Exception ex) {
      logger.error("Exception occurred while transferring the file from channel", ex);
    } 
    return list;
  }

  private BisDataMetaInfo prepareLogInfo(String pattern, String localFilePath,
      Date recieveDate, Long size,
      String actualDataName, Long channelId, Long routeId, String destinationPath) {
    BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
    new BisDataMetaInfo();
    bisDataMetaInfo.setFilePattern(pattern);
    bisDataMetaInfo.setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
    bisDataMetaInfo.setReceivedDataName(localFilePath);
    bisDataMetaInfo.setDataSizeInBytes(size);
    bisDataMetaInfo.setActualDataName(actualDataName);
    bisDataMetaInfo.setChannelType(BisChannelType.SFTP);
    bisDataMetaInfo.setProcessState(BisProcessState.INPROGRESS.value());
    bisDataMetaInfo.setComponentState(BisComponentState.DATA_INPROGRESS.value());
    bisDataMetaInfo.setActualReceiveDate(recieveDate);
    bisDataMetaInfo.setChannelId(channelId);
    bisDataMetaInfo.setRouteId(routeId);
    // sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());
    bisDataMetaInfo.setDestinationPath(destinationPath);

    return bisDataMetaInfo;

  }

  private String getFilePath(File localDirectory, LsEntry entry) {
    File file = new File(localDirectory.getPath() + File.separator
            +  FilenameUtils.getBaseName(entry.getFilename())
            + "." + IntegrationUtils.renameFileAppender() + "."
            + FilenameUtils.getExtension(entry.getFilename()));
    return file.getPath();
  }
  
  private File createTargetFile(File localDirectory,LsEntry entry) {
    return new File(localDirectory.getPath() + File.separator
        + FilenameUtils.getBaseName(entry.getFilename()) + "."
        + IntegrationUtils.renameFileAppender() + "."
        + FilenameUtils.getExtension(entry.getFilename()));
  }

  private Date getActualRecDate(LsEntry entry) {
    return new Date(((long) entry.getAttrs().getATime()) * 1000L);
  }
  
  /**
   * Checks and adds if '/' is missing in beginning.
   * Returns default drop location if destination is null.
   * 
   * @param destinationLoc destination path.
   * @return destination location
   */
  private String constructDestinationPath(String destinationLoc) {
    String destinationPath = "";
    if (destinationLoc == null) {
      destinationPath = this.defaultDestinationLocation;
    } else {
      if (destinationLoc.startsWith(File.separator)) {
        destinationPath = destinationLoc;
      } else {
        destinationPath = File.separator + destinationLoc;
      }
    }
    return destinationPath;

  }
 


  /**
   * This is method to handle inconsistency during failure.
   */
  @Scheduled(fixedDelayString = "${sip.service.retry.delay}")
  @Transactional
  public void recoverFromInconsistentState() {
    logger.trace("recoverFromInconsistentState execution starts here");
    int countOfRecords = sipLogService.countRetryIds(retryDiff);
    logger.trace("Count listOfRetryIds :" + countOfRecords);
    int totalNoOfPages = IntegrationUtils.calculatePages(countOfRecords, retryPageSize);
    logger.debug("totalNoOfPages :" + totalNoOfPages);
    for (int i = pageStart; i < totalNoOfPages; i++) {
      List<BisFileLog> logs =
          sipLogService.listOfRetryIds(retryDiff, i, retryPageSize, "checkpointDate");
      logger.trace("Data listOfRetryIds :" + logs);
      for (BisFileLog log : logs) {
        logger.trace("ids which is in inconsistent state :" + log.getPid());
        long routeId = log.getRouteSysId();
        logger.trace("Route Id :" + routeId);
        long channelId = log.getBisChannelSysId();
        logger.trace("Channel Id :" + channelId);
        Optional<BisRouteEntity> bisRouteEntityPresent =
            bisRouteDataRestRepository.findById(routeId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        BisRouteEntity bisRouteEntity = bisRouteEntityPresent.get();
        JsonNode nodeEntity = null;
        ObjectNode rootNode = null;
        try {
          nodeEntity = objectMapper.readTree(bisRouteEntity.getRouteMetadata());
          rootNode = (ObjectNode) nodeEntity;
          if (rootNode.get("disableDuplicate") != null
              && !rootNode.get("disableDuplicate").isNull()) {
            String disableDupFlag = rootNode.get("disableDuplicate").asText();
            Boolean isDisable = Boolean.valueOf(disableDupFlag);
            if (isDisable) {
              logger.trace("Inside isDisable starts here");
              if (sipLogService.isRouteAndChannelExists(routeId, channelId)) {
                updateAndDeleteCorruptFiles(log, fileStatus, procesStatus);
                // To retry only specific file instead of downloading all files
                // in the source folder
                if (log.getFileName() != null) {
                  logger.trace("Inside isDisable transferData starts here");
                  transferData(channelId, routeId, FilenameUtils.getName(log.getFileName()),
                      isDisable);
                } else {
                  logger.trace("Inside isDisable transferData when starts here "
                      + "log.getFileName() is null");
                  transferData(channelId, routeId, null, isDisable);
                }
              }
              logger.trace("Inside isDisable ends here");
            } else {
              updateAndDeleteCorruptFiles(log, fileStatus, procesStatus);
              // To retry only specific file instead of downloading all files in
              // the in source folder
              if (log.getFileName() != null) {
                transferData(channelId, routeId, FilenameUtils.getName(log.getFileName()), true);
              } else {
                transferData(channelId, routeId, null, true);
              }
            }
          }
        } catch (IOException e) {
          logger.error("Exception occurred while reading duplicate attribute ", e);
        }
      } // end of second for loop
    } // end of first for loop
    logger.debug("recoverFromInconsistentState execution ends here");
  } 

  /**
   * This is a common method to update the status.
   *
   * @param log log instance which has the details.
   * @param fileStatus file status to be entered
   * @param procesStatus component status to be updated
   */
  private void updateAndDeleteCorruptFiles(BisFileLog log, String fileStatus, String procesStatus) {
    int rowId = 0;
    rowId = sipLogService.updateStatusFailed(fileStatus, procesStatus, log.getPid());
    logger.trace("rowId updated :" + rowId);
    if (rowId <= 0) {
      throw new PersistenceException(
          "Exception occured while updating the bis log table to handle inconsistency");
    }
    String fileName = null;
    if (log.getRecdFileName() != null) {
      fileName = log.getRecdFileName();
      logger.trace("Delete the corrupted file :" + fileName);
      File fileDelete = new File(fileName);
      if (fileDelete != null) {
        logger.trace("Parent Directory deleted : " + fileDelete);
        File[] files = fileDelete.getParentFile().listFiles(new FileFilter() {
          @Override
          public boolean accept(File file) {
            return !file.isHidden();
          }
        });
        if (files != null && files.length > 1) {
          fileDelete.delete();
        } else {
          logger.trace("Directory deleted :", fileDelete);
          IntegrationUtils.removeDirectory(fileDelete.getParentFile());
        }
      }
    } else {
      logger.trace("Corrupted file does not exist.");
    }
  }
}

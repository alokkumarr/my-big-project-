package com.synchronoss.saw.apipull.plugin.service;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import com.synchronoss.saw.apipull.pojo.BodyParameters;
import com.synchronoss.saw.apipull.pojo.ChannelMetadata;
import com.synchronoss.saw.apipull.pojo.HeaderParameter;
import com.synchronoss.saw.apipull.pojo.HttpMethod;
import com.synchronoss.saw.apipull.pojo.QueryParameter;
import com.synchronoss.saw.apipull.pojo.RouteMetadata;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import com.synchronoss.saw.apipull.service.HttpClient;
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisProcessState;
import com.synchronoss.saw.logs.constants.SourceType;
import com.synchronoss.saw.logs.entities.BisJobEntity;
import com.synchronoss.saw.logs.service.SipLogging;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.validation.constraints.NotNull;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import sncr.bda.core.file.FileProcessor;
import sncr.bda.core.file.FileProcessorFactory;

@Service("apipullService")
public class ApiPullServiceImpl extends SipPluginContract {

  private static final Logger logger = LoggerFactory.getLogger(ApiPullServiceImpl.class);
  @Autowired private BisChannelDataRestRepository bisChannelDataRestRepository;

  @Autowired private BisRouteDataRestRepository bisRouteDataRestRepository;

  @Autowired private SipLogging sipLogService;

  @Value("${bis.default-data-drop-location}")
  @NotNull
  private String defaultDestinationLocation;

  @Value("${bis.destination-fs-user}")
  @NotNull
  private String mapRfsUser;

  private FileProcessor processor;
  FileSystem fs;
  Configuration conf;
  /** This method is to test connect the route. */
  @PostConstruct
  private void init() throws Exception {

    processor = FileProcessorFactory.getFileProcessor(defaultDestinationLocation);

    if (!processor.isDestinationExists(defaultDestinationLocation)) {
      logger.trace("Defautl drop location not found");
      logger.trace("Creating folders for default drop location :: " + defaultDestinationLocation);

      processor.createDestination(defaultDestinationLocation, new StringBuffer());

      logger.trace(
          "Default drop location folders created? :: "
              + processor.isDestinationExists(defaultDestinationLocation));
    }

    String location = defaultDestinationLocation.replace(FileProcessor.maprFsPrefix, "");
    conf = new Configuration();
    conf.set("hadoop.job.ugi", mapRfsUser);
    fs = FileSystem.get(URI.create(location), conf);
  }

  @Override
  public String connectRoute(Long entityId) throws SipNestedRuntimeException {
    logger.trace("Connecting to route :" + entityId);
    StringBuffer connectionLogs = new StringBuffer();

    connectionLogs.append("Fetching route details\n");
    Optional<BisRouteEntity> bisRouteEntity = this.findRouteById(entityId);

    if (bisRouteEntity.isPresent()) {
      BisRouteEntity entity = bisRouteEntity.get();
      long channelId = entity.getBisChannelSysId();

      connectionLogs.append("Fetching channel details\n");
      Optional<BisChannelEntity> bisChannelEntity = this.findChannelById(channelId);

      if (!bisChannelEntity.isPresent()) {
        throw new SipNestedRuntimeException(
            "Unable to extract channel information for channel id: " + channelId);
      }

      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter());
      Gson gson = gsonBuilder.create();
      BisChannelEntity channelEntity = bisChannelEntity.get();

      String channelMetadataStr = channelEntity.getChannelMetadata();

      ChannelMetadata channelMetadata = gson.fromJson(channelMetadataStr, ChannelMetadata.class);

      String hostAddress = channelMetadata.getHostAddress();
      Integer port = channelMetadata.getPort();

      String routeMetadataStr = entity.getRouteMetadata();
      RouteMetadata routeMetadata = gson.fromJson(routeMetadataStr, RouteMetadata.class);

      String apiEndPoint = routeMetadata.getApiEndPoint();
      String destinationLocation = routeMetadata.getDestinationLocation();
      HttpMethod method = routeMetadata.getHttpMethod();

      SipApiRequest apiRequest = new SipApiRequest();
      String url = generateUrl(hostAddress, port, apiEndPoint);
      apiRequest.setUrl(url);

      apiRequest.setHttpMethod(method != null ? method : HttpMethod.GET);

      List<QueryParameter> queryParameters = routeMetadata.getQueryParameters();
      if (queryParameters != null && queryParameters.size() != 0) {
        apiRequest.setQueryParameters(queryParameters);
      }

      List<HeaderParameter> headerParameters = routeMetadata.getHeaderParameters();
      if (headerParameters != null && headerParameters.size() != 0) {
        apiRequest.setHeaderParameters(headerParameters);
      }

      BodyParameters bodyParameters = routeMetadata.getBodyParameters();
      if (bodyParameters != null) {
        apiRequest.setBodyParameters(bodyParameters);
      }

      try {
        HttpClient httpClient = new HttpClient();

        connectionLogs.append("Connecting to ").append(url).append("\n");
        ApiResponse response = httpClient.execute(apiRequest);

        connectionLogs.append("Fetching data from ").append(url).append("\n");
        MediaType responseContentType = response.getHttpHeaders().getContentType();
        HttpStatus httpStatus = response.getHttpStatus();
        logger.info("Http Status = " + httpStatus);

        connectionLogs.append("Connection Status = " + httpStatus).append("\n");

        Object content = response.getResponseBody();
        connectionLogs.append(content.toString()).append("\n");

      } catch (Exception exception) {
        throw new SipNestedRuntimeException(exception.getMessage(), exception);
      }
    } else {
      throw new SipNestedRuntimeException(
          "Unable to find route information for route id: " + entityId);
    }

    return connectionLogs.toString();
  }

  /** This method is to test connect the source. */
  @Override
  public String connectChannel(Long entityId) throws SipNestedRuntimeException {
    StringBuffer connectionLogs = new StringBuffer();

    logger.info("Inside connectChannel");

    return connectionLogs.toString();
  }

  /** This method is to test connect the route. */
  @Override
  public String immediateConnectRoute(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException, IOException {
    logger.info("Inside immediateConnectRoute");

    return null;
  }

  /** This method is to test connect the source. */
  @Override
  public String immediateConnectChannel(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException {
    logger.info("Inside immediateConnectChannel");

    Object queryParamObj = payload.getQueryParameters();
    List<QueryParameter> queryParameters = null;

    if (queryParamObj != null) {
      queryParameters = (List<QueryParameter>) queryParamObj;
      logger.debug("Query Params = " + queryParameters);
    }

//    Object headerParamObj = payload.get
    //    List<HeaderParameter> headerParameters = (List<HeaderParameter>)

    return null;
  }

  @Override
  public List<BisDataMetaInfo> immediateTransfer(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException {
    logger.info("Inside API Pull immediateTransfer");
    List<BisDataMetaInfo> infoList = new ArrayList<>();

    BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
    bisDataMetaInfo.setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
    bisDataMetaInfo.setDataSizeInBytes(1024l);
    bisDataMetaInfo.setActualDataName(
        payload.getSourceLocation() + File.separator + "newFile.json");
    bisDataMetaInfo.setReceivedDataName("newFile.json");
    bisDataMetaInfo.setChannelType(BisChannelType.APIPULL);
    bisDataMetaInfo.setProcessState(BisProcessState.SUCCESS.value());
    bisDataMetaInfo.setActualReceiveDate(new Date());

    infoList.add(bisDataMetaInfo);

    return infoList;
  }

  public List<BisDataMetaInfo> scanFilesForPattern(
      Long channelId,
      Long routeId,
      String filePattern,
      boolean isDisable,
      String source,
      Optional<Long> jobId,
      String channelType)
      throws SipNestedRuntimeException {
    logger.trace("Inside API Pull scanFilesForPattern");

    logger.debug(
        "Channel ID = "
            + channelId
            + " Route Id = "
            + routeId
            + " File Pattern = "
            + filePattern
            + " Channel Type = "
            + channelType
            + " Job ID = "
            + jobId
            + " Source Type = "
            + source);
    Preconditions.checkNotNull(channelId != null, "payload.getChannelId() cannot be null");
    Preconditions.checkNotNull(routeId != null, "payload.getRouteId() cannot be null");

    BisJobEntity jobEntity = null;
    if (source.equals(SourceType.REGULAR.toString())) {
      jobEntity = this.executeSipJob(channelId, routeId, filePattern, channelType);
    } else {
      if (jobId.isPresent()) {
        jobEntity = sipLogService.retriveJobById(jobId.get());
      }
    }

    logger.debug("JobEntity = " + jobEntity);

    List<BisDataMetaInfo> infoList = new ArrayList<>();

    try {
      Optional<BisRouteEntity> bisRouteEntity = this.findRouteById(routeId);

      if (bisRouteEntity.isPresent()) {
        BisRouteEntity routeEntity = bisRouteEntity.get();

        logger.debug("Route Entity = " + routeEntity);
        long channelIdNew = routeEntity.getBisChannelSysId();

        Optional<BisChannelEntity> bisChannelEntity = this.findChannelById(channelIdNew);

        if (!bisChannelEntity.isPresent()) {
          throw new SipNestedRuntimeException(
              "Unable to extract channel information for channel id: " + channelIdNew);
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter());
        Gson gson = gsonBuilder.create();
        BisChannelEntity channelEntity = bisChannelEntity.get();

        logger.debug("Channel Entity = " + channelEntity);

        String channelMetadataStr = channelEntity.getChannelMetadata();

        ChannelMetadata channelMetadata = gson.fromJson(channelMetadataStr, ChannelMetadata.class);

        String hostAddress = channelMetadata.getHostAddress();
        Integer port = channelMetadata.getPort();

        String routeMetadataStr = routeEntity.getRouteMetadata();
        RouteMetadata routeMetadata = gson.fromJson(routeMetadataStr, RouteMetadata.class);

        String apiEndPoint = routeMetadata.getApiEndPoint();
        String destinationLocation = routeMetadata.getDestinationLocation();
        HttpMethod method = routeMetadata.getHttpMethod();

        SipApiRequest apiRequest = new SipApiRequest();
        String url = generateUrl(hostAddress, port, apiEndPoint);

        logger.debug("URL = " + url);
        apiRequest.setUrl(url);

        apiRequest.setHttpMethod(method != null ? method : HttpMethod.GET);

        List<QueryParameter> queryParameters = routeMetadata.getQueryParameters();
        if (queryParameters != null && queryParameters.size() != 0) {
          apiRequest.setQueryParameters(queryParameters);
        }

        List<HeaderParameter> headerParameters = routeMetadata.getHeaderParameters();
        if (headerParameters != null && headerParameters.size() != 0) {
          apiRequest.setHeaderParameters(headerParameters);
        }

        BodyParameters bodyParameters = routeMetadata.getBodyParameters();
        if (bodyParameters != null) {
          apiRequest.setBodyParameters(bodyParameters);
        }

        try {
          HttpClient httpClient = new HttpClient();

          logger.debug("Fetching API data");

          ApiResponse response = httpClient.execute(apiRequest);

          logger.debug("API Response = " + response);

          MediaType responseContentType = response.getHttpHeaders().getContentType();
          Object content = response.getResponseBody();

          logger.debug("API Response data = " + content);

          String destination = this.constructDestinationPath(destinationLocation);
          logger.debug("Destination = " + destination);
          logger.debug("Default destination location = " + defaultDestinationLocation);

          String path =
              processor.getFilePath(
                  defaultDestinationLocation, destination, File.separator + getBatchId());

          String fileName = null;

          logger.debug("Path from processor = " + path);
          logger.debug("Content Type = " + responseContentType);

          logger.debug("Creating destination directory " + path);
          processor.createDestination(path, new StringBuffer());

          if (responseContentType.equals(MediaType.APPLICATION_JSON)
              || responseContentType.equals(MediaType.APPLICATION_JSON_UTF8)) {
            logger.debug("Content type json");
            fileName = "data_" + getBatchId() + ".json";
          } else if (responseContentType.equals(MediaType.TEXT_XML)
              || responseContentType.equals(MediaType.APPLICATION_XML)) {
            fileName = "data_" + getBatchId() + ".xml";
          } else if (responseContentType.equals(MediaType.TEXT_HTML)) {
            fileName = "data_" + getBatchId() + ".html";
          } else if (responseContentType.equals(MediaType.TEXT_PLAIN)) {
            logger.debug("Content type is text");
            fileName = "data_" + getBatchId() + ".txt";
          } else {
            // Not implemented yet
          }
          String destinationFilePath = path + fileName;
          logger.info("Writing data to " + destinationFilePath);

          writeDataToLocation(content.toString(), destinationFilePath);

        } catch (IOException exception) {
          logger.error("IO Error occurred ", exception);
          throw new SipNestedRuntimeException(exception.getMessage(), exception);
        } catch (Exception exception) {
          logger.error("Error occurred ", exception);
          throw new SipNestedRuntimeException(exception.getMessage(), exception);
        }
      } else {
        throw new SipNestedRuntimeException(
            "Unable to find route information for route id: " + routeId);
      }
    } catch (Exception ex) {

    }

    BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
    bisDataMetaInfo.setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
    bisDataMetaInfo.setDataSizeInBytes(1024l);
    bisDataMetaInfo.setActualDataName("location" + File.separator + "newFile.json");
    bisDataMetaInfo.setReceivedDataName("newFile.json");
    bisDataMetaInfo.setChannelType(BisChannelType.APIPULL);
    bisDataMetaInfo.setProcessState(BisProcessState.SUCCESS.value());
    bisDataMetaInfo.setActualReceiveDate(new Date());

    infoList.add(bisDataMetaInfo);

    return infoList;
  }

  /** This method executes actual file transfer used by worker threads. */
  @Override
  public void executeFileTransfer(
      String logId, Long jobId, Long channelId, Long routeId, String fileName) {
    logger.info("Inside executeFileTransfer");

    try {
      Optional<BisRouteEntity> bisRouteEntity = this.findRouteById(routeId);

      if (bisRouteEntity.isPresent()) {
        BisRouteEntity routeEntity = bisRouteEntity.get();

        logger.debug("Route Entity = " + routeEntity);
        long channelIdNew = routeEntity.getBisChannelSysId();

        Optional<BisChannelEntity> bisChannelEntity = this.findChannelById(channelIdNew);

        if (!bisChannelEntity.isPresent()) {
          throw new SipNestedRuntimeException(
              "Unable to extract channel information for channel id: " + channelIdNew);
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter());
        Gson gson = gsonBuilder.create();
        BisChannelEntity channelEntity = bisChannelEntity.get();

        logger.debug("Channel Entity = " + channelEntity);

        String channelMetadataStr = channelEntity.getChannelMetadata();

        ChannelMetadata channelMetadata = gson.fromJson(channelMetadataStr, ChannelMetadata.class);

        String hostAddress = channelMetadata.getHostAddress();
        Integer port = channelMetadata.getPort();

        String routeMetadataStr = routeEntity.getRouteMetadata();
        RouteMetadata routeMetadata = gson.fromJson(routeMetadataStr, RouteMetadata.class);

        String apiEndPoint = routeMetadata.getApiEndPoint();
        String destinationLocation = routeMetadata.getDestinationLocation();
        HttpMethod method = routeMetadata.getHttpMethod();

        SipApiRequest apiRequest = new SipApiRequest();
        String url = generateUrl(hostAddress, port, apiEndPoint);

        logger.debug("URL = " + url);
        apiRequest.setUrl(url);

        apiRequest.setHttpMethod(method != null ? method : HttpMethod.GET);

        List<QueryParameter> queryParameters = routeMetadata.getQueryParameters();
        if (queryParameters != null && queryParameters.size() != 0) {
          apiRequest.setQueryParameters(queryParameters);
        }

        List<HeaderParameter> headerParameters = routeMetadata.getHeaderParameters();
        if (headerParameters != null && headerParameters.size() != 0) {
          apiRequest.setHeaderParameters(headerParameters);
        }

        BodyParameters bodyParameters = routeMetadata.getBodyParameters();
        if (bodyParameters != null) {
          apiRequest.setBodyParameters(bodyParameters);
        }

        try {
          HttpClient httpClient = new HttpClient();

          logger.debug("Fetching API data");

          ApiResponse response = httpClient.execute(apiRequest);

          logger.debug("API Response = " + response);

          MediaType responseContentType = response.getHttpHeaders().getContentType();
          Object content = response.getResponseBody();

          logger.debug("API Response data = " + content);

          String destination = this.constructDestinationPath(destinationLocation);
          logger.debug("Destination = " + destination);
          logger.debug("Default destination location = " + defaultDestinationLocation);

          String path =
              processor.getFilePath(
                  defaultDestinationLocation, destination, File.separator + getBatchId());

          logger.debug("Path from processor = " + path);
          logger.debug("Content Type = " + responseContentType);

          logger.debug("Creating destination directory " + path);
          processor.createDestination(path, new StringBuffer());

          if (responseContentType.equals(MediaType.APPLICATION_JSON)
              || responseContentType.equals(MediaType.APPLICATION_JSON_UTF8)) {
            logger.debug("Content type json");
            fileName = "data_" + getBatchId() + ".json";
          } else if (responseContentType.equals(MediaType.TEXT_XML)
              || responseContentType.equals(MediaType.APPLICATION_XML)) {
            fileName = "data_" + getBatchId() + ".xml";
          } else if (responseContentType.equals(MediaType.TEXT_HTML)) {
            fileName = "data_" + getBatchId() + ".html";
          } else if (responseContentType.equals(MediaType.TEXT_PLAIN)) {
            logger.debug("Content type is text");
            fileName = "data_" + getBatchId() + ".txt";
          } else {
            // Not implemented yet
          }
          String destinationFilePath = path + fileName;
          logger.info("Writing data to " + destinationFilePath);

          writeDataToLocation(content.toString(), destinationFilePath);

        } catch (IOException exception) {
          logger.error("IO Error occurred ", exception);
          throw new SipNestedRuntimeException(exception.getMessage(), exception);
        } catch (Exception exception) {
          logger.error("Error occurred ", exception);
          throw new SipNestedRuntimeException(exception.getMessage(), exception);
        }
      } else {
        throw new SipNestedRuntimeException(
            "Unable to find route information for route id: " + routeId);
      }
    } catch (Exception ex) {

    }
  }

  @Override
  public boolean isDataExists(String data) throws Exception {
    return false;
  }

  @Transactional(TxType.REQUIRED)
  public Optional<BisRouteEntity> findRouteById(Long routeId) {
    return bisRouteDataRestRepository.findById(routeId);
  }

  @Transactional(TxType.REQUIRED)
  public Optional<BisChannelEntity> findChannelById(Long channelId) {
    return bisChannelDataRestRepository.findById(channelId);
  }

  private String generateUrl(String hostAddress, Integer port, String apiEndPoint) {
    StringBuilder builder = new StringBuilder();

    builder.append(hostAddress);

    if (port != null) {
      builder.append(":").append(port);
    }

    if (apiEndPoint != null && apiEndPoint.length() != 0) {
      if (apiEndPoint.startsWith("/")) {
        builder.append(apiEndPoint);
      } else {
        builder.append("/").append(apiEndPoint);
      }
    }

    return builder.toString();
  }

  /**
   * Checks and adds if '/' is missing in beginning. Returns default drop location if destination is
   * null.
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

  private boolean writeDataToLocation(String content, String destinationFilePath) throws Exception {
    boolean status = true;

    InputStream stream = new ByteArrayInputStream(content.getBytes());
    File file = new File(destinationFilePath);
    processor.transferFile(stream, file, defaultDestinationLocation, mapRfsUser);

    return status;
  }

  private BisJobEntity executeSipJob(
      Long channelId, Long routeId, String filePattern, String channelType) {
    logger.debug("Inside executeSipJob");
    logger.debug("File Pattern = " + filePattern);
    //    if (filePattern == null) {
    //      Optional<BisRouteEntity> routeInfo = this.findRouteById(routeId);
    //
    //      if (routeInfo.isPresent()) {
    //        BisRouteEntity route = routeInfo.get();
    //        ObjectMapper objectMapper = new ObjectMapper();
    //        JsonNode rootNode;
    //        try {
    //          rootNode = objectMapper.readTree(route.getRouteMetadata());
    //          filePattern = rootNode.get("filePattern").asText();
    //          logger.trace("File pattern::" + filePattern);
    //        } catch (IOException exception) {
    //          logger.error("Exception during parsing" + exception.getMessage());
    //        }
    //      }
    //    }
    return sipLogService.createJobLog(channelId, routeId, filePattern, channelType);
  }
}

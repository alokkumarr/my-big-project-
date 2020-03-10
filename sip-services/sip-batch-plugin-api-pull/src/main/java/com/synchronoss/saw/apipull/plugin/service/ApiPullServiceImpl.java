package com.synchronoss.saw.apipull.plugin.service;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.json.JsonSanitizer;
import com.synchronoss.saw.apipull.pojo.SipApiResponse;
import com.synchronoss.saw.apipull.pojo.BodyParameters;
import com.synchronoss.saw.apipull.pojo.ApiChannelMetadata;
import com.synchronoss.saw.apipull.pojo.HeaderParameter;
import com.synchronoss.saw.apipull.pojo.HttpMethod;
import com.synchronoss.saw.apipull.pojo.QueryParameter;
import com.synchronoss.saw.apipull.pojo.ApiRouteMetadata;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import com.synchronoss.saw.apipull.service.SipHttpClient;
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisComponentState;
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

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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
import org.springframework.web.client.RestClientException;
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

  @PostConstruct
  private void init() throws Exception {

    processor = FileProcessorFactory.getFileProcessor(defaultDestinationLocation);

    if (!processor.isDestinationExists(defaultDestinationLocation)) {
      logger.trace("Default drop location not found");
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

  /** This method is to test connect the route. */
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
      String sanitizedChannelMetadataStr = JsonSanitizer.sanitize(channelMetadataStr);

      ApiChannelMetadata apiChannelMetadata =
          gson.fromJson(sanitizedChannelMetadataStr, ApiChannelMetadata.class);

      String hostAddress = apiChannelMetadata.getHostAddress();
      Integer port = apiChannelMetadata.getPort();

      String routeMetadataStr = entity.getRouteMetadata();
      String sanitizedRouteMetadataStr = JsonSanitizer.sanitize(routeMetadataStr);
      ApiRouteMetadata apiRouteMetadata = gson.fromJson(sanitizedRouteMetadataStr, ApiRouteMetadata.class);

      String apiEndPoint = apiRouteMetadata.getApiEndPoint();
      String destinationLocation = apiRouteMetadata.getDestinationLocation();
      HttpMethod method = apiRouteMetadata.getHttpMethod();

      SipApiRequest apiRequest = new SipApiRequest();
      String url = generateUrl(hostAddress, port, apiEndPoint);
      apiRequest.setUrl(url);

      apiRequest.setHttpMethod(method != null ? method : HttpMethod.GET);

      List<QueryParameter> queryParameters = apiRouteMetadata.getQueryParameters();
      if (queryParameters != null && queryParameters.size() != 0) {
        apiRequest.setQueryParameters(queryParameters);
      }

      List<HeaderParameter> headerParameters = apiRouteMetadata.getHeaderParameters();
      if (headerParameters != null && headerParameters.size() != 0) {
        apiRequest.setHeaderParameters(headerParameters);
      }

      BodyParameters bodyParameters = apiRouteMetadata.getBodyParameters();
      if (bodyParameters != null) {
        apiRequest.setBodyParameters(bodyParameters);
      }

      try {
        SipHttpClient sipHttpClient = new SipHttpClient();

        connectionLogs.append("Connecting to ").append(url).append("\n");
        SipApiResponse response = sipHttpClient.execute(apiRequest);

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
    Optional<BisChannelEntity> bisChannelEntity = this.findChannelById(entityId);

    if (!bisChannelEntity.isPresent()) {
      throw new SipNestedRuntimeException(
          "Unable to extract channel information for channel id: " + entityId);
    }

    BisChannelEntity channelEntity = bisChannelEntity.get();

    String channelMetadataStr = channelEntity.getChannelMetadata();
    String sanitizedChannelMetadataStr = JsonSanitizer.sanitize(channelMetadataStr);

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter());
    Gson gson = gsonBuilder.create();

    ApiChannelMetadata apiChannelMetadata =
        gson.fromJson(sanitizedChannelMetadataStr, ApiChannelMetadata.class);

    SipApiRequest apiRequest = new SipApiRequest();

    String hostName = apiChannelMetadata.getHostAddress();
    Integer port = apiChannelMetadata.getPort();

    String httpMethodStr = apiChannelMetadata.getHttpMethod().toString();
    if (httpMethodStr != null) {
      HttpMethod method = HttpMethod.fromValue(httpMethodStr);

      apiRequest.setHttpMethod(method);
    }

    String apiEndPoint = apiChannelMetadata.getApiEndPoint();

    String url = generateUrl(hostName, port, apiEndPoint);

    apiRequest.setUrl(url);

    List<QueryParameter> queryParameters = apiChannelMetadata.getQueryParameters();

    if (queryParameters != null && !queryParameters.isEmpty()) {
      apiRequest.setQueryParameters(queryParameters);
    }

    List<HeaderParameter> headerParameters = apiChannelMetadata.getHeaderParameters();

    if (headerParameters != null && !headerParameters.isEmpty()) {
      apiRequest.setHeaderParameters(headerParameters);
    }

    if (httpMethodStr.equalsIgnoreCase(HttpMethod.POST.toString())) {
      Object bodyParamsObj = apiChannelMetadata.getBodyParameters();

      LinkedHashMap<String, Object> bodyParamMap = (LinkedHashMap<String, Object>) bodyParamsObj;

      logger.debug(bodyParamMap.toString());
      BodyParameters bodyParameters = new BodyParameters();

      bodyParameters.setContent(bodyParamMap.get("content"));

      logger.debug("Body Parameter = " + bodyParameters);

      apiRequest.setBodyParameters(bodyParameters);
    }

    try {
      SipHttpClient sipHttpClient = new SipHttpClient();

      connectionLogs.append("Connecting to ").append(url).append("\n");
      SipApiResponse response = sipHttpClient.execute(apiRequest);

      connectionLogs.append("Fetching data from ").append(url).append("\n");
      HttpStatus httpStatus = response.getHttpStatus();
      logger.info("Http Status = " + httpStatus);

      connectionLogs.append("Connection Status = " + httpStatus).append("\n");

      Object content = response.getResponseBody();

      if (content != null && content.toString().length() != 0) {
        connectionLogs
            .append("Content Length: ")
            .append(content.toString().length())
            .append(" bytes")
            .append("\n");
      }

    } catch (RestClientException exception) {
      throw new SipNestedRuntimeException("Unsupported content type text/html;charset=UTF-8");
    } catch (Exception exception) {
      logger.error("Exception caught");
      throw new SipNestedRuntimeException(exception.getMessage(), exception);
    }

    return connectionLogs.toString();
  }

  /** This method is to test connect the route. */
  @Override
  public String immediateConnectRoute(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException, IOException {
    logger.info("Inside immediateConnectRoute");

    StringBuilder connectionLogs = new StringBuilder();
    logger.debug("Test payload = " + payload);

    Long channelId = Long.valueOf(payload.getChannelId());
    Optional<BisChannelEntity> bisChannelEntity = this.findChannelById(channelId);

    if (!bisChannelEntity.isPresent()) {
      throw new SipNestedRuntimeException(
          "Unable to extract channel information for channel id: " + channelId);
    }

    BisChannelEntity channelEntity = bisChannelEntity.get();

    String channelMetadataStr = channelEntity.getChannelMetadata();
    String sanitizedChannelMetadataStr = JsonSanitizer.sanitize(channelMetadataStr);

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter());
    Gson gson = gsonBuilder.create();

    ApiChannelMetadata apiChannelMetadata =
        gson.fromJson(sanitizedChannelMetadataStr, ApiChannelMetadata.class);

    SipApiRequest apiRequest = new SipApiRequest();

    String hostName = apiChannelMetadata.getHostAddress();
    Integer port = apiChannelMetadata.getPort();

    String httpMethodStr = payload.getHttpMethod();
    if (httpMethodStr != null) {
      HttpMethod method = HttpMethod.fromValue(httpMethodStr);

      apiRequest.setHttpMethod(method);
    }

    String apiEndPoint = payload.getApiEndPoint();

    String url = generateUrl(hostName, port, apiEndPoint);

    apiRequest.setUrl(url);

    List<Object> queryParamObj = payload.getQueryParameters();

    List<QueryParameter> queryParameters = new ArrayList<>();

    if (queryParamObj != null) {
      for (Object paramObject : queryParamObj) {
        logger.debug("Object = " + paramObject);
        logger.debug("Object type = " + paramObject.getClass().getName());
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) paramObject;

        QueryParameter param = new QueryParameter();

        param.setKey(map.get("key"));
        param.setValue(map.get("value"));

        logger.debug("Param = " + param);

        queryParameters.add(param);
      }

      apiRequest.setQueryParameters(queryParameters);
    }

    List<Object> headerParamObj = payload.getHeaderParameters();
    List<HeaderParameter> headerParameters = new ArrayList<>();

    if (headerParamObj != null) {
      for (Object headerObject : headerParamObj) {
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) headerObject;

        HeaderParameter param = new HeaderParameter();

        param.setKey(map.get("key"));
        param.setValue(map.get("value"));

        logger.debug("Param = " + param);

        headerParameters.add(param);
      }
      logger.debug("Header Params = " + headerParameters);

      apiRequest.setHeaderParameters(headerParameters);
    }

    if (httpMethodStr.equalsIgnoreCase(HttpMethod.POST.toString())) {
      Object bodyParamsObj = payload.getBodyParameters();

      LinkedHashMap<String, Object> bodyParamMap = (LinkedHashMap<String, Object>) bodyParamsObj;

      logger.debug(bodyParamMap.toString());
      BodyParameters bodyParameters = new BodyParameters();

      bodyParameters.setContent(bodyParamMap.get("content"));

      logger.debug("Body Parameter = " + bodyParameters);

      apiRequest.setBodyParameters(bodyParameters);
    }

    try {
      SipHttpClient sipHttpClient = new SipHttpClient();

      connectionLogs.append("Connecting to ").append(url).append("\n");
      SipApiResponse response = sipHttpClient.execute(apiRequest);

      connectionLogs.append("Fetching data from ").append(url).append("\n");
      HttpStatus httpStatus = response.getHttpStatus();
      logger.info("Http Status = " + httpStatus);

      connectionLogs.append("Connection Status = " + httpStatus).append("\n");

      Object content = response.getResponseBody();

      if (content != null && content.toString().length() != 0) {
        connectionLogs
            .append("Content Length: ")
            .append(content.toString().length())
            .append(" bytes")
            .append("\n");
      }

    } catch (RestClientException exception) {
      throw new SipNestedRuntimeException("Unsupported content type text/html;charset=UTF-8");
    } catch (Exception exception) {
      logger.error("Exception caught");
      throw new SipNestedRuntimeException(exception.getMessage(), exception);
    }

    return connectionLogs.toString();
  }

  /** This method is to test connect the source. */
  @Override
  public String immediateConnectChannel(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException {
    logger.info("Inside immediateConnectChannel");
    StringBuilder connectionLogs = new StringBuilder();

    String hostName = payload.getHostName();
    if (hostName == null || hostName.length() == 0) {
      throw new SipNestedRuntimeException("Host name is mandatory");
    }

    Integer port = payload.getPortNo();
    String apiEndPoint = payload.getApiEndPoint() != null ? payload.getApiEndPoint() : "/";
    String url = generateUrl(hostName, port, apiEndPoint);

    SipApiRequest apiRequest = new SipApiRequest();
    apiRequest.setUrl(url);

    String httpMethodStr = payload.getHttpMethod();
    if (httpMethodStr != null) {
      HttpMethod method = HttpMethod.fromValue(httpMethodStr);

      apiRequest.setHttpMethod(method);
    }

    List<Object> queryParamObj = payload.getQueryParameters();

    logger.debug("Query Param Obj = " + queryParamObj);
    logger.debug("Query Param obj type = " + queryParamObj.getClass().getName());
    List<QueryParameter> queryParameters = new ArrayList<>();

    if (queryParamObj != null) {

      logger.debug("Query Params = " + queryParameters);

      for (Object paramObject : queryParamObj) {
        logger.debug("Object = " + paramObject);
        logger.debug("Object type = " + paramObject.getClass().getName());
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) paramObject;

        QueryParameter param = new QueryParameter();

        param.setKey(map.get("key"));
        param.setValue(map.get("value"));

        logger.debug("Param = " + param);

        queryParameters.add(param);
      }

      apiRequest.setQueryParameters(queryParameters);
    }

    List<Object> headerParamObj = payload.getHeaderParameters();
    List<HeaderParameter> headerParameters = new ArrayList<>();

    if (headerParamObj != null) {
      for (Object headerObject : headerParamObj) {
        logger.debug("Object = " + headerObject);
        logger.debug("Object type = " + headerObject.getClass().getName());
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) headerObject;

        System.out.println("Key = " + map.get("key"));
        System.out.println("Value = " + map.get("value"));

        logger.debug("Key Map = " + map);
        HeaderParameter param = new HeaderParameter();

        param.setKey(map.get("key"));
        param.setValue(map.get("value"));

        headerParameters.add(param);
      }
      logger.debug("Header Params = " + headerParameters);

      apiRequest.setHeaderParameters(headerParameters);
    }

    if (httpMethodStr.equalsIgnoreCase("POST")) {
      Object bodyParamsObj = payload.getBodyParameters();

      LinkedHashMap<String, Object> bodyParamMap = (LinkedHashMap<String, Object>) bodyParamsObj;

      logger.debug(bodyParamMap.toString());
      BodyParameters bodyParameters = new BodyParameters();

      bodyParameters.setContent(bodyParamMap.get("content"));

      logger.debug("Body Parameter = " + bodyParameters);

      apiRequest.setBodyParameters(bodyParameters);
    }

    try {
      SipHttpClient sipHttpClient = new SipHttpClient();

      connectionLogs.append("Connecting to ").append(url).append("\n");
      SipApiResponse response = sipHttpClient.execute(apiRequest);

      connectionLogs.append("Fetching data from ").append(url).append("\n");
      HttpStatus httpStatus = response.getHttpStatus();
      logger.info("Http Status = " + httpStatus);

      connectionLogs.append("Connection Status = " + httpStatus).append("\n");

      Object content = response.getResponseBody();

      if (content != null && content.toString().length() != 0) {
        connectionLogs
            .append("Content Length: ")
            .append(content.toString().length())
            .append(" bytes")
            .append("\n");
      }

    } catch (RestClientException exception) {
      throw new SipNestedRuntimeException("Unsupported content type text/html;charset=UTF-8");
    } catch (Exception exception) {
      throw new SipNestedRuntimeException(exception.getMessage(), exception);
    }

    return connectionLogs.toString();
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

    filePattern = "N/A";

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

    List<BisDataMetaInfo> infoList = new ArrayList<>();
    logger.debug("JobEntity = " + jobEntity);

    BisDataMetaInfo bisDataMetaInfo =
        prepareLogInfo(
            filePattern,
            "",
            source,
            new Date(),
            0L,
            "N/A",
            channelId,
            routeId,
            "",
            jobEntity.getJobId());

    // Set job status to In Progress
    BisJobEntity job = sipLogService.retriveJobById(jobEntity.getJobId());
    logger.debug("Job Entity = " + job);

    // For API jobs, total file count is always one as one job contains one HTTP request, which
    // results to a single file
    job.setTotalCount(1);
    job.setJobStatus("INPROGRESS");
    sipLogService.saveJob(job);

    logger.debug("Adding file log entry");
    sipLogService.upsert(bisDataMetaInfo, bisDataMetaInfo.getProcessId());

    infoList.add(bisDataMetaInfo);

    return infoList;
  }

  /** This method executes actual file transfer used by worker threads. */
  @Override
  public void executeFileTransfer(
      String logId, Long jobId, Long channelId, Long routeId, String fileName,
      Optional<String> destinationDirPath) {
    logger.info("Inside executeFileTransfer");
    sipLogService.upsertInProgressStatus(logId);
    BisJobEntity bisJobEntity = sipLogService.retriveJobById(jobId);

    try {
      Optional<BisRouteEntity> bisRouteEntity = this.findRouteById(routeId);

      final BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
      bisDataMetaInfo.setProcessId(logId);

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
        String sanitizedChannelMetadataStr = JsonSanitizer.sanitize(channelMetadataStr);

        ApiChannelMetadata apiChannelMetadata =
            gson.fromJson(sanitizedChannelMetadataStr, ApiChannelMetadata.class);

        logger.debug("Channel Metadata = " + apiChannelMetadata);

        String hostAddress = apiChannelMetadata.getHostAddress();
        Integer port = apiChannelMetadata.getPort();
        logger.debug("Port = " + port);

        String routeMetadataStr = routeEntity.getRouteMetadata();
        String sanitizedRouteMetadataStr = JsonSanitizer.sanitize(routeMetadataStr);
        ApiRouteMetadata apiRouteMetadata =
            gson.fromJson(sanitizedRouteMetadataStr, ApiRouteMetadata.class);

        logger.debug("Route metadata = " + apiRouteMetadata);

        String apiEndPoint = apiRouteMetadata.getApiEndPoint();
        String destinationLocation = apiRouteMetadata.getDestinationLocation();
        HttpMethod method = apiRouteMetadata.getHttpMethod();

        SipApiRequest apiRequest = new SipApiRequest();
        String url = generateUrl(hostAddress, port, apiEndPoint);

        logger.debug("URL = " + url);
        apiRequest.setUrl(url);

        apiRequest.setHttpMethod(method != null ? method : HttpMethod.GET);

        List<QueryParameter> queryParameters = apiRouteMetadata.getQueryParameters();
        if (queryParameters != null && queryParameters.size() != 0) {
          apiRequest.setQueryParameters(queryParameters);
        }

        List<HeaderParameter> headerParameters = apiRouteMetadata.getHeaderParameters();
        if (headerParameters != null && headerParameters.size() != 0) {
          apiRequest.setHeaderParameters(headerParameters);
        }

        BodyParameters bodyParameters = apiRouteMetadata.getBodyParameters();
        if (bodyParameters != null) {
          apiRequest.setBodyParameters(bodyParameters);
        }

        try {
          ZonedDateTime fileTransStartTime = ZonedDateTime.now();
          logger.debug("File" + fileName + " transfer strat time:: " + fileTransStartTime);
          SipHttpClient sipHttpClient = new SipHttpClient();

          logger.trace("Fetching API data");

          SipApiResponse response = sipHttpClient.execute(apiRequest);

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

          StringBuilder fileNameBuilder = new StringBuilder();

          String routeName = apiRouteMetadata.getRouteName();

          fileNameBuilder
              .append(routeName.toLowerCase().replaceAll(" ", "_"))
              .append("_")
              .append(getBatchId());

          if (responseContentType.equals(MediaType.APPLICATION_JSON)
              || responseContentType.equals(MediaType.APPLICATION_JSON_UTF8)) {
            logger.debug("Content type json");
            content = gson.toJson(content);
            fileNameBuilder.append(".json");
          } else if (responseContentType.equals(MediaType.TEXT_XML)
              || responseContentType.equals(MediaType.APPLICATION_XML)) {
            fileNameBuilder.append(".xml");
          } else if (responseContentType.equals(MediaType.TEXT_HTML)
              || responseContentType.equals("text/html;charset=UTF-8")) {
            fileNameBuilder.append(".html");
          } else if (responseContentType.equals(MediaType.TEXT_PLAIN)) {
            logger.debug("Content type is text");
            fileNameBuilder.append(".txt");
          }

          fileName = fileNameBuilder.toString();

          String destinationFilePath = path + fileName;
          logger.info("Writing data to " + destinationFilePath);

          writeDataToLocation(content.toString(), destinationFilePath);

          logger.trace(
              "Streaming the content of " + "the file in the directory " + "ends here " + fileName);
          ZonedDateTime fileTransEndTime = ZonedDateTime.now();
          logger.trace("File" + fileName + "transfer end time:: " + fileTransEndTime);
          bisDataMetaInfo.setFileTransferStartTime(Date.from(fileTransStartTime.toInstant()));
          bisDataMetaInfo.setFileTransferEndTime(Date.from(fileTransEndTime.toInstant()));
          bisDataMetaInfo.setDataSizeInBytes((long) content.toString().length());
          bisDataMetaInfo.setFileTransferDuration(
              Duration.between(fileTransStartTime, fileTransEndTime).toMillis());
          bisDataMetaInfo.setReceivedDataName(fileName);

          sipLogService.upsertSuccessStatus(logId, bisDataMetaInfo);
          logger.trace("File transfer duration :: " + bisDataMetaInfo.getFileTransferDuration());

          //          bisJobEntity.setSuccessCount(bisJobEntity.getSuccessCount() + 1);
          //          sipLogService.saveJob(bisJobEntity);
          sipLogService.updateSuccessCnt(jobId);
          sipLogService.updateJobStatus(jobId);

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
    return sipLogService.createJobLog(channelId, routeId, filePattern, channelType);
  }

  private BisDataMetaInfo prepareLogInfo(
      String pattern,
      String localFilePath,
      String source,
      Date receiveDate,
      Long size,
      String actualDataName,
      Long channelId,
      Long routeId,
      String destinationPath,
      long jobId) {
    BisDataMetaInfo bisDataMetaInfo = new BisDataMetaInfo();
    bisDataMetaInfo.setFilePattern(pattern);
    bisDataMetaInfo.setSource(source);
    bisDataMetaInfo.setProcessId(new UUIDGenerator().generateId(bisDataMetaInfo).toString());
    bisDataMetaInfo.setReceivedDataName(localFilePath);
    bisDataMetaInfo.setDataSizeInBytes(size);
    bisDataMetaInfo.setActualDataName(actualDataName);
    bisDataMetaInfo.setChannelType(BisChannelType.APIPULL);
    bisDataMetaInfo.setProcessState(BisProcessState.OPEN.value());
    bisDataMetaInfo.setComponentState(BisComponentState.OPEN.value());
    bisDataMetaInfo.setActualReceiveDate(receiveDate);
    bisDataMetaInfo.setChannelId(channelId);
    bisDataMetaInfo.setRouteId(routeId);
    bisDataMetaInfo.setDestinationPath(destinationPath);
    bisDataMetaInfo.setJobId(jobId);
    return bisDataMetaInfo;
  }
}

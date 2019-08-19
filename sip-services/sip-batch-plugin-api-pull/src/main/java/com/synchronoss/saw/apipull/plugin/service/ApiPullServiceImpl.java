package com.synchronoss.saw.apipull.plugin.service;

import com.google.gson.Gson;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import com.synchronoss.saw.apipull.pojo.BodyParameters;
import com.synchronoss.saw.apipull.pojo.ChannelMetadata;
import com.synchronoss.saw.apipull.pojo.HeaderParameter;
import com.synchronoss.saw.apipull.pojo.HttpMethod;
import com.synchronoss.saw.apipull.pojo.QueryParameter;
import com.synchronoss.saw.apipull.pojo.RouteMetadata;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import com.synchronoss.saw.apipull.service.HttpClient;
import com.synchronoss.saw.apipull.service.contentwriters.ContentWriter;
import com.synchronoss.saw.apipull.service.contentwriters.TextContentWriter;
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import java.io.IOException;
import java.net.URI;

import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sncr.bda.core.file.FileProcessor;
import sncr.bda.core.file.FileProcessorFactory;

@Service("apipullService")
public class ApiPullServiceImpl extends SipPluginContract {

  private static final Logger logger = LoggerFactory.getLogger(ApiPullServiceImpl.class);
  @Autowired private BisChannelDataRestRepository bisChannelDataRestRepository;

  @Autowired private BisRouteDataRestRepository bisRouteDataRestRepository;

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
    Optional<BisRouteEntity> bisRouteEntity = this.findRouteById(entityId);

    if (bisRouteEntity.isPresent()) {
      BisRouteEntity entity = bisRouteEntity.get();
      long channelId = entity.getBisChannelSysId();
      Optional<BisChannelEntity> bisChannelEntity = this.findChannelById(channelId);

      if (!bisChannelEntity.isPresent()) {
        // TODO: Throw exception here
      }

      Gson gson = new Gson();
      BisChannelEntity channelEntity = bisChannelEntity.get();

      String channelMetadataStr = channelEntity.getChannelMetadata();

      ChannelMetadata channelMetadata = gson.fromJson(channelMetadataStr, ChannelMetadata.class);

      String hostAddress = channelMetadata.getHostAddress();
      int port = channelMetadata.getPort();

      String routeMetadataStr = entity.getRouteMetadata();
      RouteMetadata routeMetadata = gson.fromJson(routeMetadataStr, RouteMetadata.class);

      List<QueryParameter> queryParameters = routeMetadata.getQueryParameters();
      List<HeaderParameter> headerParameters = routeMetadata.getHeaderParameters();
      BodyParameters bodyParameters = routeMetadata.getBodyParameters();

      String apiEndPoint = routeMetadata.getApiEndPoint();
      String destinationLocation = routeMetadata.getDestinationLocation();
      HttpMethod method = routeMetadata.getHttpMethod();

      HttpClient httpClient = new HttpClient();

      SipApiRequest apiRequest = new SipApiRequest();
      apiRequest.setUrl(generateUrl(hostAddress, port, apiEndPoint));

      apiRequest.setHttpMethod(method != null ? method : HttpMethod.GET);

      if (queryParameters != null && queryParameters.size() != 0) {
        apiRequest.setQueryParameters(queryParameters);
      }

      if (headerParameters != null && headerParameters.size() != 0) {
        apiRequest.setHeaderParameters(headerParameters);
      }

      if (bodyParameters != null) {
        apiRequest.setBodyParameters(bodyParameters);
      }

      ApiResponse response = httpClient.execute(apiRequest);

      String responseContentType = response.getContentType();

      Object content = response.getResponseBody();

      try {

        ContentWriter contentWriter = null;
        if (responseContentType == "application/json" || responseContentType == "text/plain") {
          contentWriter = new TextContentWriter(content.toString(), responseContentType);
        } else {
          // Yet to implement
        }

        contentWriter.write("");
      } catch (IOException exception) {
        throw new SipNestedRuntimeException(ExceptionUtils.getFullStackTrace(exception));
      }
    } else {
      // TODO: Throw exception here
    }

    return null;
  }

  /** This method is to test connect the source. */
  @Override
  public String connectChannel(Long entityId) throws SipNestedRuntimeException {
    return null;
  }

  /** This method is to test connect the route. */
  @Override
  public String immediateConnectRoute(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException, IOException {
    return null;
  }

  /** This method is to test connect the source. */
  @Override
  public String immediateConnectChannel(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException {
    return null;
  }

  /** This method executes actual file transfer used by worker threads. */
  @Override
  public void executeFileTransfer(
      String logId, Long jobId, Long channelId, Long routeId, String fileName) {}

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
}

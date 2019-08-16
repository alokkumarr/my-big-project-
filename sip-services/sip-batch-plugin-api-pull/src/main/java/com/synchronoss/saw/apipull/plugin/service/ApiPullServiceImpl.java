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
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("apipullService")
public class ApiPullServiceImpl extends SipPluginContract {

  @Autowired private BisChannelDataRestRepository bisChannelDataRestRepository;

  @Autowired private BisRouteDataRestRepository bisRouteDataRestRepository;
  /** This method is to test connect the route. */
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

      //TODO: Handle api response
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

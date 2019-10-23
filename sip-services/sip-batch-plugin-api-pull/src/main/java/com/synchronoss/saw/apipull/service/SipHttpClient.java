package com.synchronoss.saw.apipull.service;

import com.synchronoss.saw.apipull.exceptions.SipApiPullExecption;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import com.synchronoss.saw.apipull.pojo.BodyParameters;
import com.synchronoss.saw.apipull.pojo.HeaderParameter;
import com.synchronoss.saw.apipull.pojo.QueryParameter;
import com.synchronoss.saw.apipull.pojo.HttpMethod;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class SipHttpClient {
  private static final Logger logger = LoggerFactory.getLogger(SipHttpClient.class);

  public ApiResponse execute(SipApiRequest sipApiRequest) throws SipApiPullExecption {
    boolean validRequest =
        !StringUtils.isEmpty(sipApiRequest.getUrl())
            && !StringUtils.isEmpty(sipApiRequest.getHttpMethod());
    ApiResponse apiResponse = new ApiResponse();
    if (validRequest) {
      String url = sipApiRequest.getUrl();

      if (sipApiRequest.getHttpMethod() == HttpMethod.GET) {
        logger.debug("HttpMethod : {GET}");
        SipHttpClientGet get = new SipHttpClientGet(url);
        if (!CollectionUtils.isEmpty(sipApiRequest.getQueryParameters())) {
          for (QueryParameter qp : sipApiRequest.getQueryParameters()) {
            get.setQueryParam(qp.getKey(), qp.getValue());
          }
        }

        if (!CollectionUtils.isEmpty(sipApiRequest.getHeaderParameters())) {
          for (HeaderParameter hp : sipApiRequest.getHeaderParameters()) {
            get.setHeaderParams(hp.getKey(), hp.getValue());
          }
        }
        apiResponse = get.execute();
        return apiResponse;
      } else {
        logger.debug("HttpMethod : {POST}");
        SipHttpClientPost post = new SipHttpClientPost(url);
        if (!CollectionUtils.isEmpty(sipApiRequest.getQueryParameters())) {
          for (QueryParameter qp : sipApiRequest.getQueryParameters()) {
            post.setQueryParam(qp.getKey(), qp.getValue());
          }
        }

        if (!CollectionUtils.isEmpty(sipApiRequest.getHeaderParameters())) {
          for (HeaderParameter hp : sipApiRequest.getHeaderParameters()) {
            post.setHeaderParams(hp.getKey(), hp.getValue());
          }
        } else {
          logger.trace("Setting default header");
          post.setHeaderParam(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }

        if (sipApiRequest.getBodyParameters() != null) {
          BodyParameters body = sipApiRequest.getBodyParameters();

          String contentType = body.getType();

          if(contentType == null || contentType.length() == 0) {
              contentType = MediaType.APPLICATION_JSON_VALUE;
          }

          post.setRawData(body.getContent().toString(), contentType);
        }

        apiResponse = post.execute();
        return apiResponse;
      }

    } else {
      if (StringUtils.isEmpty(sipApiRequest.getUrl())) {
        throw new SipApiPullExecption("Url can't be null or empty!!");
      } else {
        throw new SipApiPullExecption(
            "Invalid HttpMethod, For SIP BIS API - PULL, Http Method supported are GET and POST");
      }
    }
  }
}

package com.synchronoss.saw.apipull.service;

import com.synchronoss.saw.apipull.pojo.ApiResponse;
import com.synchronoss.saw.apipull.pojo.HeaderParameter;
import com.synchronoss.saw.apipull.pojo.QueryParameter;
import com.synchronoss.saw.apipull.pojo.HttpMethod;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class HttpClient {
  private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

  public ApiResponse execute(SipApiRequest sipApiRequest) {
    boolean validRequest =
        !StringUtils.isEmpty(sipApiRequest.getUrl())
            && !StringUtils.isEmpty(sipApiRequest.getHttpMethod());
    ApiResponse apiResponse = new ApiResponse();
    if (validRequest) {
      String url = sipApiRequest.getUrl();

      if (sipApiRequest.getHttpMethod() == HttpMethod.GET) {
        logger.debug("HttpMethod : {GET}");
        HttpClientGet get = new HttpClientGet(url);
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

        logger.info("Response Body to be returned :{}", get.execute().getResponseBody());
        return get.execute();
      }
    }
    return apiResponse;
  }
}

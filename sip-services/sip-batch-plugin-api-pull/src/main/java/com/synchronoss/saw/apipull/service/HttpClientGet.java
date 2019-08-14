package com.synchronoss.saw.apipull.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class HttpClientGet extends SncrBaseHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientGet.class);
  RestTemplate restTemplate = new RestTemplate();

  /**
   * This Constructor accepts host name(a mandotory parameter for all methods)
   *
   * @param host
   */
  public HttpClientGet(String host) {
    super(host);
  }

  @Override
  public ApiResponse execute() {
    ApiResponse apiResponse = new ApiResponse();
    url = this.generateUrl(apiEndPoint, queryParams);

    try {
      apiResponse.setResponseBody(restTemplate.getForObject(url, JsonNode.class));
      HttpHeaders httpHeaders = restTemplate.headForHeaders(url);
      apiResponse.setHttpHeaders(httpHeaders);
      if (httpHeaders.getContentType() != null) {
        apiResponse.setContentType(httpHeaders.getContentType().toString());
      }

    } catch (Exception e) {
        logger.error("Unable to fetch data for url : {}",url);
        logger.error("Stacktrace : {}",e.getMessage());
    }
    return apiResponse;
  }
}

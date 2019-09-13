package com.synchronoss.saw.apipull.service;

import com.synchronoss.saw.apipull.pojo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    logger.info("Url : {}", url);

    try {
      HttpHeaders headers = new HttpHeaders();
      headerParams.entrySet().stream()
          .forEach(entry -> headers.set(entry.getKey(), entry.getValue().toString()));
      HttpEntity httpEntity = new HttpEntity(headers);
      ResponseEntity<Object> response =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object.class);

      apiResponse.setResponseBody(response.getBody());
      HttpHeaders httpHeaders = response.getHeaders();
      apiResponse.setHttpHeaders(httpHeaders);
      logger.info("Response Code : {}", response.getStatusCode());
      logger.info("Response Body : {}", response.getBody());
      logger.info("Response headers : {}", response.getHeaders().toString());
    } catch (Exception e) {
      logger.error("Unable to fetch data for url : " + url, e);
    }
    return apiResponse;
  }
}

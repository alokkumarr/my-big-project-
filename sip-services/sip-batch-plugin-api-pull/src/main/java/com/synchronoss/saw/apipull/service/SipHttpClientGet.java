package com.synchronoss.saw.apipull.service;

import com.synchronoss.saw.apipull.pojo.SipApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class SipHttpClientGet extends SipBaseHttpClient {
  private static final Logger logger = LoggerFactory.getLogger(SipHttpClientGet.class);
  RestTemplate restTemplate = new RestTemplate();

  /**
   * This Constructor accepts host name(a mandotory parameter for all methods)
   *
   * @param host
   */
  public SipHttpClientGet(String host) {
    super(host);
  }

  @Override
  public SipApiResponse execute() {
    SipApiResponse sipApiResponse = new SipApiResponse();
    url = this.generateUrl(apiEndPoint, queryParams);
    logger.info("Url : {}", url);

    try {
      HttpHeaders headers = new HttpHeaders();
      headerParams.entrySet().stream()
          .forEach(entry -> headers.set(entry.getKey(), entry.getValue().toString()));
      HttpEntity httpEntity = new HttpEntity(headers);
      ResponseEntity<Object> response =
          restTemplate.exchange(url, HttpMethod.GET, httpEntity, Object.class);

      sipApiResponse.setResponseBody(response.getBody());
      sipApiResponse.setHttpStatus(response.getStatusCode());
      HttpHeaders httpHeaders = response.getHeaders();
      sipApiResponse.setHttpHeaders(httpHeaders);
      logger.debug("Response Code : {}", response.getStatusCode());
      logger.debug("Response Body : {}", response.getBody());
      logger.debug("Response headers : {}", response.getHeaders().toString());
    } catch (RestClientException ex) {
      logger.error("RestClient Exception occurred");
      throw ex;
    } catch (Exception ex) {
      logger.error("Exception occurred");
      throw ex;
    }
    return sipApiResponse;
  }
}

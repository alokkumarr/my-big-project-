package com.synchronoss.saw.apipull.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class HttpClientGet extends SncrBaseHttpClient {
  // Create object for HttpGet
  HttpGet request;
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
  public JsonNode execute() {
      JsonNode jsonNode = null;
    url = this.generateUrl(apiEndPoint, queryParams);
    request = new HttpGet(url);

    // Create a reference for CloseableHttpResponse
    CloseableHttpResponse response = null;
    try {
        jsonNode = restTemplate.getForObject(url, JsonNode.class);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return jsonNode;
  }
}

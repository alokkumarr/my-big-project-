package com.synchronoss.saw.apipull.service;

import com.google.gson.Gson;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class HttpClientPost extends SncrBaseHttpClient {

  Map<String, Object> formData;
  String textData;

  HttpPost httpPost;

  RestTemplate restTemplate = new RestTemplate();
  ApiResponse apiResponse = new ApiResponse();

  public HttpClientPost(String host) {
    super(host);
  }

  private void setPostHeaders() {
    headerParams.entrySet().stream()
        .forEach(entry -> httpPost.setHeader(entry.getKey(), entry.getValue().toString()));
  }

  /**
   * This function is used to set the raw data for the post request
   *
   * @param content - Actual content
   * @param contentType - Type of the content. E.g.: application/json, text/plain etc
   * @throws Exception
   */
  public void setRawData(String content, String contentType) throws Exception {
    textData = content;
    this.setHeaderParam("Content-Type", contentType);
    this.setHeaderParam("Accept", contentType);
  }

  /**
   * Function returns a boolean value that checks for valid Json
   *
   * @param jsonInString - Json Content
   * @return - True if Json is valid else false
   */
  public static boolean isJSONValid(String jsonInString) {
    try {
      Gson gson = new Gson();
      gson.fromJson(jsonInString, Object.class);
      return true;
    } catch (com.google.gson.JsonSyntaxException ex) {
      return false;
    }
  }

  public static boolean isApplicationXmlValid(String xml) throws ParserConfigurationException {

    try {
      // TODO : validate against request body
      return true;
    } catch (Exception e) {
      System.out.println("Invalid XML");
      e.printStackTrace();
    }
    return false;
  }

  public static boolean isTextXmlValid(String xml) {
    // TODO : validate against request body
    return true;
  }

  public static boolean isHtmlValid(String html) {
    // TODO : validate against request body
    return true;
  }

  @Override
  public ApiResponse execute() throws Exception {
    url = this.generateUrl(apiEndPoint, queryParams);
    httpPost = new HttpPost(url);

    if (this.headerParams.size() != 0) {
      setPostHeaders();
    }

    HttpHeaders httpHeaders = new HttpHeaders();
    String contentType = (String) headerParams.get("Content-Type");

    if (contentType == ContentType.MULTIPART_FORM_DATA.getMimeType()) {

      if (formData != null && formData.size() > 0) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity =
            new HttpEntity(formData, httpHeaders);
        apiResponse.setResponseBody(restTemplate.postForEntity(url, httpEntity, Object.class));
        HttpHeaders resHeaders = restTemplate.headForHeaders(url);
        apiResponse.setHttpHeaders(resHeaders);
      }
    } else if (contentType == ContentType.APPLICATION_JSON.getMimeType()) {
      if (textData != null && textData.length() > 0) {
        headerParams.entrySet().stream()
            .forEach(entry -> httpHeaders.set(entry.getKey(), entry.getValue().toString()));

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (!isJSONValid(textData)) {
          throw new RuntimeException("Not a valid Json!!");
        }
        HttpEntity httpEntity = new HttpEntity(textData, httpHeaders);
        ResponseEntity<Object> response =
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);

        apiResponse.setResponseBody(response.getBody());
        HttpHeaders responseHeaders = response.getHeaders();
        apiResponse.setHttpHeaders(responseHeaders);
        if (responseHeaders.getContentType() != null) {
          apiResponse.setContentType(httpHeaders.getContentType().toString());
        }
      }
    } else if (contentType == ContentType.TEXT_PLAIN.getMimeType()) {
      if (textData != null && textData.length() > 0) {
        StringEntity stringEntity = new StringEntity(textData);
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        apiResponse.setResponseBody(restTemplate.postForObject(url, stringEntity, Object.class));
        HttpHeaders resHeaders = restTemplate.headForHeaders(url);
        apiResponse.setHttpHeaders(resHeaders);
      }
    } else if (contentType == ContentType.APPLICATION_XML.getMimeType()) {
      if (textData != null && textData.length() > 0) {
        StringEntity stringEntity = new StringEntity(textData);
        if (!isApplicationXmlValid(textData)) {
          throw new RuntimeException("Not a valid XML!!");
        }
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        apiResponse.setResponseBody(restTemplate.postForObject(url, stringEntity, Object.class));
        HttpHeaders resHeaders = restTemplate.headForHeaders(url);
        apiResponse.setHttpHeaders(resHeaders);
      }
    } else if (contentType == ContentType.TEXT_XML.getMimeType()) {
      if (textData != null && textData.length() > 0) {
        StringEntity stringEntity = new StringEntity(textData);
        if (!isTextXmlValid(textData)) {
          throw new RuntimeException("Not a valid XML!!");
        }
        httpHeaders.setContentType(MediaType.TEXT_XML);
        apiResponse.setResponseBody(restTemplate.postForObject(url, stringEntity, Object.class));
        HttpHeaders resHeaders = restTemplate.headForHeaders(url);
        apiResponse.setHttpHeaders(resHeaders);
      }
    } else if (contentType == ContentType.TEXT_HTML.getMimeType()) {
      if (textData != null && textData.length() > 0) {
        StringEntity stringEntity = new StringEntity(textData);
        if (!isHtmlValid(textData)) {
          throw new RuntimeException("Invalid HTML body!!");
        }
        httpHeaders.setContentType(MediaType.TEXT_HTML);
        apiResponse.setResponseBody(restTemplate.postForObject(url, stringEntity, Object.class));
        HttpHeaders resHeaders = restTemplate.headForHeaders(url);
        apiResponse.setHttpHeaders(resHeaders);
      }
    }

    return apiResponse;
  }
}

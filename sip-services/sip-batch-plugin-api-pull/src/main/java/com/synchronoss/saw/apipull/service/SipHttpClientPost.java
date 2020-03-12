package com.synchronoss.saw.apipull.service;

import com.google.gson.Gson;
import com.synchronoss.saw.apipull.exceptions.SipApiPullExecption;
import com.synchronoss.saw.apipull.pojo.SipApiResponse;
import com.synchronoss.sip.utils.SipCommonUtils;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.validation.SchemaFactory;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SipHttpClientPost extends SipBaseHttpClient {
  private static final Logger logger = LoggerFactory.getLogger(SipHttpClientPost.class);

  Map<String, Object> formData;
  String textData;

  RestTemplate restTemplate = new RestTemplate();
  SipApiResponse sipApiResponse = new SipApiResponse();

  public SipHttpClientPost(String host) {
    super(host);
  }

  /**
   * This function is used to set the raw data for the post request
   *
   * @param content - Actual content
   * @param contentType - Type of the content. E.g.: application/json, text/plain etc
   * @throws Exception
   */
  public void setRawData(String content, String contentType) {
    logger.debug("Setting RequestBody : Content-Type = " + contentType + " \t Body : " + content);
    textData = content;
    this.setHeaderParam(CONTENT_TYPE, contentType);
    this.setHeaderParam(ACCEPT, contentType);
  }

  /**
   * Function returns a boolean value that checks for valid Json
   *
   * @param jsonInString - Json Content
   * @return - True if Json is valid else false
   */
  public static boolean isJSONValid(String jsonInString) {
    try {
      String sanitizedJsonInString = SipCommonUtils.sanitizeJson(jsonInString);
      Gson gson = new Gson();
      gson.fromJson(sanitizedJsonInString, Object.class);
      logger.debug("Request body is a Valid Json");
      return true;
    } catch (com.google.gson.JsonSyntaxException ex) {
      logger.error("Request body is not a Valid Json");
      return false;
    }
  }

  public static boolean isApplicationXmlValid(String xml) throws SipApiPullExecption {

    try {
      // TODO : validate against request body
      SchemaFactory schema = SchemaFactory.newInstance(xml);

      return true;
    } catch (Exception e) {
      logger.error("Invalid XML", e.getMessage());
    }
    return false;
  }

  public static boolean isTextXmlValid(String xml) {
    // TODO : validate against request body
    return true;
  }

  public static boolean isHtmlValid(String html) {
    Pattern pattern = Pattern.compile(html);
    Matcher matcher = pattern.matcher(html);

    return matcher.matches();
  }

  @Override
  public SipApiResponse execute() throws SipApiPullExecption {
    logger.trace("Inside Post Execute method !!");
    url = this.generateUrl(apiEndPoint, queryParams);
    logger.info("Url : {}", url);

    HttpHeaders httpHeaders = new HttpHeaders();
    if (headerParams != null && headerParams.get(CONTENT_TYPE) != null) {

      String contentType = (String) headerParams.get(CONTENT_TYPE);
      logger.info(CONTENT_TYPE + " : ", contentType);

      headerParams.entrySet().stream()
          .forEach(entry -> httpHeaders.set(entry.getKey(), entry.getValue().toString()));

      if (contentType == ContentType.MULTIPART_FORM_DATA.getMimeType()) {

        if (formData == null || !(formData.size() > 0)) {
          throw new SipApiPullExecption("Missing Form data..!!");
        }
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity =
            new HttpEntity(formData, httpHeaders);
        ResponseEntity<Object> response =
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
        logger.debug("Response Code : {}", response.getStatusCode());
        logger.debug("Response Body : {}", response.getBody());
        sipApiResponse.setResponseBody(response.getBody().toString());
        logger.debug("Response headers : {}", response.getHeaders().toString());
        HttpHeaders resHeaders = response.getHeaders();
        sipApiResponse.setHttpHeaders(resHeaders);
        sipApiResponse.setHttpStatus(response.getStatusCode());
        return sipApiResponse;

      } else if (contentType == ContentType.APPLICATION_JSON.getMimeType()) {
        if (textData != null && textData.length() > 0) {

          httpHeaders.setContentType(MediaType.APPLICATION_JSON);
          if (!isJSONValid(textData)) {
            throw new SipApiPullExecption("Not a valid Json!!");
          }
          HttpEntity httpEntity = new HttpEntity(textData, httpHeaders);
          ResponseEntity<Object> response =
              restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
          logger.debug("Response Code : {}", response.getStatusCode());
          logger.debug("Response Body : {}", response.getBody());
          sipApiResponse.setResponseBody(response.getBody());
          logger.info("Response headers : {}", response.getHeaders().toString());
          HttpHeaders responseHeaders = response.getHeaders();
          sipApiResponse.setHttpHeaders(responseHeaders);
          sipApiResponse.setHttpStatus(response.getStatusCode());
        }
      } else if (contentType == ContentType.TEXT_PLAIN.getMimeType()) {
        if (textData != null && textData.length() > 0) {
          httpHeaders.setContentType(MediaType.TEXT_PLAIN);
          HttpEntity httpEntity = new HttpEntity(textData, httpHeaders);
          ResponseEntity<Object> response =
              restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
          logger.debug("Response Code : {}", response.getStatusCode());
          logger.debug("Response Body : {}", response.getBody());
          sipApiResponse.setResponseBody(response.getBody());
          logger.debug("Response headers : {}", response.getHeaders().toString());
          HttpHeaders resHeaders = response.getHeaders();
          sipApiResponse.setHttpHeaders(resHeaders);
          sipApiResponse.setHttpStatus(response.getStatusCode());
        }
      } else if (contentType == ContentType.APPLICATION_XML.getMimeType()) {
        if (textData != null && textData.length() > 0) {
          if (!isApplicationXmlValid(textData)) {
            throw new SipApiPullExecption("Not a valid XML!!");
          }
          httpHeaders.setContentType(MediaType.APPLICATION_XML);
          HttpEntity httpEntity = new HttpEntity(textData, httpHeaders);
          ResponseEntity<Object> response =
              restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
          logger.debug("Response Code : {}", response.getStatusCode());
          logger.debug("Response Body : {}", response.getBody());
          sipApiResponse.setResponseBody(response.getBody());
          logger.debug("Response headers : {}", response.getHeaders().toString());
          HttpHeaders resHeaders = response.getHeaders();
          sipApiResponse.setHttpHeaders(resHeaders);
          sipApiResponse.setHttpStatus(response.getStatusCode());
        }
      } else if (contentType == ContentType.TEXT_XML.getMimeType()) {
        if (textData != null && textData.length() > 0) {
          if (!isTextXmlValid(textData)) {
            throw new SipApiPullExecption("Not a valid XML!!");
          }
          httpHeaders.setContentType(MediaType.TEXT_XML);
          HttpEntity httpEntity = new HttpEntity(textData, httpHeaders);
          ResponseEntity<Object> response =
              restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
          logger.debug("Response Code : {}", response.getStatusCode());
          logger.debug("Response Body : {}", response.getBody());
          sipApiResponse.setResponseBody(response.getBody());
          logger.info("Response headers : {}", response.getHeaders().toString());
          HttpHeaders resHeaders = response.getHeaders();
          sipApiResponse.setHttpHeaders(resHeaders);
          sipApiResponse.setHttpStatus(response.getStatusCode());
        }
      } else if (contentType == ContentType.TEXT_HTML.getMimeType()) {
        if (textData != null && textData.length() > 0) {
          if (!isHtmlValid(textData)) {
            throw new SipApiPullExecption("Invalid HTML body!!");
          }
          httpHeaders.setContentType(MediaType.TEXT_HTML);
          HttpEntity httpEntity = new HttpEntity(textData, httpHeaders);
          ResponseEntity<Object> response =
              restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
          logger.debug("Response Code : {}", response.getStatusCode());
          logger.debug("Response Body : {}", response.getBody());
          sipApiResponse.setResponseBody(response.getBody());
          logger.debug("Response headers : {}", response.getHeaders().toString());
          HttpHeaders resHeaders = response.getHeaders();
          sipApiResponse.setHttpHeaders(resHeaders);
          sipApiResponse.setHttpStatus(response.getStatusCode());
        }
      }
    }

    return sipApiResponse;
  }
}

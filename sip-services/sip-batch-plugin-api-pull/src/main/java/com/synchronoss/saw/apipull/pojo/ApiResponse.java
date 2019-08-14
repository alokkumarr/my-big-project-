package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
  Object responseBody;
  String contentType;
  HttpHeaders httpHeaders;

  /**
   * This object can hold any response of Api(String, Json, array..etc)
   *
   * @return Object object
   */
  public Object getResponseBody() {
    return responseBody;
  }

  public void setResponseBody(Object responseBody) {
    this.responseBody = responseBody;
  }

  /** ( Content type of an API response) */
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /** Api response headers */
  public HttpHeaders getHttpHeaders() {
    return httpHeaders;
  }

  /** Api response headers */
  public void setHttpHeaders(HttpHeaders httpHeaders) {
    this.httpHeaders = httpHeaders;
  }
}

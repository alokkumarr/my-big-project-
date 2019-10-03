package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
  Object responseBody;
  HttpStatus httpStatus;
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

  /** Api response headers */
  public HttpHeaders getHttpHeaders() {
    return httpHeaders;
  }

  /** Api response headers */
  public void setHttpHeaders(HttpHeaders httpHeaders) {
    this.httpHeaders = httpHeaders;
  }

  /** HttpStatus code */
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  /** HttpStatus code */
  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }
}

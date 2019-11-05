package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel
public class SipApiResponse {

  @JsonProperty("responseBody")
  @ApiModelProperty(
      notes = "Response body sent to the caller",
      name = "responseBody",
      required = true)
  Object responseBody;

  @JsonProperty("httpStatus")
  @ApiModelProperty(
      notes = "Http status code and message",
      name = "httpStatus",
      required = true)
  HttpStatus httpStatus;

  @JsonProperty("httpHeaders")
  @ApiModelProperty(
      notes = "Http response headers",
      name = "httpHeaders",
      required = true)
  HttpHeaders httpHeaders;

  /**
   * This object can hold any response of Api(String, Json, array..etc)
   *
   * @return Object object
   */
  @JsonProperty("responseBody")
  public Object getResponseBody() {
    return responseBody;
  }

  @JsonProperty("responseBody")
  public void setResponseBody(Object responseBody) {
    this.responseBody = responseBody;
  }

  /** Api response headers */
  @JsonProperty("httpHeaders")
  public HttpHeaders getHttpHeaders() {
    return httpHeaders;
  }

  /** Api response headers */
  @JsonProperty("httpHeaders")
  public void setHttpHeaders(HttpHeaders httpHeaders) {
    this.httpHeaders = httpHeaders;
  }

  /** HttpStatus code */
  @JsonProperty("httpStatus")
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  /** HttpStatus code */
  @JsonProperty("httpStatus")
  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }
}

package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "hostAddress",
  "port",
  "apiEndPoint",
  "httpMethod",
  "queryParameters",
  "headerParameters",
  "urlParameters",
  "bodyParameters"
})
public class ChannelMetadata {

  @JsonProperty("hostAddress")
  private String hostAddress;

  @JsonProperty("port")
  private Integer port;

  @JsonProperty("apiEndPoint")
  private String apiEndPoint;
  /** (Required) */
  @JsonProperty("httpMethod")
  private HttpMethod httpMethod = HttpMethod.fromValue("GET");
  /** (Required) */
  @JsonProperty("queryParameters")
  private List<QueryParameter> queryParameters = null;

  @JsonProperty("headerParameters")
  private List<HeaderParameter> headerParameters = null;
  /** (Required) */
  @JsonProperty("urlParameters")
  private List<Object> urlParameters = null;

  @JsonProperty("bodyParameters")
  private BodyParameters bodyParameters;

  @JsonProperty("hostAddress")
  public String getHostAddress() {
    return hostAddress;
  }

  @JsonProperty("hostAddress")
  public void setHostAddress(String hostAddress) {
    this.hostAddress = hostAddress;
  }

  @JsonProperty("port")
  public Integer getPort() {
    return port;
  }

  @JsonProperty("port")
  public void setPort(Integer port) {
    this.port = port;
  }

  /** (Required) */
  @JsonProperty("apiEndPoint")
  public String getApiEndPoint() {
    return apiEndPoint;
  }

  /** (Required) */
  @JsonProperty("apiEndPoint")
  public void setApiEndPoint(String apiEndPoint) {
    this.apiEndPoint = apiEndPoint;
  }

  /** (Required) */
  @JsonProperty("httpMethod")
  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  /** (Required) */
  @JsonProperty("httpMethod")
  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  /** (Required) */
  @JsonProperty("queryParameters")
  public List<QueryParameter> getQueryParameters() {
    return queryParameters;
  }

  /** (Required) */
  @JsonProperty("queryParameters")
  public void setQueryParameters(List<QueryParameter> queryParameters) {
    this.queryParameters = queryParameters;
  }

  @JsonProperty("headerParameters")
  public List<HeaderParameter> getHeaderParameters() {
    return headerParameters;
  }

  @JsonProperty("headerParameters")
  public void setHeaderParameters(List<HeaderParameter> headerParameters) {
    this.headerParameters = headerParameters;
  }

  /** (Required) */
  @JsonProperty("urlParameters")
  public List<Object> getUrlParameters() {
    return urlParameters;
  }

  /** (Required) */
  @JsonProperty("urlParameters")
  public void setUrlParameters(List<Object> urlParameters) {
    this.urlParameters = urlParameters;
  }

  @JsonProperty("bodyParameters")
  public BodyParameters getBodyParameters() {
    return bodyParameters;
  }

  @JsonProperty("bodyParameters")
  public void setBodyParameters(BodyParameters bodyParameters) {
    this.bodyParameters = bodyParameters;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("hostAddress", hostAddress)
        .append("port", port)
        .append("apiEndPoint", apiEndPoint)
        .append("httpMethod", httpMethod)
        .append("queryParameters", queryParameters)
        .append("headerParameters", headerParameters)
        .append("urlParameters", urlParameters)
        .append("bodyParameters", bodyParameters)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(hostAddress)
        .append(port)
        .append(headerParameters)
        .append(apiEndPoint)
        .append(bodyParameters)
        .append(httpMethod)
        .append(urlParameters)
        .append(queryParameters)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof RouteMetadata) == false) {
      return false;
    }
    ChannelMetadata rhs = ((ChannelMetadata) other);
    return new EqualsBuilder()
        .append(hostAddress, rhs.hostAddress)
        .append(port, rhs.port)
        .append(headerParameters, rhs.headerParameters)
        .append(apiEndPoint, rhs.apiEndPoint)
        .append(bodyParameters, rhs.bodyParameters)
        .append(httpMethod, rhs.httpMethod)
        .append(urlParameters, rhs.urlParameters)
        .append(queryParameters, rhs.queryParameters)
        .isEquals();
  }
}

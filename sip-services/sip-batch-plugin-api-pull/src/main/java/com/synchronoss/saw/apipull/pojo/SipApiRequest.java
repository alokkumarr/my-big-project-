package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "url",
  "httpMethod",
  "queryParameters",
  "headerParameters",
  "urlParameters",
  "bodyParameters"
})
@ApiModel
public class SipApiRequest {

  /** (Required) */
  @JsonProperty("url")
  @ApiModelProperty(
      notes = "URL from where the data should be pulled",
      name = "url",
      required = true)
  private String url;

  /** (Required) */
  @JsonProperty("httpMethod")
  @ApiModelProperty(
      notes = "HTTP method to be used for the request (GET/POST)",
      name = "httpMethod",
      required = true)
  private HttpMethod httpMethod = HttpMethod.fromValue("GET");

  @JsonProperty("queryParameters")
  @ApiModelProperty(
      notes = "List fo query parameters used for the API request",
      name = "queryParameters")
  private List<QueryParameter> queryParameters = null;

  @JsonProperty("headerParameters")
  @ApiModelProperty(
      notes = "List fo header parameters used for the API request",
      name = "headerParameters")
  private List<HeaderParameter> headerParameters = null;

  /** Added just as a place holder. No implementation for now */
  @JsonProperty("urlParameters")
  @ApiModelProperty(
      notes = "List fo url parameters used for the API request",
      name = "urlParameters")
  private List<Object> urlParameters = null;

  @JsonProperty("bodyParameters")
  @ApiModelProperty(notes = "Body parameters used for the API request",
      name="bodyParameters")
  private BodyParameters bodyParameters;

  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  @JsonProperty("url")
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonProperty("httpMethod")
  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  @JsonProperty("httpMethod")
  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  @JsonProperty("queryParameters")
  public List<QueryParameter> getQueryParameters() {
    return queryParameters;
  }

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

  @JsonProperty("urlParameters")
  public List<Object> getUrlParameters() {
    return urlParameters;
  }

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
        .append("url", url)
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
        .append(url)
        .append(headerParameters)
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
    if ((other instanceof SipApiRequest) == false) {
      return false;
    }
    SipApiRequest rhs = ((SipApiRequest) other);
    return new EqualsBuilder()
        .append(url, rhs.url)
        .append(headerParameters, rhs.headerParameters)
        .append(bodyParameters, rhs.bodyParameters)
        .append(httpMethod, rhs.httpMethod)
        .append(urlParameters, rhs.urlParameters)
        .append(queryParameters, rhs.queryParameters)
        .isEquals();
  }
}

package com.synchronoss.saw.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "apiEndPoint",
  "httpMethod",
  "queryParameters",
  "headerParameters",
  "urlParameters",
  "bodyParameters"
})
public class RouteMedata {

  /** (Required) */
  @JsonProperty("apiEndPoint")
  private String apiEndPoint;
  /** (Required) */
  @JsonProperty("httpMethod")
  private RouteMedata.HttpMethod httpMethod = RouteMedata.HttpMethod.fromValue("GET");
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
  public RouteMedata.HttpMethod getHttpMethod() {
    return httpMethod;
  }

  /** (Required) */
  @JsonProperty("httpMethod")
  public void setHttpMethod(RouteMedata.HttpMethod httpMethod) {
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
    if ((other instanceof RouteMedata) == false) {
      return false;
    }
    RouteMedata rhs = ((RouteMedata) other);
    return new EqualsBuilder()
        .append(headerParameters, rhs.headerParameters)
        .append(apiEndPoint, rhs.apiEndPoint)
        .append(bodyParameters, rhs.bodyParameters)
        .append(httpMethod, rhs.httpMethod)
        .append(urlParameters, rhs.urlParameters)
        .append(queryParameters, rhs.queryParameters)
        .isEquals();
  }

  public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");
    private final String value;
    private static final Map<String, RouteMedata.HttpMethod> CONSTANTS =
        new HashMap<String, RouteMedata.HttpMethod>();

    static {
      for (RouteMedata.HttpMethod c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private HttpMethod(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    @JsonValue
    public String value() {
      return this.value;
    }

    @JsonCreator
    public static RouteMedata.HttpMethod fromValue(String value) {
      RouteMedata.HttpMethod constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }
}

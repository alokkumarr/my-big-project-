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
  "apiEndPoint",
  "httpMethod",
  "queryParameters",
  "headerParameters",
  "urlParameters",
  "bodyParameters",
  "destinationLocation",
  "schedulerExpression"
})
public class RouteMetadata {

  /** (Required) */
  @JsonProperty("apiEndPoint")
  private String apiEndPoint;
  /** (Required) */
  @JsonProperty("httpMethod")
  private RouteMetadata.HttpMethod httpMethod = RouteMetadata.HttpMethod.fromValue("GET");
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

  @JsonProperty("destinationLocation")
  private String destinationLocation;

  @JsonProperty("schedulerExpression")
  private SchedulerExpression schedulerExpression;

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
  public RouteMetadata.HttpMethod getHttpMethod() {
    return httpMethod;
  }

  /** (Required) */
  @JsonProperty("httpMethod")
  public void setHttpMethod(RouteMetadata.HttpMethod httpMethod) {
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

  @JsonProperty("destinationLocation")
  public String getDestinationLocation() {
    return destinationLocation;
  }

  @JsonProperty("destinationLocation")
  public void setDestinationLocation(String destinationLocation) {
    this.destinationLocation = destinationLocation;
  }

  @JsonProperty("schedulerExpression")
  public SchedulerExpression getSchedulerExpression() {
    return schedulerExpression;
  }

  @JsonProperty("schedulerExpression")
  public void setSchedulerExpression(SchedulerExpression schedulerExpression) {
    this.schedulerExpression = schedulerExpression;
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
        .append("destinationLocation", destinationLocation)
        .append("schedulerExpression", schedulerExpression)
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
        .append(schedulerExpression)
        .append(destinationLocation)
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
    RouteMetadata rhs = ((RouteMetadata) other);
    return new EqualsBuilder()
        .append(headerParameters, rhs.headerParameters)
        .append(apiEndPoint, rhs.apiEndPoint)
        .append(bodyParameters, rhs.bodyParameters)
        .append(httpMethod, rhs.httpMethod)
        .append(urlParameters, rhs.urlParameters)
        .append(schedulerExpression, rhs.schedulerExpression)
        .append(destinationLocation, rhs.destinationLocation)
        .append(queryParameters, rhs.queryParameters)
        .isEquals();
  }

  public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");
    private final String value;
    private static final Map<String, RouteMetadata.HttpMethod> CONSTANTS =
        new HashMap<String, RouteMetadata.HttpMethod>();

    static {
      for (RouteMetadata.HttpMethod c : values()) {
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
    public static RouteMetadata.HttpMethod fromValue(String value) {
      RouteMetadata.HttpMethod constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }
}

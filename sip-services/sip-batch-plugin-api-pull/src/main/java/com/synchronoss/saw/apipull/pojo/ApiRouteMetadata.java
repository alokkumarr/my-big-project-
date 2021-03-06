package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "routeName",
  "description",
  "apiEndPoint",
  "httpMethod",
  "queryParameters",
  "headerParameters",
  "urlParameters",
  "bodyParameters",
  "destinationLocation",
  "schedulerExpression"
})
public class ApiRouteMetadata {

  /** (Required) */
  @JsonProperty("routeName")
  private String routeName;

  @JsonProperty("description")
  private String description;
  /** (Required) */
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

  @JsonProperty("destinationLocation")
  private String destinationLocation;

  @JsonProperty("schedulerExpression")
  private SchedulerExpression schedulerExpression;

  /** (Required) */
  @JsonProperty("routeName")
  public String getRouteName() {
    return routeName;
  }

  /** (Required) */
  @JsonProperty("routeName")
  public void setRouteName(String routeName) {
    this.routeName = routeName;
  }

  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
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
        .append("routeName", routeName)
        .append("description", description)
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
        .append(description)
        .append(schedulerExpression)
        .append(destinationLocation)
        .append(routeName)
        .append(queryParameters)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof ApiRouteMetadata) == false) {
      return false;
    }
    ApiRouteMetadata rhs = ((ApiRouteMetadata) other);
    return new EqualsBuilder()
        .append(headerParameters, rhs.headerParameters)
        .append(apiEndPoint, rhs.apiEndPoint)
        .append(bodyParameters, rhs.bodyParameters)
        .append(httpMethod, rhs.httpMethod)
        .append(urlParameters, rhs.urlParameters)
        .append(description, rhs.description)
        .append(schedulerExpression, rhs.schedulerExpression)
        .append(destinationLocation, rhs.destinationLocation)
        .append(routeName, rhs.routeName)
        .append(queryParameters, rhs.queryParameters)
        .isEquals();
  }
}

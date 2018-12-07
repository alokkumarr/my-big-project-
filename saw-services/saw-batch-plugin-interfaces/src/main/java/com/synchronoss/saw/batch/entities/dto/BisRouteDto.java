package com.synchronoss.saw.batch.entities.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;


@ApiModel("This model payload holds the details to create a route in the system")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BisRouteDto implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "This property is unique identifier for route resource")
  @JsonProperty("bisRouteSysId")
  private Long bisRouteSysId;
  @ApiModelProperty(value = "This property used by consumer while updating the route resource")
  @JsonProperty("modifiedBy")
  private String modifiedBy;
  @ApiModelProperty(value = "This property used by consumer while creating the route resource")
  @JsonProperty("createdBy")
  private String createdBy;
  @ApiModelProperty(required = true, value = "It holds json string, it needs to escaped")
  @JsonProperty("routeMetadata")
  private String routeMetadata;
  @ApiModelProperty(value = "This property is unique identifier for channel resource")
  @JsonProperty("bisChannelSysId")
  private Long bisChannelSysId;

  @JsonProperty("modifiedDate")
  private Long modifiedDate;

  @JsonProperty("createdDate")
  private Long createdDate;

  @JsonProperty("modifiedDate")
  public Long getModifiedDate() {
    return modifiedDate;
  }

  @JsonProperty("modifiedDate")
  public void setModifiedDate(Long modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  @JsonProperty("createdDate")
  public Long getCreatedDate() {
    return createdDate;
  }

  @JsonProperty("createdDate")
  public void setCreatedDate(Long createdDate) {
    this.createdDate = createdDate;
  }

  @JsonProperty("bisRouteSysId")
  public Long getBisRouteSysId() {
    return bisRouteSysId;
  }

  @JsonProperty("bisRouteSysId")
  public void setBisRouteSysId(Long bisRouteSysId) {
    this.bisRouteSysId = bisRouteSysId;
  }

  @JsonProperty("modifiedBy")
  public String getModifiedBy() {
    return modifiedBy;
  }

  @JsonProperty("modifiedBy")
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }

  @JsonProperty("createdBy")
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @JsonProperty("routeMetadata")
  public String getRouteMetadata() {
    return routeMetadata;
  }

  @JsonProperty("routeMetadata")
  public void setRouteMetadata(String routeMetadata) {
    this.routeMetadata = routeMetadata;
  }

  @JsonProperty("bisChannelSysId")
  public Long getBisChannelSysId() {
    return bisChannelSysId;
  }

  @JsonProperty("bisChannelSysId")
  public void setBisChannelSysId(Long bisChannelSysId) {
    this.bisChannelSysId = bisChannelSysId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (bisRouteSysId != null ? bisRouteSysId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof BisRouteDto)) {
      return false;
    }
    BisRouteDto other = (BisRouteDto) object;
    if ((this.bisRouteSysId == null && other.bisRouteSysId != null)
        || (this.bisRouteSysId != null && !this.bisRouteSysId.equals(other.bisRouteSysId))) {
      return false;
    }
    return true;
  }
}

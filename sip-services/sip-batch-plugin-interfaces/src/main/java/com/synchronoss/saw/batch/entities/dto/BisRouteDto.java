package com.synchronoss.saw.batch.entities.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

@ApiModel("This model payload holds the details to create a route in the system")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BisRouteDto implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "Unique identifier for route resource", dataType = "Long")
  @JsonProperty("bisRouteSysId")
  private Long bisRouteSysId;
  @ApiModelProperty(value = "Consumer while updating the route resource", dataType = "String")
  @JsonProperty("modifiedBy")
  private String modifiedBy;
  @ApiModelProperty(value = "Consumer while creating the route resource", dataType = "String")
  @JsonProperty("createdBy")
  private String createdBy;
  @ApiModelProperty(required = true, value = "It holds json string, it needs to escaped",
      dataType = "String")
  @JsonProperty("routeMetadata")
  private String routeMetadata;
  @ApiModelProperty(value = "Unique identifier for channel resource", dataType = "Long")
  @JsonProperty("bisChannelSysId")
  private Long bisChannelSysId;

  @ApiModelProperty(value = "To indicates Activate or inactive", dataType = "Long")
  @JsonProperty("status")
  private Long status;

  @ApiModelProperty(value = "Resource modification date", dataType = "Date")
  @JsonProperty("modifiedDate")
  private Long modifiedDate;

  @ApiModelProperty(value = "Resource creation date", dataType = "Date")
  @JsonProperty("createdDate")
  private Long createdDate;

  @ApiModelProperty(value = "job lastFire time", dataType = "Long")
  @JsonProperty("lastFireTime")
  private Long lastFireTime;

  @ApiModelProperty(value = "Job nextFire time", dataType = "Long")
  @JsonProperty("nextFireTime")
  private Long nextFireTime;

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
    this.routeMetadata = SipCommonUtils.sanitizeJson(routeMetadata);
  }

  @JsonProperty("bisChannelSysId")
  public Long getBisChannelSysId() {
    return bisChannelSysId;
  }

  @JsonProperty("bisChannelSysId")
  public void setBisChannelSysId(Long bisChannelSysId) {
    this.bisChannelSysId = bisChannelSysId;
  }

  @JsonProperty("status")
  public Long getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(Long status) {
    this.status = status;
  }

  @JsonProperty("lastFireTime")
  public Long getLastFireTime() {
    return lastFireTime;
  }

  @JsonProperty("lastFireTime")
  public void setLastFireTime(Long lastFireTime) {
    this.lastFireTime = lastFireTime;
  }

  @JsonProperty("nextFireTime")
  public Long getNextFireTime() {
    return nextFireTime;
  }

  @JsonProperty("nextFireTime")
  public void setNextFireTime(Long nextFireTime) {
    this.nextFireTime = nextFireTime;
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

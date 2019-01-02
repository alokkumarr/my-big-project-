package com.synchronoss.saw.batch.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;


@ApiModel("This model payload holds the details to create a channel in the system")
public class BisChannelDto implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "This property is unique identifier for channel resource")
  @JsonProperty("bisChannelSysId")
  private Long bisChannelSysId;
  @ApiModelProperty(value = "This property used by consumer while updating the channel resource")
  @JsonProperty("modifiedBy")
  private String modifiedBy;
  @ApiModelProperty(value = "This property used by consumer while creating the channel resource")
  @JsonProperty("createdBy")
  private String createdBy;
  @ApiModelProperty(
      value = "This property used by consumer to set the product code for that application",
      example = "SAWD000001")
  @JsonProperty("productCode")
  private String productCode = "SAWD000001";
  @ApiModelProperty(
      value = "This property used by consumer to set the project code for that application",
      example = "workbench")
  @JsonProperty("projectCode")
  private String projectCode = "workbench";
  @ApiModelProperty(
      value = "This property used by consumer to set the customer code for that application",
      example = "SYNCHRONOSS")
  @JsonProperty("customerCode")
  private String customerCode = "SYNCHRONOSS";
  @ApiModelProperty(value = "This property used by consumer to set the channel type ",
      example = "sftp")
  @JsonProperty("channelType")
  private String channelType = "sftp";
  @ApiModelProperty(value = "This property used by consumer to set the channel related metadata ")
  @JsonProperty("channelMetadata")
  private String channelMetadata;

  @JsonProperty("modifiedDate")
  private Long modifiedDate;

  @JsonProperty("createdDate")
  private Long createdDate;
  

  @JsonProperty("status")
  private Long status;

  

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

  @JsonProperty("bisChannelSysId")
  public Long getBisChannelSysId() {
    return bisChannelSysId;
  }

  @JsonProperty("bisChannelSysId")
  public void setBisChannelSysId(Long bisChannelSysId) {
    this.bisChannelSysId = bisChannelSysId;
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

  @JsonProperty("productCode")
  public String getProductCode() {
    return productCode;
  }

  @JsonProperty("productCode")
  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  @JsonProperty("projectCode")
  public String getProjectCode() {
    return projectCode;
  }

  @JsonProperty("projectCode")
  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
  }

  @JsonProperty("customerCode")
  public String getCustomerCode() {
    return customerCode;
  }

  @JsonProperty("customerCode")
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  @JsonProperty("channelMetadata")
  public String getChannelMetadata() {
    return channelMetadata;
  }

  @JsonProperty("channelMetadata")
  public void setChannelMetadata(String channelMetadata) {
    this.channelMetadata = channelMetadata;
  }

  @JsonProperty("channelType")
  public String getChannelType() {
    return channelType;
  }

  @JsonProperty("channelType")
  public void setChannelType(String channelType) {
    this.channelType = channelType;
  }
  
  @JsonProperty("status")
  public Long getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(Long status) {
    this.status = status;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (bisChannelSysId != null ? bisChannelSysId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof BisChannelDto)) {
      return false;
    }
    BisChannelDto other = (BisChannelDto) object;
    if ((this.bisChannelSysId == null && other.bisChannelSysId != null)
        || (this.bisChannelSysId != null && !this.bisChannelSysId.equals(other.bisChannelSysId))) {
      return false;
    }
    return true;
  }

}

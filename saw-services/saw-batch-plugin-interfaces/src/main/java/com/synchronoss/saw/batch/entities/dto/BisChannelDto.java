package com.synchronoss.saw.batch.entities.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("This model payload holds the details to create a channel in the system")
public class BisChannelDto implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(hidden=true)
  @JsonProperty("bisChannelSysId")
  private Long bisChannelSysId;
  
  @JsonProperty("modifiedBy")
  private String modifiedBy;
  
  @JsonProperty("createdBy")
  private String createdBy;
  
  @JsonProperty("productCode")
  private String productCode;
  
  @JsonProperty("projectCode")
  private String projectCode;

  @JsonProperty("customerCode")
  private String customerCode;
  
  @JsonProperty("channelType")
  private String channelType;
  
  @JsonProperty("channelMetadata")
  private String channelMetadata;
  
  
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

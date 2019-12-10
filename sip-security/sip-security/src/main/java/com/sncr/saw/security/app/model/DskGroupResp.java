package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sncr.saw.security.common.bean.repo.dsk.DskDetails;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class DskGroupResp {
  @JsonProperty("securityGroupSysId")
  @ApiModelProperty(
      notes = "Security group ID",
      name = "securityGroupSysId",
      required = true)
  private Long securityGroupSysId;

  @JsonProperty("securityGroupName")
  @ApiModelProperty(
      notes = "Security group name",
      name = "securityGroupName")
  private String securityGroupName;

  @JsonProperty("attributes")
  @ApiModelProperty(
      notes = "Security group attributes",
      name = "attributes",
      required = true)
  private List<DskDetails> attributes;

  @JsonProperty("valid")
  @ApiModelProperty(
      notes = "Status of the operation",
      name = "valid",
      required = true)
  private Boolean valid;

  @JsonProperty("message")
  @ApiModelProperty(
      notes = "Reason for failure if any",
      name = "message")
  private String message;

  public Long getSecurityGroupSysId() {
    return securityGroupSysId;
  }

  public void setSecurityGroupSysId(Long securityGroupSysId) {
    this.securityGroupSysId = securityGroupSysId;
  }

  public String getSecurityGroupName() {
    return securityGroupName;
  }

  public void setSecurityGroupName(String securityGroupName) {
    this.securityGroupName = securityGroupName;
  }

  public List<DskDetails> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<DskDetails> attributes) {
    this.attributes = attributes;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

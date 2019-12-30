package com.synchronoss.bda.sip.dsk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonInclude(Include.NON_NULL)
public class DskGroupPayload {
  @JsonProperty("securityGroupSysId")
  @ApiModelProperty(notes = "Security group ID", name = "securityGroupSysId")
  private Long securityGroupSysId;

  @JsonProperty("groupName")
  @ApiModelProperty(notes = "Security group Name", name = "groupName", required = true)
  private String groupName;

  @JsonProperty("groupDescription")
  @ApiModelProperty(notes = "Security group description", name = "groupDescription")
  private String groupDescription;

  @JsonProperty("dskAttributes")
  @ApiModelProperty(notes = "Security group attribute model", name = "dskAttributes")
  private SipDskAttribute dskAttributes;

  @JsonProperty("valid")
  @ApiModelProperty(notes = "Status of the operation", name = "valid", required = true)
  private Boolean valid;

  @JsonProperty("message")
  @ApiModelProperty(notes = "Reason for failure if any", name = "message")
  private String message;

  @JsonProperty("securityGroupSysId")
  public Long getSecurityGroupSysId() {
    return securityGroupSysId;
  }

  @JsonProperty("securityGroupSysId")
  public void setSecurityGroupSysId(Long securityGroupSysId) {
    this.securityGroupSysId = securityGroupSysId;
  }

  @JsonProperty("groupName")
  public String getGroupName() {
    return groupName;
  }

  @JsonProperty("groupName")
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  @JsonProperty("description")
  public String getGroupDescription() {
    return groupDescription;
  }

  @JsonProperty("description")
  public void setGroupDescription(String groupDescription) {
    this.groupDescription = groupDescription;
  }

  @JsonProperty("attributes")
  public SipDskAttribute getDskAttributes() {
    return dskAttributes;
  }

  @JsonProperty("attributes")
  public void setDskAttributes(SipDskAttribute dskAttributes) {
    this.dskAttributes = dskAttributes;
  }

  @JsonProperty("valid")
  public Boolean getValid() {
    return valid;
  }

  @JsonProperty("valid")
  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }
}

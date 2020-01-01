package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
@ApiModel
public class DskFieldsInfo {
  @JsonProperty("valid")
  @ApiModelProperty(notes = "Indicates the validity of the operation", name = "valid")
  private Boolean valid;

  @JsonProperty("message")
  @ApiModelProperty(notes = "Error message if any", name = "message")
  private String message;

  @JsonProperty("dskEligibleData")
  @ApiModelProperty(notes = "DSK eligible data for a given customer", name = "dskEligibleData")
  Map<Long, Map<Long, Map<String, List<DskField>>>> dskEligibleData = new HashMap<>();

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

  @JsonProperty("dskEligibleData")
  public Map<Long, Map<Long, Map<String, List<DskField>>>> getDskEligibleData() {
    return dskEligibleData;
  }

  @JsonProperty("dskEligibleData")
  public void setDskEligibleData(
      Map<Long, Map<Long, Map<String, List<DskField>>>> dskEligibleData) {
    this.dskEligibleData = dskEligibleData;
  }
}

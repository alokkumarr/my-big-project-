package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class DskFieldsInfo {
  @JsonProperty("valid")
  private Boolean valid;

  @JsonProperty("message")
  private String message;

  @JsonProperty("dskEligibleData")
  Map<String, Map<String, Map<String, List<DskField>>>> dskEligibleData = new HashMap<>();

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
  public Map<String, Map<String, Map<String, List<DskField>>>> getDskEligibleData() {
    return dskEligibleData;
  }

  @JsonProperty("dskEligibleData")
  public void setDskEligibleData(
      Map<String, Map<String, Map<String, List<DskField>>>> dskEligibleData) {
    this.dskEligibleData = dskEligibleData;
  }
}

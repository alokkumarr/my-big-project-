package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class DskFieldsInfo {
  private Boolean valid;
  private String message;

  Map<String, Map<String, Map<Long, List<DskField>>>> dskEligibleData = new HashMap<>();

  public Boolean getValid() {
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

  public Map<String, Map<String, Map<Long, List<DskField>>>> getDskEligibleData() {
    return dskEligibleData;
  }

  public void setDskEligibleData(
      Map<String, Map<String, Map<Long, List<DskField>>>> dskEligibleData) {
    this.dskEligibleData = dskEligibleData;
  }
}

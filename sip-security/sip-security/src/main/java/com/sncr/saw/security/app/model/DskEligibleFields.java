package com.sncr.saw.security.app.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DskEligibleFields {
  private String customerCode;
  private String projectCode;
//  private Long semanticId;

  private Map<Long, List<DskField>> semanticFieldMap = new HashMap<>();

  private List<DskField> fields;

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public String getProjectCode() {
    return projectCode;
  }

  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
  }

//  public Long getSemanticId() {
//    return semanticId;
//  }
//
//  public void setSemanticId(Long semanticId) {
//    this.semanticId = semanticId;
//  }

  public List<DskField> getFields() {
    return fields;
  }

  public void setFields(List<DskField> fields) {
    this.fields = fields;
  }

  public Map<Long, List<DskField>> getSemanticFieldMap() {
    return semanticFieldMap;
  }

  public void setSemanticFieldMap(Map<Long, List<DskField>> semanticFieldMap) {
    this.semanticFieldMap = semanticFieldMap;
  }
}

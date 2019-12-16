package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DskEligibleFields {
  private Long customerSysId;
  private Long productSysId;
  private String semantic_id;
  private String createdBy;

  private Map<Long, List<DskField>> semanticFieldMap = new HashMap<>();

  private List<DskField> fields;

  public Map<Long, List<DskField>> getSemanticFieldMap() {
    return semanticFieldMap;
  }

  public Long getCustomerSysId() {
    return customerSysId;
  }

  public void setCustomerSysId(Long customerSysId) {
    this.customerSysId = customerSysId;
  }

  public Long getProductSysId() {
    return productSysId;
  }

  public void setProductSysId(Long productSysId) {
    this.productSysId = productSysId;
  }

  public String getSemantic_id() {
    return semantic_id;
  }

  public void setSemantic_id(String semantic_id) {
    this.semantic_id = semantic_id;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public List<DskField> getFields() {
    return fields;
  }

  public void setFields(List<DskField> fields) {
    this.fields = fields;
  }
}

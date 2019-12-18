package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DskEligibleFields {
  @JsonProperty("customerSysId")
  private Long customerSysId;

  @JsonProperty("productSysId")
  private Long productSysId;

  @JsonProperty("semanticId")
  private String semanticId;

  @JsonProperty("createdBy")
  private String createdBy;

  @JsonProperty("fields")
  private List<DskField> fields;

  @JsonProperty("customerSysId")
  public Long getCustomerSysId() {
    return customerSysId;
  }

  @JsonProperty("customerSysId")
  public void setCustomerSysId(Long customerSysId) {
    this.customerSysId = customerSysId;
  }

  @JsonProperty("productSysId")
  public Long getProductSysId() {
    return productSysId;
  }

  @JsonProperty("productSysId")
  public void setProductSysId(Long productSysId) {
    this.productSysId = productSysId;
  }

  @JsonProperty("semanticId")
  public String getSemanticId() {
    return semanticId;
  }

  @JsonProperty("semanticId")
  public void setSemanticId(String semanticId) {
    this.semanticId = semanticId;
  }

  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }

  @JsonProperty("createdBy")
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @JsonProperty("fields")
  public List<DskField> getFields() {
    return fields;
  }

  @JsonProperty("fields")
  public void setFields(List<DskField> fields) {
    this.fields = fields;
  }
}

package com.synchronoss.saw.analysis.modal;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.synchronoss.saw.model.SipQuery;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Analysis {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
      "type",
      "semanticId",
      "name",
      "description",
      "id",
      "category",
      "customerCode",
      "projectCode",
      "module",
      "createdTime",
      "createdBy",
      "modifiedTime",
      "modifiedBy",
      "sipQuery"
  })
  @JsonProperty("type")
  private String type;

  @JsonProperty("semanticId")
  private String semanticId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("id")
  private String id;

  @JsonProperty("category")
  private String category;

  @JsonProperty("customerCode")
  private String customerCode;

  @JsonProperty("projectCode")
  private String projectCode;

  @JsonProperty("module")
  private String module;

  @JsonProperty("createdTime")
  private Long createdTime;

  @JsonProperty("createdBy")
  private String createdBy;

  @JsonProperty("modifiedTime")
  private Long modifiedTime;

  @JsonProperty("modifiedBy")
  private String modifiedBy;

  @JsonProperty("sipQuery")
  private SipQuery sipQuery;

  @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("semanticId")
  public String getSemanticId() {
    return semanticId;
  }

  @JsonProperty("semanticId")
  public void setSemanticId(String semanticId) {
    this.semanticId = semanticId;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @JsonProperty("customerCode")
  public String getCustomerCode() {
    return customerCode;
  }

  @JsonProperty("customerCode")
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  @JsonProperty("projectCode")
  public String getProjectCode() {
    return projectCode;
  }

  @JsonProperty("projectCode")
  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
  }

  @JsonProperty("module")
  public String getModule() {
    return module;
  }

  @JsonProperty("module")
  public void setModule(String module) {
    this.module = module;
  }

  @JsonProperty("createdTime")
  public Long getCreatedTime() {
    return createdTime;
  }

  @JsonProperty("createdTime")
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }

  @JsonProperty("createdBy")
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @JsonProperty("modifiedTime")
  public Long getModifiedTime() {
    return modifiedTime;
  }

  @JsonProperty("modifiedTime")
  public void setModifiedTime(Long modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  @JsonProperty("modifiedBy")
  public String getModifiedBy() {
    return modifiedBy;
  }

  @JsonProperty("modifiedBy")
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  @JsonProperty("sipQuery")
  public SipQuery getSipQuery() {
    return sipQuery;
  }

  @JsonProperty("sipQuery")
  public void setSipQuery(SipQuery sipQuery) {
    this.sipQuery = sipQuery;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("type", type)
        .append("semanticId", semanticId)
        .append("name", name)
        .append("id", id)
        .append("customerCode", customerCode)
        .append("projectCode", projectCode)
        .append("module", module)
        .append("createdTime", createdTime)
        .append("createdBy", createdBy)
        .append("modifiedTime", modifiedTime)
        .append("modifiedBy", modifiedBy)
        .append("sipQuery", sipQuery)
        .append("additionalProperties", additionalProperties)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(module)
        .append(projectCode)
        .append(createdTime)
        .append(modifiedBy)
        .append(modifiedTime)
        .append(type)
        .append(sipQuery)
        .append(id)
        .append(createdBy)
        .append(additionalProperties)
        .append(semanticId)
        .append(customerCode)
        .append(name)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Analysis) == false) {
      return false;
    }
    Analysis rhs = ((Analysis) other);
    return new EqualsBuilder()
        .append(module, rhs.module)
        .append(projectCode, rhs.projectCode)
        .append(createdTime, rhs.createdTime)
        .append(modifiedBy, rhs.modifiedBy)
        .append(modifiedTime, rhs.modifiedTime)
        .append(type, rhs.type)
        .append(sipQuery, rhs.sipQuery)
        .append(id, rhs.id)
        .append(createdBy, rhs.createdBy)
        .append(additionalProperties, rhs.additionalProperties)
        .append(semanticId, rhs.semanticId)
        .append(customerCode, rhs.customerCode)
        .append(name, rhs.name)
        .isEquals();
  }
}

package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"dataSecurityKey"})
public class DataSecurityKey {

  /** (Required) */
  @JsonProperty("dataSecurityKey")
  private List<DataSecurityKeyDef> dataSecuritykey;

  @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("dataSecurityKey")
  public List<DataSecurityKeyDef> getDataSecuritykey() {
    return dataSecuritykey;
  }

  @JsonProperty("dataSecurityKey")
  public void setDataSecuritykey(List<DataSecurityKeyDef> dataSecuritykey) {
    this.dataSecuritykey = dataSecuritykey;
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
    return new ToStringBuilder(this).append("dataSecuritykey", dataSecuritykey).toString();
  }
}

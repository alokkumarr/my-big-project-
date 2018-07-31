

package com.synchronoss.saw.storage.proxy.model.response;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "mappings"})
public class MappingIndexResponse {

  /**
   * The Health Schema
   * <p>
   * 
   * 
   */
  @JsonProperty("name")
  private String name = "";
  /**
   * The Status Schema
   * <p>
   * 
   * 
   */
  @JsonProperty("mappings")
  private Object mappings;



  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("mappings")
  public Object getMappings() {
    return mappings;
  }

  @JsonProperty("mappings")
  public void setMappings(Object mappings) {
    this.mappings = mappings;
  }

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();



  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}

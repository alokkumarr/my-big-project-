package com.synchronoss.saw.storage.proxy.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type",})

public class StoreField {

  private static final Logger logger = LoggerFactory.getLogger(StoreField.class);

  private String name;
  @JsonProperty("type")
  private String type;

  @JsonProperty("fields")
  private Object fields;
  
  @JsonProperty("format")
  private String format;
  
  
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }
  @JsonProperty("fields")
  public Object getFields() {
    return fields;
  }
  @JsonProperty("fields")
  public void setFields(Object fields) {
    this.fields = fields;
  }
  
  
  @JsonProperty("format")
  public String getFormat() {
    return format;
  }
  @JsonProperty("format")
  public void setFormat(String format) {
    this.format = format;
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
    String result = null;
    try {
      result= new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(this);
  } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      logger.error(e.getMessage());
  }
  return result;
  }
  
  
  
}

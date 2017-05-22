package com.synchronoss.querybuilder.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"storage_type", "index_name", "object_type", "verb", "query", "module_name",
    "username", "dsk"})
public class ESProxy {

  @JsonProperty("storage_type")
  private String storageType;
  @JsonProperty("index_name")
  private String indexName;
  @JsonProperty("object_type")
  private String objectType;
  @JsonProperty("verb")
  private String verb;
  @JsonProperty("query")
  private String query;
  @JsonProperty("module_name")
  private String moduleName;
  @JsonProperty("username")
  private String username;
  @JsonProperty("dsk")
  private String dsk;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("storage_type")
  public String getStorageType() {
    return storageType;
  }

  @JsonProperty("storage_type")
  public void setStorageType(String storageType) {
    this.storageType = storageType;
  }

  @JsonProperty("index_name")
  public String getIndexName() {
    return indexName;
  }

  @JsonProperty("index_name")
  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  @JsonProperty("object_type")
  public String getObjectType() {
    return objectType;
  }

  @JsonProperty("object_type")
  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  @JsonProperty("verb")
  public String getVerb() {
    return verb;
  }

  @JsonProperty("verb")
  public void setVerb(String verb) {
    this.verb = verb;
  }

  @JsonProperty("query")
  public String getQuery() {
    return query;
  }

  @JsonProperty("query")
  public void setQuery(String query) {
    this.query = query;
  }

  @JsonProperty("module_name")
  public String getModuleName() {
    return moduleName;
  }

  @JsonProperty("module_name")
  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  @JsonProperty("username")
  public void setUsername(String username) {
    this.username = username;
  }

  @JsonProperty("dsk")
  public String getDsk() {
    return dsk;
  }

  @JsonProperty("dsk")
  public void setDsk(String dsk) {
    this.dsk = dsk;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}

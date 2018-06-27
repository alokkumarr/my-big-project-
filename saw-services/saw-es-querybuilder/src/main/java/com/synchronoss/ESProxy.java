package com.synchronoss;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"storage", "indexName", "objectType", "action", "query", "moduleName",
    "username", "dataSecurityKey", "resultFormat", "requestedTime", "productCode","requestBy"})
public class ESProxy {

  @JsonProperty("storage")
  private String storageType;
  @JsonProperty("indexName")
  private String indexName;
  @JsonProperty("objectType")
  private String objectType;
  @JsonProperty("action")
  private String action = "search";
  @JsonProperty("query")
  private String query;
  @JsonProperty("moduleName")
  private String moduleName ="ANALYZE";
  @JsonProperty("dataSecurityKey")
  private String dsk;
  @JsonProperty("resultFormat")
  private String resultFormat = "json";
  @JsonProperty("productCode")
  private String productCode;
  @JsonProperty("requestBy")
  private String requestBy;
  @JsonProperty("requestedTime")
  private String requestedTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
  
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("storage")
  public String getStorageType() {
    return storageType;
  }

  @JsonProperty("storage")
  public void setStorageType(String storageType) {
    this.storageType = storageType;
  }

  @JsonProperty("indexName")
  public String getIndexName() {
    return indexName;
  }

  @JsonProperty("indexName")
  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  @JsonProperty("objectType")
  public String getObjectType() {
    return objectType;
  }

  @JsonProperty("objectType")
  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  @JsonProperty("action")
  public String getAction() {
    return action;
  }
  @JsonProperty("action")
  public void setAction(String action) {
    this.action = action;
  }
  @JsonProperty("resultFormat")
  public String getResultFormat() {
    return resultFormat;
  }
  @JsonProperty("resultFormat")
  public void setResultFormat(String resultFormat) {
    this.resultFormat = resultFormat;
  }
  @JsonProperty("productCode")
  public String getProductCode() {
    return productCode;
  }
  @JsonProperty("productCode")
  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }
  @JsonProperty("resultFormat")
  public String getRequestedTime() {
    return requestedTime;
  }
  @JsonProperty("resultFormat")
  public void setRequestedTime(String requestedTime) {
    this.requestedTime = requestedTime;
  }
  @JsonProperty("requestBy")
  public String getRequestBy() {
    return requestBy;
  }
  @JsonProperty("requestBy")
  public void setRequestBy(String requestBy) {
    this.requestBy = requestBy;
  }

  @JsonProperty("query")
  public String getQuery() {
    return query;
  }

  @JsonProperty("query")
  public void setQuery(String query) {
    this.query = query;
  }

  @JsonProperty("moduleName")
  public String getModuleName() {
    return moduleName;
  }

  @JsonProperty("moduleName")
  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  @JsonProperty("dataSecurityKey")
  public String getDsk() {
    return dsk;
  }

  @JsonProperty("dataSecurityKey")
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

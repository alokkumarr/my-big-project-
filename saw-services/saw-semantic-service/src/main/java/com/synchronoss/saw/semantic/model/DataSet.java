
package com.synchronoss.saw.semantic.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "_id",
  "system",
  "userData",
  "asOfNow",
  "schema",
  "recordCount"
})
public class DataSet {

    @JsonProperty("_id")
    private String dataSetId;
  
    @JsonProperty("system")
    private Object system;
  
    @JsonProperty("asOutput")
    private String asOutput;

    @JsonProperty("joinEligible")
    private boolean joinEligible;

    @JsonProperty("storageType")
    private String storageType;

    @JsonProperty("size")
    private Object size;
    
    @JsonProperty("userData")
    private Object userData;
    
    @JsonProperty("transformations")
    private Object transformations = null;

    @JsonProperty("component")
    private String component = null;

    @JsonProperty("asInput")
    private List<Object> asInput = null;
   
    
    @JsonProperty("asOfNow")
    private Object asOfNow = null;

    @JsonProperty("schema")
    private Object schema = null;

    @JsonProperty("recordCount")
    private long recordCount;
    
//    @JsonProperty("createdTime")
//    private long createdTime;
//
//    @JsonProperty("modifiedTime")
//    private long modifiedTime;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("joinEligible")
    public boolean isJoinEligible() {
      return joinEligible;
    }
    @JsonProperty("joinEligible")    
    public void setJoinEligible(boolean joinEligible) {
      this.joinEligible = joinEligible;
    }
    @JsonProperty("storageType")    
    public String getStorageType() {
      return storageType;
    }
    @JsonProperty("storageType")    
    public void setStorageType(String storageType) {
      this.storageType = storageType;
    }
    @JsonProperty("size")    
    public Object isSize() {
      return size;
    }
    @JsonProperty("size")    
    public void setSize(Object size) {
      this.size = size;
    }
    @JsonProperty("_id")
    public String getDataSetId() {
      return dataSetId;
    }
    @JsonProperty("_id")
    public void setDataSetId(String dataSetId) {
      this.dataSetId = dataSetId;
    }
    @JsonProperty("system")
    public Object getSystem() {
      return system;
    }
    @JsonProperty("system")
    public void setSystem(Object system) {
      this.system = system;
    }
    @JsonProperty("asOutput")
    public String getAsOutput() {
      return asOutput;
    }
    @JsonProperty("asOutput")
    public void setAsOutput(String asOutput) {
      this.asOutput = asOutput;
    }
    @JsonProperty("userData")
    public Object getUserData() {
      return userData;
    }
    @JsonProperty("userData")
    public void setUserData(Object userData) {
      this.userData = userData;
    }
    @JsonProperty("transformations")
    public Object getTransformations() {
      return transformations;
    }
    @JsonProperty("transformations")
    public void setTransformations(Object transformations) {
      this.transformations = transformations;
    }
    @JsonProperty("component")
    public String getComponent() {
      return component;
    }
    @JsonProperty("component")
    public void setComponent(String component) {
      this.component = component;
    }
    @JsonProperty("asOfNow")
    public Object getAsOfNow() {
      return asOfNow;
    }
    @JsonProperty("asOfNow")
    public void setAsOfNow(Object asOfNow) {
      this.asOfNow = asOfNow;
    }
    @JsonProperty("asInput")
    public List<Object> getAsInput() {
      return asInput;
    }
    @JsonProperty("asInput")
    public void setAsInput(List<Object> asInput) {
      this.asInput = asInput;
    }

    @JsonProperty("schema")
    public Object getSchema() {
        return this.schema;
    }

    @JsonProperty("schema")
    public void setSchema(Object schema) {
        this.schema = schema;
    }

    @JsonProperty("recordCount")
    public long getRecordCount() {
        return this.recordCount;
    }

    @JsonProperty("recordCount")
    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

//    @JsonProperty("createdTime")
//    public long getCreatedTime() {
//        return this.createdTime;
//    }
//
//    @JsonProperty("createdTime")
//    public void setCreatedTime(long createdTime) {
//        this.createdTime = createdTime;
//    }
//
//    @JsonProperty("modifiedTime")
//    public long getModifiedTime() {
//        return this.modifiedTime;
//    }
//
//    @JsonProperty("modifiedTime")
//    public void setModifiedTime(long modifiedTime) {
//        this.modifiedTime = modifiedTime;
//    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
     
}

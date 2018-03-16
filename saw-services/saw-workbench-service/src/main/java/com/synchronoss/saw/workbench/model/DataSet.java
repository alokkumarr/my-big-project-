
package com.synchronoss.saw.workbench.model;

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
  "asOfNow"
})
public class DataSet {

    @JsonProperty("_id")
    private String dataSetId;
  
    @JsonProperty("system")
    private Object system;
  
    @JsonProperty("asOutput")
    private String asOutput;

    @JsonProperty("userData")
    private Object userData;
    
    @JsonProperty("transformations")
    private Object transformations = null;

    @JsonProperty("asInput")
    private List<Object> asInput = null;
   
    
    @JsonProperty("asOfNow")
    private Object asOfNow = null;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


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
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
     
}

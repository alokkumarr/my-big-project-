
package com.synchronoss.saw.workbench.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "projectId",
  "path"
})
public class Project {

    @JsonProperty("projectId")
    private String projectId;
  
    @JsonProperty("path")
    private String path;
  
    @JsonProperty("statusMessage")
    private String statusMessage;

    @JsonProperty("inspect")
    private Inspect inspect;
    
    @JsonProperty("inspect")
    public Inspect getInspect() {
    return inspect;
    }

    @JsonProperty("inspect")
    public void setInspect(Inspect inspect) {
    this.inspect = inspect;
    }
    
    @JsonProperty("entityId")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String entityId;

    @JsonProperty("dataSecurityKey")
    private List<Object> dataSecurityKey = null;
    /**
     * The Resultformat Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("resultFormat")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Project.ResultFormat resultFormat = null;
    @JsonProperty("data")
    private List<Object> data = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("statusMessage")
    public String getStatusMessage() {
      return statusMessage;
    }
    @JsonProperty("statusMessage")
    public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
    }

    @JsonProperty("dataSecurityKey")
    public List<Object> getDataSecurityKey() {
        return dataSecurityKey;
    }

    @JsonProperty("dataSecurityKey")
    public void setDataSecurityKey(List<Object> dataSecurityKey) {
        this.dataSecurityKey = dataSecurityKey;
    }

    /**
     * The Resultformat Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("resultFormat")
    public Project.ResultFormat getResultFormat() {
        return resultFormat;
    }

    /**
     * The Resultformat Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("resultFormat")
    public void setResultFormat(Project.ResultFormat resultFormat) {
        this.resultFormat = resultFormat;
    }

    @JsonProperty("data")
    public List<Object> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Object> data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("entityId")
    public String getEntityId() {
    return entityId;
    }

    /**
    * It holds the query
    * <p>
    * An explanation about the purpose of this instance.
    * 
    */
    @JsonProperty("entityId")
    public void setEntityId(String entityId) {
    this.entityId = entityId;
    }
 
    @JsonProperty("projectId")
     public String getProjectId() {
      return projectId;
    }
    @JsonProperty("projectId")    
    public void setProjectId(String projectId) {
      this.projectId = projectId;
    }
    @JsonProperty("path")    
    public String getPath() {
      return path;
    }
    @JsonProperty("path")    
    public void setPath(String path) {
      this.path = path;
    }
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
      this.additionalProperties = additionalProperties;
    }

   


    public enum ResultFormat {

        TABULAR("tabular"),
        JSON("json");
        private final String value;
        private final static Map<String, Project.ResultFormat> CONSTANTS = new HashMap<String, Project.ResultFormat>();

        static {
            for (Project.ResultFormat c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private ResultFormat(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Project.ResultFormat fromValue(String value) {
            Project.ResultFormat constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }  
}

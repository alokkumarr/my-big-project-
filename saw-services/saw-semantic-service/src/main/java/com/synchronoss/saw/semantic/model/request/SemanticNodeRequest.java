
package com.synchronoss.saw.semantic.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "customerCode",
    "projectCode",
    "dataSetId",
    "username",
    "dataSecurityKey",
    "module",
    "metricName",
    "supports",
    "artifacts",
    "esRepository",
    "repository"
})
public class SemanticNodeRequest {

    /**
     * The Customercode Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("customerCode")
    private String customerCode;
    /**
     * The Projectcode Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("projectCode")
    private String projectCode;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("dataSetId")
    private List<Object> dataSetId = null;
    /**
     * The Username Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("username")
    private String username;
    /**
     * The Datasecuritykey Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("dataSecurityKey")
    private String dataSecurityKey;
    /**
     * The Module Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("module")
    private SemanticNodeRequest.Module module = SemanticNodeRequest.Module.fromValue("ANALYZE");
    /**
     * The Metricname Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("metricName")
    private String metricName;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("supports")
    private List<Object> supports = null;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("artifacts")
    private List<Object> artifacts = null;
    /**
     * The Esrepository Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("esRepository")
    private EsRepository esRepository;
    /**
     * The Repository Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("repository")
    private Repository repository;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Customercode Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("customerCode")
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * The Customercode Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("customerCode")
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * The Projectcode Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("projectCode")
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * The Projectcode Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("projectCode")
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("dataSetId")
    public List<Object> getDataSetId() {
        return dataSetId;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("dataSetId")
    public void setDataSetId(List<Object> dataSetId) {
        this.dataSetId = dataSetId;
    }

    /**
     * The Username Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     * The Username Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The Datasecuritykey Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("dataSecurityKey")
    public String getDataSecurityKey() {
        return dataSecurityKey;
    }

    /**
     * The Datasecuritykey Schema 
     * <p>
     * 
     * 
     */
    @JsonProperty("dataSecurityKey")
    public void setDataSecurityKey(String dataSecurityKey) {
        this.dataSecurityKey = dataSecurityKey;
    }

    /**
     * The Module Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("module")
    public SemanticNodeRequest.Module getModule() {
        return module;
    }

    /**
     * The Module Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("module")
    public void setModule(SemanticNodeRequest.Module module) {
        this.module = module;
    }

    /**
     * The Metricname Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("metricName")
    public String getMetricName() {
        return metricName;
    }

    /**
     * The Metricname Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("metricName")
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("supports")
    public List<Object> getSupports() {
        return supports;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("supports")
    public void setSupports(List<Object> supports) {
        this.supports = supports;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("artifacts")
    public List<Object> getArtifacts() {
        return artifacts;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("artifacts")
    public void setArtifacts(List<Object> artifacts) {
        this.artifacts = artifacts;
    }

    /**
     * The Esrepository Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("esRepository")
    public EsRepository getEsRepository() {
        return esRepository;
    }

    /**
     * The Esrepository Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("esRepository")
    public void setEsRepository(EsRepository esRepository) {
        this.esRepository = esRepository;
    }

    /**
     * The Repository Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("repository")
    public Repository getRepository() {
        return repository;
    }

    /**
     * The Repository Schema 
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("repository")
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Module {

        ANALYZE("ANALYZE"),
        OBSERVE("OBSERVE"),
        ALERT("ALERT"),
        WORKBENCH("WORKBENCH");
        private final String value;
        private final static Map<String, SemanticNodeRequest.Module> CONSTANTS = new HashMap<String, SemanticNodeRequest.Module>();

        static {
            for (SemanticNodeRequest.Module c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Module(String value) {
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
        public static SemanticNodeRequest.Module fromValue(String value) {
            SemanticNodeRequest.Module constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}

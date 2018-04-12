
package com.synchronoss.saw.storage.proxy.model.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "total",
    "successful",
    "failed",
    "skipped"
    
})
public class Shards {

    /**
     * The Total Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("total")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer total = 0;
    /**
     * The Successful Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("successful")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer successful = 0;
    /**
     * The Failed Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("failed")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer failed = 0;
    
    @JsonProperty("skipped")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer skipped = 0;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Total Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    /**
     * The Total Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * The Successful Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("successful")
    public Integer getSuccessful() {
        return successful;
    }

    /**
     * The Successful Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("successful")
    public void setSuccessful(Integer successful) {
        this.successful = successful;
    }

    /**
     * The Failed Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("failed")
    public Integer getFailed() {
        return failed;
    }

    /**
     * The Failed Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("failed")
    public void setFailed(Integer failed) {
        this.failed = failed;
    }
    
    
    @JsonProperty("skipped")
    public Integer getSkipped() {
      return skipped;
    }
    @JsonProperty("skipped")
    public void setSkipped(Integer skipped) {
      this.skipped = skipped;
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
    public String toString(){
        return org.apache.commons.lang3.builder.ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }


    
}

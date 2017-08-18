
package com.synchronoss.saw.composite.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "contents",
    "links"
})
public class Payload implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 
     * (Required)
     * 
     */
    @JsonProperty("contents")
    private Contents contents;
    /**
     * Empty object schema.
     * <p>
     * Allows ANY object but describes nothing.
     * 
     */
    @JsonProperty("links")
    @JsonPropertyDescription("Allows ANY object but describes nothing.")
    private Links links;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("contents")
    public Contents getContents() {
        return contents;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("contents")
    public void setContents(Contents contents) {
        this.contents = contents;
    }

    public Payload withContents(Contents contents) {
        this.contents = contents;
        return this;
    }

    /**
     * Empty object schema.
     * <p>
     * Allows ANY object but describes nothing.
     * 
     */
    @JsonProperty("links")
    public Links getLinks() {
        return links;
    }

    /**
     * Empty object schema.
     * <p>
     * Allows ANY object but describes nothing.
     * 
     */
    @JsonProperty("links")
    public void setLinks(Links links) {
        this.links = links;
    }

    public Payload withLinks(Links links) {
        this.links = links;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Payload withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

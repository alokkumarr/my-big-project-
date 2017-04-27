
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
@JsonPropertyOrder({
    "columnName",
    "type",
    "format",
    "aggregate",
    "name"
})
public class DataField {

    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("type")
    private String type;
    @JsonProperty("format")
    private Format format;
    @JsonProperty("aggregate")
    private String aggregate;
    @JsonProperty("name")
    private String name;
 
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

   
    
    @JsonProperty("name")
    public String getName() {
		return name;
	}
    @JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("aggregate")
    public String getAggregate() {
		return aggregate;
	}

    @JsonProperty("aggregate")
	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	@JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("format")
    public Format getFormat() {
        return format;
    }

    @JsonProperty("format")
    public void setFormat(Format format) {
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

}

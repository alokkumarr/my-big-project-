
package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dataSecurityKey"
})
public class DataSecurityKey {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("dataSecurityKey")
    private List<DataSecurityKeyDef> dataSecuritykey;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    
    @JsonProperty("dataSecurityKey")
    public List<DataSecurityKeyDef> getDataSecuritykey() {
		return dataSecuritykey;
	}
    @JsonProperty("dataSecurityKey")
	public void setDataSecuritykey(List<DataSecurityKeyDef> dataSecuritykey) {
		this.dataSecuritykey = dataSecuritykey;
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
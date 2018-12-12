package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"name",
"values"
})
public class DataSecurityKeyDef {

/**
* The name schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("name")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String name;
@JsonProperty("values")
private List<String> values = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* The name schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("name")
public String getName() {
return name;
}

/**
* The name schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("values")
public List<String> getValues() {
return values;
}

@JsonProperty("values")
public void setValues(List<String> values) {
this.values = values;
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


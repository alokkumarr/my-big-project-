package com.synchronoss.saw.workbench.model.response;

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
"count",
"_shards"
})
public class CountESResponse {

/**
* The Count Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("count")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Integer count = 0;
@JsonProperty("_shards")
private Shards shards;

@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* The Count Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("count")
public Integer getCount() {
return count;
}

/**
* The Count Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("count")
public void setCount(Integer count) {
this.count = count;
}

@JsonProperty("_shards")
public Shards getShards() {
return shards;
}

@JsonProperty("_shards")
public void setShards(Shards shards) {
this.shards = shards;
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
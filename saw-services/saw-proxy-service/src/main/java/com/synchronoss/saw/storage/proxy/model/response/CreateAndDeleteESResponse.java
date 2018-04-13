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
"_shards",
"_index",
"_type",
"_id",
"_version",
"_seq_no",
"_primary_term",
"result"
})
public class CreateAndDeleteESResponse {

@JsonProperty("_shards")
private Shards shards;
/**
* The _index Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_index")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String index = "";
/**
* The _type Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_type")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String type;
/**
* The _id Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_id")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String id = "";
/**
* The _version Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_version")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Integer version = 0;
/**
* The _seq_no Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_seq_no")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Integer seqNo = 0;
/**
* The _primary_term Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_primary_term")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Integer primaryTerm = 0;
/**
* The Result Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("result")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String result;

@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();


@JsonProperty("_shards")
public Shards getShards() {
return shards;
}

@JsonProperty("_shards")
public void setShards(Shards shards) {
this.shards = shards;
}

/**
* The _index Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_index")
public String getIndex() {
return index;
}

/**
* The _index Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_index")
public void setIndex(String index) {
this.index = index;
}

/**
* The _type Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_type")
public String getType() {
return type;
}

/**
* The _type Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_type")
public void setType(String type) {
this.type = type;
}

/**
* The _id Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_id")
public String getId() {
return id;
}

/**
* The _id Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_id")
public void setId(String id) {
this.id = id;
}

/**
* The _version Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_version")
public Integer getVersion() {
return version;
}

/**
* The _version Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_version")
public void setVersion(Integer version) {
this.version = version;
}

/**
* The _seq_no Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_seq_no")
public Integer getSeqNo() {
return seqNo;
}

/**
* The _seq_no Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_seq_no")
public void setSeqNo(Integer seqNo) {
this.seqNo = seqNo;
}

/**
* The _primary_term Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_primary_term")
public Integer getPrimaryTerm() {
return primaryTerm;
}

/**
* The _primary_term Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("_primary_term")
public void setPrimaryTerm(Integer primaryTerm) {
this.primaryTerm = primaryTerm;
}

/**
* The Result Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("result")
public String getResult() {
return result;
}

/**
* The Result Schema
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("result")
public void setResult(String result) {
this.result = result;
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
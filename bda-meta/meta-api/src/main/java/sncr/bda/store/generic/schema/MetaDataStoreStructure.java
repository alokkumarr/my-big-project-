package sncr.bda.store.generic.schema;

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
"category",
"action",
"xdf-root",
"output",
"id",
"query",
"source"
})
public class MetaDataStoreStructure {

/**
* The Category Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("category")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Category category;
/**
* The Action Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("action")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Action action;
/**
* The Output Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("output")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String output = "";

/**
* Input base path
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("xdf-root")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String xdfRoot;

/**
* The Id Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("id")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private String id = "";
@JsonProperty("query")
private Query query = null;
@JsonProperty("source")
private Object source;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* The Category Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("category")
public Category getCategory() {
return category;
}

/**
* The Category Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("category")
public void setCategory(Category category) {
this.category = category;
}

/**
* The Action Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("action")
public Action getAction() {
return action;
}

/**
* The Action Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("action")
public void setAction(Action action) {
this.action = action;
}

/**
* The Output Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("output")
public String getOutput() {
return output;
}

/**
* The Output Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("output")
public void setOutput(String output) {
this.output = output;
}

/**
* The Id Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("id")
public String getId() {
return id;
}

/**
* The Id Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("query")
public Query getQuery() {
return query;
}

@JsonProperty("query")
public void setQuery(Query query) {
this.query = query;
}

@JsonProperty("source")
public Object getSource() {
return source;
}

@JsonProperty("source")
public void setSource(Object source) {
this.source = source;
}

/**
* Input base path
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("xdf-root")
public String getXdfRoot() {
return xdfRoot;
}

/**
* Input base path
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("xdf-root")
public void setXdfRoot(String xdfRoot) {
this.xdfRoot = xdfRoot;
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
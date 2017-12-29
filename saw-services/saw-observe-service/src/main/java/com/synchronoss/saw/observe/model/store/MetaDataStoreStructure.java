package com.synchronoss.saw.observe.model.store;

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
private MetaDataStoreStructure.Category category;
/**
* The Action Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("action")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private MetaDataStoreStructure.Action action;
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
public MetaDataStoreStructure.Category getCategory() {
return category;
}

/**
* The Category Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("category")
public void setCategory(MetaDataStoreStructure.Category category) {
this.category = category;
}

/**
* The Action Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("action")
public MetaDataStoreStructure.Action getAction() {
return action;
}

/**
* The Action Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("action")
public void setAction(MetaDataStoreStructure.Action action) {
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

public enum Action {

CREATE("create"),
DELETE("delete"),
READ("read"),
UPDATE("update"),
SEARCH("search");
private final String value;
private final static Map<String, MetaDataStoreStructure.Action> CONSTANTS = new HashMap<String, MetaDataStoreStructure.Action>();

static {
for (MetaDataStoreStructure.Action c: values()) {
CONSTANTS.put(c.value, c);
}
}

private Action(String value) {
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
public static MetaDataStoreStructure.Action fromValue(String value) {
MetaDataStoreStructure.Action constant = CONSTANTS.get(value);
if (constant == null) {
throw new IllegalArgumentException(value);
} else {
return constant;
}
}

}

public enum Category {

DATA_SET("DataSet"),
TRANSFORMATION("Transformation"),
DATA_POD("DataPod"),
DATA_SEGMENT("DataSegment"),
USER_INTERFACE("PortalDataSet");
private final String value;
private final static Map<String, MetaDataStoreStructure.Category> CONSTANTS = new HashMap<String, MetaDataStoreStructure.Category>();

static {
for (MetaDataStoreStructure.Category c: values()) {
CONSTANTS.put(c.value, c);
}
}

private Category(String value) {
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
public static MetaDataStoreStructure.Category fromValue(String value) {
MetaDataStoreStructure.Category constant = CONSTANTS.get(value);
if (constant == null) {
throw new IllegalArgumentException(value);
} else {
return constant;
}
}

}

}
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
"conjunction",
"filter"
})
public class Query {

/**
* The Conjunction Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("conjunction")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Query.Conjunction conjunction;
@JsonProperty("filter")
private List<Filter> filter = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* The Conjunction Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("conjunction")
public Query.Conjunction getConjunction() {
return conjunction;
}

/**
* The Conjunction Schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("conjunction")
public void setConjunction(Query.Conjunction conjunction) {
this.conjunction = conjunction;
}

@JsonProperty("filter")
public List<Filter> getFilter() {
return filter;
}

@JsonProperty("filter")
public void setFilter(List<Filter> filter) {
this.filter = filter;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

public enum Conjunction {

AND("and"),
OR("or");
private final String value;
private final static Map<String, Query.Conjunction> CONSTANTS = new HashMap<String, Query.Conjunction>();

static {
for (Query.Conjunction c: values()) {
CONSTANTS.put(c.value, c);
}
}

private Conjunction(String value) {
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
public static Query.Conjunction fromValue(String value) {
Query.Conjunction constant = CONSTANTS.get(value);
if (constant == null) {
throw new IllegalArgumentException(value);
} else {
return constant;
}
}

}

}


package com.synchronoss.saw.storage.proxy.model.response;

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
"alias",
"index",
"filter",
"routing.index",
"routing.search"
})
public class ClusterAliasesResponse {

/**
* The Health Schema 
* <p>
* 
* 
*/
@JsonProperty("alias")
private String alias = "";
/**
* The Index Schema 
* <p>
* 
* 
*/
@JsonProperty("index")
private String index = "";
/**
* The Uuid Schema 
* <p>
* 
* 
*/
@JsonProperty("filter")
private String filter = "";
/**
* The Pri Schema 
* <p>
* 
* 
*/
@JsonProperty("routing.index")
private String routingIndex = "";
/**
* The Rep Schema 
* <p>
* 
* 
*/
@JsonProperty("routing.search")
private String routingSearch = "";

@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();



@JsonProperty("alias")
public String getAlias() {
  return alias;
}
@JsonProperty("alias")
public void setAlias(String alias) {
  this.alias = alias;
}
@JsonProperty("index")
public String getIndex() {
  return index;
}
@JsonProperty("index")
public void setIndex(String index) {
  this.index = index;
}
@JsonProperty("filter")
public String getFilter() {
  return filter;
}
@JsonProperty("filter")
public void setFilter(String filter) {
  this.filter = filter;
}
@JsonProperty("routing.index")
public String getRoutingIndex() {
  return routingIndex;
}
@JsonProperty("routing.index")
public void setRoutingIndex(String routingIndex) {
  this.routingIndex = routingIndex;
}
@JsonProperty("routing.search")
public String getRoutingSearch() {
  return routingSearch;
}
@JsonProperty("routing.search")
public void setRoutingSearch(String routingSearch) {
  this.routingSearch = routingSearch;
}

public void setAdditionalProperties(Map<String, Object> additionalProperties) {
  this.additionalProperties = additionalProperties;
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
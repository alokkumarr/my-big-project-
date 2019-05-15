

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
"health",
"status",
"index",
"uuid",
"pri",
"rep",
"docs.count",
"docs.deleted",
"store.size",
"pri.store.size"
})
public class ClusterIndexResponse {

/**
* The Health Schema 
* <p>
* 
* 
*/
@JsonProperty("health")
private String health = "";
/**
* The Status Schema 
* <p>
* 
* 
*/
@JsonProperty("status")
private String status = "";
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
@JsonProperty("uuid")
private String uuid = "";
/**
* The Pri Schema 
* <p>
* 
* 
*/
@JsonProperty("pri")
private String pri = "";
/**
* The Rep Schema 
* <p>
* 
* 
*/
@JsonProperty("rep")
private String rep = "";
/**
* The Docs.count Schema 
* <p>
* 
* 
*/
@JsonProperty("docs.count")
private String docsCount = "";
/**
* The Docs.deleted Schema 
* <p>
* 
* 
*/
@JsonProperty("docs.deleted")
private String docsDeleted = "";
/**
* The Store.size Schema 
* <p>
* 
* 
*/
@JsonProperty("store.size")
private String storeSize = "";
/**
* The Pri.store.size Schema 
* <p>
* 
* 
*/
@JsonProperty("pri.store.size")
private String priStoreSize = "";
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* The Health Schema 
* <p>
* 
* 
*/
@JsonProperty("health")
public String getHealth() {
return health;
}

/**
* The Health Schema 
* <p>
* 
* 
*/
@JsonProperty("health")
public void setHealth(String health) {
this.health = health;
}

/**
* The Status Schema 
* <p>
* 
* 
*/
@JsonProperty("status")
public String getStatus() {
return status;
}

/**
* The Status Schema 
* <p>
* 
* 
*/
@JsonProperty("status")
public void setStatus(String status) {
this.status = status;
}

/**
* The Index Schema 
* <p>
* 
* 
*/
@JsonProperty("index")
public String getIndex() {
return index;
}

/**
* The Index Schema 
* <p>
* 
* 
*/
@JsonProperty("index")
public void setIndex(String index) {
this.index = index;
}

/**
* The Uuid Schema 
* <p>
* 
* 
*/
@JsonProperty("uuid")
public String getUuid() {
return uuid;
}

/**
* The Uuid Schema 
* <p>
* 
* 
*/
@JsonProperty("uuid")
public void setUuid(String uuid) {
this.uuid = uuid;
}

/**
* The Pri Schema 
* <p>
* 
* 
*/
@JsonProperty("pri")
public String getPri() {
return pri;
}

/**
* The Pri Schema 
* <p>
* 
* 
*/
@JsonProperty("pri")
public void setPri(String pri) {
this.pri = pri;
}

/**
* The Rep Schema 
* <p>
* 
* 
*/
@JsonProperty("rep")
public String getRep() {
return rep;
}

/**
* The Rep Schema 
* <p>
* 
* 
*/
@JsonProperty("rep")
public void setRep(String rep) {
this.rep = rep;
}

/**
* The Docs.count Schema 
* <p>
* 
* 
*/
@JsonProperty("docs.count")
public String getDocsCount() {
return docsCount;
}

/**
* The Docs.count Schema 
* <p>
* 
* 
*/
@JsonProperty("docs.count")
public void setDocsCount(String docsCount) {
this.docsCount = docsCount;
}

/**
* The Docs.deleted Schema 
* <p>
* 
* 
*/
@JsonProperty("docs.deleted")
public String getDocsDeleted() {
return docsDeleted;
}

/**
* The Docs.deleted Schema 
* <p>
* 
* 
*/
@JsonProperty("docs.deleted")
public void setDocsDeleted(String docsDeleted) {
this.docsDeleted = docsDeleted;
}

/**
* The Store.size Schema 
* <p>
* 
* 
*/
@JsonProperty("store.size")
public String getStoreSize() {
return storeSize;
}

/**
* The Store.size Schema 
* <p>
* 
* 
*/
@JsonProperty("store.size")
public void setStoreSize(String storeSize) {
this.storeSize = storeSize;
}

/**
* The Pri.store.size Schema 
* <p>
* 
* 
*/
@JsonProperty("pri.store.size")
public String getPriStoreSize() {
return priStoreSize;
}

/**
* The Pri.store.size Schema 
* <p>
* 
* 
*/
@JsonProperty("pri.store.size")
public void setPriStoreSize(String priStoreSize) {
this.priStoreSize = priStoreSize;
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
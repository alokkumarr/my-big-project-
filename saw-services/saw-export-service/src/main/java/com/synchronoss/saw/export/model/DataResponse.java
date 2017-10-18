
package com.synchronoss.saw.export.model;

import java.util.HashMap;
import java.util.List;
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
"data",
"rowsToExport",
"totalRows"
})
public class DataResponse {

@JsonProperty("data")
private List<Object> data = null;
/**
* The rowstoexport schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("rowsToExport")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Integer rowsToExport = 0;
/**
* The totalrows schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("totalRows")
@JsonPropertyDescription("An explanation about the purpose of this instance.")
private Integer totalRows = 0;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("data")
public List<Object> getData() {
return data;
}

@JsonProperty("data")
public void setData(List<Object> data) {
this.data = data;
}

public DataResponse withData(List<Object> data) {
this.data = data;
return this;
}

/**
* The rowstoexport schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("rowsToExport")
public Integer getRowsToExport() {
return rowsToExport;
}

/**
* The rowstoexport schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("rowsToExport")
public void setRowsToExport(Integer rowsToExport) {
this.rowsToExport = rowsToExport;
}

public DataResponse withRowsToExport(Integer rowsToExport) {
this.rowsToExport = rowsToExport;
return this;
}

/**
* The totalrows schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("totalRows")
public Integer getTotalRows() {
return totalRows;
}

/**
* The totalrows schema.
* <p>
* An explanation about the purpose of this instance.
* 
*/
@JsonProperty("totalRows")
public void setTotalRows(Integer totalRows) {
this.totalRows = totalRows;
}

public DataResponse withTotalRows(Integer totalRows) {
this.totalRows = totalRows;
return this;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

public DataResponse withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}

}
package com.synchronoss.saw.workbench.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"file",
"lineSeparator",
"delimiter",
"quoteChar",
"quoteEscapeChar",
"hederSize",
"fieldNamesLine",
"dateFormats",
"rowsToInspect",
"sampleSize",
"delimiterType",
"header",
"description"
})
public class Inspect {

@JsonProperty("file")
private String file;
@JsonProperty("lineSeparator")
private String lineSeparator;
@JsonProperty("delimiter")
private String delimiter;
@JsonProperty("quoteChar")
private String quoteChar;
@JsonProperty("quoteEscapeChar")
private String quoteEscapeChar;
@JsonProperty("hederSize")
private Integer hederSize;
@JsonProperty("fieldNamesLine")
private Integer fieldNamesLine;
@JsonProperty("dateFormats")
private List<String> dateFormats = null;
@JsonProperty("rowsToInspect")
private Integer rowsToInspect;
@JsonProperty("sampleSize")
private Integer sampleSize;
@JsonProperty("delimiterType")
private String delimiterType;
@JsonProperty("header")
private String header;
@JsonProperty("description")
private String description;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("file")
public String getFile() {
return file;
}

@JsonProperty("file")
public void setFile(String file) {
this.file = file;
}

@JsonProperty("lineSeparator")
public String getLineSeparator() {
return lineSeparator;
}

@JsonProperty("lineSeparator")
public void setLineSeparator(String lineSeparator) {
this.lineSeparator = lineSeparator;
}

@JsonProperty("delimiter")
public String getDelimiter() {
return delimiter;
}

@JsonProperty("delimiter")
public void setDelimiter(String delimiter) {
this.delimiter = delimiter;
}

@JsonProperty("quoteChar")
public String getQuoteChar() {
return quoteChar;
}

@JsonProperty("quoteChar")
public void setQuoteChar(String quoteChar) {
this.quoteChar = quoteChar;
}

@JsonProperty("quoteEscapeChar")
public String getQuoteEscapeChar() {
return quoteEscapeChar;
}

@JsonProperty("quoteEscapeChar")
public void setQuoteEscapeChar(String quoteEscapeChar) {
this.quoteEscapeChar = quoteEscapeChar;
}

@JsonProperty("hederSize")
public Integer getHederSize() {
return hederSize;
}

@JsonProperty("hederSize")
public void setHederSize(Integer hederSize) {
this.hederSize = hederSize;
}

@JsonProperty("fieldNamesLine")
public Integer getFieldNamesLine() {
return fieldNamesLine;
}

@JsonProperty("fieldNamesLine")
public void setFieldNamesLine(Integer fieldNamesLine) {
this.fieldNamesLine = fieldNamesLine;
}

@JsonProperty("dateFormats")
public List<String> getDateFormats() {
return dateFormats;
}

@JsonProperty("dateFormats")
public void setDateFormats(List<String> dateFormats) {
this.dateFormats = dateFormats;
}

@JsonProperty("rowsToInspect")
public Integer getRowsToInspect() {
return rowsToInspect;
}

@JsonProperty("rowsToInspect")
public void setRowsToInspect(Integer rowsToInspect) {
this.rowsToInspect = rowsToInspect;
}

@JsonProperty("sampleSize")
public Integer getSampleSize() {
return sampleSize;
}

@JsonProperty("sampleSize")
public void setSampleSize(Integer sampleSize) {
this.sampleSize = sampleSize;
}

@JsonProperty("delimiterType")
public String getDelimiterType() {
return delimiterType;
}

@JsonProperty("delimiterType")
public void setDelimiterType(String delimiterType) {
this.delimiterType = delimiterType;
}

@JsonProperty("header")
public String getHeader() {
return header;
}

@JsonProperty("header")
public void setHeader(String header) {
this.header = header;
}

@JsonProperty("description")
public String getDescription() {
return description;
}

@JsonProperty("description")
public void setDescription(String description) {
this.description = description;
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
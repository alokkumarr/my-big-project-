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
})
public class Inspect {

@JsonProperty("file")
private String file;
@JsonProperty("lineSeparator")
private String lineSeparator ="\n";
@JsonProperty("delimiter")
private String delimiter = ",";
@JsonProperty("quoteChar")
private String quoteChar = "'";
@JsonProperty("quoteEscapeChar")
private String quoteEscapeChar = "\\";
@JsonProperty("headerSize")
private Integer headerSize = 1;
@JsonProperty("fieldNamesLine")
private Integer fieldNamesLine = 1;
@JsonProperty("dateFormats")
private List<String> dateFormats = null;
@JsonProperty("rowsToInspect")
private Integer rowsToInspect = 10000;
@JsonProperty("sampleSize")
private Integer sampleSize;
@JsonProperty("delimiterType")
private String delimiterType = "delimited";
@JsonProperty("description")
private String description = "It's delimited file inspecting to verify & understand the content of file";
@JsonProperty("fields")
private List<Object> fields;
@JsonProperty("info")
private Object info;
@JsonProperty("samplesParsed")
private List<Object> samplesParsed;
@JsonProperty("samples")
private List<Object> samples;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();



@JsonProperty("samplesParsed")
public List<Object> getSamplesParsed() {
  return samplesParsed;
}
@JsonProperty("samplesParsed")
public void setSamplesParsed(List<Object> samplesParsed) {
  this.samplesParsed = samplesParsed;
}
@JsonProperty("samples")
public List<Object> getSamples() {
  return samples;
}
@JsonProperty("samples")
public void setSamples(List<Object> samples) {
  this.samples = samples;
}
@JsonProperty("file")
public String getFile() {
return file;
}
@JsonProperty("info")
public Object getInfo() {
  return info;
}
@JsonProperty("info")
public void setInfo(Object info) {
  this.info = info;
}
@JsonProperty("fields")
public List<Object> getFields() {
  return fields;
}

@JsonProperty("fields")
public void setFields(List<Object> fields) {
  this.fields = fields;
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

@JsonProperty("headerSize")
public Integer getHeaderSize() {
return headerSize;
}

@JsonProperty("headerSize")
public void setHeaderSize(Integer headerSize) {
this.headerSize = headerSize;
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
package com.synchronoss.saw.semantic.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SemanticNodeColumn {
  @JsonProperty("columnName")
  private String columnName;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("aliasName")
  private String aliasName;

  @JsonProperty("name")
  private String name;

  @JsonProperty("table")
  private String table;

  @JsonProperty("type")
  private String type;

  @JsonProperty("geoType")
  private String geoType;

  @JsonProperty("include")
  private Boolean include;

  @JsonProperty("filterEligible")
  private Boolean filterEligible;

  @JsonProperty("kpiEligible")
  private Boolean kpiEligible;

  @JsonProperty("joinEligible")
  private Boolean joinEligible;

  @JsonProperty("dskEligible")
  private Boolean dskEligible;

  @JsonProperty("columnName")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("columnName")
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("displayName")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty("aliasName")
  public String getAliasName() {
    return aliasName;
  }

  @JsonProperty("aliasName")
  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("table")
  public String getTable() {
    return table;
  }

  @JsonProperty("table")
  public void setTable(String table) {
    this.table = table;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("geoType")
  public String getGeoType() {
    return geoType;
  }

  @JsonProperty("geoType")
  public void setGeoType(String geoType) {
    this.geoType = geoType;
  }

  @JsonProperty("include")
  public Boolean getInclude() {
    return include;
  }

  @JsonProperty("include")
  public void setInclude(Boolean include) {
    this.include = include;
  }

  @JsonProperty("filterEligible")
  public Boolean getFilterEligible() {
    return filterEligible;
  }

  @JsonProperty("filterEligible")
  public void setFilterEligible(Boolean filterEligible) {
    this.filterEligible = filterEligible;
  }

  @JsonProperty("kpiEligible")
  public Boolean getKpiEligible() {
    return kpiEligible;
  }

  @JsonProperty("kpiEligible")
  public void setKpiEligible(Boolean kpiEligible) {
    this.kpiEligible = kpiEligible;
  }

  @JsonProperty("joinEligible")
  public Boolean getJoinEligible() {
    return joinEligible;
  }

  @JsonProperty("joinEligible")
  public void setJoinEligible(Boolean joinEligible) {
    this.joinEligible = joinEligible;
  }

  @JsonProperty("dskEligible")
  public Boolean getDskEligible() {
    return dskEligible;
  }

  @JsonProperty("dskEligible")
  public void setDskEligible(Boolean dskEligible) {
    this.dskEligible = dskEligible;
  }
}

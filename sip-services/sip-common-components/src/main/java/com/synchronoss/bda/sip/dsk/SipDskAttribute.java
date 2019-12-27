package com.synchronoss.bda.sip.dsk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;


@JsonInclude(Include.NON_NULL)
@ApiModel
public class SipDskAttribute {
  @JsonProperty("attributeId")
  @ApiModelProperty(notes = "Attribute ID", name = "attributeId")
  private String attributeId;

  @JsonProperty("columnName")
  @ApiModelProperty(notes = "Column name/Attribute Name", name = "columnName")
  private String columnName;

  @JsonProperty("model")
  @ApiModelProperty(notes = "Attribute Model", name = "model")
  private Model model;

  @JsonProperty("booleanCriteria")
  @ApiModelProperty(notes = "Conjunction used", name = "booleanCriteria")
  private BooleanCriteria booleanCriteria;

  @JsonProperty("booleanQuery")
  @ApiModelProperty(
      notes = "List of attributes on which boolean criteria has to be applied",
      name = "booleanQuery")
  private List<SipDskAttribute> booleanQuery;

  public String getAttributeId() {
    return attributeId;
  }

  public void setAttributeId(String attributeId) {
    this.attributeId = attributeId;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public Model getModel() {
    return model;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public BooleanCriteria getBooleanCriteria() {
    return booleanCriteria;
  }

  public void setBooleanCriteria(BooleanCriteria booleanCriteria) {
    this.booleanCriteria = booleanCriteria;
  }

  public List<SipDskAttribute> getBooleanQuery() {
    return booleanQuery;
  }

  public void setBooleanQuery(List<SipDskAttribute> booleanQuery) {
    this.booleanQuery = booleanQuery;
  }
}

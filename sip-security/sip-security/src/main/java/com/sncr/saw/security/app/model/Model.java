package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Model {
  @JsonProperty("operator")
  private Operator operator;

  @JsonProperty("values")
  private List<String> values;

  @JsonProperty("operator")
  public Operator getOperator() {
    return operator;
  }

  @JsonProperty("operator")
  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  @JsonProperty("values")
  public List<String> getValues() {
    return values;
  }

  @JsonProperty("values")
  public void setValues(List<String> values) {
    this.values = values;
  }
}

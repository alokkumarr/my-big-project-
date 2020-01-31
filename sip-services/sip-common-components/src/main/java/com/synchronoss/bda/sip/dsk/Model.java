package com.synchronoss.bda.sip.dsk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(Include.NON_NULL)
@ApiModel
public class Model {
  @JsonProperty("operator")
  @ApiModelProperty(notes = "Boolean operator", name = "operator")
  private Operator operator;

  @JsonProperty("values")
  @ApiModelProperty(notes = "List of values for the attribute", name = "values")
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

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("operator", operator)
        .append("values", values)
        .toString();
  }
}

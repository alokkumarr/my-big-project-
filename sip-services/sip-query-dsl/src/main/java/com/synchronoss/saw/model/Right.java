package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"artifactsName", "columnName"})
public class Right implements Serializable {

  private static final long serialVersionUID = 2636986253745802122L;
  @JsonProperty("artifactsName")
  private String artifactsName;
  @JsonProperty("columnName")
  private String columnName;

  @JsonProperty("artifactsName")
  public String getArtifactsName() {
    return artifactsName;
  }

  @JsonProperty("artifactsName")
  public void setArtifactsName(String artifactsName) {
    this.artifactsName = artifactsName;
  }

  @JsonProperty("columnName")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("columnName")
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("artifactsName", artifactsName)
        .append("columnName", columnName)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(artifactsName).append(columnName).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Right) == false) {
      return false;
    }
    Right rhs = ((Right) other);
    return new EqualsBuilder()
        .append(artifactsName, rhs.artifactsName)
        .append(columnName, rhs.columnName)
        .isEquals();
  }
}

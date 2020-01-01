package com.synchronoss.saw.semantic.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"artifactsName", "columns"})
public class SemanticNodeArtifact {
  @JsonProperty("artifactsName")
  private String artifactsName;

  @JsonProperty("columns")
  private List<SemanticNodeColumn> columns = null;

  @JsonProperty("artifactsName")
  public String getArtifactsName() {
    return artifactsName;
  }

  @JsonProperty("artifactsName")
  public void setArtifactsName(String artifactsName) {
    this.artifactsName = artifactsName;
  }

  @JsonProperty("columns")
  public List<SemanticNodeColumn> getColumns() {
    return columns;
  }

  @JsonProperty("columns")
  public void setColumns(List<SemanticNodeColumn> columns) {
    this.columns = columns;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("artifactsName", artifactsName)
        .append("columns", columns)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(artifactsName).append(columns).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof SemanticNodeArtifact) == false) {
      return false;
    }
    SemanticNodeArtifact rhs = ((SemanticNodeArtifact) other);
    return new EqualsBuilder()
        .append(artifactsName, rhs.artifactsName)
        .append(columns, rhs.columns)
        .isEquals();
  }
}

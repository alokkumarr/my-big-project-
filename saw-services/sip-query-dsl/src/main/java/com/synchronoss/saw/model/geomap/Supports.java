package com.synchronoss.saw.model.geomap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Supports {

  @JsonProperty("category")
  String category;

  @JsonProperty("label")
  String label;

  @JsonProperty("children")
  List<Children> children;

  @JsonProperty("category")
  public String getCategory() {
    return category;
  }

  @JsonProperty("category")
  public void setCategory(String category) {
    this.category = category;
  }

  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  @JsonProperty("label")
  public void setLabel(String label) {
    this.label = label;
  }

  @JsonProperty("children")
  public List<Children> getChildren() {
    return children;
  }

  @JsonProperty("children")
  public void setChildren(List<Children> children) {
    this.children = children;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("category", category)
        .append("label", label)
        .append("children", children)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(category).append(label).append(children).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Supports) == false) {
      return false;
    }

    Supports rhs = (Supports) other;
    return new EqualsBuilder()
        .append(category, rhs.category)
        .append(label, rhs.label)
        .append(children, rhs.children)
        .isEquals();
  }
}

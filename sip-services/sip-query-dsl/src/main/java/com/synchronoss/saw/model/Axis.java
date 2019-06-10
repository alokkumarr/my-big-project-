package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Axis {
  @JsonProperty("title")
  String title;

  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "Axis{" + "title='" + title + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Axis axis = (Axis) o;

    return new EqualsBuilder().append(title, axis.title).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(title).toHashCode();
  }
}

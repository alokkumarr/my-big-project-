package com.synchronoss.saw.model.geomap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoRegion {
  @JsonProperty("name")
  String name;

  @JsonProperty("path")
  String path;

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("path")
  public String getPath() {
    return path;
  }

  @JsonProperty("path")
  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("name", name).append("path", path).toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).append(path).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof GeoRegion) == false) {
      return false;
    }

    GeoRegion rhs = (GeoRegion) other;
    return new EqualsBuilder().append(name, rhs.name).append(path, rhs.path).isEquals();
  }
}

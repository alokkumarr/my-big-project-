package com.synchronoss.saw.model.geomap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapOptions {
  @JsonProperty("mapStyle")
  String mapStyle;

  @JsonProperty("mapType")
  String mapType;

  @JsonProperty("mapStyle")
  public String getMapStyle() {
    return mapStyle;
  }

  @JsonProperty("mapStyle")
  public void setMapStyle(String mapStyle) {
    this.mapStyle = mapStyle;
  }

  @JsonProperty("mapType")
  public String getMapType() {
    return mapType;
  }

  @JsonProperty("mapType")
  public void setMapType(String mapType) {
    this.mapType = mapType;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("mapStyle", mapStyle)
        .append("path", mapType)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(mapStyle).append(mapType).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof MapOptions) == false) {
      return false;
    }

    MapOptions rhs = (MapOptions) other;
    return new EqualsBuilder()
        .append(mapStyle, rhs.mapStyle)
        .append(mapType, rhs.mapType)
        .isEquals();
  }
}

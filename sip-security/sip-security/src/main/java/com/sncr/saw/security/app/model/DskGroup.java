package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DskGroup {
  @JsonProperty("groupName")
  private String groupName;

  @JsonProperty("groupDescription")
  private String groupDescription;

  @JsonProperty("dataSecurity")
  private List<DskValues> dataSecurity;

  @JsonProperty("groupName")
  public String getGroupName() {
    return groupName;
  }

  @JsonProperty("groupName")
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getGroupDescription() {
    return groupDescription;
  }

  public void setGroupDescription(String groupDescription) {
    this.groupDescription = groupDescription;
  }

    @JsonProperty("dataSecurity")
  public List<DskValues> getDataSecurity() {
    return dataSecurity;
  }

  @JsonProperty("dataSecurity")
  public void setDataSecurity(List<DskValues> dataSecurity) {
    this.dataSecurity = dataSecurity;
  }
}

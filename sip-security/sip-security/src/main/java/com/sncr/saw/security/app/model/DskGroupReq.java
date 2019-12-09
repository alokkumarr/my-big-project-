package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sncr.saw.security.common.bean.repo.dsk.Conjunction;
import java.util.List;

public class DskGroupReq {
  @JsonProperty("groupName")
  private String groupName;

  @JsonProperty("groupDescription")
  private String groupDescription;

  @JsonProperty("dskAttributes")
  private List<DskAttribute> dskAttributes;

  @JsonProperty("conjunction")
  private Conjunction conjunction;

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

  @JsonProperty("dskAttributes")
  public List<DskAttribute> getDskAttributes() {
    return dskAttributes;
  }

  @JsonProperty("dskAttributes")
  public void setDskAttributes(List<DskAttribute> dskAttributes) {
    this.dskAttributes = dskAttributes;
  }

  @JsonProperty("conjunction")
  public Conjunction getConjunction() {
    return conjunction;
  }

  @JsonProperty("conjunction")
  public void setConjunction(Conjunction conjunction) {
    this.conjunction = conjunction;
  }
}

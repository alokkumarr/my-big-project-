package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sncr.saw.security.common.bean.repo.dsk.Conjunction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

@ApiModel
public class DskGroupReq {
  @JsonProperty("groupName")
  @ApiModelProperty(
      notes = "Security group Name",
      name = "groupName",
      required = true)
  private String groupName;

  @JsonProperty("groupDescription")
  @ApiModelProperty(
      notes = "Security group description",
      name = "groupDescription")
  private String groupDescription;

  @JsonProperty("dskAttributes")
  @ApiModelProperty(
      notes = "Security group attributes",
      name = "dskAttributes",
      required = true)
  private List<DskAttribute> dskAttributes;

  @JsonProperty("conjunction")
  @ApiModelProperty(
      notes = "Conjunction value (AND/OR). By default, the value is AND",
      name = "conjunction")
  private Conjunction conjunction = Conjunction.AND;

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

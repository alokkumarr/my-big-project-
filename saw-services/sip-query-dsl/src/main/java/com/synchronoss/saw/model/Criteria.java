package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"joinCondition"})
public class Criteria implements Serializable {

  private static final long serialVersionUID = -6202723440049243096L;
  @JsonProperty("joinCondition")
  private JoinCondition joinCondition;

  @JsonProperty("joinCondition")
  public JoinCondition getJoinCondition() {
    return joinCondition;
  }

  @JsonProperty("joinCondition")
  public void setJoinCondition(JoinCondition joinCondition) {
    this.joinCondition = joinCondition;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("joinCondition", joinCondition).toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(joinCondition).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Criteria) == false) {
      return false;
    }
      Criteria rhs = ((Criteria) other);
    return new EqualsBuilder().append(joinCondition, rhs.joinCondition).isEquals();
  }
}

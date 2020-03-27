package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SubscriberDetails {
  @JsonProperty("subscriberId")
  private String subscriberId;

  @JsonProperty("channelTypes")
  private List<NotificationChannelType> channelTypes;

  public SubscriberDetails(String subscriberId, List<NotificationChannelType> channelTypes) {
    this.subscriberId = subscriberId;
    this.channelTypes = channelTypes;
  }

  public SubscriberDetails() {}

  @JsonProperty("subscriberId")
  public String getSubscriberId() {
    return subscriberId;
  }

  @JsonProperty("subscriberId")
  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }

  @JsonProperty("channelTypes")
  public List<NotificationChannelType> getChannelTypes() {
    return channelTypes;
  }

  @JsonProperty("channelTypes")
  public void setChannelTypes(List<NotificationChannelType> channelTypes) {
    this.channelTypes = channelTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SubscriberDetails that = (SubscriberDetails) o;

    return new EqualsBuilder()
        .append(subscriberId, that.subscriberId)
        .append(channelTypes, that.channelTypes)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(subscriberId).append(channelTypes).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("subscriberId", subscriberId)
        .append("channelTypes", channelTypes)
        .toString();
  }
}

package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@JsonInclude(Include.NON_NULL)
public class NotificationSubscriber {
  @JsonProperty("id")
  @Id
  private String id;

  @JsonProperty("subscriberId")
  private String subscriberId;

  @JsonProperty("subscriberName")
  private String subscriberName;

  @JsonProperty("channelType")
  @ApiModelProperty(notes = "SIP supported Channel Types", name = "channelType")
  @Enumerated(EnumType.STRING)
  private NotificationChannelType channelType;

  @JsonProperty("channelValue")
  private String channelValue;

  @JsonProperty("customerCode")
  private String customerCode;

  @JsonProperty("active")
  private Boolean active = Boolean.TRUE;

  @JsonProperty("createdTime")
  private Date createdTime;

  @JsonProperty("modifiedTime")
  private Date modifiedTime;

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("subscriberId")
  public String getSubscriberId() {
    return subscriberId;
  }

  @JsonProperty("subscriberId")
  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }

  @JsonProperty("subscriberName")
  public String getSubscriberName() {
    return subscriberName;
  }

  @JsonProperty("subscriberName")
  public void setSubscriberName(String subscriberName) {
    this.subscriberName = subscriberName;
  }

  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  @JsonProperty("active")
  public void setActive(Boolean active) {
    this.active = active;
  }

  @JsonProperty("customerCode")
  public String getCustomerCode() {
    return customerCode;
  }

  @JsonProperty("customerCode")
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  @JsonProperty("createdTime")
  public Date getCreatedTime() {
    return createdTime;
  }

  @JsonProperty("createdTime")
  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  @JsonProperty("modifiedTime")
  public Date getModifiedTime() {
    return modifiedTime;
  }

  @JsonProperty("modifiedTime")
  public void setModifiedTime(Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  @JsonProperty("channelType")
  public NotificationChannelType getChannelType() {
    return channelType;
  }

  @JsonProperty("channelType")
  public void setChannelType(NotificationChannelType channelType) {
    this.channelType = channelType;
  }

  @JsonProperty("channelValue")
  public String getChannelValue() {
    return channelValue;
  }

  @JsonProperty("channelValue")
  public void setChannelValue(String channelValue) {
    this.channelValue = channelValue;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", "id")
        .append("subscriberId", subscriberId)
        .append("subscriberName", subscriberName)
        .append("channelType", channelType)
        .append("channelValue", channelValue)
        .append("customerCode", customerCode)
        .append("active", active)
        .append("createdTime", createdTime)
        .append("modifiedTime", modifiedTime)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NotificationSubscriber that = (NotificationSubscriber) o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(subscriberId, that.subscriberId)
        .append(subscriberName, that.subscriberName)
        .append(channelType, that.channelType)
        .append(channelValue, that.channelValue)
        .append(customerCode, that.customerCode)
        .append(active, that.active)
        .append(createdTime, that.createdTime)
        .append(modifiedTime, that.modifiedTime)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .append(subscriberId)
        .append(subscriberName)
        .append(channelType)
        .append(channelValue)
        .append(customerCode)
        .append(active)
        .append(createdTime)
        .append(modifiedTime)
        .toHashCode();
  }
}

package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@JsonInclude(Include.NON_NULL)
public class ModuleSubscriberMapping {
  @JsonProperty("id")
  @Id
  private String id;

  @JsonProperty("moduleId")
  private String moduleId;

  @JsonProperty("moduleName")
  @Enumerated(EnumType.STRING)
  private ModuleName moduleName;

  @JsonProperty("subscriberId")
  private String subscriberId;

  @JsonProperty("channelType")
  @Enumerated(EnumType.STRING)
  private NotificationChannelType channelType;

  @JsonProperty("acknowledged")
  private Boolean acknowledged;

  /**
   * Constructor.
   *
   * @param id ID
   * @param moduleId Module ID
   * @param moduleName Module Name
   * @param subscriberId Subscriber ID
   * @param channelType Channel Type
   * @param acknowledged Scknowledged flag
   */
  public ModuleSubscriberMapping(
      String id,
      String moduleId,
      ModuleName moduleName,
      String subscriberId,
      NotificationChannelType channelType,
      Boolean acknowledged) {
    this.id = id;
    this.moduleId = moduleId;
    this.moduleName = moduleName;
    this.subscriberId = subscriberId;
    this.channelType = channelType;
    this.acknowledged = acknowledged;
  }

  public ModuleSubscriberMapping() {}

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("moduleId")
  public String getModuleId() {
    return moduleId;
  }

  @JsonProperty("moduleId")
  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  @JsonProperty("moduleName")
  public ModuleName getModuleName() {
    return moduleName;
  }

  @JsonProperty("moduleName")
  public void setModuleName(ModuleName moduleName) {
    this.moduleName = moduleName;
  }

  @JsonProperty("subscriberId")
  public String getSubscriberId() {
    return subscriberId;
  }

  @JsonProperty("subscriberId")
  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }

  @JsonProperty("channelType")
  public NotificationChannelType getChannelType() {
    return channelType;
  }

  @JsonProperty("channelType")
  public void setChannelType(NotificationChannelType channelType) {
    this.channelType = channelType;
  }

  @JsonProperty("acknowledged")
  public Boolean getAcknowledged() {
    return acknowledged;
  }

  @JsonProperty("acknowledged")
  public void setAcknowledged(Boolean acknowledged) {
    this.acknowledged = acknowledged;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ModuleSubscriberMapping that = (ModuleSubscriberMapping) o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(moduleId, that.moduleId)
        .append(moduleName, that.moduleName)
        .append(subscriberId, that.subscriberId)
        .append(channelType, that.channelType)
        .append(acknowledged, that.acknowledged)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .append(moduleId)
        .append(moduleName)
        .append(subscriberId)
        .append(channelType)
        .append(acknowledged)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("moduleId", moduleId)
        .append("moduleName", moduleName)
        .append("subscriberId", subscriberId)
        .append("channelType", channelType)
        .append("acknowledged", acknowledged)
        .toString();
  }
}

package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ModuleSubscriberMappingPayload {
  @JsonProperty("moduleId")
  private String moduleId;

  @JsonProperty("moduleName")
  @Enumerated(EnumType.STRING)
  private ModuleName moduleName;

  @JsonProperty("subscribers")
  private List<SubscriberDetails> subscribers;

  /**
   * Constructor.
   *
   * @param moduleId Module Id
   * @param moduleName Module Name
   * @param subscribers List of Subscribers
   */
  public ModuleSubscriberMappingPayload(
      String moduleId, ModuleName moduleName, List<SubscriberDetails> subscribers) {
    this.moduleId = moduleId;
    this.moduleName = moduleName;
    this.subscribers = subscribers;
  }

  public ModuleSubscriberMappingPayload() {}

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

  @JsonProperty("subscribers")
  public List<SubscriberDetails> getSubscribers() {
    return subscribers;
  }

  @JsonProperty("subscribers")
  public void setSubscribers(List<SubscriberDetails> subscribers) {
    this.subscribers = subscribers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ModuleSubscriberMappingPayload that = (ModuleSubscriberMappingPayload) o;

    return new EqualsBuilder()
        .append(moduleId, that.moduleId)
        .append(moduleName, that.moduleName)
        .append(subscribers, that.subscribers)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(moduleId)
        .append(moduleName)
        .append(subscribers)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("moduleId", moduleId)
        .append("moduleName", moduleName)
        .append("subscribers", subscribers)
        .toString();
  }
}

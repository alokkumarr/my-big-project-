package com.synchronoss.saw.rtis.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"customerCode", "appKey", "message"})
public class ConfigResponse {

  private static final long serialVersionUID = 1L;

  @JsonProperty("customerCode")
  private String customerCode;

  @JsonProperty("appKey")
  private String appKey;

  @JsonProperty("message")
  private String message;

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConfigResponse)) {
      return false;
    }
    ConfigResponse that = (ConfigResponse) o;
    return getCustomerCode().equals(that.getCustomerCode())
        && getAppKey().equals(that.getAppKey())
        && getMessage().equals(that.getMessage());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCustomerCode(), getAppKey(), getMessage());
  }
}

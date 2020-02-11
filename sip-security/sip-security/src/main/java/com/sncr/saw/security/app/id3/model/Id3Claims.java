package com.sncr.saw.security.app.id3.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class Id3Claims {

  private String domainName;
  private String clientId;
  private String masterLoginId;
  private String customerCode;
  private Type type;

  /** @return String get the domain name. */
  public String getDomainName() {
    return domainName;
  }

  /** @param domainName Set the domain name. */
  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  /** @return Get the client Id. */
  public String getClientId() {
    return clientId;
  }

  /** @param clientId Set the client Id. */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /** @return get the master login Id. */
  public String getMasterLoginId() {
    return masterLoginId;
  }

  /** @param masterLoginId Set the master login Id. */
  public void setMasterLoginId(String masterLoginId) {
    this.masterLoginId = masterLoginId;
  }

  /**
   * Get customer code.
   *
   * @return
   */
  public String getCustomerCode() {
    return customerCode;
  }

  /**
   * Set Customer Code.
   *
   * @param customerCode
   */
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  /** @return get the type of Token. */
  public Type getType() {
    return type;
  }

  /** @param type set the type of the token. */
  public void setType(Type type) {
    this.type = type;
  }

  public enum Type {
    REFRESH("refresh"),
    BEARER("Bearer"),
    ID("ID");
    private static final Map<String, Type> CONSTANTS = new HashMap<>();

    static {
      for (Type t : values()) {
        CONSTANTS.put(t.value, t);
      }
    }

    private final String value;

    private Type(String value) {
      this.value = value;
    }

    @JsonCreator
    public static Type fromValue(String value) {
      Type constant = CONSTANTS.get(value.toLowerCase());
      if (constant == null) {
        throw new IllegalArgumentException("type not supported: " + value);
      } else {
        return constant;
      }
    }

    @Override
    public String toString() {
      return this.value;
    }

    @JsonValue
    public String value() {
      return this.value;
    }
  }
}

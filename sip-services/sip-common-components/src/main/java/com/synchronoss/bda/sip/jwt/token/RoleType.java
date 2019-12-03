package com.synchronoss.bda.sip.jwt.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;

public enum RoleType {
  ADMIN("ADMIN"),
  USER("USER");
  private static final Map<String, RoleType> CONSTANTS = new HashMap<>();

  static {
    for (RoleType c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  private RoleType(String value) {
    this.value = value;
  }

  /**
   * Get Role type from string.
   *
   * @param value Role type Value.
   * @return RoleType
   */
  @JsonCreator
  public static RoleType fromValue(String value) {
    RoleType constant = CONSTANTS.get(value.toUpperCase());
    if (constant == null) {
      throw new IllegalArgumentException(value);
    } else {
      return constant;
    }
  }
	/**
	 * Validate Role type from string.
	 *
	 * @param type Role type Value.
	 * @return true if matches else false
	 */
	public static boolean validRoleType(String type) {
		RoleType[] roleTypes = RoleType.values();
		for (RoleType roleType : roleTypes)
			if (roleType.name().equalsIgnoreCase(type))
				return true;
		return false;
	}
}

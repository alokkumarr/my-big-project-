package com.synchronoss.sip.utils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This utility provide all the privileges operations.
 *
 * @author alok.kumarr
 * @since 3.5.0
 */
public class PrivilegeUtils {

  private static final Logger logger = LoggerFactory.getLogger(SipCommonUtils.class);

  static EnumMap<PrivilegeNames, Integer> privilegeCodes;

  public enum PrivilegeNames {
    ACCESS("ACCESS"),
    CREATE("CREATE"),
    EXECUTE("EXECUTE"),
    PUBLISH("PUBLISH"),
    FORK("FORK"),
    EDIT("EDIT"),
    EXPORT("EXPORT"),
    DELETE("DELETE"),
    ALL("ALL");

    private static final Map<String, PrivilegeNames> CONSTANTS = new HashMap<>();

    static {
      for (PrivilegeNames c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    PrivilegeNames(String value) {
      this.value = value;
    }

    /**
     * Get Privilege Names from string.
     *
     * @param value Privilege Names Value.
     * @return Privilege Names
     */
    public static PrivilegeNames fromValue(String value) {
      PrivilegeNames constant = CONSTANTS.get(value.toUpperCase());
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }

  /**
   * Default constructor to initial enumMap.
   */
  static {
    privilegeCodes = new EnumMap<>(PrivilegeNames.class);
    privilegeCodes.put(PrivilegeNames.ACCESS, 0);
    privilegeCodes.put(PrivilegeNames.CREATE, 1);
    privilegeCodes.put(PrivilegeNames.EXECUTE, 2);
    privilegeCodes.put(PrivilegeNames.PUBLISH, 3);
    privilegeCodes.put(PrivilegeNames.FORK, 4);
    privilegeCodes.put(PrivilegeNames.EDIT, 5);
    privilegeCodes.put(PrivilegeNames.EXPORT, 6);
    privilegeCodes.put(PrivilegeNames.DELETE, 7);
    privilegeCodes.put(PrivilegeNames.ALL, 8);
  }

  /**
   * Get the decimal value of binary for the privileges.
   *
   * @return privileges codes
   */
  public static Long getPrivilegeCode(List<String> accessNames) {
    long privilegeCode = 0L;
    try {
      StringBuilder builder = new StringBuilder();
      Integer[] privilege = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      for (String accessName : accessNames) {
        int value = privilegeCodes.get(PrivilegeNames.fromValue(accessName));
        privilege[value] = 1;
      }

      // build integer array to string for decimal conversion
      for (Integer n : privilege) {
        builder.append(n);
      }

      privilegeCode = Long.parseLong(builder.toString(), 2);
    } catch (Exception ex) {
      logger.error("Error while getting decimal value for privileges.");
    }
    return privilegeCode;
  }

  /**
   * Validate privilege type from string.
   *
   * @param type privilege name type Value.
   * @return true if matches else false
   */
  public static boolean validPrivilege(String type) {
    PrivilegeNames[] privilegeNames = PrivilegeNames.values();
    for (PrivilegeNames privilege : privilegeNames) {
      if (privilege.name().equalsIgnoreCase(type)) {
        return true;
      }
    }
    return false;
  }
}

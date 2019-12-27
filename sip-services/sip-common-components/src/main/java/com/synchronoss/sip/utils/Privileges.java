package com.synchronoss.sip.utils;

import static com.synchronoss.sip.utils.SipCommonUtils.decToBinary;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Privileges {

  private static final Logger logger = LoggerFactory.getLogger(SipCommonUtils.class);

  public enum PrivilegeNames {
    ACCESS(0),
    CREATE(1),
    EXECUTE(2),
    PUBLISH(3),
    FORK(4),
    EDIT(5),
    EXPORT(6),
    DELETE(7),
    ALL(8);

    private static final Map<PrivilegeNames, Integer> CONSTANTS = new HashMap<>();

    static {
      for (PrivilegeNames c : values()) {
        CONSTANTS.put(c, c.value);
      }
    }

    private final Integer value;

    PrivilegeNames(Integer value) {
      this.value = value;
    }

    public int getIndex() {
      return this.value;
    }
  }

  /**
   * Validate and check whether privilege is present.
   *
   * @param privName Privilege Name
   * @param dec Decimal number
   * @return Boolean value
   */
  public boolean isPriviegePresent(PrivilegeNames privName, Long dec) {
    if (dec == null || privName == null) {
      logger.error("PrivName or decimal code can't be null!!");
      return false;
    }
    int[] privCode = decToBinary(dec);
    int privValue = privName.getIndex();

    logger.info(String.format("Privilege Name : %s , Privilege Value : %d ", privName, privValue));

    if (privCode[PrivilegeNames.ALL.getIndex()] == 1 || privCode[privValue] == 1) {
      return true;
    }

    return false;
  }
}

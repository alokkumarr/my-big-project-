package com.synchronoss.sip.utils;

import static com.synchronoss.sip.utils.SipCommonUtils.decToBinary;

import java.util.EnumMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Privileges {

  EnumMap<PrivilegeNames, Integer> privilegeCodes;

  private static final Logger logger = LoggerFactory.getLogger(SipCommonUtils.class);

  public enum PrivilegeNames {
    ACCESS,
    CREATE,
    EXECUTE,
    PUBLISH,
    FORK,
    EDIT,
    EXPORT,
    DELETE,
    ALL
  }

  /** Default constructor to intaialize enumMap. */
  public Privileges() {
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
   * Validate and check whether privilege is present.
   *
   * @param privName Privilege Name
   * @param dec Decimal number
   * @return Boolean value
   */
  public boolean isPriviegePresent(PrivilegeNames privName, Long dec) {
    int[] privCode = decToBinary(dec);
    int privValue = privilegeCodes.get(privName);

    logger.info(String.format("Privilege Name : %s , Privilege Value : %d ", privName, privValue));
    if (privCode[privilegeCodes.get(PrivilegeNames.ALL)] == 1 || privCode[privValue] == 1) {
      return true;
    }

    return false;
  }
}

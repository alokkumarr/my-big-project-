package com.synchronoss.sip.utils;

import static com.synchronoss.sip.utils.SipCommonUtils.decToBinary;

import java.util.EnumMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Privileges {

  EnumMap<PRIVILEGE_NAMES, Integer> privilegeCodes;

  private static final Logger logger = LoggerFactory.getLogger(SipCommonUtils.class);

  public enum PRIVILEGE_NAMES {
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

  public Privileges() {
    privilegeCodes = new EnumMap<>(PRIVILEGE_NAMES.class);
    privilegeCodes.put(PRIVILEGE_NAMES.ACCESS, 0);
    privilegeCodes.put(PRIVILEGE_NAMES.CREATE, 1);
    privilegeCodes.put(PRIVILEGE_NAMES.EXECUTE, 2);
    privilegeCodes.put(PRIVILEGE_NAMES.PUBLISH, 3);
    privilegeCodes.put(PRIVILEGE_NAMES.FORK, 4);
    privilegeCodes.put(PRIVILEGE_NAMES.EDIT, 5);
    privilegeCodes.put(PRIVILEGE_NAMES.EXPORT, 6);
    privilegeCodes.put(PRIVILEGE_NAMES.DELETE, 7);
    privilegeCodes.put(PRIVILEGE_NAMES.ALL, 8);
  }

  public boolean isPriviegePresent(PRIVILEGE_NAMES privName, Long dec) {
    int privCode[] = decToBinary(dec);
    int privValue = privilegeCodes.get(privName);

    logger.info(String.format("Privilege Name : %s , Privilege Value : %d ", privName, privValue));
    if (privCode[privilegeCodes.get(PRIVILEGE_NAMES.ALL)] == 1 || privCode[privValue] == 1) {
      return true;
    }

    return false;
  }
}

package com.synchronoss;

import static com.synchronoss.sip.utils.SipCommonUtils.decToBinary;

import com.synchronoss.sip.utils.Privileges;
import com.synchronoss.sip.utils.Privileges.PRIVILEGE_NAMES;
import org.junit.Assert;
import org.junit.Test;

public class SipCommonUtilTest {

  @Test
  public void decimalToBinaryTest() {
    Assert.assertEquals(getBinaryCode(decToBinary(128L)), "0000000010000000");
    Assert.assertEquals(getBinaryCode(decToBinary(33280L)), "1000001000000000");
    Assert.assertEquals(getBinaryCode(decToBinary(49152L)), "1100000000000000");
  }

  @Test
  public void TestPrivilege() {
    Privileges privileges = new Privileges();
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.ACCESS, 33280L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.EXPORT, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.CREATE, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.EXECUTE, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.PUBLISH, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.FORK, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.EDIT, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.DELETE, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.ALL, 33280L));

    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.ACCESS, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.CREATE, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.EXECUTE, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.PUBLISH, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.FORK, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.EDIT, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.EXPORT, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.DELETE, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.ALL, 128L));

    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.ACCESS, 49152L));
    Assert.assertTrue(privileges.isPriviegePresent(PRIVILEGE_NAMES.CREATE, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.EXPORT, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.EXECUTE, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.PUBLISH, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.FORK, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.EDIT, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.DELETE, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PRIVILEGE_NAMES.ALL, 49152L));
  }

  public static String getBinaryCode(int[] privCode) {
    String binCode = "";
    for (int ind : privCode) {
      binCode = binCode.concat(String.valueOf(ind));
    }
    return binCode;
  }
}

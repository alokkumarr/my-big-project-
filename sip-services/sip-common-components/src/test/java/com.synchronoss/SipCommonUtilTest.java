package com.synchronoss;

import static com.synchronoss.sip.utils.SipCommonUtils.decToBinary;

import com.synchronoss.sip.utils.Privileges;
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;
import org.junit.Assert;
import org.junit.Test;

public class SipCommonUtilTest {

  @Test
  public void decimalToBinaryTest() {
    Assert.assertEquals(getBinaryCode(decToBinary(128L)), "0000000010000000");
    Assert.assertEquals(getBinaryCode(decToBinary(33280L)), "1000001000000000");
    Assert.assertEquals(getBinaryCode(decToBinary(49152L)), "1100000000000000");
    System.out.println(getBinaryCode(decToBinary(35328L)));
  }

  @Test
  public void privilegeTest() {
    Privileges privileges = new Privileges();
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.ACCESS, 33280L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.EXPORT, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.CREATE, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.EXECUTE, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.PUBLISH, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.FORK, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.EDIT, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.DELETE, 33280L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.ALL, 33280L));

    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.ACCESS, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.CREATE, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.EXECUTE, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.PUBLISH, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.FORK, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.EDIT, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.EXPORT, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.DELETE, 128L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.ALL, 128L));

    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.ACCESS, 49152L));
    Assert.assertTrue(privileges.isPriviegePresent(PrivilegeNames.CREATE, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.EXPORT, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.EXECUTE, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.PUBLISH, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.FORK, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.EDIT, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.DELETE, 49152L));
    Assert.assertFalse(privileges.isPriviegePresent(PrivilegeNames.ALL, 49152L));
  }

  /**
   * Get binary code from int array for comparision.
   *
   * @param privCode Boinary code of int array
   * @return concatenated string
   */
  public static String getBinaryCode(int[] privCode) {
    String binCode = "";
    for (int ind : privCode) {
      binCode = binCode.concat(String.valueOf(ind));
    }
    return binCode;
  }
}

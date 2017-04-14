package com.sncr.nsso.common.util;

import static org.junit.Assert.*;

import org.junit.Test;
/**
 * 
 * @author ssom0002
 *
 */
public class CcodeUtilTest {

	@Test
	public void test() {
		
		String password = "devpass";
		String encryptedPassword = "1m0PBBfvPVeSR2IlSGwLWg==";
		
		
		String encryptedPassword_Actual = CcodeUtil.cencode(password);
		String password_Actual = CcodeUtil.cdecode(encryptedPassword);
		
		
		assertEquals(encryptedPassword, encryptedPassword_Actual);
		assertEquals(password, password_Actual);
		
	}

}

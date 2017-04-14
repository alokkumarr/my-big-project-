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
		
		String encryptedPassword = CcodeUtil.cencode(password);
		String password_Actual = CcodeUtil.cdecode(encryptedPassword);
		String encrypted_Actual = CcodeUtil.cdecode(password_Actual);
		
		assertEquals(password, password_Actual);		
		assertEquals(encryptedPassword, encrypted_Actual);
	}

}

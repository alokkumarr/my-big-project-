package com.sncr.nsso.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
/**
 * 
 * @author ssom0002
 *
 */
public class CcodeUtil {
	final static byte[] key = {0x20, (byte)0x89, (byte)0xC3, 0x13, (byte)0xD6, 0x29, (byte)0x9E, 
								(byte)0x91, 0x6C, 0x6C, (byte)0xC2, 0x4D, 0x0A, (byte)0xB5, 0x2C, (byte)0xD4};
	final static String initVector = "RandomInitVector";
	/**
	 * 
	 * @param password
	 * @return
	 */
	public static String cencode(String password){
		
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(password.getBytes());
			return Base64.encodeBase64String(encrypted);
			

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
	}
	/**
	 * 
	 * @param encryptedPassword
	 * @return
	 */
	public static String cdecode(String encryptedPassword){
		
		byte[] original = null;
		try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            original = cipher.doFinal(Base64.decodeBase64(encryptedPassword));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }		
		
	}
	
	
	public static void main(String[] pwd) {
		
		if (pwd.length == 1) {
			String password = pwd[0];
			System.out.println(cencode(password));
			
		} else if (pwd.length == 2) {
			String encryptedPassword = pwd[0];
			System.out.println(cdecode(encryptedPassword));
		}
	}
}

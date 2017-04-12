package com.sncr.nsso.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

public class EncryptPassword {

	/**
	 * AES Encryption Algorithm with base64
	 * @param pwd
	 */

	public static void main(String[] pwd) {
		if (pwd.length == 1) {
			String password = pwd[0];

			String key = "SawSecurity12345"; // 128 bit key
			String initVector = "RandomInitVector"; // 16 bytes IV
			try {
				IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
				SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

				byte[] encrypted = cipher.doFinal(password.getBytes());
				System.out.println(Base64.encodeBase64String(encrypted));

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} 
	}
}

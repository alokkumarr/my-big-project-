/*
 * Copyright (C) 2007 Razorsight. All Rights Reserved. The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */
package com.sncr.nsso.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.Base64;

public class EncriptionUtil {
	public static String MESSAGE_DIGEST_ALGORITHM_MD5 = "MD5";
    public static String MESSAGE_DIGEST_ALGORITHM_SHA = "SHA";
	private static MessageDigest messageDigest;

	private static byte[] digest(String algorithm, String plainText) {
		try {
			if (algorithm == null)
				algorithm = MESSAGE_DIGEST_ALGORITHM_SHA;
			messageDigest = MessageDigest.getInstance(algorithm);
			return messageDigest.digest(plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new ExceptionInInitializerError(
					"NoSuchAlgorithmException Exception encountered");
		}
	}

	public static String encrypt(String plainText) {
		return encrypt(plainText, null);
	}
	
	/**
	 * API used for login validation. Decryption is not possible as the validation is one-way 
	 * @param plainText
	 * @return
	 */
	public static String encrypt(String plainText, String algorithm) {
		String cipherText = null;
		if (plainText != null) {
			byte[] encryptedBytes = digest(algorithm, plainText);
			Base64 base64Codec = new Base64();
			encryptedBytes = base64Codec.encode(encryptedBytes);
			cipherText = new String(encryptedBytes);
		}
		return cipherText;
	}

	public static boolean match(String plainText, String cipherText) {
		return match(plainText, cipherText, null); 
	}

	public static boolean match(String plainText, String cipherText, String algorithm) {
		boolean matchFlag = false;
		String computedCipherText = encrypt(plainText, algorithm);
		if (computedCipherText != null && computedCipherText.equals(cipherText)) {
			matchFlag = true;
		}
		return matchFlag;
	}

	
	/**
	 * Used for text encryption like password. Decryption is possible with this encryption method 
	 * @param plainText
	 * @return
	 */
	public static String encryptText(String plainText) {
		String cipherText = null;
		if (plainText != null) {
			Base64 base64Codec = new Base64();
			byte[] encryptedBytes = base64Codec.encode(plainText.getBytes());
			cipherText = new String(encryptedBytes);
		}
		return cipherText;
	}

	public static String decryptText(String cipherText) {
		String plainText = null;
		if (cipherText != null) {
			Base64 base64Codec = new Base64();
			byte[] decryptedBytes = base64Codec.decode(cipherText.getBytes());
			plainText = new String(decryptedBytes);
		}
		return plainText;
	}
    
/*	public static void main(String args[]) {
		//String cipherText = encryptText("Razorsight123");
		String cipherText = encrypt("Razorsight123");
		System.out.println("cipher text SHA=="+cipherText);//6poIYZJaGAwlmN0IYT4i/d8jjFQ=
		//System.out.println("cipher text MD5=="+encrypt("Razorsight", MESSAGE_DIGEST_ALGORITHM_MD5));
		
		System.out.println("Original text=="+decryptText(cipherText));
	}*/
}

package com.synchronoss.saw.batch.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SipObfuscation {

	private Cipher ecipher;
	private Cipher dcipher;

	public SipObfuscation(SecretKey key) throws Exception {
		this.ecipher = Cipher.getInstance("AES");
		this.dcipher = Cipher.getInstance("AES");
		this.ecipher.init(Cipher.ENCRYPT_MODE, key);
		this.dcipher.init(Cipher.DECRYPT_MODE, key);
	}

	@SuppressWarnings("restriction")
	public String encrypt(String str) throws Exception {
		byte[] utf8 = str.getBytes("UTF8");
		byte[] enc = ecipher.doFinal(utf8);
		return new sun.misc.BASE64Encoder().encode(enc);
	}

	@SuppressWarnings("restriction")
	public String decrypt(String str) throws Exception {
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
		byte[] utf8 = dcipher.doFinal(dec);
		return new String(utf8, "UTF8");
	}

	public static void main(String args[]) throws Exception {
		String data = "sawadmin123";
		String k = "Bar12345Bar12345";
		SecretKey key = new SecretKeySpec(k.getBytes(), "AES");
		SipObfuscation encrypter = new SipObfuscation(key);
		System.out.println("Original String: " + data);
		String encrypted = encrypter.encrypt(data);
		System.out.println("Encrypted String: " + encrypted);
		String decrypted = encrypter.decrypt(encrypted);
		System.out.println("Decrypted String: " + decrypted);

	}
}

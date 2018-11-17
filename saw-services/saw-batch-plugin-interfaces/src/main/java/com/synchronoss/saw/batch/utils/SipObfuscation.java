package com.synchronoss.saw.batch.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class SipObfuscation {

  private Cipher ecipher;
  private Cipher dcipher;

  /**
   * Parameterized Contstrutor.
   * 
   * @param key SecretKey i.e. salt.
   * @throws Exception exception.
   */
  public SipObfuscation(SecretKey key) throws Exception {
    this.ecipher = Cipher.getInstance("AES");
    this.dcipher = Cipher.getInstance("AES");
    this.ecipher.init(Cipher.ENCRYPT_MODE, key);
    this.dcipher.init(Cipher.DECRYPT_MODE, key);
  }

  /**
   * This method encrypts.
   * 
   * @param str input String.
   * @return String encrypted String.
   * @throws Exception exception.
   */
  @SuppressWarnings("restriction")
  public String encrypt(String str) throws Exception {
    byte[] utf8 = str.getBytes("UTF8");
    byte[] enc = ecipher.doFinal(utf8);
    return new sun.misc.BASE64Encoder().encode(enc);
  }

  /**
   * This method decrypts.
   * 
   * @param str input String.
   * @return String encrypted String.
   * @throws Exception exception.
   */
  @SuppressWarnings("restriction")
  public String decrypt(String str) throws Exception {
    byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
    byte[] utf8 = dcipher.doFinal(dec);
    return new String(utf8, "UTF8");
  }

}

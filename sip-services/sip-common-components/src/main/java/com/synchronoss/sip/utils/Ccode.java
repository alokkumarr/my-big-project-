package com.synchronoss.sip.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used for encoding and decoding the paasword.
 * @author ssom0002 */
public class Ccode {

  static final byte[] initVector = "RandomInitVector".getBytes();

  // Random bytes
  static final byte[] key = {
    0x20,
    (byte) 0x89,
    (byte) 0xC3,
    0x13,
    (byte) 0xD6,
    0x29,
    (byte) 0x9E,
    (byte) 0x91,
    0x6C,
    0x35,
    (byte) 0xC2,
    0x4D,
    0x0A,
    (byte) 0xB5,
    0x2C,
    (byte) 0xD4
  };

  /**
   * This method encrypts the parameter passed.
   * @param password pareter that has to e encoded
   * @return
   */
  public static String cencode(String password) {

    try {
      IvParameterSpec iv = new IvParameterSpec(initVector);
      SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

      byte[] encrypted = cipher.doFinal(password.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      return null;
    }
  }

  /**
   * This method decrypts the parameter passed.
   * @param encryptedPassword encryptedPassword
   * @return
   */
  public static String cdecode(String encryptedPassword) {

    try {
      byte[] encrypted = Base64.getDecoder().decode(encryptedPassword);

      IvParameterSpec iv = new IvParameterSpec(initVector);
      SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

      byte[] original = cipher.doFinal(encrypted);
      return new String(original);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      return null;
    }
  }

  /**
   * Main method used while executing the jar.
   *
   * @param args args
   */
  public static void main(String[] args) {

    int rc = 0;
    String res = null;
    boolean chkres = false;
    if (args.length == 1) {
      String password = args[0];
      res = cencode(password);
      chkres = true;
    } else if (args.length == 2) {
      String encryptedPassword = args[0];
      res = cdecode(encryptedPassword);
      chkres = true;
    }
    if (chkres) {
      if (res == null) {
        rc = 1;
      } else {
        System.out.print(res);
      }
    }
    System.exit(rc);
  }
}

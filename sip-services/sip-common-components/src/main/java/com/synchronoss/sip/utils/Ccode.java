package com.synchronoss.sip.utils;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collector;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used for encoding and decoding the paasword.
 * @author ssom0002 */
public class Ccode {

  static final byte[] initVector = "RandomInitVector".getBytes();

  /**
   * This method encrypts the parameter passed.
   * @param password pareter that has to e encoded
   * @return
   */
  public static String cencode(String password,byte []key) {

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
  public static String cdecode(String encryptedPassword, byte []key) {

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
    if (args.length == 2) {
      String password = args[0];
      String hexKey = args[1];
      byte []key = convertHexStringToByteArray(hexKey);
      res = cencode(password, key);
      chkres = true;
    } else if (args.length == 3) {
      String encryptedPassword = args[0];
      String hexKey = args[1];
      byte[] key = convertHexStringToByteArray(hexKey);
      res = cdecode(encryptedPassword, key);
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

  /**
   * Hex string to byte array.
   *
   * @param hexCode Hex code string
   * @return key in byte array
   */
  public static byte[] convertHexStringToByteArray(String hexCode) {

    String[] split = hexCode.split("-");

    Integer[] key =
        Arrays.stream(split).map(val -> Integer.parseInt(val, 16)).toArray(Integer[]::new);

    byte[] byteArray = new byte[key.length];
    for (int i = 0; i < key.length; i++) {
      byteArray[i] = key[i].byteValue();
    }

    return byteArray;
  }
}

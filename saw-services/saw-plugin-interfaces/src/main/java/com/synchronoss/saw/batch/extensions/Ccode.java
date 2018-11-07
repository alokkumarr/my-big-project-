package com.synchronoss.saw.batch.extensions;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * This is the class used for encrypting the logic.
 * @author ssom0002
 */
public class Ccode {
  // Random bytes
  static final byte[] key = {0x20, (byte) 0x89, (byte) 0xC3, 0x13, (byte) 0xD6, 0x29, (byte) 0x9E,
      (byte) 0x91, 0x6C, 0x35, (byte) 0xC2, 0x4D, 0x0A, (byte) 0xB5, 0x2C, (byte) 0xD4};
  static final byte[] initVector = "RandomInitVector".getBytes(); // "UTF-8");

  /**
   * This is the encoding logic.
   * @return
   */
  public static String cencode(String password) {

    try {
      IvParameterSpec iv = new IvParameterSpec(initVector);
      SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

      byte[] encrypted = cipher.doFinal(password.getBytes());
      // return Base64.encodeBase64String(encrypted);
      String encoded = Base64.getEncoder().encodeToString(encrypted);
      return encoded;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }

  }

  /**
   * encrypt password.
   * @param encryptedPassword String
   * @return encryptedPassword String
   */
  public static String cdecode(String encryptedPassword) {

    try {
      byte[] encrypted = Base64.getDecoder().decode(encryptedPassword);

      IvParameterSpec iv = new IvParameterSpec(initVector);
      SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

      // byte[] original = cipher.doFinal(Base64.decodeBase64(encryptedPassword));
      byte[] original = cipher.doFinal(encrypted);
      return new String(original);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * This is the entry method of the class.
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

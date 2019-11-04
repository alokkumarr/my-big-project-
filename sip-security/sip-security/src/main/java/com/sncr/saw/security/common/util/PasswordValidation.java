package com.sncr.saw.security.common.util;

import com.sncr.saw.security.common.bean.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public class PasswordValidation {

  private static final Pattern hasNumber = Pattern.compile("\\d");

  private static final Pattern hasLowercase = Pattern.compile("[a-z]");

  private static final Pattern hasUpperCase = Pattern.compile("[A-Z]");

  private static final Pattern hasSpeChar = Pattern.compile("[~!@#$%^&*?<>]");

  /**
   * Validate Password as specified in Synchronoss Password Policy.
   *
   * @param pwd Password
   * @param userName Login Id
   * @return Valid Object
   */
  public static Valid validatePassword(String pwd, String userName) {
    String message = null;
    Valid valid = new Valid();
    valid.setValid(false);
    if (StringUtils.isEmpty(pwd)) {
      valid.setValidityMessage("Password can't be empty or null.!!");
      return valid;
    }
    if (userName.equals(pwd)) {
      message = "User Name can't be assigned as password.";
      valid.setValidityMessage(message);
      return valid;
    }

    if (pwd.length() < 8) {
      message = "New password should be minimum of 8 character.";
      valid.setValidityMessage(message);
      return valid;
    }

    int validPatternCnt = 0;
    Matcher m = hasUpperCase.matcher(pwd);
    if (!m.find()) {
      validPatternCnt++;
    }

    m = hasSpeChar.matcher(pwd);
    if (!m.find()) {
      validPatternCnt++;
    }
    m = hasNumber.matcher(pwd);
    if (!m.find()) {
      validPatternCnt++;
    }
    m = hasLowercase.matcher(pwd);
    if (!m.find()) {
      validPatternCnt++;
    }

    // Synchronoss password Policy : Must contain at least 3 of the 4 one uppercase
    // character, one lowercase character, one numeric character, one special character
    if (validPatternCnt > 1) {
      message =
          "Password must contain at least 3 of the 4 one uppercase character,"
              + " one lowercase character, one numeric character, one special character ";
      valid.setValidityMessage(message);
      return valid;
    } else {
      valid.setValid(true);
      valid.setValidityMessage("Strong password.");
      return valid;
    }
  }
}

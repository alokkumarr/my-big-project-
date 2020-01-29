package com.sncr.saw.security.common.util;

import java.util.regex.Pattern;

public class SecurityUtils {

  public static final String NAME_PATTERN = "^[a-zA-Z0-9]*$";
  public static final String EMAIL_PATTERN =
      "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*";
  public static final String LOGIN_ID_PATTERN =
      "^[A-z\\d_@.#$=!%^)(\\]:\\*;\\?\\/\\,}{'\\|<>\\[&\\+-`~]*$";

  public static boolean isEmailValid(String email) {
    final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    return emailPattern.matcher(email).matches();
  }

  public static boolean isValidName(String name) {
    return name.matches(NAME_PATTERN);
  }

  public static boolean isValidMasterLoginId(String masterLoginId) {
    return masterLoginId.matches(LOGIN_ID_PATTERN);
  }
}

package com.sncr.saw.security.common.constants;

public class ErrorMessages {
  public static final String nullOrEmptyErrorMessage = "%s can't be null or empty";

  public static final String nullErrorMessage = "%s can't be null";

  public static final String notExistErrorMessage = "%s does not exists";

  public static final String invalidMessage = "invalid %s";

  public static final String unAuthorizedMessage =
      "You are not authorized to perform this operation";

  public static final String roleInActiveMessage =
      "User can't be created as role is in Inactive state";

  public static final String unableToUpdateUser =
      "No records found for update /" + " you are Not Authorized to update the User : ";

  public static final String INVALID_TOKEN = "Token is not valid.";
  public static final String TOKEN_EXPIRED = "Token has expired. Please re-login.";
  public static final String HEADER_ERROR = "Missing or invalid Authorization header.";
}

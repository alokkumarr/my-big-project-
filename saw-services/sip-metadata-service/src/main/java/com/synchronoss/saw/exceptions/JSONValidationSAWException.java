package com.synchronoss.saw.exceptions;

public class JSONValidationSAWException extends MetadataRuntimeException {

  private static final long serialVersionUID = 1L;

  public JSONValidationSAWException(String msg) {
    super(msg);
  }

  /**
   * Create a new BeansException with the specified message and root cause.
   *
   * @param msg the detail message
   * @param cause the root cause
   */
  public JSONValidationSAWException(String msg, Throwable cause) {
    super(msg, cause);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof JSONValidationSAWException)) {
      return false;
    }
    JSONValidationSAWException otherBe = (JSONValidationSAWException) other;
    return (getMessage().equals(otherBe.getMessage())
        && ExceptionSAWUtils.nullSafeEquals(getCause(), otherBe.getCause()));
  }

  @Override
  public int hashCode() {
    return getMessage().hashCode();
  }
}

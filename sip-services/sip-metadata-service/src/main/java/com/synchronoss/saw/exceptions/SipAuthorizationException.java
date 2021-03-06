package com.synchronoss.saw.exceptions;


public class SipAuthorizationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public SipAuthorizationException() {}

  public SipAuthorizationException(String msg) {
    super(msg);
  }

  /**
   * Create a new BeansException with the specified message and root cause.
   *
   * @param msg the detail message
   * @param cause the root cause
   */
  public SipAuthorizationException(String msg, Throwable cause) {
    super(msg, cause);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SipAuthorizationException)) {
      return false;
    }
    SipAuthorizationException otherBe = (SipAuthorizationException) other;
    return (getMessage().equals(otherBe.getMessage())
        && SipExceptionUtils.nullSafeEquals(getCause(), otherBe.getCause()));
  }

  @Override
  public int hashCode() {
    return getMessage().hashCode();
  }
}

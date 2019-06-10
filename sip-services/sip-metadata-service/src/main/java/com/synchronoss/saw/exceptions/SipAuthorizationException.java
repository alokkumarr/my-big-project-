package com.synchronoss.saw.exceptions;

import com.synchronoss.saw.util.MessageBundle;

public class SipAuthorizationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public SipAuthorizationException() {}

  public SipAuthorizationException(String msg) {
    super(msg);
  }

  public SipAuthorizationException(Integer errorcode) {
    super(MessageBundle.getMessage(errorcode.toString()));
  }

  public SipAuthorizationException(Integer errorcode, Object[] values) {
    super(MessageBundle.getMessage(errorcode.toString(), values));
  }

  public SipAuthorizationException(Integer errorcode, Object value) {
    super(MessageBundle.getMessage(errorcode.toString(), value));
  }

  public SipAuthorizationException(Integer errorcode, Object value1, Object value2) {
    super(MessageBundle.getMessage(errorcode.toString(), value1,value2));
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

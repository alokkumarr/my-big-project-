package com.synchronoss.saw.exceptions;

public class SipUpdateEntityException extends MetadataRuntimeException {

  private static final long serialVersionUID = 1L;

  public SipUpdateEntityException(String msg) {
    super(msg);
  }

  /**
   * Create a new BeansException with the specified message and root cause.
   *
   * @param msg the detail message
   * @param cause the root cause
   */
  public SipUpdateEntityException(String msg, Throwable cause) {
    super(msg, cause);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SipUpdateEntityException)) {
      return false;
    }
    SipUpdateEntityException otherBe = (SipUpdateEntityException) other;
    return (getMessage().equals(otherBe.getMessage())
        && SipExceptionUtils.nullSafeEquals(getCause(), otherBe.getCause()));
  }

  @Override
  public int hashCode() {
    return getMessage().hashCode();
  }
}

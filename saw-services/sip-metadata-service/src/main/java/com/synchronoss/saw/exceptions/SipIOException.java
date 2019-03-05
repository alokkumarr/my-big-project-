package com.synchronoss.saw.exceptions;

public class SipIOException extends MetadataRuntimeException {

  /** */
  private static final long serialVersionUID = 1L;

  public SipIOException(String msg) {
    super(msg);
  }

  /**
   * Create a new BeansException with the specified message and root cause.
   *
   * @param msg the detail message
   * @param cause the root cause
   */
  public SipIOException(String msg, Throwable cause) {
    super(msg, cause);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SipIOException)) {
      return false;
    }
    SipIOException otherBe = (SipIOException) other;
    return (getMessage().equals(otherBe.getMessage())
        && SipExceptionUtils.nullSafeEquals(getCause(), otherBe.getCause()));
  }

  @Override
  public int hashCode() {
    return getMessage().hashCode();
  }
}

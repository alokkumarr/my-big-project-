package com.synchronoss.saw.exception;

import com.synchronoss.saw.exceptions.NestedExceptionUtils;
import com.synchronoss.saw.exceptions.SipNestedRuntimeException;

@SuppressWarnings("serial")
public class SftpProcessorException extends SipNestedRuntimeException {

  public SftpProcessorException(String msg) {
    super(msg);
  }

  public SftpProcessorException(String msg, Throwable cause) {
    super(msg, cause);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SftpProcessorException)) {
      return false;
    }
    SftpProcessorException otherBe = (SftpProcessorException) other;
    return (getMessage().equals(otherBe.getMessage())
      && NestedExceptionUtils.nullSafeEquals(getCause(), otherBe.getCause()));
  }

  @Override
   public int hashCode() {
    return getMessage().hashCode();
  }
}

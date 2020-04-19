package com.synchronoss.bda.sip.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class SipNotProcessedSipEntityException extends IOException {

  public SipNotProcessedSipEntityException(String msg) {
    super(msg);
  }

  /**
   * Create a new BeansException with the specified message and root cause.
   * 
   * @param msg the detail message
   * @param cause the root cause
   */
  public SipNotProcessedSipEntityException(String msg, Throwable cause) {
    super(msg, cause);
  }
}

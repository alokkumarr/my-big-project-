package com.synchronoss.sip.alert.exceptions;

public class SipAlertRunTimeExceptions extends RuntimeException {

  public SipAlertRunTimeExceptions(String msg) {
    super(msg);
  }

  public SipAlertRunTimeExceptions(String msg, Throwable cause) {
    super(msg, cause);
  }
}

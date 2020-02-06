package com.sncr.saw.security.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SipFileNotFoundException extends RuntimeException {
  public SipFileNotFoundException(String message) {
    super(message);
  }

  public SipFileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
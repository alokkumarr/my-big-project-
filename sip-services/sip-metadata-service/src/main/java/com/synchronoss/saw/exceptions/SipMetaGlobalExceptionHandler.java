package com.synchronoss.saw.exceptions;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SipMetaGlobalExceptionHandler {
  @ExceptionHandler(SipAuthorizationException.class)
  public void handle(HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}

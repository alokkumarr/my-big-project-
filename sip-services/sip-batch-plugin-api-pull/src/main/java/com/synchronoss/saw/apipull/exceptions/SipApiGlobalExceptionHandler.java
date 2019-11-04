package com.synchronoss.saw.apipull.exceptions;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SipApiGlobalExceptionHandler {
  @ExceptionHandler(SipApiPullExecption.class)
  public void handle(HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
  }
}

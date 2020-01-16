package com.synchronoss.saw.scheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SchedulerEntityException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SchedulerEntityException() {
    super();
  }

  public SchedulerEntityException(String message) {
    super(message);
  }

  public SchedulerEntityException(String message, Throwable cause) {
    super(message, cause);
  }
}

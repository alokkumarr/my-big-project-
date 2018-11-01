package com.synchronoss.saw.logs.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.QueryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Global exception handler for rest services.
   * 
   * @param  exception details of the exception.
   * @return response based on exception type.
   */
  @ExceptionHandler(QueryException.class)
  public ResponseEntity<Map<String, String>> yourExceptionHandler(QueryException exception) {
    Map<String, String> response = new HashMap<String, String>();
    response.put("message", "Bad Request");
    return new ResponseEntity<Map<String, String>>(response, HttpStatus.BAD_REQUEST); 
  }
}

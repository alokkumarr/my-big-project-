package com.synchronoss.saw.batch.exception;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class SipAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(SipAsyncExceptionHandler.class);

  @Override
  public void handleUncaughtException(Throwable ex, Method method, Object... params) {

    logger.error("Exception Cause : " + ex);
    logger.error("Method name : " + method.getName());
    for (Object param : params) {
      logger.error("Parameter value : " + param);
    }

  }

}

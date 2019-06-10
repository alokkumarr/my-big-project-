package com.synchronoss.saw.util;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

public final class MessageBundle {
  protected static final Logger logger = LoggerFactory.getLogger(MessageBundle.class);

  private MessageBundle() {
    // Utility classes should not have a public or default constructor.
  }

  /**
   * Overloaded method to get localized message.
   *
   * @param key errocode
   * @return
   */
  public static String getMessage(String key) {
    return MessageBundle.getMessage(key, null);
  }

  /**
   * Overloaded method to get localized message that has a single parameter.
   *
   * @param key errocode
   * @param value parameter to pass to error code
   * @return
   */
  public static String getMessage(String key, Object value) {
    return MessageBundle.getMessage(key, new Object[] {value});
  }

  /**
   * Overloaded method to get localized message that has two parameters.
   *
   * @param key errocode
   * @param value1 parameter1 to pass to error code
   * @param value2 parameter2 to pass to error code
   * @return
   */
  public static String getMessage(String key, Object value1, Object value2) {
    return MessageBundle.getMessage(key, new Object[] {value1, value2});
  }

  /**
   * Overloaded method to get localized message locale en_US is taken as default.
   *
   * @param key errocode
   * @param values parameters passed to error code
   * @return
   */
  public static String getMessage(String key, Object[] values) {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    String message = key;
    try {
      message = messageSource.getMessage(key, values, Locale.US);
    } catch (NoSuchMessageException e) {
      logger.trace(e.getLocalizedMessage(), e);
    }
    return message;
  }
}

package com.synchronoss.sip.utils;

import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SipCommonUtils {

  private static final Logger logger = LoggerFactory.getLogger(SipCommonUtils.class);

  /**
   * This method to validate jwt token then return the validated ticket for further processing.
   *
   * @param request HttpServletRequest
   * @return Ticket
   */
  public static Ticket getTicket(HttpServletRequest request) {
    Ticket ticket = null;
    try {
      String token = getToken(request);
      ticket = TokenParser.retrieveTicket(token);
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching token", e);
    }
    return ticket;
  }

  /**
   * Get JWT token details.
   *
   * @param req http Request
   * @return String
   * @throws IllegalAccessException If Authorization not found
   */
  public static String getToken(final HttpServletRequest req) throws IllegalAccessException {
    String authHeader = null;
    if (!("OPTIONS".equals(req.getMethod()))) {
      authHeader = req.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new IllegalAccessException("Missing or invalid Authorization header.");
      }
      return authHeader.substring(7); // The part after "Bearer "
    }
    return authHeader;
  }

  /**
   * Validates the Name for file, analysis etc.
   *
   * @param name name
   */
  public static void validateName(String name) {
    if (name.isEmpty()) {
      throw new IllegalArgumentException("analysisName must not be null");
    }
    // validate name length and avoid any invalid specific symbol for file name
    boolean hasValidLength = name.length() >= 1 && name.length() <= 30;
    if (hasValidLength) {
      if (name.matches("[`~!@#$%^&*()+={}|\"':;?/>.<,*:/?\\[\\]\\\\]")) {
        throw new IllegalArgumentException(
            "Analysis name must not consists of special characters except '- _'");
      }
    } else {
      throw new IllegalArgumentException(
          String.format(
              "analysisName %s is invalid - character count MUST be greater than or equal to 1 and "
                  + "less than or equal to 30",
              name));
    }
  }
}

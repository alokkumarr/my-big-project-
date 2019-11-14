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
   * Functions returns the binary equivalent given a decimal num.
   *
   * @param n decimal integer
   * @return binary integer data
   */
  public static int[] decToBinary(Long n) {
    int privCode[] = new int[16];
    int j = 0;

    for (Long i = 15L; i >= 0; i--) {
      Long k = n >> i;
      if ((k & 1) > 0) privCode[j++] = 1;
      else privCode[j++] = 0;
    }

    String binCode = "";
    for (int ind : privCode) {
      binCode = binCode.concat(String.valueOf(ind));
    }
    logger.info(String.format("Binary Equivalent of : %s is = %s ", n, binCode));

    return privCode;
  }
}

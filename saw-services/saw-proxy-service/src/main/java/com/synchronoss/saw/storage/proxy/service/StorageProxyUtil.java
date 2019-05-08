package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageProxyUtil {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyUtil.class);
  /**
   * This method to validate jwt token then return the validated ticket for further processing.
   *
   * @param request HttpServletRequest
   * @return Ticket
   */
  public static Ticket getTicket(HttpServletRequest request) {
    try {
      String token = getToken(request);
      return TokenParser.retrieveTicket(token);
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching the alert details", e);
    }
    return null;
  }

  /**
   * Get JWT token details.
   *
   * @param req http Request
   * @return String
   * @throws IllegalAccessException If Authorization not found
   */
  public static String getToken(final HttpServletRequest req) throws IllegalAccessException {

    if (!("OPTIONS".equals(req.getMethod()))) {

      final String authHeader = req.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {

        throw new IllegalAccessException("Missing or invalid Authorization header.");
      }

      return authHeader.substring(7); // The part after "Bearer "
    }

    return null;
  }

  /**
   * Iterate the ticket DskList to return DataSecurity Object.
   *
   * @param {@link List} of {@link TicketDSKDetails}
   * @return {@link List} of {@link DataSecurityKeyDef}
   */
  public static List<DataSecurityKeyDef> getDsks(List<TicketDSKDetails> dskList) {
    DataSecurityKeyDef dataSecurityKeyDef;
    List<DataSecurityKeyDef> dskDefList = new ArrayList<>();
    for (TicketDSKDetails dsk : dskList) {
      dataSecurityKeyDef = new DataSecurityKeyDef();
      dataSecurityKeyDef.setName(dsk.getName());
      dataSecurityKeyDef.setValues(dsk.getValues());
      dskDefList.add(dataSecurityKeyDef);
    }
    return (dskDefList);
  }
}

package com.synchronoss.bda.sip.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import java.io.IOException;
import java.util.Base64;

public class TokenParser {

  public static final String SEPARATOR = "\\.";

  /**
   * Retrieve Ticket will read the values from jwt token.
   *
   * @param jwtToken Token.
   * @return Ticket object.
   * @throws IOException throws IO Exception if unable to read token.
   */
  public static Ticket retrieveTicket(String jwtToken) throws IOException {
    String[] jwtParts = jwtToken.split(SEPARATOR);
    Ticket ticket = null;
    ObjectMapper objectMapper = new ObjectMapper();
    if (jwtParts.length == 3) {
      String payload = new String(Base64.getDecoder().decode(jwtParts[1]));

      String sanitizedPayload = JsonSanitizer.sanitize(payload);
      JsonNode decodedToken = objectMapper.readTree(sanitizedPayload);
      ticket = objectMapper.convertValue(decodedToken.get("ticket"), Ticket.class);
    }
    return ticket;
  }
}

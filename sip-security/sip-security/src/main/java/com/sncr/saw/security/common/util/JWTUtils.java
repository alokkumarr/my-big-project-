package com.sncr.saw.security.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/** */
@Component
public class JWTUtils {

  public static String[] parseToken(String token, String secretKey) {
    Claims claims = null;
    try {
      claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (final SignatureException e) {
      throw new SignatureException("Invalid token.");
    } catch (MalformedJwtException ex) {
      throw new SignatureException("Invalid token");
    } catch (ExpiredJwtException expired) {
      throw new SignatureException("token has expired");
    }

    // This checks the validity of the token. logging out does not need
    // the token to be active.
    Iterator<?> it = ((Map<String, Object>) claims.get("ticket")).entrySet().iterator();
    String[] parsedValues = new String[5];
    while (it.hasNext()) {
      Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
      if (pair.getKey().equals("userId")) {
        parsedValues[0] = (pair.getValue().toString());
      }
      if (pair.getKey().equals("custID")) {
        parsedValues[1] = (pair.getValue().toString());
      }
      if (pair.getKey().equals("userFullName")) {
        parsedValues[2] = (pair.getValue().toString());
      }
      if (pair.getKey().equals("roleType")) {
        parsedValues[3] = (pair.getValue().toString());
      }
      if (pair.getKey().equals("masterLoginId")) {
        parsedValues[4] = (pair.getValue().toString());
      }
    }
    return parsedValues;
  }

  public static String getToken(final HttpServletRequest req) {
    if (!("OPTIONS".equals(req.getMethod()))) {
      final String authHeader = req.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new MalformedJwtException("Missing or invalid Authorization header.");
      }
      return authHeader.substring(7); // The part after "Bearer "
    }
    return null;
  }
}

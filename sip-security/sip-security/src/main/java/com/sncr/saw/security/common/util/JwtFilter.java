package com.sncr.saw.security.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

public class JwtFilter extends GenericFilterBean {

  private String jwtSecretKey;

  private static final ObjectMapper mapper = new ObjectMapper();

  public JwtFilter(String jwtSecretKey) {
    this.jwtSecretKey = jwtSecretKey;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
      throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) req;
    final HttpServletResponse response = (HttpServletResponse) res;
    if (!("OPTIONS".equals(request.getMethod()))) {
      Ticket ticket = null;

      final String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new ServletException("Missing or invalid Authorization header.");
      }

      final String token = authHeader.substring(7); // The part after
      // "Bearer "
      Claims claims = null;

      try {
        claims =
            Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();
        request.setAttribute("claims", claims);
      } catch (final SignatureException e) {
        throw new ServletException("Invalid token.");
      } catch (MalformedJwtException ex) {
        throw new ServletException("Invalid token");
      } catch (ExpiredJwtException expired) {
        throw new ServletException("token has expired");
      }

      // This checks the validity of the token. logging out does not need
      // the token to be active.
      if (!request.getRequestURI().equals("/saw-security/auth/doLogout")) {
        ticket = mapper.convertValue(claims, Ticket.class);
        if (!ticket.isValid()) {
          response.sendError(401, "Token has expired. Please re-login.");
        }
        if (!ticket.getRoleType().equals(RoleType.ADMIN)){
            response.sendError(401, "You are not authorized to perform this operation.");
        }
      }
    }
    chain.doFilter(req, response);
  }
}

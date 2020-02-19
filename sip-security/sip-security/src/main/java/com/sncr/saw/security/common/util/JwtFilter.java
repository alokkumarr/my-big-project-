package com.sncr.saw.security.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sncr.saw.security.app.service.TicketHelper;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

@Service
public class JwtFilter extends GenericFilterBean {

  private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

  private static String SIP_AUTH = "/sip-security/auth";

  private final String INVALID_TOKEN = "Token is not valid.";
  private final String TOKEN_EXPIRED = "Token has expired. Please re-login.";
  private final String HEADER_ERROR = "Missing or invalid Authorization header.";
  private final String UNAUTHORISED_USER = "You are not authorized to perform this operation.";

  private final String jwtSecretKey;
  private final TicketHelper ticketHelper;
  private static final ObjectMapper mapper = new ObjectMapper();

  public JwtFilter(String jwtSecretKey, TicketHelper ticketHelper) {
    this.jwtSecretKey = jwtSecretKey;
    this.ticketHelper = ticketHelper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) req;
    final HttpServletResponse response = (HttpServletResponse) res;
    boolean haveInValidFlow = false;
    String errorMessage = null;

    if (!("OPTIONS".equals(request.getMethod()))) {
      final String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        haveInValidFlow = true;
        errorMessage = HEADER_ERROR;
      } else {
        final String token = authHeader.substring(7); // The part after Bearer
        Claims claims = null;
        try {
          claims = Jwts.parser()
              .setSigningKey(jwtSecretKey)
              .parseClaimsJws(token)
              .getBody();
          request.setAttribute("claims", claims);
        } catch (final SignatureException e) {
          haveInValidFlow = true;
          errorMessage = TOKEN_EXPIRED;
        } catch (MalformedJwtException ex) {
          haveInValidFlow = true;
          errorMessage = TOKEN_EXPIRED;
        } catch (ExpiredJwtException expired) {
          haveInValidFlow = true;
          errorMessage = TOKEN_EXPIRED;
        }

        // This checks the validity of the token. logging out does not need
        // the token to be active.
        String requestURI = request.getRequestURI();
        logger.trace("Request Header URI : " + requestURI);
        if (!requestURI.equals(SIP_AUTH + "/doLogout")) {
          mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
          Object claimTicket = claims != null ? claims.get("ticket") : null;
          Ticket ticket = mapper.convertValue(claimTicket, Ticket.class);
          if (ticket == null) {
            haveInValidFlow = true;
            errorMessage = INVALID_TOKEN;
          } else if (!ticket.isValid()) {
            haveInValidFlow = true;
            errorMessage = TOKEN_EXPIRED;
          } else if (requestURI.startsWith(SIP_AUTH + "/admin") && !ticket.getRoleType().equals(RoleType.ADMIN)) {
            haveInValidFlow = true;
            errorMessage = UNAUTHORISED_USER;
          } else if (!(ticket.getTicketId() != null && ticketHelper.checkTicketValid(ticket.getTicketId(), ticket.getMasterLoginId()))) {
            haveInValidFlow = true;
            errorMessage = INVALID_TOKEN;
          }
        }
      }
    }
    if (haveInValidFlow) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    } else {
      chain.doFilter(req, response);
    }
  }
}
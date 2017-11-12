package com.sncr.saw.security.common.bean;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.ExpiredJwtException;

public class JwtFilter extends GenericFilterBean {

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		if (!("OPTIONS".equals(request.getMethod()))) {
			Ticket ticket = new Ticket();

			final String authHeader = request.getHeader("Authorization");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				throw new ServletException("Missing or invalid Authorization header.");
			}

			final String token = authHeader.substring(7); // The part after
															// "Bearer "
			Claims claims = null;

			try {
				claims = Jwts.parser().setSigningKey("sncrsaw2").parseClaimsJws(token).getBody();
				request.setAttribute("claims", claims);
			} catch (final SignatureException e) {
				throw new ServletException("Invalid token.");
			} catch (MalformedJwtException ex) {
				throw  new ServletException("Invalid token");
			} catch (ExpiredJwtException expired) {
				throw new ServletException("token has expired");
			}

			// This checks the validity of the token. logging out does not need
			// the token to be active.
			if (!request.getRequestURI().equals("/saw-security/auth/doLogout")) {
				Iterator<?> it = ((Map<String, Object>) claims.get("ticket")).entrySet().iterator();

				while (it.hasNext()) {
					Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
					if (pair.getKey().equals("validUpto")) {
						ticket.setValidUpto(Long.parseLong(pair.getValue().toString()));
					}
					if (pair.getKey().equals("valid")) {
						ticket.setValid(Boolean.parseBoolean(pair.getValue().toString()));
					}

					it.remove();
				}
				if (!ticket.isValid()) {
					response.sendError(401, "Token has expired. Please re-login.");
				}
			}
		}
		chain.doFilter(req, response);
	}

}

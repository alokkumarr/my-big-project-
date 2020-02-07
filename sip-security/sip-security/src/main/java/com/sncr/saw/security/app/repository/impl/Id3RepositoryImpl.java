package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.id3.Id3TokenException;
import com.sncr.saw.security.app.id3.model.AuthorizationCodeDetails;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.Id3Repository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class Id3RepositoryImpl implements Id3Repository {

  @Autowired NSSOProperties nssoProperties;

  private static final Logger logger = LoggerFactory.getLogger(Id3RepositoryImpl.class);

  /**
   * Method to validate the Id3 request to make sure Domain and client_id whitelisted in SIP.
   *
   * @param masterLoginId
   * @param id3DomainName
   * @param clientId
   * @return
   */
  @Override
  public boolean validateId3Request(String masterLoginId, String id3DomainName, String clientId) {
    // FIXME method Implementation.
    return true;
  }

  /**
   * This Method obtains the Authorization Code for Id3 user , domain and client-Id .
   *
   * @param masterLoginId
   * @param id3Request
   * @return
   */
  @Override
  public String obtainAuthorizationCode(
      String masterLoginId,
      Id3AuthenticationRequest id3Request,
      String domainName,
      String clientId) {
    // FIXME:  Return dummy Code until method implementation.
    Map<String, Object> map = new HashMap<>();
    map.put("validUpto", System.currentTimeMillis() + 2 * 60 * 1000);
    map.put("masterLoginId", masterLoginId);
    map.put("domainName", domainName);
    map.put("clientId", clientId);
    map.put("sipTicketId", UUID.randomUUID().toString());
    return Jwts.builder()
        .setSubject(masterLoginId)
        .claim("ticket", map)
        .setIssuedAt(new Date())
        .signWith(SignatureAlgorithm.HS256, nssoProperties.getJwtSecretKey())
        .compact();
  }

  /**
   * Validate the authorization code issued by SIP for authentication after SIP sso redirect.
   *
   * @param authorizationCode
   * @return
   */
  @Override
  public AuthorizationCodeDetails validateAuthorizationCode(
      String authorizationCode, Id3AuthenticationRequest id3AuthenticationRequest) {
    AuthorizationCodeDetails authorizationCodeDetails = new AuthorizationCodeDetails();
    // FIXME : Method implementation
    Claims ssoToken =
        Jwts.parser()
            .setSigningKey(nssoProperties.getJwtSecretKey())
            .parseClaimsJws(authorizationCode)
            .getBody();
    // Check if the code is valid
    Set<Map.Entry<String, Object>> entrySet =
        ((Map<String, Object>) ssoToken.get("ticket")).entrySet();
    boolean validity = false;
    String masterLoginId = null;
    for (Map.Entry<String, Object> pair : entrySet) {
      if (pair.getKey().equals("validUpto")) {
        validity = Long.parseLong(pair.getValue().toString()) > (new Date().getTime());
      }
      if (pair.getKey().equals("masterLoginId")) {
        masterLoginId = pair.getValue().toString();
      }
    }
    if (validity && masterLoginId != null) {
      logger.trace("Successfully validated request for user: " + masterLoginId);
      authorizationCodeDetails.setMasterLoginId(masterLoginId);
      return authorizationCodeDetails;
    }
    logger.info("Authentication failed request for user: " + masterLoginId);
    return authorizationCodeDetails;
  }

  /**
   * This Method will provide the mechanism to on board the ID3 clients in SIP for whitelisting.
   *
   * @param id3Request
   * @return
   */
  @Override
  public boolean onBoardId3client(Id3AuthenticationRequest id3Request) {
    throw new Id3TokenException("onBoardId3client method is not Yet implemented");
  }
}

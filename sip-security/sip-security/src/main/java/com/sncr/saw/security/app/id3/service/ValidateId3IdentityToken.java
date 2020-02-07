package com.sncr.saw.security.app.id3.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sncr.saw.security.app.id3.Id3TokenException;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.Id3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

@Service
public class ValidateId3IdentityToken {

  private final String id3BaseUrl;
  private static final Logger logger = LoggerFactory.getLogger(ValidateId3IdentityToken.class);
  private final Id3Repository id3Repository;

  @Autowired
  private ValidateId3IdentityToken(NSSOProperties nssoProperties, Id3Repository id3Repository) {
    this.id3BaseUrl = nssoProperties.getId3BaseUrl();
    this.id3Repository = id3Repository;
  }

  public String validateToken(String token) {
    String masterLoginId = null;
    String id3DomainName =null;
    String clientId = null;
    try {
      String[] jwtParts = token.split("\\.");
        ObjectMapper objectMapper = new ObjectMapper();
        if (jwtParts.length == 3) {
            String payload = new String(Base64.getDecoder().decode(jwtParts[1]));
            JsonNode decodedToken = objectMapper.readTree(payload);
            String iss = decodedToken.get("iss").asText();
            masterLoginId = decodedToken.get("sub").asText();
            clientId = decodedToken.get("aud").asText();
            id3DomainName = iss.substring(iss.lastIndexOf('/') + 1);
            validateId3Domain(id3DomainName, clientId, masterLoginId);
        }
      URL certUrl =
          new URL(
              String.format(
                  "%s/api/v1/domains/%s/openid-connect/certs",
                  id3BaseUrl, id3DomainName));
      RSAKeyProvider keyProvider = new Id3RsaKeyProvider(certUrl);
      String issuer = String.format("%s/auth/realms/%s", id3BaseUrl, id3DomainName);
      Algorithm algorithm = Algorithm.RSA256(keyProvider);
      JWTVerifier verifier =
          JWT.require(algorithm)
              // validate the appropriate id³ issuer
              .withIssuer(issuer)
              // add more checks here if you like
              .build(); // Reusable verifier instance
      DecodedJWT jwt = verifier.verify(token);
      masterLoginId = jwt.getClaims().get("sub").asString();

    } catch (JWTVerificationException | IOException ex) {
      String errorMessage =
          "Error occurred while verifying JWT with identity token."
              + " signature on the JWT verification failed or Malformed URL.";
      logger.error(String.format("%s %s", errorMessage, ex.getMessage()));
    }
    return masterLoginId;
  }

  private boolean validateId3Domain(
      String id3DomainName, String clientId , String masterLoginId) {
    // Check token domain name and request domain are same to make sure request is not malformed.
    Boolean valid;
    if (id3Repository.validateId3Request(masterLoginId, id3DomainName, clientId )) {
      valid = true;
    } else {
      valid = false;
    }
    return valid;
  }
}

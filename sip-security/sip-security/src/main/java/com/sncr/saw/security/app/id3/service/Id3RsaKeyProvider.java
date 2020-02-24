package com.sncr.saw.security.app.id3.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.sncr.saw.security.app.id3.Id3TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class Id3RsaKeyProvider implements RSAKeyProvider {
  private static final Logger logger = LoggerFactory.getLogger(Id3RsaKeyProvider.class);
  JwkProvider jwkProvider;

  public Id3RsaKeyProvider(URL certUrl) {
    this.jwkProvider = new JwkProviderBuilder(certUrl).cached(true).build();
  }

  @Override
  public RSAPublicKey getPublicKeyById(String kid) {
    try {
      Jwk jwk = jwkProvider.get(kid);
      return (RSAPublicKey) jwk.getPublicKey();
    } catch (JwkException e) {
      String errorMessage =
          "Error occurred while getting Public Key instance with the Id."
              + " Used to verify the signature on the JWT verification stage ";
      logger.error(String.format("%s %s", errorMessage, e.getMessage()));
    }
    return null;
  }

  @Override
  public RSAPrivateKey getPrivateKey() {
    // only needed for signing, not validation
    throw new Id3TokenException(
        "Not needed for the token validation,Method implementation missing ");
  }

  @Override
  public String getPrivateKeyId() {
    // only needed for signing, not validation
    throw new Id3TokenException(
        "Not needed for the token validation,Method implementation missing ");
  }
}

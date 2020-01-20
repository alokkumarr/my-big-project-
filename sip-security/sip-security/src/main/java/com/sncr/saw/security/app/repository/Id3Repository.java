package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.app.id3.Id3TokenException;
import com.sncr.saw.security.app.id3.model.AuthorizationCodeDetails;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;

public interface Id3Repository {

  /**
   * Method to validate the Id3 request to make sure Domain and client_id whitelisted in SIP.
   *
   * @param id3Request
   * @param masterLoginId
   * @return
   */
  boolean validateId3Request(Id3AuthenticationRequest id3Request, String masterLoginId);

  /**
   * This Method obtains the Authorization Code for Id3 user , domain and client-Id.
   *
   * @param masterLoginId
   * @param id3Request
   * @return
   */
  String obtainAuthorizationCode(String masterLoginId, Id3AuthenticationRequest id3Request);

  /**
   * Validate the authorization code issued by SIP for authentication after SIP sso redirect.
   *
   * @param authorizationCode
   * @return
   */
  AuthorizationCodeDetails validateAuthorizationCode(String authorizationCode, Id3AuthenticationRequest id3AuthenticationRequest);

  /**
   * This Method will provide the mechanism to on board the ID3 clients in SIP for whitelisting.
   *
   * @param id3Request
   * @return
   */
  boolean onBoardId3client(Id3AuthenticationRequest id3Request);
}

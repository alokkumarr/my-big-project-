package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.app.id3.model.AuthorizationCodeDetails;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.common.bean.external.response.Id3User;

public interface Id3Repository {

  /**
   * Method to validate the Id3 request to make sure Domain and client_id whitelisted in SIP.
   *
   * @param masterLoginId
   * @param id3DomainName
   * @param clientId
   * @return
   */
  boolean validateId3Request(String masterLoginId, String id3DomainName, String clientId);

  /**
   * This Method obtains the Authorization Code for Id3 user , domain and client-Id.
   *
   * @param authorizationCodeDetails
   * @return
   */
  String obtainAuthorizationCode(AuthorizationCodeDetails authorizationCodeDetails);

  /**
   * Validate the authorization code issued by SIP for authentication after SIP sso redirect.
   *
   * @param authorizationCode
   * @return
   */
  AuthorizationCodeDetails validateAuthorizationCode(
      String authorizationCode, Id3AuthenticationRequest id3AuthenticationRequest);

  /**
   * This Method will provide the mechanism to on board the ID3 clients in SIP for whitelisting.
   *
   * @param id3Request
   * @return
   */
  boolean onBoardId3client(Id3AuthenticationRequest id3Request);

    Id3User getId3Userdetails(String masterLoginId);
}

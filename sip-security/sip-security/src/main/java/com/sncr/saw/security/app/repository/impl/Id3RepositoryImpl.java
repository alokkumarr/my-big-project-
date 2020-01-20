package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.id3.Id3TokenException;
import com.sncr.saw.security.app.id3.model.AuthorizationCodeDetails;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.app.repository.Id3Repository;
import org.springframework.stereotype.Service;

@Service
public class Id3RepositoryImpl implements Id3Repository {

  /**
   * Method to validate the Id3 request to make sure Domain and client_id whitelisted in SIP.
   *
   * @param id3Request
   * @param masterLoginId
   * @return
   */
  @Override
  public boolean validateId3Request(Id3AuthenticationRequest id3Request, String masterLoginId) {
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
  public String obtainAuthorizationCode(String masterLoginId, Id3AuthenticationRequest id3Request) {
    // FIXME:  Return dummy Code until method implementation.
    return "jdhjfdcdbcjdsbfdgfxcnxbcnxbcbdsbfdcxnbsnbvcdsbhdfcsnxbnbhvbsdhgfhdbcnbncbsbchsdgvhcgd";
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
    // FIXME : Method implementation
    AuthorizationCodeDetails authorizationCodeDetails = new AuthorizationCodeDetails();
    authorizationCodeDetails.setMasterLoginId("sawadmin@synchronoss.com");
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
    // ToDo Method implementation
    throw new Id3TokenException("onBoardId3client method is not Yet implemented");
  }
}

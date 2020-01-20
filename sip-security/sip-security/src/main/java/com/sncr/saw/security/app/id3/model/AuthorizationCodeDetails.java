package com.sncr.saw.security.app.id3.model;

/**
 * This class contains authorization code details issued using ID3 Identity token to provide the SSO
 * login in SIP.
 */
public class AuthorizationCodeDetails {

  /** Unique Sip ticket Id to validate the one time use of the authorization code. */
  private String sipTicketId;
  /** SIP customer code associated with authorization code. */
  private String customerCode;
  /** SIP master login Id for authorization code issued. */
  private String masterLoginId;
  /** Id3 Client associated with authorization code. */
  private String id3ClientId;

  /**
   * Get the Sip Ticket ID (Unique identifier for authorization code) .
   *
   * @return String
   */
  public String getSipTicketId() {
    return sipTicketId;
  }

  /**
   * * Set the Sip Ticket ID (Unique identifier for authorization code) .
   *
   * @param sipTicketId
   */
  public void setSipTicketId(String sipTicketId) {
    this.sipTicketId = sipTicketId;
  }

  /**
   * Get SIP Customer code associated with authorization code.
   *
   * @return String
   */
  public String getCustomerCode() {
    return customerCode;
  }

  /**
   * Set SIP Customer code associated with authorization code.
   *
   * @param customerCode
   */
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  /**
   * Get SIP Master login id associated with authorization code.
   *
   * @return
   */
  public String getMasterLoginId() {
    return masterLoginId;
  }

  /**
   * Set SIP Master login id associated with authorization code.
   *
   * @param masterLoginId
   */
  public void setMasterLoginId(String masterLoginId) {
    this.masterLoginId = masterLoginId;
  }

  /**
   * Get Id3 client id associated with authorization code.
   *
   * @return String
   */
  public String getId3ClientId() {
    return id3ClientId;
  }

  /**
   * set Id3 client id associated with authorization code.
   *
   * @param id3ClientId
   */
  public void setId3ClientId(String id3ClientId) {
    this.id3ClientId = id3ClientId;
  }
}

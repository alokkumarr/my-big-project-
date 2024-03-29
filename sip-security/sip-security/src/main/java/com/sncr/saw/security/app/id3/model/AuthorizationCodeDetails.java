package com.sncr.saw.security.app.id3.model;

import java.io.Serializable;

/**
 * This class contains authorization code details issued using ID3 Identity token to provide the SSO
 * login in SIP.
 */
public class AuthorizationCodeDetails implements Serializable {

  /** unique Id for ticket Details; */
  private long ticketDetailsId;
  /** Unique Sip ticket Id to validate the one time use of the authorization code. */
  private String sipTicketId;
  /** SIP customer code associated with authorization code. */
  private String customerCode;
  /** SIP master login Id for authorization code issued. */
  private String masterLoginId;
  /** Id3 Client associated with authorization code. */
  private String id3ClientId;
  /** Id3 Domain name. */
  private String id3DomainName;
  /** Validity of authorization code. */
  private long validUpto;
  /** to check authorization code is valid */
  private boolean valid;

  /**
   * get the ticket details id.
   *
   * @return long
   */
  public long getTicketDetailsId() {
    return ticketDetailsId;
  }

  /**
   * get the ticket details id.
   *
   * @param ticketDetailsId Ticket details ID.
   */
  public void setTicketDetailsId(long ticketDetailsId) {
    this.ticketDetailsId = ticketDetailsId;
  }

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
   * @param sipTicketId SIP Ticket ID .
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
   * @param customerCode Customer Code.
   */
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  /**
   * Get SIP Master login id associated with authorization code.
   *
   * @return String
   */
  public String getMasterLoginId() {
    return masterLoginId;
  }

  /**
   * Set SIP Master login id associated with authorization code.
   *
   * @param masterLoginId Master Login ID.
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
   * @param id3ClientId Id3 Client Id.
   */
  public void setId3ClientId(String id3ClientId) {
    this.id3ClientId = id3ClientId;
  }

  /**
   * Get the Domain Name for Id3 client .
   *
   * @return String
   */
  public String getId3DomainName() {
    return id3DomainName;
  }

  /**
   * set the Domain Name for Id3 client .
   *
   * @param id3DomainName Id3 Domain Name.
   */
  public void setId3DomainName(String id3DomainName) {
    this.id3DomainName = id3DomainName;
  }

  /**
   * Get the authorization code validity time.
   *
   * @return Long.
   */
  public long getValidUpto() {
    return validUpto;
  }

  /**
   * set the authorization code validity time.
   *
   * @param validUpto Token Valid till the time .
   */
  public void setValidUpto(long validUpto) {
    this.validUpto = validUpto;
  }

  /**
   * check authorization code is valid.
   *
   * @return Boolean is token valid.
   */
  public boolean isValid() {
    return valid;
  }
  /**
   * set authorization code validity.
   */
  public void setValid(boolean valid) {
    this.valid = valid;
  }
}

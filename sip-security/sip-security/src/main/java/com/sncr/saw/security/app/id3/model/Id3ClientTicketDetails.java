package com.sncr.saw.security.app.id3.model;

public class Id3ClientTicketDetails extends Id3ClientDetails {
  private String sipTicketId;
  private boolean validIndicator;

  public String getSipTicketId() {
    return sipTicketId;
  }

  public void setSipTicketId(String sipTicketId) {
    this.sipTicketId = sipTicketId;
  }

  public boolean isValidIndicator() {
    return validIndicator;
  }

  public void setValidIndicator(boolean validIndicator) {
    this.validIndicator = validIndicator;
  }
}

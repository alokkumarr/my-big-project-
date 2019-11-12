package com.synchronoss.bda.sip.jwt.token;

import java.io.Serializable;
import java.util.List;

/**
 * Model class of data security key.
 *
 * @author alok.kumarr
 * @since 3.4.0
 */
public class DataSecurityKeys implements Serializable {

  private static final long serialVersionUID = 7546190895561288031L;

  private String message;
  private List<TicketDSKDetails> dataSecurityKeys;

  /**
   * Gets message.
   *
   * @return value of message
   */
  public String getMessage() {
    return message;
  }

  /** Sets value of message.  */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets dataSecurityKeys.
   *
   * @return value of dataSecurityKeys
   */
  public List<TicketDSKDetails> getDataSecurityKeys() {
    return dataSecurityKeys;
  }

  /** Sets value of dataSecurityKeys.  */
  public void setDataSecurityKeys(List<TicketDSKDetails> dataSecurityKeys) {
    this.dataSecurityKeys = dataSecurityKeys;
  }
}

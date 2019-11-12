package com.synchronoss.bda.sip.jwt.token;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author alok.kumarr
 * @since 3.4.0
 *
 */
public class DataSecurityKeys implements Serializable {

  private static final long serialVersionUID = 7546190895561288031L;

  private List<TicketDSKDetails> dataSecurityKeys;

  public List<TicketDSKDetails> getDataSecurityKeys() {
    return dataSecurityKeys;
  }

  public void setDataSecurityKeys(List<TicketDSKDetails> dataSecurityKeys) {
    this.dataSecurityKeys = dataSecurityKeys;
  }
}

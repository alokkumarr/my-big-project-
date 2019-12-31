package com.sncr.saw.security.common.bean.external.response;

import java.io.Serializable;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class Privilege implements Serializable {
  private static final long serialVersionUID = 6816103312586589548L;

  private Long privilegeId;
  private String privilegeDesc;

  public Long getPrivilegeId() {
    return privilegeId;
  }

  public void setPrivilegeId(Long privilegeId) {
    this.privilegeId = privilegeId;
  }

  public String getPrivilegeDesc() {
    return privilegeDesc;
  }

  public void setPrivilegeDesc(String privilegeDesc) {
    this.privilegeDesc = privilegeDesc;
  }

}

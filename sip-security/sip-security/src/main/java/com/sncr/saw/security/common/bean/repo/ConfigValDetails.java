package com.sncr.saw.security.common.bean.repo;

import java.io.Serializable;

/**
 * @author pras0004
 * @since 3.5.0
 */
public class ConfigValDetails implements Serializable {
  private static final long serialVersionUID = 8604685133559470661L;

  private long configValSysId;
  private String configValCode;
  private String configValDesc;
  private String configValObjType;
  private String configValObjGroup;
  private int activeStatusInd;
  private String createdDate;
  private String createdBy;
  private int filterByCustCode;

  public long getConfigValSysId() {
    return configValSysId;
  }

  public void setConfigValSysId(long configValSysId) {
    this.configValSysId = configValSysId;
  }

  public String getConfigValCode() {
    return configValCode;
  }

  public void setConfigValCode(String configValCode) {
    this.configValCode = configValCode;
  }

  public String getConfigValDesc() {
    return configValDesc;
  }

  public void setConfigValDesc(String configValDesc) {
    this.configValDesc = configValDesc;
  }

  public String getConfigValObjType() {
    return configValObjType;
  }

  public void setConfigValObjType(String configValObjType) {
    this.configValObjType = configValObjType;
  }

  public String getConfigValObjGroup() {
    return configValObjGroup;
  }

  public void setConfigValObjGroup(String configValObjGroup) {
    this.configValObjGroup = configValObjGroup;
  }

  public int getActiveStatusInd() {
    return activeStatusInd;
  }

  public void setActiveStatusInd(int activeStatusInd) {
    this.activeStatusInd = activeStatusInd;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public int getFilterByCustCode() {
    return filterByCustCode;
  }

  public void setFilterByCustCode(int filterByCustCode) {
    this.filterByCustCode = filterByCustCode;
  }
}

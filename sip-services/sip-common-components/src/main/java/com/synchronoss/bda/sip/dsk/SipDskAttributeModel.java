package com.synchronoss.bda.sip.dsk;

import java.util.List;

public class SipDskAttributeModel {
  private String dskAttributeSysId;
  private Long secGroupSysId;
  private String dskAttributeParentId;
  private String booleanCriteria;
  private String columnName;
  private String operator;
  private List<String> values;

  public String getDskAttributeSysId() {
    return dskAttributeSysId;
  }

  public void setDskAttributeSysId(String dskAttributeSysId) {
    this.dskAttributeSysId = dskAttributeSysId;
  }

  public Long getSecGroupSysId() {
    return secGroupSysId;
  }

  public void setSecGroupSysId(Long secGroupSysId) {
    this.secGroupSysId = secGroupSysId;
  }

  public String getDskAttributeParentId() {
    return dskAttributeParentId;
  }

  public void setDskAttributeParentId(String dskAttributeParentId) {
    this.dskAttributeParentId = dskAttributeParentId;
  }

  public String getBooleanCriteria() {
    return booleanCriteria;
  }

  public void setBooleanCriteria(String booleanCriteria) {
    this.booleanCriteria = booleanCriteria;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }
}

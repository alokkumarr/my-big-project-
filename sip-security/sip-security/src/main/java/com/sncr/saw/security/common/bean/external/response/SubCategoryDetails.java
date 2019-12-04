package com.sncr.saw.security.common.bean.external.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class SubCategoryDetails implements Serializable {

  private static final long serialVersionUID = -6682525829922872306L;

  private long subCategoryId;
  private String subCategoryName;
  private String subCategoryDesc;
  private Long activeStatusInd;
  private List<String> privilege;

  public long getSubCategoryId() {
    return subCategoryId;
  }

  public void setSubCategoryId(long subCategoryId) {
    this.subCategoryId = subCategoryId;
  }

  public String getSubCategoryName() {
    return subCategoryName;
  }

  public void setSubCategoryName(String subCategoryName) {
    this.subCategoryName = subCategoryName;
  }

  public String getSubCategoryDesc() {
    return subCategoryDesc;
  }

  public void setSubCategoryDesc(String subCategoryDesc) {
    this.subCategoryDesc = subCategoryDesc;
  }

  public Long getActiveStatusInd() {
    return activeStatusInd;
  }

  public void setActiveStatusInd(Long activeStatusInd) {
    this.activeStatusInd = activeStatusInd;
  }

  public List<String> getPrivilege() {
    return privilege;
  }

  public void setPrivilege(List<String> privilege) {
    this.privilege = privilege;
  }
}

package com.synchronoss.bda.sip.jwt.token;

import java.io.Serializable;
import java.util.ArrayList;

public class Products implements Serializable {

  private static final long serialVersionUID = -7208115566251565952L;

  private String productName;
  private String productDesc;
  private String productCode;
  private String productID;
  private long privilegeCode;
  private ArrayList<ProductModules> productModules;

  public String getProductID() {
    return productID;
  }

  public void setProductID(String productID) {
    this.productID = productID;
  }

  public long getPrivilegeCode() {
    return privilegeCode;
  }

  public void setPrivilegeCode(long privilegeCode) {
    this.privilegeCode = privilegeCode;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductDesc() {
    return productDesc;
  }

  public void setProductDesc(String productDesc) {
    this.productDesc = productDesc;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public ArrayList<ProductModules> getProductModules() {
    return productModules;
  }

  public void setProductModules(ArrayList<ProductModules> productModules) {
    this.productModules = productModules;
  }
}

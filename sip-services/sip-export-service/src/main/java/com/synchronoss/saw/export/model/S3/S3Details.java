package com.synchronoss.saw.export.model.S3;

public class S3Details {
  private String customerCode;
  private String alias;
  private String bucketName;
  private String region;
  private String accessKey;
  private String secretKey;
  private String cannedAcl;
  private String outputLocation;
  private String type;

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public String getCannedAcl() {
    return cannedAcl;
  }

  public void setCannedAcl(String cannedAcl) {
    this.cannedAcl = cannedAcl;
  }

  public String getOutputLocation() {
    return outputLocation;
  }

  public void setOutputLocation(String outputLocation) {
    this.outputLocation = outputLocation;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

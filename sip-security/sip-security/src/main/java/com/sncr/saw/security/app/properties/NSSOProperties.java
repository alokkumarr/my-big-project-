package com.sncr.saw.security.app.properties;

import com.synchronoss.sip.utils.Ccode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for new authentication component
 *
 * @author girija.sankar
 */
@Configuration
// @RefreshScope
public class NSSOProperties {

  @Value("${ticket.validity.mins}")
  private String validityMins;

  @Value("${ticket.rToken.validity.mins}")
  private String refreshTokenValidityMins;

  @Value("${mail.host}")
  private String mailHost;

  @Value("${mail.port}")
  private int mailPort;

  @Value("${mail.from}")
  private String mailFrom;

  @Value("${mail.subject}")
  private String mailSubject;

  @Value("${mail.username}")
  private String mailUserName;

  @Value("${mail.password}")
  private byte[] mailPassword;

  @Value("${sso.secret.key}")
  private String ssoSecretKey;

  @Value("${jwt.secret.key}")
  private String jwtSecretKey;

  @Value("${encryption.key}")
  private String encryptionKey;

  private byte []encryptionKeyBytes;

  @Value("${user.lockingTime.mins}")
  private int lockingTime;

  @Value("${user.maxUnsuccessfulLoginAttemptAllowed}")
  private int maxInvalidPwdLimit;

  @Value("${id3.base.url}")
  private String id3BaseUrl;

  public String getRefreshTokenValidityMins() {
    return refreshTokenValidityMins;
  }

  public void setRefreshTokenValidityMins(String refreshTokenValidityMins) {
    this.refreshTokenValidityMins = refreshTokenValidityMins;
  }

  /** @return the validityMins */
  public String getValidityMins() {
    return validityMins;
  }

  /** @param validityMins the validityMins to set */
  public void setValidityMins(String validityMins) {
    this.validityMins = validityMins;
  }

  /** @return the mailHost */
  public String getMailHost() {
    return mailHost;
  }

  /** @param mailHost the mailHost to set */
  public void setMailHost(String mailHost) {
    this.mailHost = mailHost;
  }

  /** @return the mailPort */
  public int getMailPort() {
    return mailPort;
  }

  /** @param mailPort the mailPort to set */
  public void setMailPort(int mailPort) {
    this.mailPort = mailPort;
  }

  /**
   * @return the mailFrom
   */
  public String getMailFrom() {
    return mailFrom;
  }

  /**
   * @param mailFrom the mailFrom to set
   */
  public void setMailFrom(String mailFrom) {
    this.mailFrom = mailFrom;
  }

  /**
   * @return the mailSubject
   */
  public String getMailSubject() {
    return mailSubject;
  }

  /**
   * @param mailSubject the mailSubject to set
   */
  public void setMailSubject(String mailSubject) {
    this.mailSubject = mailSubject;
  }

  public String getMailUserName() {
    return mailUserName;
  }

  public void setMailUserName(String mailUserName) {
    this.mailUserName = mailUserName;
  }

  public byte[] getMailPassword() {
    return mailPassword;
  }

  public void setMailPassword(byte[] mailPassword) {
    this.mailPassword = mailPassword;
  }

  /**
   * Gets ssoSecretKey
   *
   * @return value of ssoSecretKey
   */
  public String getSsoSecretKey() {
    return ssoSecretKey;
  }

  /**
   * Sets ssoSecretKey
   */
  public void setSsoSecretKey(String ssoSecretKey) {
    this.ssoSecretKey = ssoSecretKey;
  }

  /**
   * Gets jwtSecretKey
   *
   * @return value of jwtSecretKey
   */
  public String getJwtSecretKey() {

    if (encryptionKeyBytes == null) {
      encryptionKeyBytes = Ccode.convertHexStringToByteArray(encryptionKey);
    }

    return Ccode.cencode(jwtSecretKey, encryptionKeyBytes);
  }

  /** Sets jwtSecretKey */
  public void setJwtSecretKey(String jwtSecretKey) {
    this.jwtSecretKey = jwtSecretKey;
  }

  /** Get encryption key */
  public String getEncryptionKey() {
    return encryptionKey;
  }

  /** Sets encryption key */
  public void setEncryptionKey(String encryptionKey) {
    this.encryptionKey = encryptionKey;

    if (encryptionKeyBytes == null) {
      encryptionKeyBytes = Ccode.convertHexStringToByteArray(encryptionKey);
    }
  }

  /** Get encryptionKeyBytes */
  public byte[] getEncryptionKeyBytes() {
    return encryptionKeyBytes;
  }

  /** Gets Account LockTime in mins when maximum invalid limit reaches */
  public int getLockingTime() {
    return lockingTime;
  }

  /** Sets Account LockTime in mins when maximum invalid limit reaches */
  public void setLockingTime(int lockingTime) {
    this.lockingTime = lockingTime;
  }

  /** Gets Max number of attempts the user can retry */
  public int getMaxInvalidPwdLimit() {
    return maxInvalidPwdLimit;
  }

  /** Sets Max number of attempts the user can retry */
  public void setMaxInvalidPwdLimit(int maxInvalidPwdLimit) {
    this.maxInvalidPwdLimit = maxInvalidPwdLimit;
  }

  /**
   * Get the id3 base url.
   * @return string */
  public String getId3BaseUrl() {
    return id3BaseUrl;
  }

  /**
   * set the id3 base url.
   *  @param id3BaseUrl */
  public void setId3BaseUrl(String id3BaseUrl) {
    this.id3BaseUrl = id3BaseUrl;
  }
}

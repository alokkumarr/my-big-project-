package com.synchronoss.saw.batch.sftp.integration;

import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

public class SftpPayloadProcessor {
 
  private BisConnectionTestPayload payload;
  private DefaultSftpSessionFactory sessionFactoryPayload;

  public BisConnectionTestPayload getPayload() {
    return payload;
  }
  
  public void setPayload(BisConnectionTestPayload payload) {
    this.payload = payload;
  }
  
  public DefaultSftpSessionFactory getSessionFactoryPayload() {
    return sessionFactoryPayload;
  }
  
  public void setSessionFactoryPayload(DefaultSftpSessionFactory sessionFactoryPayload) {
    this.sessionFactoryPayload = sessionFactoryPayload;
  }
}

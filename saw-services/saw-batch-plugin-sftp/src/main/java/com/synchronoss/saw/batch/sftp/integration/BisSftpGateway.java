package com.synchronoss.saw.batch.sftp.integration;

import java.util.List;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.sftp.session.SftpFileInfo;
import org.springframework.messaging.Message;

@MessagingGateway
public interface BisSftpGateway {

  @Gateway(requestChannel = "transferChannel", replyTimeout = 120000,requestTimeout = 120000)
  Message<?> transferFiles(Message<?> messagePayload);
  
  @Gateway(requestChannel = "requestSftpFiles", replyTimeout = 120000,requestTimeout = 120000)
  List<SftpFileInfo> lsFiles(String directory); 

}

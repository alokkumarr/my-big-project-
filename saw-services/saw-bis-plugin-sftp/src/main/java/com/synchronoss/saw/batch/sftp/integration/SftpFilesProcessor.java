package com.synchronoss.saw.batch.sftp.integration;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;

@MessageEndpoint
public class SftpFilesProcessor {

  /**
   * This is service activator to transfer files.
   */
  @ServiceActivator(inputChannel = "transferChannel")
  public void transferFiles(SftpPayloadProcessor processor) {
    ChainFileListFilter<LsEntry> list = new ChainFileListFilter<LsEntry>();
    SipSftpFilter sftpFilter = new SipSftpFilter();
    sftpFilter.setFilePatterns(processor.getPayload().getPattern());
    list.addFilter(sftpFilter);
    SipFileFilterOnLastModifiedTime  lastModifiedTime = 
              new SipFileFilterOnLastModifiedTime();
    lastModifiedTime.setTimeDifference(10000L);
    list.addFilter(lastModifiedTime);
    SftpOutboundGateway sftpOutboundGateway = new SftpOutboundGateway(
        processor.getSessionFactoryPayload(),"mget", processor.getPayload().getPattern());
    sftpOutboundGateway.setAutoCreateLocalDirectory(true);
    sftpOutboundGateway.setLoggingEnabled(true);
    sftpOutboundGateway.setRequiresReply(true);
    sftpOutboundGateway.setSendTimeout(120000);
    sftpOutboundGateway.setFilter(list);
    sftpOutboundGateway.setLocalDirectory(new File(processor.getPayload().getDestinationLocation() 
              + File.pathSeparator + getBatchId() + File.pathSeparator));
    sftpOutboundGateway.setLocalFilenameGeneratorExpressionString(
          "T(org.apache.commons.io.FilenameUtils).getBaseName"
              + "(#remoteFileName)+'.'+ new java.text.SimpleDateFormat("
              + "T(razorsight.mito.integration.IntegrationUtils)"
              + ".getRenameDateFormat()).format(new java.util.Date()) "
              + "+'.'+T(org.apache.commons.io.FilenameUtils).getExtension(#remoteFileName)");
    
  }
  
  protected String getBatchId() {
    DateFormat dtFormat = new SimpleDateFormat("MMddyyyyHHmmss");
    Date currentDate = Calendar.getInstance().getTime();        
    return dtFormat.format(currentDate);
  }

}

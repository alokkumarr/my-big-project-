package com.synchronoss.saw.batch.plugin;

import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.plugin.service.S3ServiceImpl;
import com.synchronoss.saw.batch.plugin.service.SftpServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SipIngestionPluginFactory {

  @Autowired
  private SftpServiceImpl sftpServiceImpl;

  @Autowired
  S3ServiceImpl s3ServiceImpl;

  private static final Logger logger = LoggerFactory.getLogger(SipIngestionPluginFactory.class);

  /**
   * Retrive instance based on ingestion type.
   * 
   * @param ingestionType channel type
   * @return pluginContract
   */
  public SipPluginContract getInstance(String ingestionType) {

    SipPluginContract sipConnectionService = null;

    if (ingestionType.toUpperCase().equals(ChannelType.SFTP.name())) {
      sipConnectionService = this.sftpServiceImpl;
    } else if (ingestionType.equals(ChannelType.S3.name())) {
      sipConnectionService = this.s3ServiceImpl;
    }

    return sipConnectionService;

  }

}

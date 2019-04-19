package com.synchronoss.saw.batch.plugin;

import com.synchronoss.saw.batch.extensions.SipRetryContract;
import com.synchronoss.saw.batch.plugin.service.S3RetryServiceImpl;
import com.synchronoss.saw.batch.plugin.service.SftpRetryServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SipRetryPluginFactory {
  @Autowired
  private SftpRetryServiceImpl sftpRetryServiceImpl;

  @Autowired
  S3RetryServiceImpl s3RetryServiceImpl;

  private static final Logger logger = LoggerFactory
      .getLogger(SipIngestionPluginFactory.class);

  /**
   * Retrive instance based on ingestion type.
   * 
   * @param ingestionType channel type
   * @return pluginContract
   */
  public SipRetryContract getInstance(String ingestionType) {

    SipRetryContract sipRetryService = null;

    if (ingestionType.toUpperCase().equals(ChannelType.SFTP.name())) {
      sipRetryService = this.sftpRetryServiceImpl;
    } else if (ingestionType.equals(ChannelType.S3.name())) {
      sipRetryService = this.s3RetryServiceImpl;
    }

    return sipRetryService;

  }

}

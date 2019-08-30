package com.synchronoss.saw.batch.plugin;

import com.synchronoss.saw.apipull.plugin.service.ApiPullServiceImpl;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisChannelType;
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

  @Autowired
  private ApiPullServiceImpl apiPullService;

  private static final Logger logger = LoggerFactory.getLogger(SipIngestionPluginFactory.class);

  /**
   * Retrive instance based on ingestion type.
   * 
   * @param ingestionType channel type
   * @return pluginContract
   */
  public SipPluginContract getInstance(String ingestionType) {

    SipPluginContract sipConnectionService = null;

    if (ingestionType.equals(BisChannelType.SFTP.value())) {
      sipConnectionService = this.sftpServiceImpl;
    } else if (ingestionType.equals(BisChannelType.S3.value())) {
      sipConnectionService = this.s3ServiceImpl;
    } else if (ingestionType.equals(BisChannelType.APIPULL.value())) {
      sipConnectionService = this.apiPullService;
    }

    return sipConnectionService;

  }

}

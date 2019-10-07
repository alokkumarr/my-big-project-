package com.synchronoss.saw.batch.plugin;

import com.synchronoss.saw.apipull.plugin.service.ApiPullRetryServiceImpl;
import com.synchronoss.saw.batch.extensions.SipRetryContract;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.plugin.service.S3RetryServiceImpl;
import com.synchronoss.saw.batch.plugin.service.SftpRetryServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SipRetryPluginFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SipIngestionPluginFactory.class);

  @Autowired
  private S3RetryServiceImpl s3RetryServiceImpl;
  @Autowired
  private SftpRetryServiceImpl sftpRetryServiceImpl;
  @Autowired
  private ApiPullRetryServiceImpl apiPullRetryService;

  /**
   * Retrieve instance based on ingestion type.
   *
   * @param ingestionType channel type
   * @return pluginContract
   */
  public SipRetryContract getInstance(String ingestionType) {

    SipRetryContract sipRetryService = null;
    switch (BisChannelType.fromValue(ingestionType)) {
      case SFTP:
        sipRetryService = this.sftpRetryServiceImpl;
        break;
      case S3:
        sipRetryService = this.s3RetryServiceImpl;
        break;
      case APIPULL:
        sipRetryService = this.apiPullRetryService;
        break;
    }

    LOGGER.trace("Instance of retry process : {}", sipRetryService.getClass());
    return sipRetryService;
  }
}

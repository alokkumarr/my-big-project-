package com.synchronoss.saw.batch.sftp.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageHandler;


@EnableIntegration
@Configuration
class SftpConfiguration {

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Bean
  DelegatingSessionFactory delegatingSessionFactory(
      RuntimeSessionFactoryLocator runtimeSessionFactoryLocator) {
    return new DelegatingSessionFactory(runtimeSessionFactoryLocator);
  }

  @Bean
  public MessageHandler dynamicSftpLoggingChannel() {
    LoggingHandler loggingHandler =  new LoggingHandler(LoggingHandler.Level.TRACE.name());
    loggingHandler.setLoggerName("dynamicSftpLoggingChannel");
    loggingHandler.setShouldLogFullMessage(true);
    return loggingHandler;
  }
  
}
package com.synchronoss.saw;

import info.faljse.SDNotify.SDNotify;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@ComponentScan("com.synchronoss.saw")
@EnableAsync
public class SipMetadataServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SipMetadataServiceApplication.class);

  /**
   * Start sip-semantic-service.
   *
   * @param args Arguments
   */
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SipMetadataServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");
  }

  /**
   * Task Executors .
   *
   * @return TaskExecutor
   */
  @Bean(name = "workExecutor")
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setQueueCapacity(10);
    taskExecutor.afterPropertiesSet();
    return taskExecutor;
  }

  /**
   *  tomcatEmbedded.
   * @return TomcatServletWebServerFactory
   * */
  @Bean
  public TomcatServletWebServerFactory tomcatEmbedded() {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
    tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
      if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
        ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
      }
    });
    return tomcat;
  }

  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOG.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();
  }
}

package com.synchronoss.saw.semantic;

import info.faljse.SDNotify.SDNotify;

import org.apache.coyote.http11.AbstractHttp11Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ComponentScan("com.synchronoss")
public class SAWSemanticServiceApplication {
  private static final Logger LOG = LoggerFactory.getLogger(SAWSemanticServiceApplication.class);
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWSemanticServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

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

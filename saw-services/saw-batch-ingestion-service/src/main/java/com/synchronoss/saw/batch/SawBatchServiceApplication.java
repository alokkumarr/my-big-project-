package com.synchronoss.saw.batch;

import info.faljse.SDNotify.SDNotify;
import javax.persistence.EntityManagerFactory;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaAuditing
@EnableIntegration
@EnableAsync
@EnableRetry
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.synchronoss.saw.batch"})
public class SawBatchServiceApplication {
  private static final Logger LOG = LoggerFactory.getLogger(SawBatchServiceApplication.class);

  @Autowired
  private Environment environment;

  /**
   * This is the entry method of the class.
   */
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SawBatchServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

  /**
   * TomcatServletWebServerFactory has been overridden.
   */

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

  @EventListener
  public void exitEvent(ExitCodeEvent event) {
    LOG.info("Application exiting : " + event.getExitCode());

  }

  /**
   * Initializing Transaction Manager.
   * 
   * @param entityManagerFactory entity manager.
   * @return {@link JpaTransactionManager}.
   */

  @Bean
  JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }

  /**
   * Initializing RetryTemplate.
   * @return {@link RetryTemplate}.
   */
  @Bean
  public RetryTemplate retryTemplate() {
    int maxAttempt = Integer.parseInt(environment.getProperty("sip.service.max.attempts"));
    int retryTimeInterval = Integer.parseInt(environment.getProperty("sip.service.retry.delay"));
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(maxAttempt);
    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(retryTimeInterval); // 1.5 seconds
    RetryTemplate template = new RetryTemplate();
    template.setRetryPolicy(retryPolicy);
    template.setBackOffPolicy(backOffPolicy);
    return template;
  }


}

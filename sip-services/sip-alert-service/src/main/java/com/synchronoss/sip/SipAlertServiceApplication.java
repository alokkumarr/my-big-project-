package com.synchronoss.sip;

import com.synchronoss.sip.alert.service.evaluator.EvaluatorListener;
import info.faljse.SDNotify.SDNotify;
import javax.validation.constraints.NotNull;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
@EnableAsync
@ComponentScan(basePackages = {"com.synchronoss.sip", "com.synchronoss.sip.utils"})
public class SipAlertServiceApplication {

  private static final Logger logger = LoggerFactory.getLogger(SipAlertServiceApplication.class);

  @Value("${sip.service.taskExecutor.maxPoolSize}")
  @NotNull
  private Integer maximumPoolSize;

  @Value("${sip.service.taskExecutor.queueCapacity}")
  @NotNull
  private Integer queueCapacity;

  /**
   * Start sip-semantic-service.
   *
   * @param args Arguments
   */
  public static void main(String[] args) {
    ConfigurableApplicationContext context =
        SpringApplication.run(SipAlertServiceApplication.class, args);
    logger.info(context.getApplicationName() + " has started.");
    Runnable r =
        () -> {
          try {
            EvaluatorListener evaluatorListener = context.getBean(EvaluatorListener.class);
            evaluatorListener.runStreamConsumer();
          } catch (Exception e) {
            logger.error("Error occurred while running the stream consumer : " + e);
          }
        };
    new Thread(r).start();
    logger.info(context.getApplicationName() + " started the StreamConsumer thread ");
  }

  /**
   * Task Executors .
   *
   * @return TaskExecutor
   */
  @Bean(name = "workExecutor")
  public TaskExecutor asyncExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setMaxPoolSize(maximumPoolSize);
    taskExecutor.setQueueCapacity(queueCapacity);
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

  /**
   * Listener.
   *  @param event event */
  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
    logger.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();
  }
}

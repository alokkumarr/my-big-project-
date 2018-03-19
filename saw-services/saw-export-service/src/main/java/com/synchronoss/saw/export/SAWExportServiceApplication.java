package com.synchronoss.saw.export;

import info.faljse.SDNotify.SDNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;



@SpringBootApplication
@ComponentScan("com.synchronoss")
@EnableAsync
public class SAWExportServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SAWExportServiceApplication.class);

  @Bean(name="workExecutor")
  public TaskExecutor taskExecutor() {
      ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
      taskExecutor.setMaxPoolSize(10);
      taskExecutor.setQueueCapacity(10);
      taskExecutor.afterPropertiesSet();
      return taskExecutor;
  }
  
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWExportServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOG.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();
  }
}

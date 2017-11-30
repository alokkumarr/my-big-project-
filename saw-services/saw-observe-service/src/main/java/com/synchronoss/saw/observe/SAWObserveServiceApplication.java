package com.synchronoss.saw.observe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;



@SpringBootApplication
@ComponentScan("com.synchronoss")
@EnableAsync
public class SAWObserveServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SAWObserveServiceApplication.class);

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
        SpringApplication.run(SAWObserveServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }
}

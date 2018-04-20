package com.synchronoss.saw.storage.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@ComponentScan("com.synchronoss")
public class SAWStorageServiceApplication {
  private static final Logger LOG = LoggerFactory.getLogger(SAWStorageServiceApplication.class);
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWStorageServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }
}

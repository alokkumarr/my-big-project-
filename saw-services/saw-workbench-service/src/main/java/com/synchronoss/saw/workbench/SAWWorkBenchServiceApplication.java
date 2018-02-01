package com.synchronoss.saw.workbench;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@ComponentScan("com.synchronoss")
public class SAWWorkBenchServiceApplication {
  private static final Logger LOG = LoggerFactory.getLogger(SAWWorkBenchServiceApplication.class);
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWWorkBenchServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }
}

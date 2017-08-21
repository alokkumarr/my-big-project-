package com.synchronoss.saw.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
public class SAWCompositeServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SAWCompositeServiceApplication.class);

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }


  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWCompositeServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

}

package com.synchronoss.saw.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;



@SpringBootApplication
public class SAWGatewayServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SAWGatewayServiceApplication.class);

  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }


  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWGatewayServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

}

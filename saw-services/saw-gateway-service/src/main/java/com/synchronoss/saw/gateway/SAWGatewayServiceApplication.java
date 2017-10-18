package com.synchronoss.saw.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;



@SpringBootApplication
public class SAWGatewayServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SAWGatewayServiceApplication.class);
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWGatewayServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

}

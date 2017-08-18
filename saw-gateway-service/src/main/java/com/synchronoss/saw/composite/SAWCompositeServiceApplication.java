package com.synchronoss.saw.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;



// TODO : Enable Edge Sever with Zuul
// TODO : Enable Feign client later once base service is ready, the request shall
// hostname:port/serviceId/actualContextPath
@SpringBootApplication
// @EnableZuulProxy
// @EnableFeignClients
// @EnableCircuitBreaker
// @EnableDiscoveryClient
public class SAWCompositeServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SAWCompositeServiceApplication.class);

  @Bean
  // @LoadBalanced
  RestTemplate restTemplate() {
    return new RestTemplate();
  }


  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWCompositeServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

}

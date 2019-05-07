package com.sncr.saw.security.app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

// package scanning is necessary because we are
// having module dependency
@SpringBootApplication(scanBasePackages =
    {"com.sncr.saw.security.app"})
public class SpringAdminApp {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(SpringAdminApp.class, args);
  }

}

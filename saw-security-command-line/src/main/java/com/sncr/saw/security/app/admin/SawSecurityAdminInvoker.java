package com.sncr.saw.security.app.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SawSecurityAdminInvoker {
  @Autowired
  public static SawSecurityShell s1;

  public static void main(String[] args) {
    ConfigurableApplicationContext context =  SpringApplication
        .run(SawSecurityAdminInvoker.class, args);
  }

}

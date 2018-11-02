package com.synchronoss.saw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@ComponentScan(basePackages = {"com.extensions", "com.synchronoss.saw"})
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableIntegration
@IntegrationComponentScan
public class SpringTestPluginSftpApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringTestPluginSftpApplication.class, args);
  }
}

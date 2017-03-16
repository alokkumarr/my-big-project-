package com.synchronoss.saw.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties
@Configuration
@ComponentScan("com.synchronoss.saw.composite")
public class SAWCompositeServiceApplication {

	private static final Logger LOG = LoggerFactory.getLogger(SAWCompositeServiceApplication.class);
	
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SAWCompositeServiceApplication.class, args);
        LOG.info(ctx.getApplicationName() + " has started.");
	}

}

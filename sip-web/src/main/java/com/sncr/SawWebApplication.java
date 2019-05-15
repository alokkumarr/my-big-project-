package com.sncr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.bind.annotation.RestController;



@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableOAuth2Client
@RestController
public class SawWebApplication  extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder builder) {
		return builder.sources(SawWebApplication.class);
	}	
	
    public static void main(String[] args) {
    	SpringApplication.run(SawWebApplication.class, args);    	
    }
	
}

package com.synchronoss.saw.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SAWSchedulerServiceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SAWSchedulerServiceApplication.class);
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
		SpringApplication.run(SAWSchedulerServiceApplication.class, args);
		LOGGER.info(ctx.getApplicationName() + " has started.");
	}
}

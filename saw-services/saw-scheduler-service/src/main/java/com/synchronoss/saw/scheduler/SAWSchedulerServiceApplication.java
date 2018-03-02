package com.synchronoss.saw.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SAWSchedulerServiceApplication extends SpringBootServletInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SAWSchedulerServiceApplication.class);

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		return new TomcatEmbeddedServletContainerFactory();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

		return builder.sources(SAWSchedulerServiceApplication.class);
	}
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
		SpringApplication.run(SAWSchedulerServiceApplication.class, args);
		LOGGER.info(ctx.getApplicationName() + " has started.");
	}
}

package com.synchronoss.saw.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import info.faljse.SDNotify.SDNotify;

@SpringBootApplication
public class SAWSchedulerServiceApplication extends SpringBootServletInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SAWSchedulerServiceApplication.class);

	@Bean
	public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
		return new TomcatServletWebServerFactory();
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

	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("Notifying service manager about start-up completion");
		SDNotify.sendNotify();
	}
}

package com.synchronoss.saw.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import info.faljse.SDNotify.SDNotify;

@EnableRetry
@SpringBootApplication
public class SAWSchedulerServiceApplication extends SpringBootServletInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SAWSchedulerServiceApplication.class);

	@Autowired
	private Environment environment;
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
	@Bean
	  public RetryTemplate retryTemplate() {
	    int maxAttempt = Integer.parseInt(environment.getProperty("sip.service.max.attempts"));
	    int retryTimeInterval = Integer.parseInt(environment.getProperty("sip.service.retry.delay"));
	    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
	    retryPolicy.setMaxAttempts(maxAttempt);
	    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
	    backOffPolicy.setBackOffPeriod(retryTimeInterval); // 1.5 seconds
	    RetryTemplate template = new RetryTemplate();
	    template.setRetryPolicy(retryPolicy);
	    template.setBackOffPolicy(backOffPolicy);
	    return template;
	  }
}

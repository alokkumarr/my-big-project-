package com.synchronoss.saw.workbench;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import info.faljse.SDNotify.SDNotify;

@SpringBootApplication
@ComponentScan(basePackages = {"com.synchronoss.saw.workbench", "com.synchronoss.saw.workbench.executor.service", "com.synchronoss.sip.utils"})
public class SAWWorkBenchServiceApplication {
	
	

	
	
  private static final Logger LOG = LoggerFactory.getLogger(SAWWorkBenchServiceApplication.class);
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWWorkBenchServiceApplication.class, args);
    LOG.debug(ctx.getApplicationName() + " has started.######");

  }

 

  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOG.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();
  }
}

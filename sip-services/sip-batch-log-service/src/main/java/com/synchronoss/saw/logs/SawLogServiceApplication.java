package com.synchronoss.saw.logs;

import info.faljse.SDNotify.SDNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SawLogServiceApplication {

  private static final Logger LOGGER = 
            LoggerFactory.getLogger(SawLogServiceApplication.class);

  /**
   * Launching spring boot app.
   * 
   * @param args no args
   */
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx = 
                    SpringApplication.run(SawLogServiceApplication.class, args);
    LOGGER.info(ctx.getApplicationName() + " has started.");
  }

  @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
    LOGGER.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();  
  }
}

package com.synchronoss.saw.storage.proxy;

import com.synchronoss.saw.storage.proxy.service.executionResultMigrationService.MigrateAnalysisService;
import info.faljse.SDNotify.SDNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ComponentScan(basePackages = {"com.synchronoss.saw.storage.proxy", "com.synchronoss.sip.utils"})
public class SAWStorageServiceApplication {
  private static final Logger LOG = LoggerFactory.getLogger(SAWStorageServiceApplication.class);

  @Autowired MigrateAnalysisService migrateAnalysisService;

  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWStorageServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");
  }

  /**
   * Invokes the proxy execution result migration.
   *
   * @param event ready event.
   * @throws Exception exception.
   */
  @EventListener
  public void onApplicationEvent(ApplicationStartedEvent event) throws Exception {
    LOG.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();
    try {
      migrateAnalysisService.startExecutionResult();
    } catch (Exception ex) {
      LOG.error("Execution Result migration failed", ex);
    }
  }
}

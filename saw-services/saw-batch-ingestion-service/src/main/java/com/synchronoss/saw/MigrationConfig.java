package com.synchronoss.saw;

import javax.validation.constraints.NotNull;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MigrationConfig {
  private final Logger log = LoggerFactory.getLogger(MigrationConfig.class);

  @Value("${sip.service.index}")
  @NotNull
  private String serviceIndex;

  /**
   * The migration strategy has been overridden.
   * @return
   */
  @Bean
  public FlywayMigrationStrategy migrationStrategy() {
    return new FlywayMigrationStrategy() {
      @Override
      public void migrate(Flyway flyway) {
        int serviceIndexInt = Integer.parseInt(serviceIndex);
        log.info("Starting migration (service index: {})", serviceIndexInt);
        /*
         * Run migration only on first node, due to MariaDB Galera cluster not supporting the
         * locking mechanisms that Flyway depends on
         */
        if (serviceIndexInt == 0) {
          log.info("Running migration on first node");
          flyway.migrate();
        } else {
          log.info("Letting migration run on first node");
          waitForMigration(flyway, 5);
        }
        log.info("Finished migration");
      }

      private void waitForMigration(Flyway flyway, int retries) {
        if (flyway.info().pending().length > 0) {
          try {
            log.info("Waiting for first node to complete migration");
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            log.info("Interrupted");
          }
          if (retries > 0) {
            waitForMigration(flyway, retries - 1);
          } else {
            log.warn("Out of retries waiting for migration");
          }
        } else {
          log.info("Done waiting for migration");
        }
      }
    };
  }
}

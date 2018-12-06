package com.synchronoss.saw.scheduler;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
public class MigrationConfig {
  /* Number of times to retry waiting for first node having completed
   * database migration, before proceeding */
  private static final int RETRY_COUNT = 5;

  /* Time to wait between checks for first node having completed
   * database migration */
  private static final int RETRY_WAIT_MILLISECONDS = 5000;

  private final Logger log = LoggerFactory.getLogger(MigrationConfig.class);

  @Value("${sip.service.index}")
  @NotNull
  private Integer serviceIndex;

  @Bean
  public FlywayMigrationStrategy migrationStrategy() {
    return new FlywayMigrationStrategy() {
      @Override
      public void migrate(Flyway flyway) {
        log.info("Starting migration (service index: {})", serviceIndex);
        /* Run migration only on first node, due to MariaDB Galera
         * cluster not supporting the locking mechanisms that Flyway
         * depends on */
        if (serviceIndex == 0) {
          log.info("Running migration on first node");
          flyway.migrate();
        } else {
          log.info("Letting migration run on first node");
          waitForMigration(flyway, RETRY_COUNT);
        }
        log.info("Finished migration");
      }

      private void waitForMigration(Flyway flyway, int retries) {
        if (flyway.info().pending().length > 0) {
          try {
            log.info("Waiting for first node to complete migration");
            Thread.sleep(RETRY_WAIT_MILLISECONDS);
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

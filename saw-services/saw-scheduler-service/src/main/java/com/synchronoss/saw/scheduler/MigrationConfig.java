package com.synchronoss.saw.scheduler;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class MigrationConfig {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Bean
  public FlywayMigrationStrategy migrationStrategy() {
    return new FlywayMigrationStrategy() {
      @Override
      public void migrate(Flyway flyway) {
        log.info("Starting migration");
        if (flyway.info().pending().length > 0) {
          /* Delay migration start to reduce the probability of
           * multiple instances of the same service attempting
           * migration at the same */
          int delay = ThreadLocalRandom.current().nextInt(0, 10000);
          log.info("Delaying startup: {} ms", delay);
          try {
            Thread.sleep(delay);
          } catch (InterruptedException e) {
            log.info("Interrupted");
          }
        }
        flyway.migrate();
        log.info("Finished migration");
      }
    };
  }
}

package com.synchronoss.bda.sip;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;

/**
 * Configure health status metric suitable for exposing through
 * Prometheus.  Prometheus only works with numeric metrics, so the
 * health status needs to be translated into an integer.  Having the
 * health status as a Prometheus metric allows creating alerts based
 * on it, in addition to other metrics.
 */
@Configuration
@ConditionalOnMissingClass("org.junit.Test")
class HealthMetricsConfiguration {
  private CompositeHealthIndicator healthIndicator;

  public HealthMetricsConfiguration(
      HealthAggregator healthAggregator,
      List<HealthIndicator> healthIndicators,
      MeterRegistry registry) {
    healthIndicator = new CompositeHealthIndicator(healthAggregator);
    for (Integer i = 0; i < healthIndicators.size(); i++) {
      healthIndicator.addHealthIndicator(
          i.toString(), healthIndicators.get(i));
    }
    registry.gauge(
        "spring_boot_actuator_health_status",
        Collections.emptyList(), healthIndicator, health -> {
          Status status = health.health().getStatus();
          switch (status.getCode()) {
            case "UP":
              return 0;
            case "OUT_OF_SERVICE":
              return 1;
            case "DOWN":
              return 2;
            case "UNKNOWN":
            default:
              return 3;
          }
        });
  }
}

package com.synchronoss.saw.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ServiceHealthIndicator implements HealthIndicator {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApiGatewayProperties apiGatewayProperties;

  @Override
  public Health health() {
    Set<String> checked = new HashSet<>();
    List<String> errors = new ArrayList<>();
    boolean healthy = true;
    log.debug("Checking health");
    for (ApiGatewayProperties.Endpoint endpoint
             : apiGatewayProperties.getEndpoints()) {
      String uri = endpoint.getLocation() + "/actuator/health";
      if (checked.contains(uri)) {
        /* Skip endpoints that already have been checked */
        continue;
      }
      checked.add(uri);
      try {
        log.debug("Checking health: {}", uri);
        URL url = new URL(uri);
        HttpURLConnection connection =
            (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int code = connection.getResponseCode();
        log.debug("Health check respose code: {}", code);
        if (code != 200) {
          errors.add(uri + ": respose code: " + code);
        }
      } catch (Exception e) {
        log.debug("Health check error: {}", e.getMessage());
        errors.add(uri + ": health check error: " + e.getMessage());
      }
    }
    if (!errors.isEmpty()) {
      return Health.down().withDetail("errors", errors).build();
    }
    return Health.up().build();
  }
}

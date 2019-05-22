package com.synchronoss.saw.gateway;

import com.synchronoss.sip.utils.RestUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceHealthIndicator implements HealthIndicator {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApiGatewayProperties apiGatewayProperties;

  @Autowired
  private RestUtil restUtil;

  private RestTemplate restTemplate;

  @Override
  public Health health() {
    Set<String> checked = new HashSet<>();
    List<String> errors = new ArrayList<>();
    log.debug("Checking health");
    String uri = null;
    for (ApiGatewayProperties.Endpoint endpoint : apiGatewayProperties.getEndpoints()) {
      uri = endpoint.getLocation() + "/actuator/health";
      if (checked.contains(uri)) {
        /* Skip endpoints that already have been checked */
        continue;
      }
      checked.add(uri);
      try {
        log.debug("Checking health: {}", uri);
        restTemplate = restUtil.restTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        int code = result.getStatusCodeValue();

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


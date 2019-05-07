package com.synchronoss.saw;

import com.synchronoss.sip.utils.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class SipBisHealthIndicator implements HealthIndicator {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Value("${bis.scheduler-url}")
  private String bisSchedulerUrl;

  @Autowired
  private RestUtil restUtil;

  private RestTemplate restTemplate;

  @Override
  public Health health() {
    log.debug("Checking health for uri :" + bisSchedulerUrl + "/actuator/health");
    String uri = bisSchedulerUrl + "/actuator/health";
    
    String error = null;
    try {
      restTemplate = restUtil.restTemplate();
      log.debug("Checking health: {}", uri);
      ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
      int code = result.getStatusCodeValue();
      log.debug("Health check respose code: {}", code);
      if (code != 200) {
        error = uri + ": respose code: " + code;
      }
    } catch (Exception e) {
      log.debug("Health check error: {}", e.getMessage());
      error = uri + ": health check error: " + e.getMessage();
    }
    if (error != null) {
      return Health.down().withDetail("errors", error).build();
    }
    return Health.up().build();
  }
}

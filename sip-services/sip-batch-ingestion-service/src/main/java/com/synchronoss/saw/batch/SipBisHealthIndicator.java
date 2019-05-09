package com.synchronoss.saw.batch;

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

/**
 * This custom health checker has been commented
 * due to property loading issue. 
 * Now default health checker is active
 */
//@Component
public class SipBisHealthIndicator implements HealthIndicator {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Value("${bis.scheduler-url}")
  private String bisSchedulerUrl;

  @Value("${sip.trust.store}")
  private String trustStore;

  @Value("${sip.trust.password}")
  private String trustStorePassword;

  @Value("${sip.key.store}")
  private String keyStore;

  @Value("${sip.key.password}")
  private String keyStorePassword;

  
  @Autowired
  private RestUtil restUtil;

  private RestTemplate restTemplate;

  @Override
  public Health health() {
    log.debug("Checking health for uri :" + bisSchedulerUrl + "/actuator/health");
    String uri = bisSchedulerUrl + "/actuator/health";
    
    String error = null;
    try {
      log.info("From SipBisHealthIndicator starts here");
      restTemplate =
          restUtil.restTemplate(keyStore, keyStorePassword, trustStore, trustStorePassword);
      log.debug("Checking health: {}", uri);
      ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
      int code = result.getStatusCodeValue();
      log.debug("Health check respose code: {}", code);
      if (code != 200) {
        //error = uri + ": respose code: " + code;
        error = null;
      }
    } catch (Exception e) {
      log.debug("Health check error: {}", e);
      // error = uri + ": health check error: " + e.getMessage();
      error = null;
    }
    // if (error != null) {
    // return Health.down().withDetail("errors", error).build();
    // }
    log.info("From SipBisHealthIndicator ends here");
    return Health.up().build();
  }
}

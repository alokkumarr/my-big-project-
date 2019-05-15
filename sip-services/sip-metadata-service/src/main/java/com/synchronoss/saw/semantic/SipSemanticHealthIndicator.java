package com.synchronoss.saw.semantic;

import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class SipSemanticHealthIndicator implements HealthIndicator {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Value("${semantic.transport-metadata-url}")
  private String semanticTransportUrl;

  @Override
  public Health health() {
    log.debug("Checking health for uri :" + semanticTransportUrl + "/actuator/health");
    String uri = semanticTransportUrl + "/actuator/health";
    String error = null;
    HttpURLConnection connection = null;
    try {
      log.debug("Checking health: {}", uri);
      URL url = new URL(uri);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      int code = connection.getResponseCode();
      log.debug("Health check respose code: {}", code);
      if (code != 200) {
        error = uri + ": respose code: " + code;
      }
    } catch (Exception e) {
      log.debug("Health check error: {}", e.getMessage());
      error = uri + ": health check error: " + e.getMessage();
    } finally {
      if (connection != null) {
        /*
         * Disconnect explicitly to avoid keeping too many connections open which might exhaust
         * socket resources on the host
         */
        connection.disconnect();
      }
    }
    if (error != null) {
      return Health.down().withDetail("errors", error).build();
    }
    return Health.up().build();
  }
}

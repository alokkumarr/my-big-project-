package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

/**
 * Test retrieving metrics through Prometheus REST API.
 */
public class MetricsIT extends BaseIT {
  /**
   * Test that service health status metric is available from each
   * service.
   */
  @Test
  public void testHealthMetrics() throws JsonProcessingException {
    given(spec)
        .queryParam("query", "spring_boot_actuator_health_status")
        .when().get("/prometheus/api/v1/query")
        .then().assertThat().statusCode(200)
        .body("status", equalTo("success"))
        .log().all()
        .body("data.result.metric.job",
              hasItems(
                  "sip-batch-ingestion", "sip-export", "sip-observe",
                  "sip-scheduler", "sip-security", "sip-semantic",
                  "sip-workbench"));
  }
}

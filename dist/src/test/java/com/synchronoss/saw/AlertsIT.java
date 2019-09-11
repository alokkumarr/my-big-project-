package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test managing alerts, collecting metrics for alerts during data
 * ingestion and and evaluating alerts after data has been ingested.
 */
public class AlertsIT extends BaseIT {
  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  @Test
  public void testTriggerAlert() throws JsonProcessingException {
    String testId = testId();
    ObjectNode root = mapper.createObjectNode();
    root.put("metric", 100);
    given(new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build())
        .queryParam("APP_KEY", "stream_1")
        .queryParam("APP_VERSION", "1")
        .queryParam("APP_MODULE", "1")
        .queryParam("EVENT_TYPE", "1")
        .queryParam("EVENT_ID", "1")
        .queryParam("EVENT_DATE", "1")
        .queryParam("RECEIVED_TS", "1")
        .contentType(ContentType.JSON) 
        
        .body(root)
        .when().post("sip/rtis/events")
        .then().assertThat().statusCode(200)
        .body("result",equalTo("success"));
  }
}

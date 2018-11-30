package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test sending events to the Real Time Ingestion and Processing
 * Service.
 */
public class RealTimeIT extends BaseIT {
  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  @Test
  public void testSendEvent() throws JsonProcessingException {
    String testId = testId();
    ObjectNode root = mapper.createObjectNode();
    root.put("string", "rtps-" + testId);
    given(authSpec)
        .queryParam("APP_KEY", "sip-rtis")
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
        .body("result", equalTo("success"));
  }
}

package com.synchronoss.saw;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SubscriberIT extends BaseIT {
  private static final String SUBSCRIBER_BASE_API = "/saw/services/subscribers/";
  private ObjectNode subscriberData;
  private ObjectNode updateSubscriberData;

  @Before
  public void init() {
    subscriberData = getJsonObject("json/subscribers/subscriber-data.json");
    updateSubscriberData = getJsonObject("json/subscribers/subscriber-update-data.json");
  }

  @Test
  public void testCreateAndgetSubscriber() {

    /**
     * Sample subscriber data
     *
     * {
     *    "id": "2308b207-4433-45bf-ba11-6f07135d5e06",
     *    "subscriberId": "2308b207-4433-45bf-ba11-6f07135d5e06",
     *    "subscriberName": "Sunil",
     *    "channelType": "email",
     *    "channelValue": "sunilkumar.bm@synchronoss.com",
     *    "customerCode": "SYNCHRONOSS",
     *    "active": true,
     *    "createdTime": 1585563227248
     * }
     */

    String subscriberName = subscriberData.path("subscriberName").asText();
    // Create Subscriber
    String subscriberId =
        given(authSpec)
            .body(subscriberData)
            .when()
            .post(SUBSCRIBER_BASE_API)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getString("subscriberId");

    // Get Subscriber By Id
    String getSubscriberApi = SUBSCRIBER_BASE_API + subscriberId;
    String fetchedSubscriberName =
        given(authSpec)
            .when()
            .get(getSubscriberApi)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getString("subscriberName");

    Assert.assertEquals(subscriberName, fetchedSubscriberName);

    tearDownSubcriber(subscriberId);
  }

  @Test
  public void testUpdateSubscriber() {
    // Create Subscriber
    String subscriberId =
        given(authSpec)
            .body(subscriberData)
            .when()
            .post(SUBSCRIBER_BASE_API)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getString("subscriberId");

    // Update subscriber
    String updateSubscriberApi = SUBSCRIBER_BASE_API + subscriberId;
    String updateSubscriberName = updateSubscriberData.path("subscriberName").asText();

    String fetchedSubscriberName =
        given(authSpec)
            .body(updateSubscriberData)
            .when()
            .put(updateSubscriberApi)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getString("subscriberName");

    Assert.assertEquals(updateSubscriberName, fetchedSubscriberName);

    tearDownSubcriber(subscriberId);
  }

  @Test
  public void testDeleteSubscriber() {
    // Create Subscriber
    String subscriberId =
        given(authSpec)
            .body(subscriberData)
            .when()
            .post(SUBSCRIBER_BASE_API)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getString("subscriberId");

    // Delete subscriber
    tearDownSubcriber(subscriberId);
  }

  private void tearDownSubcriber(String subscriberId) {
    String deleteSubscriberApi = SUBSCRIBER_BASE_API + subscriberId;

    given(authSpec).when().delete(deleteSubscriberApi).then().assertThat().statusCode(200);
  }
}

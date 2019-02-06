package com.synchronoss.saw;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Services integration tests.  Tests health checks.
 */
public class ServicesIT extends BaseIT {
  private RequestSpecification spec;

  @Before
  public void setUp() {
    spec = new RequestSpecBuilder().build();
  }

  @Test
  public void testHealth() {
    given(spec).accept("application/json")
        .when().get("/services/actuator/health")
        .then().statusCode(200).body("status", equalTo("UP"));
  }

  @Test
  public void testAddDocument() {
    ObjectNode root = mapper.createObjectNode();
    root.put("source", "testValue");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root)
      .when().post("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));
  }

  @Test
  public void testUpdateDocument() {
    ObjectNode root = mapper.createObjectNode();
    root.put("source", "testValue123");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root)
      .when().put("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));
  }

  @Test
  public void testGetDocument() {
    given(authSpec)
      .when().get("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200);
  }

  @Test
  public void testDeleteDocument() {
    given(authSpec)
      .when().delete("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200);
  }
}

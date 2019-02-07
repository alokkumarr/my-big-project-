package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

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
  public void testAddUpdateDocument() {
    ObjectNode root = mapper.createObjectNode();
    root.put("source", "testValue");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root)
      .when().post("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    ObjectNode root1 = mapper.createObjectNode();
    root1.put("source", "testValue123");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root1)
      .when().put("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    given(authSpec)
      .when().delete("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200);
  }

  @Test
  public void testGetDeleteDocuments() {
    ObjectNode root = mapper.createObjectNode();
    root.put("source", "testValue");
    root.put("module_type","ratings");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root)
      .when().post("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    ObjectNode root1 = mapper.createObjectNode();
    root1.put("source", "testValue1");
    root1.put("module_type","ratings1234");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root1)
      .when().post("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating1")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    given(authSpec)
      .when().get("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200);

    given(authSpec)
      .when().get("/services/internal/proxy/storage/product-modules")
      .then().assertThat().statusCode(200);

    ObjectNode cond = mapper.createObjectNode();
    cond.put("module_type","ratings1234");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(cond)
      .when().post("/services/internal/proxy/storage/product-modules")
      .then().assertThat().statusCode(200);

    given(authSpec)
      .when().delete("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating")
      .then().assertThat().statusCode(200);
    given(authSpec)
      .when().delete("/services/internal/proxy/storage/product-module/bda_pc_bt::app_rating1")
      .then().assertThat().statusCode(200);
  }

}

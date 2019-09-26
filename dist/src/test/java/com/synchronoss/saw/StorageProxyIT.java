package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import org.junit.Test;

/** StorageProxy integration tests. */
public class StorageProxyIT extends BaseIT {

  @Test
  public void testAddUpdateDocument() {
    ObjectNode root = mapper.createObjectNode();
    ObjectNode sourceJson = mapper.createObjectNode();
    sourceJson.put("module_type", "ratings");
    root.set("source", sourceJson);
    given(authSpec)
        .contentType(ContentType.JSON)
        .body(root)
        .when()
        .post("/services/internal/proxy/storage/product-module/pc_bt::app_rat/configuration")
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));

    ObjectNode root1 = mapper.createObjectNode();
    ObjectNode sourceJson1 = mapper.createObjectNode();
    sourceJson1.put("module_type", "ratings123");
    root1.set("source", sourceJson1);
    given(authSpec)
        .contentType(ContentType.JSON)
        .body(root1)
        .when()
        .put("/services/internal/proxy/storage/product-module/pc_bt::app_rat/configuration")
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));

    given(authSpec)
        .when()
        .delete("/services/internal/proxy/storage/product-module/pc_bt::app_rat/configuration")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testGetDeleteDocuments() {
    ObjectNode root = mapper.createObjectNode();
    ObjectNode sourceJson = mapper.createObjectNode();
    sourceJson.put("module_type", "ratings");
    root.set("source", sourceJson);
    given(authSpec)
        .contentType(ContentType.JSON)
        .body(root)
        .when()
        .post("/services/internal/proxy/storage/product-module/pc_bt::app_rat/configuration")
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));

    ObjectNode root1 = mapper.createObjectNode();
    ObjectNode sourceJson1 = mapper.createObjectNode();
    sourceJson1.put("module_type", "ratings1");
    root1.put("source", sourceJson1);
    given(authSpec)
        .contentType(ContentType.JSON)
        .body(root1)
        .when()
        .post("/services/internal/proxy/storage/product-module/pc_bt::app_ra4/configuration")
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));

    given(authSpec)
        .when()
        .get("/services/internal/proxy/storage/product-module/pc_bt::app_rat/configuration")
        .then()
        .assertThat()
        .statusCode(200);

    given(authSpec)
        .when()
        .get("/services/internal/proxy/storage/product-module/docs")
        .then()
        .assertThat()
        .statusCode(200);

    given(authSpec)
        .when()
        .delete("/services/internal/proxy/storage/product-module/pc_bt::app_rat/configuration")
        .then()
        .assertThat()
        .statusCode(200);
    given(authSpec)
        .when()
        .delete("/services/internal/proxy/storage/product-module/pc_bt::app_ra4/configuration")
        .then()
        .assertThat()
        .statusCode(200);
  }
}

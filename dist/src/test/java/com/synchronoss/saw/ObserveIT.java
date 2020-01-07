package com.synchronoss.saw;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class ObserveIT extends BaseIT {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

  private static final String PATH = "/sip/services/observe/dashboards";
  private static final String EDIT_MESSAGE = "Entity is updated successfully";
  private static final String DELETE_MESSAGE = "Entity is deleted successfully.";
  private static final String SUCCESS_MESSAGE = "Entity has been retrieved successfully";

  @Test
  public void testDashBoardWithValidPrivileges() {
    LOGGER.trace("Create temporary dashboard..!");

    ObjectNode secGroup = getJsonObject("json/observe/dashboard-content.json");
    ResponseBody response = given(authSpec)
        .body(secGroup)
        .when()
        .post(PATH + "/create")
        .then()
        .assertThat()
        .statusCode(201)
        .extract()
        .response()
        .getBody();

    String dashboardCreated = response.jsonPath().getString("message");
    Assert.assertEquals("Entity is created successfully", dashboardCreated);

    String entityId = response.jsonPath().getString("id");
    LOGGER.trace("Entity retrieved for dashboard integration {}", entityId);

    // test get API
    String responseMessage = given(authSpec)
        .when()
        .get(PATH + "/" + entityId)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response()
        .getBody()
        .jsonPath().getString("message");

    Assert.assertEquals(SUCCESS_MESSAGE, responseMessage);

    // test edit api
    ObjectNode editContent = getJsonObject("json/observe/dashboard-edit.json");
    String editMessage = given(authSpec)
        .body(editContent)
        .when()
        .put(PATH + "/update/" + entityId)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response()
        .getBody()
        .jsonPath().getString("message");

    Assert.assertEquals(EDIT_MESSAGE, editMessage);

    // test delete API
    String deleteMessage = given(authSpec)
        .when()
        .delete("/sip/services/observe/dashboards/" + entityId)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response()
        .getBody()
        .jsonPath().getString("message");

    Assert.assertEquals(DELETE_MESSAGE, deleteMessage);
  }


  @Test
  public void testCreateDashBoardWithInvalidPrivileges() {
    LOGGER.info("Create temporary dashboard..!");

    ObjectNode secGroup = getJsonObject("json/observe/dashboard-invalid-category.json");
    ResponseBody response = given(authSpec)
        .body(secGroup)
        .when()
        .post(PATH + "/create")
        .then()
        .assertThat()
        .statusCode(401)
        .extract()
        .response()
        .getBody();

    String dashboardCreated = response.jsonPath().getString("message");
    Assert.assertEquals("Unauthorized", dashboardCreated);
  }

  @Test
  public void testViewByCategoryDashBoard() {

    ObjectNode secGroup = getJsonObject("json/observe/dashboard-content.json");
    ResponseBody response = given(authSpec)
        .body(secGroup)
        .when()
        .post(PATH + "/create")
        .then()
        .assertThat()
        .statusCode(201)
        .extract()
        .response()
        .getBody();

    String dashboardCreated = response.jsonPath().getString("message");
    Assert.assertEquals("Entity is created successfully", dashboardCreated);
    String entityId = response.jsonPath().getString("id");

    String responseMessage = given(authSpec)
        .when()
        .get(PATH + "/7/1")
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response()
        .getBody()
        .jsonPath().getString("message");

    Assert.assertEquals(SUCCESS_MESSAGE, responseMessage);

    // test delete API
    String deleteMessage = given(authSpec)
        .when()
        .delete(PATH + "/" + entityId)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response()
        .getBody()
        .jsonPath().getString("message");

    Assert.assertEquals(DELETE_MESSAGE, deleteMessage);
  }
}

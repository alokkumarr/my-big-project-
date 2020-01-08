package com.synchronoss.saw;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.ResponseBody;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is integration test to manage dashboard related CRUD operation
 * and validate authentication of valid users.
 *
 * @author alok.kumarr
 * @since 3.5.0
 */
public class ObserveIT extends BaseIT {
  private final Logger logger = LoggerFactory.getLogger(getClass().getName());

  private static final String PATH = "/sip/services/observe/dashboards";
  private static final String EDIT_MESSAGE = "Entity is updated successfully";
  private static final String DELETE_MESSAGE = "Entity is deleted successfully.";
  private static final String SUCCESS_MESSAGE = "Entity has been retrieved successfully";

  @Test
  public void testDashBoardWithValidPrivileges() {
    logger.trace("Create temporary dashboard..!");

    ObjectNode secGroup = getJsonObject("json/observe/dashboard-content.json");
    ResponseBody response = given(authSpec)
        .body(secGroup)
        .when()
        .post(PATH + "/create")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_CREATED)
        .extract()
        .response()
        .getBody();

    String dashboardCreated = response.jsonPath().getString("message");
    Assert.assertEquals("Entity is created successfully", dashboardCreated);

    String entityId = response.jsonPath().getString("id");
    logger.trace("Entity retrieved for dashboard integration {}", entityId);

    // test get API
    String responseMessage = given(authSpec)
        .when()
        .get(PATH + "/" + entityId)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
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
        .statusCode(HttpStatus.SC_OK)
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
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .response()
        .getBody()
        .jsonPath().getString("message");

    Assert.assertEquals(DELETE_MESSAGE, deleteMessage);
  }


  @Test
  public void testCreateDashBoardWithInvalidPrivileges() {
    logger.trace("Create temporary dashboard..!");

    ObjectNode secGroup = getJsonObject("json/observe/dashboard-invalid-category.json");
    ResponseBody response = given(authSpec)
        .body(secGroup)
        .when()
        .post(PATH + "/create")
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_UNAUTHORIZED)
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
        .statusCode(HttpStatus.SC_CREATED)
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
        .statusCode(HttpStatus.SC_OK)
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
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .response()
        .getBody()
        .jsonPath().getString("message");

    Assert.assertEquals(DELETE_MESSAGE, deleteMessage);
  }
}

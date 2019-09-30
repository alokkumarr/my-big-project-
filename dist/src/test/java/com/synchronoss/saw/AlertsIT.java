package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test managing alerts, collecting metrics for alerts during data ingestion and and evaluating
 * alerts after data has been ingested.
 */
public class AlertsIT extends BaseIT {
  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  private static final String OPERATORS = "operators";
  private static final String ALERT_PATH = "/saw/services/alerts";
  private static final String ALERT_COUNT = "count";
  private static final String ALERT_STATES = "states";
  private static final String MONITORINGTYPE = "monitoringtype";
  private static final String ATTRIBUTE_VALUES = "attributevalues";

  @Test
  public void testTriggerAlert() throws JsonProcessingException {
    String testId = testId();
    ObjectNode root = mapper.createObjectNode();
    root.put("metric", 100);
    given(new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation)).build())
        .queryParam("APP_KEY", "stream_1")
        .queryParam("APP_VERSION", "1")
        .queryParam("APP_MODULE", "1")
        .queryParam("EVENT_TYPE", "1")
        .queryParam("EVENT_ID", "1")
        .queryParam("EVENT_DATE", "1")
        .queryParam("RECEIVED_TS", "1")
        .contentType(ContentType.JSON)
        .body(root)
        .when()
        .post("sip/rtis/events")
        .then()
        .assertThat()
        .statusCode(200)
        .body("result", equalTo("success"));
  }

  /** This test-case is check the scenario to create a alert. */
  @Test
  public void createAlert() throws JsonProcessingException, IOException {

    HashMap<?, ?> alertObject =
        given(authSpec)
            .body(prepareAlertsDataSet())
            .when()
            .post(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("alert");

    log.debug("alertObject : " + alertObject);

    JsonNode jsonNode = mapper.convertValue(alertObject, JsonNode.class);
    String alertRulesSysId = jsonNode.get("alertRulesSysId").toString();
    log.debug("alertRulesSysId : " + alertRulesSysId);
    assertFalse(alertRulesSysId == null);

    // delete alert after testing
    this.tearDownAlert(alertRulesSysId);
  }

  /** This test-case is check the scenario to delete a alert. */
  @Test
  public void deleteAlert() throws IOException {
    HashMap<?, ?> alertObject =
        given(authSpec)
            .body(prepareAlertsDataSet())
            .when()
            .post(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("alert");

    log.debug("alertObject : " + alertObject);

    JsonNode jsonNode = mapper.convertValue(alertObject, JsonNode.class);
    String alertRulesSysId = jsonNode.get("alertRulesSysId").asText();
    log.debug("alertRulesSysId : " + alertRulesSysId);

    assertTrue(alertRulesSysId != null);

    String urlForDelete = ALERT_PATH + "/" + alertRulesSysId;
    log.debug("deleteAlerts urlForDelete : " + urlForDelete);
    given(authSpec).when().delete(urlForDelete).then().assertThat().statusCode(200);
  }

  /** This test-case is check the scenario that listing of alerts. */
  @Test
  public void readAlerts() throws IOException {
    HashMap<?, ?> alertObject =
        given(authSpec)
            .body(prepareAlertsDataSet())
            .when()
            .post(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("alert");

    log.debug("alertObject : " + alertObject);

    JsonNode jsonNode = mapper.convertValue(alertObject, JsonNode.class);
    String alertRulesSysId = jsonNode.get("alertRulesSysId").toString();
    log.debug("alertRulesSysId : " + alertRulesSysId);

    given(authSpec)
        .body(prepareAlertsDataSet())
        .when()
        .get(ALERT_PATH)
        .then()
        .assertThat()
        .statusCode(200);

    List<?> listOfAlerts =
        given(authSpec)
            .when()
            .get(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .jsonPath()
            .getList("alertRuleDetailsList");

    log.debug("readAlert :" + listOfAlerts);
    assertTrue(listOfAlerts.size() > 0);

    // delete alert after testing
    this.tearDownAlert(alertRulesSysId);
  }

  /** This test-case is check the scenario to update a alert. */
  @Test
  public void updateAlert() throws IOException {
    HashMap<?, ?> alertObject =
        given(authSpec)
            .body(prepareAlertsDataSet())
            .when()
            .post(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("alert");

    log.debug("alertObject : " + alertObject);

    JsonNode jsonNode = mapper.convertValue(alertObject, JsonNode.class);
    String alertRulesSysId = jsonNode.get("alertRulesSysId").asText();
    log.debug("alertRulesSysId : " + alertRulesSysId);
    assertFalse(alertRulesSysId == null);

    String urlForThatoUpdate = ALERT_PATH + "/" + alertRulesSysId;
    log.debug("updateAlerts urlForThetoUpdate : " + urlForThatoUpdate);
    HashMap<?, ?> alertObject1 =
        given(authSpec)
            .body(prepareUpdateAlertDataSet())
            .when()
            .put(urlForThatoUpdate)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .body()
            .jsonPath()
            .getJsonObject("alert");

    String activeInd =
        mapper.convertValue(alertObject1, JsonNode.class).get("activeInd").toString();
    log.debug("activeInd :" + activeInd);
    assertEquals("false", activeInd);

    // delete alert after testing
    this.tearDownAlert(alertRulesSysId);
  }

  /** This method is used to tear down the alert after each test case executes. */
  public void tearDownAlert(String alertRulesSysId) throws JsonProcessingException {
    assertFalse(alertRulesSysId == null);
    String urlForDelete = ALERT_PATH + "/" + alertRulesSysId;
    log.debug("deleteAlert urlForDelete : " + urlForDelete);
    given(authSpec).when().delete(urlForDelete).then().assertThat().statusCode(200);
  }

  /** This test-case is check the scenario to operator a alert. */
  @Test
  public void testOperators() throws IOException {

    HashMap<?, ?> alertObject =
        given(authSpec)
            .body(prepareAlertsDataSet())
            .when()
            .post(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("alert");

    log.debug("alertObject : " + alertObject);

    JsonNode jsonNode = mapper.convertValue(alertObject, JsonNode.class);
    String alertRulesSysId = jsonNode.get("alertRulesSysId").toString();

    log.debug("alertRulesSysId : " + alertRulesSysId);
    assertFalse(alertRulesSysId == null);

    String urlForOperators = ALERT_PATH + "/" + OPERATORS;
    log.debug("operators url to get list : " + urlForOperators);

    List<?> listOfOperators =
        given(authSpec)
            .when()
            .get(urlForOperators)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .jsonPath()
            .get("operators");

    log.debug("listOfOperators : " + listOfOperators);
    assertTrue(listOfOperators.size() > 0);

    // delete alert after testing
    this.tearDownAlert(alertRulesSysId);
  }

  /** This test-case is check the scenario to list by categoryId a alert. */
  @Test
  public void testListByCategoryId() throws IOException {
    HashMap<?, ?> alertObject =
        given(authSpec)
            .body(prepareAlertsDataSet())
            .when()
            .post(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("alert");

    log.debug("alertObject : " + alertObject);

    JsonNode jsonNode = mapper.convertValue(alertObject, JsonNode.class);
    String alertRulesSysId = jsonNode.get("alertRulesSysId").toString();

    log.debug("alertRulesSysId : " + alertRulesSysId);
    assertFalse(alertRulesSysId == null);

    List<?> listOfAlerts =
        given(authSpec)
            .queryParam("id", "12")
            .when()
            .get(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .jsonPath()
            .get("alertRuleDetailsList");

    log.debug("listOfAlerts : " + listOfAlerts.size());
    assertTrue(listOfAlerts.size() > 0);

    // delete alert after testing
    this.tearDownAlert(alertRulesSysId);
  }

  /**
   * This method is used to in test-case where creation of alerts is required.
   *
   * @return object {@link ObjectNode}
   */
  private ObjectNode prepareAlertsDataSet() {
    ObjectNode root = mapper.createObjectNode();
    root.put("alertRulesSysId", "123456");
    root.put("activeInd", "false");
    root.put("aggregation", "AVG");
    root.put("alertSeverity", "CRITICAL");
    root.put("categoryId", "12");
    root.put("product", "MCT");
    root.put("datapodId", "abc");
    root.put("datapodName", "ABc");
    root.put("monitoringEntity", "abc123");
    root.put("operator", "LT");
    root.put("attributeName", "testAttribute");
    root.put("attributeValue", "attribute123");
    root.put("alertRuleDescription", "Tests");
    root.put("alertRuleName", "myName");
    root.put("thresholdValue", "2");
    return root;
  }

  /**
   * This method is used to in test-case where updating of alerts is required.
   *
   * @return object {@link ObjectNode}
   */
  private ObjectNode prepareUpdateAlertDataSet() {
    ObjectNode root = mapper.createObjectNode();
    root.put("alertRulesSysId", "123456");
    root.put("activeInd", "false");
    root.put("aggregation", "SUM");
    root.put("alertSeverity", "CRITICAL");
    root.put("categoryId", "12");
    root.put("product", "MCT");
    root.put("datapodId", "abc");
    root.put("datapodName", "ABc");
    root.put("monitoringEntity", "abc123");
    root.put("operator", "GT");
    root.put("attributeName", "testAttribute");
    root.put("attributeValue", "attribute123");
    root.put("alertRuleDescription", "Tests");
    root.put("alertRuleName", "myName");
    root.put("thresholdValue", "2");
    return root;
  }

  /** This test-case is check the alert count. */
  @Test
  public void testAlertCount() {
    String urlForAlertCount = ALERT_PATH + "/" + ALERT_COUNT;
    log.debug("URL for Alert count : " + urlForAlertCount);
    ObjectNode root = mapper.createObjectNode();
    root.put("groupBy", "StartTime");
    ArrayNode filters = root.putArray("filters");
    ObjectNode filter1 = mapper.createObjectNode();
    filter1.put("preset", "YTD");
    filter1.put("type", "date");
    filter1.put("fieldName", "startTime");
    filters.add(filter1);
    given(authSpec)
        .contentType(ContentType.JSON)
        .body(root)
        .when()
        .post(urlForAlertCount)
        .then()
        .assertThat()
        .statusCode(200);
  }

  /** This test-case is check the alert states. */
  @Test
  public void testAlertStates() {
    String urlForAlertStates = ALERT_PATH + "/" + ALERT_STATES;
    log.debug("URL for Alert count : " + urlForAlertStates);
    ObjectNode root = mapper.createObjectNode();
    ArrayNode filters = root.putArray("filters");
    ObjectNode filter1 = mapper.createObjectNode();
    filter1.put("preset", "YTD");
    filter1.put("type", "date");
    filter1.put("fieldName", "startTime");
    filters.add(filter1);
    String message =
        given(authSpec)
            .contentType(ContentType.JSON)
            .body(root)
            .when()
            .post(urlForAlertStates)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("message");
    assertThat(message, IsEqualIgnoringCase.equalToIgnoringCase("Success"));
  }

  /** This test-case is to check the scenario to list all Alert Monitoring type. */
  @Test
  public void testAlertMonitoringType() {
    String urlForOperators = ALERT_PATH + "/" + MONITORINGTYPE;
    log.debug("operators url to get list : " + urlForOperators);

    List<?> listOfMonitoringTypes =
        given(authSpec)
            .when()
            .get(urlForOperators)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .jsonPath()
            .get();

    log.debug("listOfMonitoringTypes : " + listOfMonitoringTypes);
    assertTrue(listOfMonitoringTypes.size() > 0);
  }

  /** This test-case is to check the scenario to list all attribute values. */
  @Test
  public void testListAttributeValues() throws IOException {
    HashMap<?, ?> alertObject =
        given(authSpec)
            .body(prepareAlertsDataSet())
            .when()
            .post(ALERT_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .getBody()
            .jsonPath()
            .getJsonObject("alert");

    log.debug("alertObject : " + alertObject);

    JsonNode jsonNode = mapper.convertValue(alertObject, JsonNode.class);
    String alertRulesSysId = jsonNode.get("alertRulesSysId").asText();
    log.debug("alertRulesSysId : " + alertRulesSysId);
    assertFalse(alertRulesSysId == null);

    String urlForAttributeList = ALERT_PATH + "/" + ATTRIBUTE_VALUES;
    List<String> attributeValues =
        given(authSpec)
            .when()
            .get(urlForAttributeList)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response()
            .body()
            .jsonPath()
            .get();

    assertTrue(attributeValues.size() > 0);

    // delete alert after testing
    this.tearDownAlert(alertRulesSysId);
  }
}

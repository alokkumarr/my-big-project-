package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch Ingestion Service integration tests. CRUD Operation both Route & Channel
 */
public class BatchIngestionIT extends BaseIT {
  private static final String BATCH_CHANNEL = "channels";
  private static final String BATCH_ROUTE = "routes";

  private static final String BATCH_CHANNEL_PATH = "/services/ingestion/batch/" + BATCH_CHANNEL;

  private static final String BATCH_PATH = "/services/ingestion/batch";

  private static final String TRANSFER_DATA_PATH =
      "/services/ingestion/batch/channel/transfers/data";

  private static final String PLUGNIN_PATH = "/services/ingestion/batch";

  private static final String ROUTE_HISTORY_PATH = "/services/ingestion/batch/logs/";
  private static final String LOGS_HISTORY_INTERNAL = "/services/ingestion/batch/internal/logs";

  private static final int WAIT_RETRIES = 30;
  private static final int WAIT_SLEEP_SECONDS = 8;

  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  /**
   * This method is used to in test-case where creation of channel is required.
   * 
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareChannelDataSet() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("channelName", "docker-channel-" + testId());
    childNode.put("channelType", "SCP");
    childNode.put("hostName", "sip-admin");
    childNode.put("portNo", 22);
    childNode.put("accessType", "read");
    childNode.put("userName", "root");
    childNode.put("password", "root");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("productCode", "SIP");
    root.put("customerCode", "SNCR");
    root.put("projectCode", "workbench");
    root.put("channelType", "sftp");
    root.put("channelMetadata", new ObjectMapper().writeValueAsString(childNode));;
    return root;
  }

  /**
   * This method is used to in test-case where creation of channel is required.
   * 
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareChannelDataSetDuplicate() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("channelName", "docker-channel-duplicate");
    childNode.put("channelType", "SCP");
    childNode.put("hostName", "sip-admin");
    childNode.put("portNo", 22);
    childNode.put("accessType", "read");
    childNode.put("userName", "root");
    childNode.put("password", "root");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("productCode", "SIP");
    root.put("customerCode", "SNCR");
    root.put("projectCode", "workbench");
    root.put("channelType", "sftp");
    root.put("channelMetadata", new ObjectMapper().writeValueAsString(childNode));;
    return root;
  }

  /**
   * This method is used to in test-case where creation of route is required.
   * 
   * @param sourcePath parameter to set the sourceLocation.
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareRouteDataSetDuplicate(String sourcePath)
      throws JsonProcessingException {
    ObjectNode routeMetadata = mapper.createObjectNode();
    routeMetadata.put("routeName", "docker-route-duplicate");
    routeMetadata.put("sourceLocation", sourcePath);
    routeMetadata.put("destinationLocation", "/data");
    routeMetadata.put("filePattern", "*.csv");
    routeMetadata.put("fileExclusions", "log");
    routeMetadata.put("disableDuplicate", "false");
    routeMetadata.put("disableConcurrency", "false");
    routeMetadata.put("lastModifiedLimitHours", "");
    ObjectNode schedulerNode = mapper.createObjectNode();
    schedulerNode.put("activeTab", "immediate");
    routeMetadata.set("schedulerExpression", schedulerNode);
    routeMetadata.put("description", "file");
    ObjectNode route = mapper.createObjectNode();
    route.put("createdBy", "admin@synchronoss.com");
    route.put("routeMetadata", new ObjectMapper().writeValueAsString(routeMetadata));
    return route;
  }


  /**
   * This method is used to in test-case where creation of route is required.
   * 
   * @param sourcePath parameter to set the sourceLocation.
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareRouteDataSet(String sourcePath) throws JsonProcessingException {
    ObjectNode routeMetadata = mapper.createObjectNode();
    routeMetadata.put("routeName", "docker-route");
    routeMetadata.put("sourceLocation", sourcePath);
    routeMetadata.put("destinationLocation", "/data");
    routeMetadata.put("filePattern", "*.csv");
    routeMetadata.put("fileExclusions", "log");
    routeMetadata.put("disableDuplicate", "false");
    routeMetadata.put("disableConcurrency", "false");
    routeMetadata.put("lastModifiedLimitHours", "");
    ObjectNode schedulerNode = mapper.createObjectNode();
    schedulerNode.put("activeTab", "immediate");
    routeMetadata.set("schedulerExpression", schedulerNode);
    routeMetadata.put("description", "file");
    ObjectNode route = mapper.createObjectNode();
    route.put("createdBy", "admin@synchronoss.com");
    route.put("routeMetadata", new ObjectMapper().writeValueAsString(routeMetadata));
    return route;
  }

  /**
   * This method is used to in test-case where creation of route is required. while duplicate file
   * is allowed
   * 
   * @param sourcePath parameter to set the sourceLocation.
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareRouteDataSetWithDuplicate(String sourcePath)
      throws JsonProcessingException {
    ObjectNode routeMetadata = mapper.createObjectNode();
    routeMetadata.put("routeName", "docker-route-" + testId());
    routeMetadata.put("sourceLocation", sourcePath);
    routeMetadata.put("destinationLocation", "/");
    routeMetadata.put("filePattern", "*.csv");
    routeMetadata.put("fileExclusions", "log");
    routeMetadata.put("disableDuplicate", "true");
    routeMetadata.put("disableConcurrency", "false");
    routeMetadata.put("lastModifiedLimitHours", "");
    ObjectNode schedulerNode = mapper.createObjectNode();
    schedulerNode.put("activeTab", "immediate");
    routeMetadata.set("schedulerExpression", schedulerNode);
    routeMetadata.put("description", "file");
    ObjectNode route = mapper.createObjectNode();
    route.put("createdBy", "admin@synchronoss.com");
    route.put("routeMetadata", new ObjectMapper().writeValueAsString(routeMetadata));
    return route;
  }

  /**
   * This method is used to in test-case where updating a change is required.
   * 
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareUpdateChannelDataSet() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("channelName", "docker-channel-" + testId());
    childNode.put("channelType", "SCP");
    childNode.put("hostName", "sip-admin");
    childNode.put("portNo", 22);
    childNode.put("accessType", "read");
    childNode.put("userName", "root");
    childNode.put("password", "root");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("productCode", "SIP");
    root.put("customerCode", "SNCR");
    root.put("projectCode", "workbench");
    root.put("channelType", "sftp");
    root.put("status", "1");
    root.put("channelMetadata", new ObjectMapper().writeValueAsString(childNode));
    root.put("modifiedBy", "sncr@synchronoss.com");
    return root;
  }

  /**
   * This method is used to prepare a schedule node to use it in route meta data node while.
   * scheduling route on monthly basis
   * 
   * @return object {@link ObjectNode}
   */
  private ObjectNode prepareSchedulerNode() {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("cronexp", "0 0 12 1 * ?");
    childNode.put("activeTab", "monthly");
    childNode.put("timezone", "America/New_York");
    childNode.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));
    childNode.put("endDate", "");
    return childNode;
  }

  /**
   * This method is used to prepare a schedule node to use it in route. meta data node while
   * scheduling route on hourly basis
   * 
   * @return object {@link ObjectNode}
   */
  private ObjectNode prepareSchedulerNodeForScheduledTransfer() {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("cronexp", "0 0/1 * 1/1 * ? *");
    childNode.put("activeTab", "hourly");
    childNode.put("timezone", "America/New_York");
    childNode.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        .format(DateUtils.addMinutes(new Date(), 1)));
    childNode.put("endDate", "");
    return childNode;
  }


  /**
   * This method is being used to prepare the route to use it while updating a route.
   * 
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareUpdateRouteDataSet() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("routeName", "docker-route-" + testId());
    childNode.put("sourceLocation", "/root/saw-batch-samples");
    childNode.put("destinationLocation", "/log/updated");
    childNode.put("filePattern", "*.csv");
    childNode.put("disableDuplicate", "false");
    childNode.put("disableConcurrency", "false");
    childNode.put("lastModifiedLimitHours", "");
    childNode.put("batchSize", "10");
    childNode.set("schedulerExpression", prepareSchedulerNode());
    childNode.put("description", "route has been updated");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("modifiedBy", "dataAdmin@synchronoss.com");
    root.put("routeMetadata", new ObjectMapper().writeValueAsString(childNode));
    root.put("status", 1);
    return root;
  }

  /**
   * This method is used to prepare a route to use it while scheduling transfer. data on hourly
   * basis
   * 
   * @return object {@link ObjectNode}
   * @throws JsonProcessingException exception.
   */
  private ObjectNode prepareRouteDataSetForTransferSchedule() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("routeName", "route-scheduled-transfer-" + testId());
    childNode.put("sourceLocation", "/root/saw-batch-samples/log/small");
    childNode.put("destinationLocation", "log/scheduled");
    childNode.put("filePattern", "*.log");
    childNode.put("fileExclusions", "csv");
    childNode.put("disableDuplicate", "false");
    childNode.put("disableConcurrency", "false");
    childNode.put("lastModifiedLimitHours", "");
    childNode.put("batchSize", "10");
    childNode.set("schedulerExpression", prepareSchedulerNodeForScheduledTransfer());
    childNode.put("description", "route has been created for scheduled test case");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("routeMetadata", new ObjectMapper().writeValueAsString(childNode));
    root.put("status", 1);
    return root;
  }
  

  /**
   * This test-case is check the scenario to create a channel.
   */
  @Test
  public void createChannel() throws JsonProcessingException {

    ValidatableResponse response = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200);
    log.debug("createChannel () " + response.log());

    // delete channel after testing
    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario that duplicate channel is not allowed.
   */
  @Test
  public void isDuplicateChannelPositive() throws JsonProcessingException {
    ValidatableResponse response = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200);
    log.debug("createChannel () isDuplicateChannel " + response.log());
    Response duplicateResp = given(authSpec).queryParam("channelName", "docker-channel")
        .body(prepareChannelDataSet()).when().get(BATCH_CHANNEL_PATH + "/duplicate");
    log.debug("isDuplicateChannel isDuplicateChannel " + duplicateResp.body().prettyPrint());
    assertEquals(duplicateResp.body().asString(), "false");

    // delete channel after testing
    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario that duplicate channel is not allowed.
   */
  @Test
  public void isDuplicateChannelNegative() throws JsonProcessingException {
    ValidatableResponse response = given(authSpec).body(prepareChannelDataSetDuplicate()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200);
    log.debug("createChannel () isDuplicateChannel " + response.log());
    Response duplicateResp = given(authSpec).queryParam("channelName", "docker-channel-duplicate")
        .body(prepareChannelDataSet()).when().get(BATCH_CHANNEL_PATH + "/duplicate");
    log.debug("isDuplicateChannelNegative " + duplicateResp.body().prettyPrint());
    assertEquals(duplicateResp.body().asString(), "true");

    // delete channel after testing
    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario to create a route.
   */
  @Test
  public void createRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId createRoute : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response =
        given(authSpec).body(prepareRouteDataSet("/root/saw-batch-samples")).when().post(routeUri)
            .then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());

    // delete route after testing
    this.tearDownRoute();
    this.tearDownChannel();
  }


  /**
   * This test-case is check the scenario that duplicate route is not allowed.
   */
  @Test
  public void isDuplicateRoutePositive() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response =
        given(authSpec).body(prepareRouteDataSet("/root/saw-batch-samples")).when().post(routeUri)
            .then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
    Response duplicateResp =
        given(authSpec).queryParam("routeName", "route123").body(prepareChannelDataSet()).when()
            .get(BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/duplicate-route");
    log.debug("duplicateResp () " + duplicateResp.body().jsonPath().get("isDuplicate"));
    assertEquals(duplicateResp.body().jsonPath().getBoolean("isDuplicate"), false);

    // delete channel after testing
    this.tearDownRoute();
    this.tearDownChannel();

  }

  /**
   * This test-case is check the scenario that duplicate route is not allowed.
   */
  @Test
  public void isDuplicateRouteNegative() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response =
        given(authSpec).body(prepareRouteDataSetDuplicate("/root/saw-batch-samples")).when()
            .post(routeUri).then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
    Response duplicateResp = given(authSpec).queryParam("routeName", "docker-route-duplicate")
        .body(prepareChannelDataSet()).when()
        .get(BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/duplicate-route");
    log.debug("isDuplicateRouteNegative () " + duplicateResp.body().jsonPath().get("isDuplicate"));
    assertEquals(duplicateResp.body().jsonPath().getBoolean("isDuplicate"), true);

    // delete channel after testing
    this.tearDownRoute();
    this.tearDownChannel();

  }

  /**
   * This test-case is check the scenario that activating & deactivating a channel.
   */
  @Test
  public void activateDeactivateChannel() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);

    String channelDeActivateUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + "deactivate";
    given(authSpec).when().put(channelDeActivateUri).then().assertThat().statusCode(200);

    String channelActivateUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + "activate";
    given(authSpec).when().put(channelActivateUri).then().assertThat().statusCode(200);

    // delete channel after testing
    this.tearDownChannel();

  }

  /**
   * This test-case is check the scenario that activating & deactivating a route.
   */
  @Test
  public void activateDeactivateRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);

    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response =
        given(authSpec).body(prepareRouteDataSet("/root/saw-batch-samples")).when().post(routeUri)
            .then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());

    Long routeId = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getLong("bisRouteSysId[0]");
    log.debug(" updateRoute bisRouteSysId : " + routeId);
    String activateUrl = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE + "/"
        + routeId + "/activate";
    String deActivateUrl = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE + "/"
        + routeId + "/deactivate";
    given(authSpec).body(prepareUpdateRouteDataSet()).when().put(activateUrl).then().assertThat()
        .statusCode(200);
    given(authSpec).body(prepareUpdateRouteDataSet()).when().put(deActivateUrl).then().assertThat()
        .statusCode(200);
    this.tearDownRoute();
    this.tearDownChannel();

  }

  /**
   * This test-case is check the scenario that listing of channels.
   */
  @Test
  public void readChannel() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    given(authSpec).body(prepareChannelDataSet()).when().get(BATCH_CHANNEL_PATH).then().assertThat()
        .statusCode(200);
    List<?> listOfChannel = given(authSpec).when().get(BATCH_CHANNEL_PATH).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getList("$");
    log.debug("readChannel :" + listOfChannel);
    assertTrue(listOfChannel.size() > 0);
    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario that listing of routes.
   */
  @Test
  public void readRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet("/root/saw-batch-samples")).when().post(routeUri)
        .then().assertThat().statusCode(200);
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    List<?> listOfRoutes = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getList("$");
    log.debug("readRoute :" + listOfRoutes);
    assertTrue(listOfRoutes.size() > 0);

    this.tearDownRoute();
    this.tearDownChannel();

  }

  /**
   * This test-case is check the scenario to update a channel.
   */
  @Test
  public void updateChannel() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatoUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId;
    log.debug("updateChannel urlForThetoUpdate : " + urlForThatoUpdate);
    String modifiedBy = given(authSpec).body(prepareUpdateChannelDataSet()).when()
        .put(urlForThatoUpdate).then().assertThat().statusCode(200).extract().response().body()
        .jsonPath().getJsonObject("modifiedBy");
    log.debug("updateChannel :" + modifiedBy);
    assertEquals("sncr@synchronoss.com", modifiedBy);
    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario to update a route.
   */
  @Test
  public void updateRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet("/root/saw-batch-samples")).when()
        .post(urlForThatRouteUpdate).then().assertThat().statusCode(200);
    log.debug("updateRoute urlForThetoUpdate : " + urlForThatRouteUpdate);
    Long routeId = given(authSpec).when().get(urlForThatRouteUpdate).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getLong("bisRouteSysId[0]");
    log.debug(" updateRoute bisRouteSysId : " + routeId);
    String urlForThatRouteUpdateById =
        BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE + "/" + routeId;
    String modifiedBy = given(authSpec).body(prepareUpdateRouteDataSet()).when()
        .put(urlForThatRouteUpdateById).then().assertThat().statusCode(200).extract().response()
        .body().jsonPath().getJsonObject("modifiedBy");
    log.debug("updateRoute :" + modifiedBy);
    assertEquals("dataAdmin@synchronoss.com", modifiedBy);
    this.tearDownRoute();
    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario to delete a channel.
   */
  @Test
  public void deleteRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet("/root/saw-batch-samples")).when()
        .post(urlForThatRouteUpdate).then().assertThat().statusCode(200);
    log.debug("updateRoute urlForThetoUpdate : " + urlForThatRouteUpdate);
    Long routeId = given(authSpec).when().get(urlForThatRouteUpdate).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getLong("bisRouteSysId[0]");
    log.debug(" updateRoute bisRouteSysId : " + routeId);
    String urlForThatRouteUpdateById =
        BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE + "/" + routeId;
    given(authSpec).when().delete(urlForThatRouteUpdateById).then().assertThat().statusCode(200);

    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario to delete a route.
   */
  @Test
  public void deleteChannel() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatoUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId;
    log.debug("deleteChannel urlForThetoUpdate : " + urlForThatoUpdate);
    given(authSpec).when().delete(urlForThatoUpdate).then().assertThat().statusCode(200);
  }

  /**
   * This method is used to tear down the channel after each test case executes.
   */
  public void tearDownChannel() throws JsonProcessingException {
    Integer bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .get(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .path("[0].bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatoUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId;
    log.debug("deleteChannel urlForThetoUpdate : " + urlForThatoUpdate);
    given(authSpec).when().delete(urlForThatoUpdate).then().assertThat().statusCode(200);
  }

  /**
   * This method is used to tear down the list of log entries after. each test case executes
   * 
   * @throws JsonProcessingException exception.
   */
  public void tearDownLogs() throws JsonProcessingException {
    log.debug("Removing entries from log starts here.");
    given(authSpec).when().delete(LOGS_HISTORY_INTERNAL).then().assertThat().statusCode(200);
    log.debug("Removing entries from log ends here.");
  }

  /**
   * This method is used to tear down the routes after each test case executes.
   */
  public void tearDownRoute() throws JsonProcessingException {
    Integer bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .get(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .path("[0].bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet("/root/saw-batch-samples")).when()
        .get(urlForThatRouteUpdate).then().assertThat().statusCode(200);
    log.debug("deleteRoute urlForThetoUpdate : " + urlForThatRouteUpdate);
    log.debug(
        "deleteRoute data " + given(authSpec).when().get(urlForThatRouteUpdate).then().assertThat()
            .statusCode(200).extract().response().getBody().jsonPath().getLong("bisRouteSysId[0]"));
    Long routeId = given(authSpec).when().get(urlForThatRouteUpdate).then().assertThat()
        .statusCode(200).extract().response().getBody().jsonPath().getLong("bisRouteSysId[0]");
    log.debug(" deleteRoute bisRouteSysId : " + routeId);
    String urlForThatRouteUpdateById =
        BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE + "/" + routeId;
    given(authSpec).when().delete(urlForThatRouteUpdateById).then().assertThat().statusCode(200);
  }

  /**
   * This test-case is check the scenario to test the connectivity of the channel.
   */
  @Test
  public void connectChannel() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("connectRoute bisChannelSysId : " + bisChannelSysId);
    String connectRouteUri =
        BATCH_PATH + "/" + BATCH_CHANNEL + "/" + bisChannelSysId + "/status";
    given(authSpec).when().get(connectRouteUri).then().assertThat().statusCode(200);
    this.tearDownChannel();
  }

  /**
   * This test-case is check the scenario to test the transfer data with filePattern : *.csv.
   */
  @Test
  public void transferData() throws JsonProcessingException {
    ObjectNode routeMetadata = prepareRouteDataSet("/root/saw-batch-samples/log/small");
    Long channelId = given(authSpec).body(prepareChannelDataSet()).when().post(BATCH_CHANNEL_PATH)
        .then().assertThat().statusCode(200).extract().response().getBody().jsonPath()
        .getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(routeMetadata).when().post(routeUri).then()
        .assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getLong("bisRouteSysId[0]");
    ObjectNode transferNode = mapper.createObjectNode();
    transferNode.put("channelId", channelId);
    transferNode.put("routeId", routeId);
    given(authSpec).when().body(transferNode).when().post(TRANSFER_DATA_PATH).then().assertThat()
        .statusCode(200);
    JsonPath path = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    log.debug("Request URL for transferData :" + ROUTE_HISTORY_PATH + channelId + "/" + routeId);
    log.debug("Json Path for transfer data :" + path.prettyPrint());
    String result = path.getString("logs[0].mflFileStatus");
    log.debug("Status of download : " + result);
    
    this.waitForSuccessFileTobeAvailable(WAIT_RETRIES, channelId, routeId);
    
    JsonPath afterWaitPath = given(authSpec).when().get(
        ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    String fileName = afterWaitPath.getString("logs[0].recdFileName");
    log.debug("Name of the downloaded file : " + fileName);
    assertNotNull(fileName);
    String afterWaitStatus = afterWaitPath.getString("logs[0].mflFileStatus");
    log.debug("Status of download : " + result);
    
    assertEquals("SUCCESS", afterWaitStatus);
    this.tearDownRoute();
    this.tearDownChannel();
    this.tearDownLogs();
  }

  /**
   * This test-case is check the scenario to test the transfer data when duplicate file is not
   * allowed disableDuplicate :false. filePattern : *.csv. disableDuplicate : false. And to simulate
   * the scenario, the API has been twice to make sure the file status to return as DUPLICATE
   * 
   * @throws IOException exception.
   */
  @Test
  public void transferDataDuplicateNotAllowed() throws IOException {
    ObjectNode routeMetadata = prepareRouteDataSet("/root/saw-batch-samples/log/small");
    Long channelId = given(authSpec).body(prepareChannelDataSet()).when().post(BATCH_CHANNEL_PATH)
        .then().assertThat().statusCode(200).extract().response().getBody().jsonPath()
        .getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(routeMetadata).when().post(routeUri).then()
        .assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getLong("bisRouteSysId[0]");
    ObjectNode transferNode = mapper.createObjectNode();
    transferNode.put("channelId", channelId);
    transferNode.put("routeId", routeId);

    // First call
    given(authSpec).when().body(transferNode).when().post(TRANSFER_DATA_PATH).then().assertThat()
        .statusCode(200);

    JsonPath caller1 = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();

    log.debug(
        "Request URL for first transfer data :" + ROUTE_HISTORY_PATH + channelId + "/" + routeId);
    log.debug("Json Path for the first transfer data :" + caller1.prettyPrint());
    String result1 = caller1.getString("logs[0].mflFileStatus");
    log.debug("Status of download for the first transfer : " + result1);
    String fileName1 = caller1.getString("logs[0].recdFileName");
    log.debug("Name of the downloaded file : " + fileName1);

    // Second Call to simulate duplicate file
    given(authSpec).when().body(transferNode).when().post(TRANSFER_DATA_PATH).then().assertThat()
        .statusCode(200);
    JsonPath caller2 = given(authSpec).when().get(LOGS_HISTORY_INTERNAL).then().assertThat()
        .statusCode(200).extract().response().jsonPath();
    log.debug("Request URL for second transfer data :" + LOGS_HISTORY_INTERNAL);
    log.debug("Json Path for second transfer data :" + caller2.prettyPrint());

    // This should have latest files because it will test
    // the scenario of sorting in desc
    log.debug("Status of download : " + caller2.getList("$").get(0));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node =
        (ObjectNode) mapper.readTree(mapper.writeValueAsString(caller2.getList("$").get(0)));
    log.debug(node.get("bisProcessState").asText());
    assertNotNull(node);
    this.tearDownRoute();
    this.tearDownChannel();
    this.tearDownLogs();
  }

  /**
   * This test-case is check the scenario to test the transfer data with some file must be excluded
   * from route disableDuplicate :false. filePattern : *.csv. fileExclusions : log. disableDuplicate
   * : false. And to make sure, the existence of the file fileExclusion file pattern has been
   * checked by putting file with a name 'sample.log' at the source
   */
  @Test
  public void transferDataWithExclusion() throws IOException {
    ObjectNode routeMetadata = prepareRouteDataSet("/root/saw-batch-samples/log/small");
    ObjectMapper mapper = new ObjectMapper();
    Long channelId = given(authSpec).body(prepareChannelDataSet()).when().post(BATCH_CHANNEL_PATH)
        .then().assertThat().statusCode(200).extract().response().getBody().jsonPath()
        .getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(routeMetadata).when().post(routeUri).then()
        .assertThat().statusCode(200);
    log.debug(
        "createRoute () " + response.extract().body().jsonPath().getJsonObject("routeMetadata"));
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getLong("bisRouteSysId[0]");
    ObjectNode transferNode = mapper.createObjectNode();
    transferNode.put("channelId", channelId);
    transferNode.put("routeId", routeId);
    given(authSpec).when().body(transferNode).when().post(TRANSFER_DATA_PATH).then().assertThat()
      .statusCode(200);
    waitForSuccessFileTobeAvailable(WAIT_RETRIES, channelId, routeId);
    JsonPath caller1 = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    log.debug("Request URL for transfer data :" + ROUTE_HISTORY_PATH + channelId + "/" + routeId);
    log.debug("Json Path for transferDataWithExclusion :" + caller1.prettyPrint());
    
    String result1 = caller1.getString("logs[0].mflFileStatus");
    log.debug("Status of download : " + result1);
    String fileName1 = caller1.getString("logs[0].recdFileName");
    log.debug("Name of the downloaded file : " + fileName1);
    log.debug("routeMetadata :" + routeMetadata);
    ObjectNode routeData = (ObjectNode) mapper
        .readTree(response.extract().body().jsonPath().getString("routeMetadata"));
    final String exclusion = routeData.get("fileExclusions").asText();
    String basePath = FilenameUtils.getFullPath(fileName1);
    log.debug("basePath :" + basePath);
    String extension = "*." + exclusion;
    log.debug("extension :" + extension);
    String filePath = basePath + extension;
    log.debug("filePath :" + filePath);
    ObjectNode testPayload = mapper.createObjectNode();
    testPayload.put("destinationLocation", filePath);
    JsonPath jsonPath =
        given(authSpec).when().body(testPayload).when().post(PLUGNIN_PATH + "/data/status").then()
            .assertThat().statusCode(200).extract().response().jsonPath();
    log.debug("Json Path for exclusion :" + jsonPath.prettify());
    assertEquals(jsonPath.getBoolean("status"), false);
    this.tearDownRoute();
    this.tearDownChannel();
    this.tearDownLogs();
  }

  /**
   * This test-case is check the scenario to test the transfer data when duplicate file is allowed
   * for a route disableDuplicate :false. filePattern : *.csv. fileExclusions : log.
   * disableDuplicate : true. And to simulate the scenario, the API has been twice to make sure the
   * file status to return as SUCCESS & Process status as DATA_RECEIVED.
   */
  @Test
  @Ignore
  public void transferDataWithDuplicateAllowed() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode routeMetadata =
        prepareRouteDataSetWithDuplicate("/root/saw-batch-samples/log/small");
    Long channelId = given(authSpec).body(prepareChannelDataSet()).when().post(BATCH_CHANNEL_PATH)
        .then().assertThat().statusCode(200).extract().response().getBody().jsonPath()
        .getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(routeMetadata).when().post(routeUri).then()
        .assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getLong("bisRouteSysId[0]");
    ObjectNode transferNode = mapper.createObjectNode();
    transferNode.put("channelId", channelId);
    transferNode.put("routeId", routeId);
    // First call
    given(authSpec).when().body(transferNode).when().post(TRANSFER_DATA_PATH).then().assertThat()
        .statusCode(200);
    this.waitForSuccessFileTobeAvailable(WAIT_RETRIES, channelId, routeId);
    JsonPath caller1 = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    log.debug("Request URL for transfer data :" + ROUTE_HISTORY_PATH + channelId + "/" + routeId);
    log.debug("Json Path for transferDataWithDisableDuplicate :" + caller1.prettyPrint());
    String result1 = caller1.getString("logs[0].mflFileStatus");
    log.debug("Status of download : " + result1);
    String fileName1 = caller1.getString("logs[0].recdFileName");
    log.debug("Name of the downloaded file : " + fileName1);

    given(authSpec).when().body(transferNode).when().post(TRANSFER_DATA_PATH).then().assertThat()
        .statusCode(200);
    this.waitForSuccessFileTobeAvailable(WAIT_RETRIES, channelId, routeId);
    // Second Call to simulate duplicate file
    JsonPath caller2 = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();

    log.debug("Request URL for transfer data :" + ROUTE_HISTORY_PATH + channelId + "/" + routeId);
    log.debug("Json Path for transferDataWithDisableDuplicate :" + caller2.prettyPrint());

    String result2 = caller2.getString("logs[0].mflFileStatus");
    // This should have latest files because it will test
    // the scenario of sorting in desc
    log.debug("Status of download : " + result2);
    String fileName2 = caller2.getString("logs[0].recdFileName");
    log.debug("Name of the downloaded file after duplicate check: " + fileName2);
    ObjectNode routeData = (ObjectNode) mapper
        .readTree(response.extract().body().jsonPath().getString("routeMetadata"));
    final Boolean duplicate = Boolean.valueOf(routeData.get("disableDuplicate").asText());
    String processStatus = caller2.getString("logs[0].bisProcessState");
    assertTrue(duplicate.equals(Boolean.TRUE));
    
    assertEquals("SUCCESS", result2);
    assertEquals("DATA_RECEIVED", processStatus);
    this.tearDownRoute();
    this.tearDownChannel();
    this.tearDownLogs();
  }

  /**
   * This test-case is check the scenario to test the transfer data with disableDuplicate :false.
   * filePattern : *.csv. fileExclusions : log. disableDuplicate : false. And to make sure, the
   * entries in the log history has been verified that should not be any entry
   */
  @Test
  public void transferDataNotZeroBytesFile() throws JsonProcessingException {
    ObjectNode routeMetadata = prepareRouteDataSet("/root/saw-batch-samples/log/zero");
    Long channelId = given(authSpec).body(prepareChannelDataSet()).when().post(BATCH_CHANNEL_PATH)
        .then().assertThat().statusCode(200).extract().response().getBody().jsonPath()
        .getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(routeMetadata).when().post(routeUri).then()
        .assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getLong("bisRouteSysId[0]");
    ObjectNode transferNode = mapper.createObjectNode();
    transferNode.put("channelId", channelId);
    transferNode.put("routeId", routeId);
    given(authSpec).when().body(transferNode).when().post(TRANSFER_DATA_PATH).then().assertThat()
        .statusCode(200);
    JsonPath caller1 = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    log.debug("Json Path for transferDataNotZeroBytesFile :" + caller1.prettyPrint());
    assertTrue(caller1.getList("logs").size() == 0);
    this.tearDownRoute();
    this.tearDownChannel();
    this.tearDownLogs();
  }

  /**
   * This test-case is check the scenario to test the transfer data with every minute schedule
   * disableDuplicate :false. filePattern : *.csv. fileExclusions : log. disableDuplicate : false.
   * batchSize:10 And to make sure, it has been triggered & downloaded. wait for the file to be
   * available has been implemented.
   * This test case is temporary ignored due to build failures.
   * It will be reenabled as part of SIP-7169 after investigation
   */
  @Ignore
  @Test
  public void transferDataSchedule() throws JsonProcessingException {
    ObjectNode routeMetadata = prepareRouteDataSetForTransferSchedule();
    log.debug("route metadata used to schedule :" + mapper.writeValueAsString(routeMetadata));
    Long channelId = given(authSpec).body(prepareChannelDataSet()).when().post(BATCH_CHANNEL_PATH)
        .then().assertThat().statusCode(200).extract().response().getBody().jsonPath()
        .getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(routeMetadata).when().post(routeUri).then()
        .assertThat().statusCode(200);
    log.debug("createRoute () " + response.extract().body());
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat().statusCode(200)
        .extract().response().jsonPath().getLong("bisRouteSysId[0]");
    ObjectNode transferNode = mapper.createObjectNode();
    transferNode.put("channelId", channelId);
    transferNode.put("routeId", routeId);
    log.debug("routeId :" + routeId);
    log.debug("channelId :" + channelId);
    // Waiting for the data set to be available
    waitForFileTobeAvailable(WAIT_RETRIES, channelId, routeId);
    log.debug(
        "Request URL for transferDataSchedule :" + ROUTE_HISTORY_PATH + channelId + "/" + routeId);
    JsonPath path = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    String result = path.getString("logs[0].mflFileStatus");
    String fileName = null;
    if (result != null) {
      log.debug("Status of the file :" + result);
      fileName = path.getString("logs[0].recdFileName");
      log.debug("Name of the downloaded file : " + fileName);
      ObjectNode testPayload = mapper.createObjectNode();
      testPayload.put("destinationLocation", fileName);
      JsonPath jsonPath =
          given(authSpec).when().body(testPayload).when().post(PLUGNIN_PATH + "/data/status").then()
              .assertThat().statusCode(200).extract().response().jsonPath();
      log.debug("Json Path for wait for file to be available :" + jsonPath.prettify());
      assertEquals(true, jsonPath.getBoolean("status"));
    }
    this.waitForSuccessFileTobeAvailable(WAIT_RETRIES, channelId, routeId);
    assertEquals("SUCCESS", result);
    this.tearDownRoute();
    this.tearDownChannel();
    this.tearDownLogs();
  }
  

  private void waitForFileTobeAvailable(int retries, Long channelId, Long routeId) {
    JsonPath path = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    log.debug("Json Path for transferDataSchedule :" + path.prettyPrint());
    if (path.getList("logs").size() == 0) {
      if (retries == 0) {
        throw new RuntimeException("Timed out waiting while waiting for dataset");
      }
      try {
        Thread.sleep(WAIT_SLEEP_SECONDS * 1000);
      } catch (InterruptedException e) {
        log.debug("Interrupted");
      }
      log.debug("waitForFileTobeAvailable triggered with number of retries : " + retries);
      waitForFileTobeAvailable(retries - 1, channelId, routeId);
    } else {
      log.debug("Data has been downloaded");
    }
  }
  
  private void waitForSuccessFileTobeAvailable(int retries, Long channelId, Long routeId) {
    JsonPath path = given(authSpec).when().get(ROUTE_HISTORY_PATH + channelId + "/" + routeId)
        .then().assertThat().statusCode(200).extract().response().jsonPath();
    log.debug("Json Path for transferDataSchedule :" + path.prettyPrint());
    if (path.getList("logs").size() == 0 || !path.getString("logs[0].mflFileStatus")
        .equals("SUCCESS")) {
      if (retries == 0) {
        throw new RuntimeException("Timed out waiting while waiting for dataset");
      }
      try {
        Thread.sleep(WAIT_SLEEP_SECONDS * 1000);
      } catch (InterruptedException e) {
        log.debug("Interrupted");
      }
      log.debug("waitForFileTobeAvailable triggered with number of retries : " + retries);
      waitForFileTobeAvailable(retries - 1, channelId, routeId);
    } else {
      log.debug("Data has been downloaded");
    }
  }
  


  /**
   * This test-case is check the scenario to test the transfer data with immediate schedule
   * disableDuplicate :false. filePattern : *.csv. fileExclusions : log. disableDuplicate : false.
   * batchSize:10 And to make sure, it has been triggered & downloaded. wait for the file to be
   * available has been implemented
   */
  @Test
  public void testImmediateRoute() throws JsonProcessingException {
    String name = "test-immediate-" + testId();
    ObjectNode channelMetadata = mapper.createObjectNode();
    channelMetadata.put("channelName", "channel-" + name);
    channelMetadata.put("channelType", "SCP");
    channelMetadata.put("hostName", "sip-admin");
    channelMetadata.put("portNo", 22);
    channelMetadata.put("accessType", "read");
    channelMetadata.put("userName", "root");
    channelMetadata.put("password", "root");
    channelMetadata.put("description", "Test");
    ObjectNode channel = mapper.createObjectNode();
    channel.put("createdBy", "admin@synchronoss.com");
    channel.put("productCode", "SIP");
    channel.put("customerCode", "SNCR");
    channel.put("projectCode", "workbench");
    channel.put("channelType", "sftp");
    channel.put("channelMetadata", new ObjectMapper().writeValueAsString(channelMetadata));;
    ObjectNode routeMetadata = mapper.createObjectNode();
    routeMetadata.put("status", "active");
    routeMetadata.put("routeName", "route-" + name);
    routeMetadata.put("sourceLocation", "/root/saw-batch-samples");
    routeMetadata.put("destinationLocation", "/log/immediate");
    routeMetadata.put("filePattern", "*.csv");
    ObjectNode schedulerNode = mapper.createObjectNode();
    schedulerNode.put("activeTab", "immediate");
    routeMetadata.set("schedulerExpression", schedulerNode);
    routeMetadata.put("description", "file");
    ObjectNode route = mapper.createObjectNode();
    route.put("createdBy", "admin@synchronoss.com");
    route.put("routeMetadata", new ObjectMapper().writeValueAsString(routeMetadata));
    Long channelId =
        given(authSpec).body(channel).when().post(BATCH_CHANNEL_PATH).then().assertThat()
            .statusCode(200).extract().response().getBody().jsonPath().getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    JsonPath path = given(authSpec).body(route).when().post(routeUri).then().assertThat()
        .statusCode(200).extract().body().jsonPath();
    assertTrue(path.getLong("bisRouteSysId") > 0);
    assertTrue(path.getLong("bisChannelSysId") > 0);
    log.debug("bisChannelSysId" + path.getLong("bisChannelSysId"));
    this.tearDownRoute();
    this.tearDownChannel();
    this.tearDownLogs();
  }


}

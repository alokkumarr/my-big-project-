package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
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
      "/services/ingestion/batch/sftp/channel/transfers/data";
  
  private static final String ROUTE_HISTORY_PATH = "/services/ingestion/batch/logs/";
  
  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  private ObjectNode prepareChannelDataSet() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("channelName", "Messaging");
    childNode.put("channelType", "SCP");
    childNode.put("hostName", "localhost");
    childNode.put("portNo", 21);
    childNode.put("accessType", "read");
    childNode.put("userName", "user");
    childNode.put("password", "saw123");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("productCode", "SIP");
    root.put("customerCode", "SNCR");
    root.put("projectCode", "workbench");
    root.put("channelType", "SFTP");
    root.put("channelMetadata", new ObjectMapper().writeValueAsString(childNode));;
    return root;
  }

  private ObjectNode prepareRouteDataSet() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("status", "active");
    childNode.put("routeName", "route123");
    childNode.put("startDate", new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
    childNode.put("endDate", new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
    childNode.put("sourceLocation", "/data");
    childNode.put("destinationLocation", "/tmp");
    childNode.put("filePattern", "*.csv");
    childNode.set("schedulerExpression", prepareSchedulerNode());
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("routeMetadata", new ObjectMapper().writeValueAsString(childNode));
    return root;
  }

  private ObjectNode prepareUpdateChannelDataSet() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("channelName", "Messaging");
    childNode.put("channelType", "SCP");
    childNode.put("hostName", "localhost");
    childNode.put("portNo", 21);
    childNode.put("accessType", "read");
    childNode.put("userName", "user");
    childNode.put("password", "saw123");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("productCode", "SIP");
    root.put("customerCode", "SNCR");
    root.put("projectCode", "workbench");
    root.put("channelType", "SFTP");
    root.put("status", "1");
    root.put("channelMetadata", new ObjectMapper().writeValueAsString(childNode));;
    root.put("modifiedBy", "sncr@synchronoss.com");
    return root;
  }

  private ObjectNode prepareSchedulerNode() {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("cronexp", "0 0 12 1 * ?");
    childNode.put("activeTab", "monthly");
    return childNode;
  }
  
  private ObjectNode prepareUpdateRouteDataSet() throws JsonProcessingException {
    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("status", "active");
    childNode.put("routeName", "route456");
    childNode.put("startDate", new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
    childNode.put("endDate", new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
    childNode.put("sourceLocation", "/tmp");
    childNode.put("destinationLocation", "/tmp");
    childNode.put("filePattern", "*.csv");
    childNode.set("schedulerExpression", prepareSchedulerNode());
    childNode.put("description", "file");
    
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("modifiedBy", "dataAdmin@synchronoss.com");
    root.put("routeMetadata", new ObjectMapper().writeValueAsString(childNode));
    root.put("status", "1");
    return root;
  }

  private FakeFtpServer createFileOnFakeFtp(String username, String password, String homeDirectory,
      String filename) {
    FakeFtpServer fakeFtpServer = new FakeFtpServer();
    fakeFtpServer.setServerControlPort(0);
    fakeFtpServer.addUserAccount(new UserAccount(username, password, homeDirectory));
    FileSystem fileSystem = new UnixFakeFileSystem();
    fileSystem.add(new DirectoryEntry("/data"));
    fakeFtpServer.setFileSystem(fileSystem);
    fakeFtpServer.start();
    return fakeFtpServer;
  }

  /**
   * The test case is to create a channel in batch Ingestion.
   */
  @Test
  public void createChannel() throws JsonProcessingException {
    
    ValidatableResponse response = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200);
    log.debug("createChannel () " + response.log());
    
    //delete channel after testing
    this.tearDownChannel();
  }
  
  /**
   * The test case is to create a channel in batch Ingestion.
   */
  @Test
  public void isDupllicateChannel() throws JsonProcessingException {
    ValidatableResponse response = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200);
    log.debug("createChannel () " + response.log());
    
    Integer bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .get(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .path("[0].bisChannelSysId");
    
    Response duplicateResp = given(authSpec).queryParam("channelName", "messaging")
        .body(prepareChannelDataSet()).when()
        .get(BATCH_CHANNEL_PATH 
            + "/duplicate");
    log.debug("duplicateResp () " + duplicateResp.body());
    //Assert.assertEquals(duplicateResp.body().asString(),"true");
    
    //delete channel after testing
    this.tearDownChannel();
  }

  /**
   * The test case is to create a route in batch Ingestion.
   */
  @Test
  public void createRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(prepareRouteDataSet()).when().post(routeUri)
        .then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
    
    
    //delete route after testing
    this.tearDownRoute();
    this.tearDownChannel();
  }

  
  /**
   * The test case is to create a channel in batch Ingestion.
   */
  @Test
  public void isDupllicateRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(prepareRouteDataSet()).when().post(routeUri)
        .then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
 
    
    Response duplicateResp = given(authSpec).queryParam("routeName", "route123")
        .body(prepareChannelDataSet()).when()
        .get(BATCH_CHANNEL_PATH 
            + "/" + bisChannelSysId + "/duplicate-route");
    log.debug("duplicateResp () " + duplicateResp.body());
    //Assert.assertEquals(duplicateResp.body().asString(),"true");
    
    //delete channel after testing
    this.tearDownRoute();
    this.tearDownChannel();
    
  }
  
  /**
   * The test case is to create a channel in batch Ingestion.
   */
  @Test
  public void activateDeactivateChannel() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    
    
    
    String channelDeActivateUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + "deactivate";
    given(authSpec).when().put(channelDeActivateUri)
        .then().assertThat().statusCode(200);
    
    String channelActivateUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + "activate";
    given(authSpec).when().put(channelActivateUri)
        .then().assertThat().statusCode(200);
 
    
    //delete channel after testing
    this.tearDownChannel();
    
  }
  
  /**
   * The test case is to create a channel in batch Ingestion.
   */
  @Test
  public void activateDeactivateRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(prepareRouteDataSet()).when().post(routeUri)
        .then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
   
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getLong("bisRouteSysId[0]");
    log.debug(" updateRoute bisRouteSysId : " + routeId);
    String activateUrl = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" 
        + BATCH_ROUTE + "/" + routeId + "/activate";
    String deActivateUrl = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" 
        + BATCH_ROUTE + "/" + routeId + "/deactivate";
  
    given(authSpec).body(prepareUpdateRouteDataSet()).when()
        .put(activateUrl).then().assertThat().statusCode(200);
  
    given(authSpec).body(prepareUpdateRouteDataSet()).when()
      .put(deActivateUrl).then().assertThat().statusCode(200);

 
    
    this.tearDownRoute();
    this.tearDownChannel();
    
  }
  
  /**
   * The test case is to read a channel in batch Ingestion.
   */
  @Test
  public void readChannel() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    
    given(authSpec).body(prepareChannelDataSet()).when().get(BATCH_CHANNEL_PATH).then()
        .assertThat().statusCode(200);
    List<?> listOfChannel = given(authSpec).when().get(BATCH_CHANNEL_PATH).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getList("$");
    log.debug("readChannel :" + listOfChannel);
    assertTrue(listOfChannel.size() > 0);
    
    this.tearDownChannel();
  }

  /**
   * The test case is to read a route in batch Ingestion.
   */
  @Test
  public void readRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet()).when().post(routeUri).then().assertThat()
        .statusCode(200);
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
   * The test case is to update a channel in batch Ingestion.
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
    assertNotNull(modifiedBy);
    
    this.tearDownChannel();
  }

  /**
   * The test case is to update a route in batch Ingestion.
   */
  @Test
  public void updateRoute() throws JsonProcessingException {
    String username = "user";
    String password = "password";
    String homeDirectory = "/";
    String filename = "report.csv";
    createFileOnFakeFtp(username, password, homeDirectory, filename);
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet()).when().post(urlForThatRouteUpdate).then()
        .assertThat().statusCode(200);
    log.debug("updateRoute urlForThetoUpdate : " + urlForThatRouteUpdate);
    Long routeId = given(authSpec).when().get(urlForThatRouteUpdate).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getLong("bisRouteSysId[0]");
    // Long routeId = Long.valueOf(bisRouteSysId.get(0).get("bisRouteSysId").toString());
    log.debug(" updateRoute bisRouteSysId : " + routeId);
    String urlForThatRouteUpdateById =
        BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE + "/" + routeId;
    String modifiedBy = given(authSpec).body(prepareUpdateRouteDataSet()).when()
        .put(urlForThatRouteUpdateById).then().assertThat().statusCode(200).extract().response()
        .body().jsonPath().getJsonObject("modifiedBy");
    log.debug("updateRoute :" + modifiedBy);
    assertNotNull(modifiedBy);
    
   
    this.tearDownRoute();
    this.tearDownChannel();
  }

  /**
   * The test case is to delete a route in batch Ingestion.
   */
  @Test
  public void deleteRoute() throws JsonProcessingException {
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet()).when().post(urlForThatRouteUpdate).then()
        .assertThat().statusCode(200);
    log.debug("updateRoute urlForThetoUpdate : " + urlForThatRouteUpdate);
    Long routeId = given(authSpec).when().get(urlForThatRouteUpdate).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getLong("bisRouteSysId[0]");
    // Long routeId = Long.valueOf(bisRouteSysId.get(0).get("bisRouteSysId").toString());
    log.debug(" updateRoute bisRouteSysId : " + routeId);
    String urlForThatRouteUpdateById =
        BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE + "/" + routeId;
    given(authSpec).when().delete(urlForThatRouteUpdateById).then().assertThat().statusCode(200);
    
    this.tearDownChannel();
  }

  /**
   * The test case is to delete a route in batch Ingestion.
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
   * The method cleansup channel in batch Ingestion.
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
   * he method cleansup riyte in batch Ingestion.
   * @throws JsonProcessingException exception
   */
  public void tearDownRoute() throws JsonProcessingException {
    Integer bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .get(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .path("[0].bisChannelSysId");
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec).body(prepareRouteDataSet()).when().get(urlForThatRouteUpdate).then()
        .assertThat().statusCode(200);
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
   * The test case is to test a connectivity route in batch Ingestion.
   */
  @Test
  public void connectChannel() throws JsonProcessingException {
    String username = "user";
    String password = "password";
    String homeDirectory = "/";
    String filename = "report.csv";
    createFileOnFakeFtp(username, password, homeDirectory, filename);
    Long bisChannelSysId = given(authSpec).body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200).extract().response().getBody()
        .jsonPath().getLong("bisChannelSysId");
    log.debug("connectRoute bisChannelSysId : " + bisChannelSysId);
    String connectRouteUri =
        BATCH_PATH + "/sftp/" + BATCH_CHANNEL + "/" + bisChannelSysId + "/status";
    given(authSpec).when().get(connectRouteUri).then().assertThat().statusCode(200);
    this.tearDownChannel();
  }
  
  /**
   * The test case is to test a connectivity route in batch Ingestion.
   */
  @Test
  public void transferData() throws JsonProcessingException {
    
    
    ObjectNode channelMetadata = mapper.createObjectNode();
    channelMetadata.put("channelName", "Messaging");
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
    channel.put("channelType", "SFTP");
    channel.put("channelMetadata", new ObjectMapper()
             .writeValueAsString(channelMetadata));
    
    ObjectNode routeMetadata = mapper.createObjectNode();
    routeMetadata.put("status", "active");
    routeMetadata.put("routeName", "route123");
    routeMetadata.put("sourceLocation", "/root/saw-batch-samples/small");
    routeMetadata.put("destinationLocation", "/data");
    routeMetadata.put("filePattern", "*.csv");
    routeMetadata.put("fileExclusions", "");
    routeMetadata.put("disableDuplicate", "false");
    ObjectNode schedulerNode = mapper.createObjectNode();
    schedulerNode.put("activeTab", "immediate");
    routeMetadata.set("schedulerExpression", schedulerNode);
    routeMetadata.put("description", "file");
    ObjectNode route = mapper.createObjectNode();
    route.put("createdBy", "admin@synchronoss.com");
    route.put("routeMetadata", new ObjectMapper()
             .writeValueAsString(routeMetadata));
    Long channelId = given(authSpec).body(channel).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200)
        .extract().response().getBody().jsonPath().getLong("bisChannelSysId");
 
    
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec).body(route).when().post(routeUri)
        .then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
   
    Long routeId = given(authSpec).when().get(routeUri).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getLong("bisRouteSysId[0]");
    
    ObjectNode transferNode = mapper.createObjectNode();
    transferNode.put("channelId", channelId);
    transferNode.put("routeId", routeId);
    
    given(authSpec).when().body(transferNode).when()
    .post(TRANSFER_DATA_PATH).then().assertThat().statusCode(200);
    
    String result = given(authSpec).when()
        .get(ROUTE_HISTORY_PATH + channelId + "/" + routeId).then().assertThat()
        .statusCode(200).extract().response().jsonPath().getString("logs[0].mflFileStatus");
    assertEquals("SUCCESS",result);
   
    this.tearDownRoute();
    this.tearDownChannel();
    
    
    
   
  }

  /**
   * Test creating a channel and route that is scheduled immediately.
   */
  @Test
  public void testImmediateRoute() throws JsonProcessingException {
    String name = "test-immediate-" + testId();
    ObjectNode channelMetadata = mapper.createObjectNode();
    channelMetadata.put("channelName", "Messaging");
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
    channel.put("channelType", "SFTP");
    channel.put("channelMetadata", new ObjectMapper()
             .writeValueAsString(channelMetadata));;
    ObjectNode routeMetadata = mapper.createObjectNode();
    routeMetadata.put("status", "active");
    routeMetadata.put("routeName", "route123");
    routeMetadata.put("sourceLocation", "/data");
    routeMetadata.put("destinationLocation", "/data");
    routeMetadata.put("filePattern", "*.log");
    ObjectNode schedulerNode = mapper.createObjectNode();
    schedulerNode.put("activeTab", "immediate");
    routeMetadata.set("schedulerExpression", schedulerNode);
    routeMetadata.put("description", "file");
    ObjectNode route = mapper.createObjectNode();
    route.put("createdBy", "admin@synchronoss.com");
    route.put("routeMetadata", new ObjectMapper()
             .writeValueAsString(routeMetadata));
    Long channelId = given(authSpec).body(channel).when()
        .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200)
        .extract().response().getBody().jsonPath().getLong("bisChannelSysId");
    String routeUri = BATCH_CHANNEL_PATH + "/" + channelId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec)
        .body(route).when().post(routeUri)
        .then().assertThat().statusCode(200);
    
    this.tearDownRoute();
    this.tearDownChannel();
    
    
  }
  
  /**
   * test transfer data.
   * @param args arguments
   */
  public static void main(String []args) {
    BatchIngestionIT bit = new BatchIngestionIT();
    try {
      bit.transferData();
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

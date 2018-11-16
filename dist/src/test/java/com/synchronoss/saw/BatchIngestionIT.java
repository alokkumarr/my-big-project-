package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.ValidatableResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
  
  private static final String BATCH_CHANNEL_PATH = 
      "/services/ingestion/batch/" + BATCH_CHANNEL;

  private static final String BATCH_PATH = "/services/ingestion/batch";
  
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
    root.put("channelMetadata",new ObjectMapper().writeValueAsString(childNode));;
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
    childNode.put("schedulerExpression", "0 0 12 1/1 * ? * *");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("routeMetadata",new ObjectMapper().writeValueAsString(childNode));
    return root;
  }
  
  private ObjectNode prepareUpdateChannelDataSet() throws JsonProcessingException {

    ObjectNode childNode = mapper.createObjectNode();
    childNode.put("channelName", "Messaging");
    childNode.put("channelType", "SCP");
    childNode.put("hostName", "saw01.ana.demo.vaste.sncrcorp.net");
    childNode.put("portNo", 22);
    childNode.put("accessType", "write");
    childNode.put("userName", "sawadmin@sncr.com");
    childNode.put("password", "saw123");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("modifiedBy", "sncr@synchronoss.com");
    root.put("productCode", "SIP");
    root.put("customerCode", "SAW");
    root.put("projectCode", "workbench");
    root.put("channelType", "SFTP");
    root.put("channelMetadata",new ObjectMapper().writeValueAsString(childNode));;
    return root;
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
    childNode.put("schedulerExpression", "0 0 12 1/1 * ? * *");
    childNode.put("description", "file");
    ObjectNode root = mapper.createObjectNode();
    root.put("createdBy", "sysadmin@synchronoss.com");
    root.put("modifiedBy", "dataAdmin@synchronoss.com");
    root.put("routeMetadata",new ObjectMapper().writeValueAsString(childNode));
    return root;
  }
  
  private FakeFtpServer createFileOnFakeFtp(String username, String password, 
      String homeDirectory, String filename) {
    FakeFtpServer fakeFtpServer = new FakeFtpServer();
    fakeFtpServer.setServerControlPort(0);
    fakeFtpServer.addUserAccount(new UserAccount(username, password, homeDirectory));
    FileSystem fileSystem = new UnixFakeFileSystem();
    fileSystem.add(new DirectoryEntry("/data"));
    fakeFtpServer.setFileSystem(fileSystem);
    fakeFtpServer.start();
    return fakeFtpServer;
  }
  
  
  private Long getChannelId() {
    //List<HashMap<Object,Object>> bisChannelSysId = given(authSpec).when().get(BATCH_CHANNEL_PATH)
    //   .then().assertThat()
    //   .statusCode(200).
    //   extract().response().jsonPath().getJsonObject("content");
    Long bisChannelSysId = given(authSpec).when().get(BATCH_CHANNEL_PATH)
        .then().assertThat()
        .statusCode(200)
        .extract().response().jsonPath().getLong("bisChannelSysId[0]");
    //Long.valueOf(bisChannelSysId.get(0).get("bisChannelSysId").toString());
    return bisChannelSysId;
  }

  /**
   * The test case is to create a channel in batch Ingestion.
   */
  @Test
  public void createChannel() throws JsonProcessingException {
    ValidatableResponse response = given(authSpec)
        .body(prepareChannelDataSet()).when()
        .post(BATCH_CHANNEL_PATH)
        .then().assertThat().statusCode(200);
    log.debug("createChannel () " + response.log());
  }

  /**
   * The test case is to create a route in batch Ingestion.
   */
  @Test
  public void createRoute() throws JsonProcessingException {
    given(authSpec)
      .body(prepareChannelDataSet()).when()
      .post(BATCH_CHANNEL_PATH)
      .then().assertThat().statusCode(200);  
    Long bisChannelSysId = getChannelId();
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    ValidatableResponse response = given(authSpec)
        .body(prepareRouteDataSet())
        .when().post(routeUri).then().assertThat().statusCode(200);
    log.debug("createRoute () " + response.log());
  }

  /**
   * The test case is to read a channel in batch Ingestion.
   */
  @Test
  public void readChannel() throws JsonProcessingException {
    given(authSpec)
    .body(prepareChannelDataSet()).when()
    .post(BATCH_CHANNEL_PATH).then().assertThat().statusCode(200);  
    List<?> listOfChannel = given(authSpec).when().get(BATCH_CHANNEL_PATH).then().assertThat()
         .statusCode(200).extract().response().jsonPath().getJsonObject("content");
    log.debug("readChannel :" + listOfChannel);
    assertTrue(listOfChannel.size() > 0);
  }

  /**
   * The test case is to read a route in batch Ingestion.
  */
  @Test
  public void readRoute() throws JsonProcessingException {
    given(authSpec)
    .body(prepareChannelDataSet()).when()
    .post(BATCH_CHANNEL_PATH)
    .then().assertThat().statusCode(200);  
    Long bisChannelSysId = getChannelId();
    String routeUri = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec)
        .body(prepareRouteDataSet())
        .when().post(routeUri).then().assertThat().statusCode(200);
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    List<?> listOfRoutes = given(authSpec).when().get(routeUri)
         .then().assertThat().statusCode(200)
         .extract().response().jsonPath().getJsonObject("content");
    log.debug("readRoute :" + listOfRoutes);
    assertTrue(listOfRoutes.size() > 0);
  }

  /**
   * The test case is to update a channel in batch Ingestion.
  */
  @Test
  public void updateChannel() throws JsonProcessingException {
    given(authSpec)
    .body(prepareChannelDataSet()).when().post(BATCH_CHANNEL_PATH)
    .then().assertThat().statusCode(200);  
    Long bisChannelSysId = getChannelId();
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatoUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId;
    log.debug("updateChannel urlForThetoUpdate : " + urlForThatoUpdate);
    String modifiedBy = given(authSpec).body(prepareUpdateChannelDataSet())
        .when().put(urlForThatoUpdate).then().assertThat().statusCode(200).extract().response()
        .body().jsonPath().getJsonObject("modifiedBy");
    log.debug("updateChannel :" + modifiedBy);
    assertNotNull(modifiedBy);
  }

  /**
   * The test case is to update a route in batch Ingestion.
   */
  @Test
  public void updateRoute() throws JsonProcessingException {
    given(authSpec)
    .body(prepareChannelDataSet()).when()
    .post(BATCH_CHANNEL_PATH)
    .then().assertThat().statusCode(200);  
    Long bisChannelSysId = getChannelId();
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec)
            .body(prepareRouteDataSet())
            .when().post(urlForThatRouteUpdate).then().assertThat().statusCode(200);
    log.debug("updateRoute urlForThetoUpdate : " + urlForThatRouteUpdate);
    List<HashMap<Object,Object>> bisRouteSysId = given(authSpec)
        .when().get(urlForThatRouteUpdate).then()
        .assertThat().statusCode(200).extract().response().jsonPath().getJsonObject("content");
    Long routeId = Long.valueOf(bisRouteSysId.get(0).get("bisRouteSysId").toString());
    log.debug(" updateRoute bisRouteSysId : " + routeId);
    String urlForThatRouteUpdateById = BATCH_CHANNEL_PATH + "/" 
        + bisChannelSysId + "/" + BATCH_ROUTE + "/" + routeId;
    String modifiedBy = given(authSpec).body(prepareUpdateRouteDataSet())
        .when().put(urlForThatRouteUpdateById).then()
        .assertThat().statusCode(200).extract().response()
        .body().jsonPath()
        .getJsonObject("modifiedBy");
    log.debug("updateRoute :" + modifiedBy);
    assertNotNull(modifiedBy);
  }

  /**
    * The test case is to delete a route in batch Ingestion.
    */
  @Test
  public void deleteRoute() throws JsonProcessingException {
    given(authSpec)
   .body(prepareChannelDataSet()).when()
   .post(BATCH_CHANNEL_PATH)
   .then().assertThat().statusCode(200);  
    Long bisChannelSysId = getChannelId();
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatRouteUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId + "/" + BATCH_ROUTE;
    given(authSpec)
            .body(prepareRouteDataSet())
            .when().post(urlForThatRouteUpdate).then().assertThat().statusCode(200);
    log.debug("deleteRoute urlForThetoUpdate : " + urlForThatRouteUpdate);
    List<HashMap<Object,Object>> bisRouteSysId = given(authSpec)
            .when().get(urlForThatRouteUpdate).then()
            .assertThat().statusCode(200).extract().response().jsonPath().getJsonObject("content");
    Long routeId = Long.valueOf(bisRouteSysId.get(0).get("bisRouteSysId").toString());
    log.debug(" deleteRoute bisRouteSysId : " + routeId);
    String urlForThatRouteUpdateById = BATCH_CHANNEL_PATH + "/" 
            + bisChannelSysId + "/" + BATCH_ROUTE + "/" + routeId;
    given(authSpec)
   .when().delete(urlForThatRouteUpdateById).then()
    .assertThat().statusCode(200);
  }

  /**
   * The test case is to delete a route in batch Ingestion.
   */
  @Test
  public void deleteChannel() throws JsonProcessingException {
    given(authSpec)
    .body(prepareChannelDataSet()).when()
    .post(BATCH_CHANNEL_PATH)
    .then().assertThat().statusCode(200);  
    Long bisChannelSysId = getChannelId();
    log.debug("bisChannelSysId : " + bisChannelSysId);
    assertFalse(bisChannelSysId <= 0);
    String urlForThatoUpdate = BATCH_CHANNEL_PATH + "/" + bisChannelSysId;
    log.debug("deleteChannel urlForThetoUpdate : " + urlForThatoUpdate);
    given(authSpec).when().delete(urlForThatoUpdate).then()
   .assertThat().statusCode(200);
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
    given(authSpec)
    .body(prepareChannelDataSet()).when()
    .post(BATCH_CHANNEL_PATH)
    .then().assertThat().statusCode(200);
    Long bisChannelSysId = getChannelId();
    log.debug("connectRoute bisChannelSysId : " + bisChannelSysId);
    String connectRouteUri = BATCH_PATH + "/sftp/" + BATCH_CHANNEL + "/"
        + bisChannelSysId + "/status";
    given(authSpec).when().get(connectRouteUri).then()
   .assertThat().statusCode(200);
  }
}

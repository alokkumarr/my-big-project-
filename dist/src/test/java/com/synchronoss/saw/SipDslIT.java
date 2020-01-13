package com.synchronoss.saw;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class SipDslIT extends BaseIT {
  String analysisId = "f37cde24-b833-46ba-ae2d-42e286c3fc39";
  protected JsonObject testData = null;
  protected JsonObject sipQuery = null;
  protected JsonObject testDataForDl = null;
  protected JsonObject sipQueryDl = null;
  protected String customToken;
  protected String tokenForNegativeCases;
  private static final String TENANT_A = "TenantA";
  private static final String TENANT_B = "TenantB";
  private static final String TENANT_C = "TenantC";
  private static final String CUSTOMER_CODE = "customerCode";
  private final Logger logger = LoggerFactory.getLogger(getClass().getName());

  @Before
  public void setUpData() throws JsonProcessingException {
    customToken = authenticate("sawadmin@" + TENANT_A + ".com", "Sawsyncnewuser1!");
    tokenForNegativeCases = authenticate("reviewer@synchronoss.com", "Sawsyncnewuser1!");
    testData = new JsonObject();
    testData.addProperty("type", "esReport");
    testData.addProperty("semanticId", "workbench::sample-elasticsearch-TenantA");
    testData.addProperty("id", analysisId);
    testData.addProperty(CUSTOMER_CODE, "SYNCHRONOSS");
    testData.addProperty("projectCode", "workbench");
    testData.addProperty("module", "ANALYZE");
    testData.addProperty("createdTime", 1543921879);
    testData.addProperty("createdBy", "sipadmin@synchronoss.com");
    testData.addProperty("modifiedTime", 1543921879);
    testData.addProperty("modifiedBy", "sipadmin@synchronoss.com");
    testData.addProperty("userId", 1);
    testData.addProperty("name", "Untitled Analysis");
    testData.addProperty("semanticId", "workbench::sample-elasticsearch-TenantA");

    Instant instant = Instant.now();
    JsonObject analysis = new JsonObject();
    analysis.addProperty("type", "esReport");
    analysis.addProperty("semanticId", "workbench::sample-elasticsearch-TenantA");
    analysis.addProperty("id", analysisId);
    analysis.addProperty(CUSTOMER_CODE, "SYNCHRONOSS");
    analysis.addProperty("projectCode", "workbench");
    analysis.addProperty("module", "ANALYZE");
    analysis.addProperty("createdTime", instant.getEpochSecond());
    analysis.addProperty("createdBy", "sipadmin@synchronoss.com");
    analysis.addProperty("modifiedTime", instant.getEpochSecond());
    analysis.addProperty("modifiedBy", "sipadmin@synchronoss.com");
    analysis.addProperty("userId", 1);
    analysis.addProperty("name", "Untitled Analysis");
    testData.add("analysis", analysis);

    sipQuery = new JsonObject();

    JsonObject artifact1 = new JsonObject();
    artifact1.addProperty("artifactsName", "sample");

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", "string");
    field1.addProperty("area", "x-axis");
    field1.addProperty("alias", "String");
    field1.addProperty("columnName", "string.keyword");
    field1.addProperty("displayName", "String");
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);

    JsonObject field2 = new JsonObject();
    field2.addProperty("dataField", "integer");
    field2.addProperty("area", "y-axis");
    field2.addProperty("columnName", "integer");
    field2.addProperty("displayName", "integer");
    field2.addProperty("type", "integer");
    field2.addProperty("aggregate", "avg");
    artifactFields.add(field2);

    JsonObject field3 = new JsonObject();
    field3.addProperty("dataField", "long");
    field3.addProperty("area", "z-axis");
    field3.addProperty("columnName", "long");
    field3.addProperty("displayName", "Long");
    field3.addProperty("type", "long");
    field3.addProperty("aggregate", "avg");
    artifactFields.add(field3);

    JsonObject field4 = new JsonObject();
    field4.addProperty("dataField", "date");
    field4.addProperty("area", "g-axis");
    field4.addProperty("columnName", "date");
    field4.addProperty("displayName", "Date");
    field4.addProperty("type", "date");
    field4.addProperty("groupInterval", "year");
    artifactFields.add(field4);

    JsonObject field5 = new JsonObject();
    field5.addProperty("dataField", "double");
    field5.addProperty("area", "m-axis");
    field5.addProperty("columnName", "double");
    field5.addProperty("displayName", "Double");
    field5.addProperty("type", "double");
    field5.addProperty("aggregate", "avg");
    artifactFields.add(field5);

    JsonObject field6 = new JsonObject();
    field6.addProperty("dataField", "float");
    field6.addProperty("area", "m-axis");
    field6.addProperty("columnName", "float");
    field6.addProperty("displayName", "Float");
    field6.addProperty("type", "double");
    field6.addProperty("aggregate", "avg");
    artifactFields.add(field6);

    artifact1.add("fields", artifactFields);
    JsonArray artifacts = new JsonArray();
    artifacts.add(artifact1);
    sipQuery.add("artifacts", artifacts);
    sipQuery.addProperty("booleanCriteria", "AND");

    JsonObject filter1 = new JsonObject();
    filter1.addProperty("type", "long");
    filter1.addProperty("artifactsName", "sample");
    filter1.addProperty("isOptional", false);
    filter1.addProperty("columnName", "long");
    filter1.addProperty("isRuntimeFilter", false);
    filter1.addProperty("isGlobalFilter", false);
    JsonObject model = new JsonObject();
    model.addProperty("operator", "EQ");
    model.addProperty("value", 1000);
    filter1.add("model", model);
    JsonArray filters = new JsonArray();
    filters.add(filter1);

    JsonObject filter2 = new JsonObject();
    filter2.addProperty("type", "date");
    filter2.addProperty("tableName", "sample");
    filter2.addProperty("isOptional", false);
    filter2.addProperty("columnName", "date");
    filter2.addProperty("isRuntimeFilter", false);
    filter2.addProperty("isGlobalFilter", false);
    model = new JsonObject();
    model.addProperty("format", "epoch_second");
    model.addProperty("operator", "NEQ");
    model.addProperty("value", 1483228800000L);
    model.addProperty("otherValue", 1483228800000L);
    filter2.add("model", model);
    filters.add(filter2);

    JsonObject filter3 = new JsonObject();
    filter3.addProperty("type", "date");
    filter3.addProperty("tableName", "sample");
    filter3.addProperty("isOptional", false);
    filter3.addProperty("columnName", "date");
    filter3.addProperty("isRuntimeFilter", false);
    filter3.addProperty("isGlobalFilter", false);
    model = new JsonObject();
    model.addProperty("format", "epoch_millis");
    model.addProperty("operator", "BTW");
    model.addProperty("value", 1451606400000L);
    model.addProperty("otherValue", 1551606400000L);
    filter3.add("model", model);
    filters.add(filter3);

    JsonObject filter4 = new JsonObject();
    filter4.addProperty("type", "date");
    filter4.addProperty("tableName", "sample");
    filter4.addProperty("isOptional", false);
    filter4.addProperty("columnName", "date");
    filter4.addProperty("isRuntimeFilter", false);
    filter4.addProperty("isGlobalFilter", false);
    model = new JsonObject();
    model.addProperty("format", "YYYY");
    model.addProperty("operator", "GTE");
    model.addProperty("value", 2017);
    filter4.add("model", model);
    filters.add(filter4);

    sipQuery.add("filters", filters);

    JsonObject sort1 = new JsonObject();
    sort1.addProperty("artifacts", "sample");
    sort1.addProperty("aggregate", "sum");
    sort1.addProperty("columnName", "long");
    sort1.addProperty("type", "long");
    sort1.addProperty("order", "asc");
    JsonArray sorts = new JsonArray();
    sorts.add(sort1);

    sipQuery.add("sorts", sorts);
    sipQuery.addProperty("semanticId", "workbench::sample-elasticsearch-TenantA");

    JsonObject store = new JsonObject();
    store.addProperty("dataStore", "sampleAlias/sample");
    store.addProperty("storageType", "ES");

    sipQuery.add("store", store);
    testData.add("sipQuery", sipQuery);
  }

  @Before
  public void setUpDataForDl() {
    testDataForDl = new JsonObject();
    testDataForDl.addProperty("type", "report");
    testDataForDl.addProperty("name", "integrationTest");
    testDataForDl.addProperty("semanticId", "workbench::sample-spark");
    testDataForDl.addProperty("metricName", "sample-spark");
    testDataForDl.addProperty("id", analysisId);
    testDataForDl.addProperty(CUSTOMER_CODE, "SYNCHRONOSS");
    testDataForDl.addProperty("projectCode", "sip-sample");
    testDataForDl.addProperty("module", "productSpecific/ANALYZE");
    testDataForDl.addProperty("createdTime", 1543921879);
    testDataForDl.addProperty("createdBy", "sipadmin@synchronoss.com");
    testDataForDl.addProperty("modifiedTime", 1543921879);
    testDataForDl.addProperty("modifiedBy", "sipadmin@synchronoss.com");
    testDataForDl.addProperty("userId", 1);
    testDataForDl.addProperty("designerEdit", false);
    JsonObject artifact1 = new JsonObject();
    artifact1.addProperty("artifactsName", "SALES");
    sipQueryDl = new JsonObject();
    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", "string");
    field1.addProperty("alias", "String");
    field1.addProperty("columnName", "string.keyword");
    field1.addProperty("displayName", "String");
    field1.addProperty("table", "sales");
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);
    JsonObject field2 = new JsonObject();
    field2.addProperty("dataField", "long");
    field2.addProperty("columnName", "long");
    field2.addProperty("displayName", "long");
    field2.addProperty("table", "sales");
    field2.addProperty("type", "long");
    artifactFields.add(field2);
    JsonObject field3 = new JsonObject();
    field3.addProperty("dataField", "float");
    field3.addProperty("columnName", "float");
    field3.addProperty("displayName", "Float");
    field3.addProperty("table", "sales");
    field3.addProperty("type", "float");
    artifactFields.add(field3);
    JsonObject field4 = new JsonObject();
    field4.addProperty("dataField", "date");
    field4.addProperty("columnName", "date");
    field4.addProperty("displayName", "Date");
    field4.addProperty("table", "sales");
    field4.addProperty("type", "date");
    artifactFields.add(field4);
    JsonObject field5 = new JsonObject();
    field5.addProperty("dataField", "double");
    field5.addProperty("columnName", "double");
    field5.addProperty("displayName", "Double");
    field5.addProperty("table", "sales");
    field5.addProperty("type", "double");
    artifactFields.add(field5);
    artifact1.add("fields", artifactFields);
    JsonArray artifacts = new JsonArray();
    artifacts.add(artifact1);
    JsonObject artifact2 = new JsonObject();
    artifact2.addProperty("artifactsName", "PRODUCT");
    JsonObject field6 = new JsonObject();
    field6.addProperty("dataField", "string_2");
    field6.addProperty("columnName", "string_2");
    field6.addProperty("displayName", "string_2");
    field6.addProperty("table", "product");
    field6.addProperty("type", "string");
    JsonArray artifactFields2 = new JsonArray();
    artifactFields2.add(field6);
    JsonObject field7 = new JsonObject();
    field7.addProperty("dataField", "long_2");
    field7.addProperty("columnName", "long_2");
    field7.addProperty("displayName", "long_2");
    field7.addProperty("table", "product");
    field7.addProperty("type", "long");
    artifactFields2.add(field7);
    artifact2.add("fields", artifactFields2);
    artifacts.add(artifact2);

    sipQueryDl.add("artifacts", artifacts);
    sipQueryDl.addProperty("booleanCriteria", "AND");

    JsonObject filter1 = new JsonObject();
    filter1.addProperty("type", "double");
    filter1.addProperty("artifactsName", "SALES");
    filter1.addProperty("isOptional", false);
    filter1.addProperty("columnName", "double");
    filter1.addProperty("isRuntimeFilter", false);
    filter1.addProperty("isGlobalFilter", false);
    JsonObject model = new JsonObject();
    model.addProperty("operator", "GTE");
    model.addProperty("value", 1000);
    filter1.add("model", model);
    JsonArray filters = new JsonArray();
    filters.add(filter1);

    JsonObject filter2 = new JsonObject();
    filter2.addProperty("type", "date");
    filter2.addProperty("artifactsName", "PRODUCT");
    filter2.addProperty("isOptional", false);
    filter2.addProperty("columnName", "date_2");
    filter2.addProperty("isRuntimeFilter", false);
    filter2.addProperty("isGlobalFilter", false);
    model = new JsonObject();
    model.addProperty("preset", "LY");
    filter2.add("model", model);
    filters.add(filter2);

    sipQueryDl.add("filters", filters);

    JsonArray sorts = new JsonArray();
    sipQueryDl.add("sorts", sorts);

    JsonObject join = new JsonObject();
    join.addProperty("join", "inner");

    JsonObject leftTable = new JsonObject();
    leftTable.addProperty("artifactsName", "PRODUCT");
    leftTable.addProperty("columnName", "string_2");
    JsonObject rightTable = new JsonObject();
    rightTable.addProperty("artifactsName", "SALES");
    rightTable.addProperty("columnName", "string");
    JsonObject joinTables = new JsonObject();
    joinTables.addProperty("operator", "EQ");
    joinTables.add("left", leftTable);
    joinTables.add("right", rightTable);

    JsonObject criteria1 = new JsonObject();
    criteria1.add("joinCondition", joinTables);
    JsonArray criterianArray = new JsonArray();

    criterianArray.add(criteria1);
    join.add("criteria", criterianArray);
    JsonArray joinArray = new JsonArray();
    joinArray.add(join);

    sipQueryDl.add("joins", joinArray);
    sipQueryDl.addProperty("semanticId", "workbench::sample-spark");
    testDataForDl.add("sipQuery", sipQueryDl);
  }

  @Test
  public void testSipDsl() throws IOException {
    int testFilterData = 100; // To assert Filter condition.
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testData.toString());
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(jsonNode)
            .when()
            .post(
                "/sip/services/internal/proxy/storage/fetch"
                    + "?id=f37cde24-b833-46ba-ae2d-42e286c3fc39&ExecutionType=preview")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    ArrayNode root = response.getBody().as(ArrayNode.class);
    Assert.assertEquals(root.get(0).get("integer").asInt(), testFilterData);
  }

  @Test
  public void testSipDslExecute() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testData.toString());
    String testStringFilter = "string 1";
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(jsonNode)
            .when()
            .post("/sip/services/internal/proxy/storage/execute?id=" + analysisId)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    ObjectNode a = response.getBody().as(ObjectNode.class);
    ArrayNode data = a.withArray("data");
    Long countOfRows = a.get("totalRows").asLong();
    Assert.assertTrue(countOfRows > 0);
    Assert.assertEquals(data.get(0).get("string").asText(), testStringFilter);
    List<Map<String, String>> dataNode = response.getBody().path("data");
    Assert.assertEquals(dataNode.get(0).get("string"), testStringFilter);
  }

  @Test
  public void testCustomerCodeFilter() throws IOException {
    JsonObject sipDsl = testData;
    sipDsl.addProperty(CUSTOMER_CODE, TENANT_A);
    String validateCustCode = TENANT_A;

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", CUSTOMER_CODE);
    field1.addProperty("area", "x-axis");
    field1.addProperty("alias", CUSTOMER_CODE);
    field1.addProperty("columnName", CUSTOMER_CODE + ".keyword");
    field1.addProperty("displayName", CUSTOMER_CODE);
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);

    JsonElement js = new JsonArray();

    sipDsl
        .get("sipQuery")
        .getAsJsonObject()
        .get("artifacts")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("fields")
        .getAsJsonArray()
        .set(1, field1);
    sipDsl.get("sipQuery").getAsJsonObject().add("filters", js);
    sipDsl.get("sipQuery").getAsJsonObject().add("sorts", js);

    // Below code uses customToken (i,e. uname = "sawadmin@TenantA.com", this customer is added
    // implicitly on every docker start to validate true multi tenancy tests).
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(sipDsl.toString());
    Response response = execute(customToken, jsonNode);
    Assert.assertNotNull(response);
    ObjectNode attUserRes = response.getBody().as(ObjectNode.class);
    ArrayNode data = attUserRes.withArray("data");
    Long countOfRows = attUserRes.get("totalRows").asLong();
    Iterator<JsonNode> iterator = data.iterator();
    while (iterator.hasNext()) {
      JsonNode dataNode = iterator.next();
      Assert.assertEquals(dataNode.get(CUSTOMER_CODE).asText(), validateCustCode);
      // Verifying for each response data object to check customerCode filtering.
    }
    Assert.assertTrue(countOfRows > 0);

    // Below code makes use of regular token (i,e. uName : 'sawadmin@synchronoss.com')
    // We treat existing production customers as super admins, so no customer code filtering.
    Response response1 = execute(token, jsonNode);
    Assert.assertNotNull(response1);
    ObjectNode syncUserRes = response1.getBody().as(ObjectNode.class);
    ArrayNode syncData = syncUserRes.withArray("data");
    Assert.assertTrue(syncUserRes.get("totalRows").asLong() > 0);
    Iterator<JsonNode> itr = syncData.iterator();
    int tenantA = 0;
    int tenantB = 0;
    int tenantC = 0;
    while (itr.hasNext()) {
      JsonNode dat = itr.next();
      tenantA = dat.get(CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_A) ? ++tenantA : tenantA;
      tenantB = dat.get(CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_B) ? ++tenantB : tenantB;
      tenantC = dat.get(CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_C) ? ++tenantC : tenantC;
    }
    Assert.assertTrue(tenantA > 0);
    Assert.assertTrue(tenantB > 0);
    Assert.assertTrue(tenantC > 0);
  }

  @Test
  public void testCustomerCodeFilterWithDsk() throws IOException {
    // Add security group for TenantA customer.
    ObjectNode secGroup = mapper.createObjectNode();
    secGroup.put("description", "TestDesc5");
    secGroup.put("securityGroupName", "TestGroup5");
    Response secGroupRes =
        given(spec)
            .header("Authorization", "Bearer " + customToken)
            .contentType(ContentType.JSON)
            .body(secGroup)
            .when()
            .post("/security/auth/admin/security-groups")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    JsonNode secGroups = secGroupRes.as(JsonNode.class);
    Long groupSysId = secGroups.get("groupId").asLong();
    logger.debug("security groupId : {}",groupSysId);

    ObjectNode dskAttValues = mapper.createObjectNode();
    dskAttValues.put("booleanCriteria","AND");
    ObjectNode dskValues = mapper.createObjectNode();
    dskValues.put("columnName","string");
    ArrayNode values = mapper.createArrayNode();
    values.add("string 1");
    ObjectNode model = mapper.createObjectNode();
    model.put("operator","ISIN");
    model.put("values",values);
    dskValues.put("model",model);
    ArrayNode booleanQuery = mapper.createArrayNode();
    booleanQuery.add(dskValues);
    dskAttValues.put("booleanQuery",booleanQuery);

    given(spec)
        .header("Authorization", "Bearer " + customToken)
        .contentType(ContentType.JSON)
        .body(dskAttValues)
        .when()
        .put("/security/auth/admin/dsk-security-groups/" + groupSysId)
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));
    given(spec)
        .header("Authorization", "Bearer " + customToken)
        .body("TestGroup5")
        .when()
        .put("/security/auth/admin/users/" + 5 + "/security-group")
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));
    JsonObject sipDsl = testData;
    sipDsl.addProperty(CUSTOMER_CODE, TENANT_A);
    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", CUSTOMER_CODE);
    field1.addProperty("area", "x-axis");
    field1.addProperty("alias", CUSTOMER_CODE);
    field1.addProperty("columnName", CUSTOMER_CODE + ".keyword");
    field1.addProperty("displayName", CUSTOMER_CODE);
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);
    JsonElement js = new JsonArray();
    sipDsl
        .get("sipQuery")
        .getAsJsonObject()
        .get("artifacts")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("fields")
        .getAsJsonArray()
        .set(1, field1);
    sipDsl.get("sipQuery").getAsJsonObject().add("filters", js);
    sipDsl.get("sipQuery").getAsJsonObject().add("sorts", js);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(sipDsl.toString());
    // Update token after applying DSK.
    String customTok = authenticate("sawadmin@" + TENANT_A + ".com", "Sawsyncnewuser1!");
    Response response = execute(customTok, jsonNode);
    Assert.assertNotNull(response);
    ObjectNode attUserRes = response.getBody().as(ObjectNode.class);
    ArrayNode data = attUserRes.withArray("data");
    Assert.assertTrue(data.size() == 1);
    String validateCustCode = TENANT_A;
    Assert.assertEquals(data.get(0).get(CUSTOMER_CODE).asText(), validateCustCode);
    Assert.assertEquals(data.get(0).get("string").asText(), "string 1");
    given(authSpec)
        .body("TestGroup5")
        .when()
        .delete("/security/auth/admin/security-groups/" + groupSysId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testSipDslDataLakeExecute() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testDataForDl.toString());
    String testStringFilter = "string 247";
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(jsonNode)
            .when()
            .post("/sip/services/internal/proxy/storage/execute?id=" + analysisId)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    ObjectNode a = response.getBody().as(ObjectNode.class);
    ArrayNode data = a.withArray("data");
    Long countOfRows = a.get("totalRows").asLong();
    Assert.assertTrue(countOfRows > 0);
    Assert.assertEquals(data.get(0).get("string").asText(), testStringFilter);
    List<Map<String, String>> dataNode = response.getBody().path("data");
    Assert.assertEquals(dataNode.get(0).get("string"), testStringFilter);
  }

  @Test
  public void testDlExecuteWithCustCodeFilter() throws IOException {
    JsonObject sipDsl = testDataForDl;
    sipDsl.addProperty(CUSTOMER_CODE, TENANT_A);

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", CUSTOMER_CODE);
    field1.addProperty("table", "sales");
    field1.addProperty("alias", CUSTOMER_CODE);
    field1.addProperty("columnName", CUSTOMER_CODE);
    field1.addProperty("displayName", CUSTOMER_CODE);
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);

    sipDsl
        .get("sipQuery")
        .getAsJsonObject()
        .get("artifacts")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("fields")
        .getAsJsonArray()
        .set(1, field1);

    field1.addProperty("table", "product");
    sipDsl
        .get("sipQuery")
        .getAsJsonObject()
        .get("artifacts")
        .getAsJsonArray()
        .get(1)
        .getAsJsonObject()
        .get("fields")
        .getAsJsonArray()
        .set(1, field1);

    JsonElement js = new JsonArray();

    sipDsl.get("sipQuery").getAsJsonObject().add("filters", js);
    sipDsl.get("sipQuery").getAsJsonObject().add("sorts", js);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode sipDslQuery = objectMapper.readTree(sipDsl.toString());
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + customToken)
            .body(sipDslQuery)
            .when()
            .post("/sip/services/internal/proxy/storage/execute?id=" + analysisId)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    ObjectNode a = response.getBody().as(ObjectNode.class);
    Long countOfRows = a.get("totalRows").asLong();
    Assert.assertTrue(countOfRows > 0);

    ObjectNode attUserRes = response.getBody().as(ObjectNode.class);
    ArrayNode attData = attUserRes.withArray("data");
    Iterator<JsonNode> iterator = attData.iterator();
    String validateCustCode = TENANT_A;
    while (iterator.hasNext()) {
      JsonNode dataNode = iterator.next();
      Assert.assertEquals(dataNode.get("SALES_" + CUSTOMER_CODE).asText(), validateCustCode);
      Assert.assertEquals(dataNode.get("PRODUCT_" + CUSTOMER_CODE).asText(), validateCustCode);
      // Verifying for each response data object to check customerCode filtering.
    }

    Response syncResponse =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(sipDslQuery)
            .when()
            .post("/sip/services/internal/proxy/storage/execute?id=" + analysisId)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(syncResponse);
    ObjectNode b = syncResponse.getBody().as(ObjectNode.class);
    Long syncResCount = b.get("totalRows").asLong();
    Assert.assertTrue(syncResCount > 0);

    ObjectNode syncUserRes = syncResponse.getBody().as(ObjectNode.class);
    ArrayNode syncData = syncUserRes.withArray("data");

    int tenantA = 0;
    int tenantB = 0;
    int tenantC = 0;

    Iterator<JsonNode> itr = syncData.iterator();
    while (itr.hasNext()) {
      JsonNode dat = itr.next();
      tenantA =
          dat.get("SALES_" + CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_A)
                  && dat.get("PRODUCT_" + CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_A)
              ? ++tenantA
              : tenantA;
      tenantB =
          dat.get("SALES_" + CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_B)
                  && dat.get("PRODUCT_" + CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_B)
              ? ++tenantB
              : tenantB;
      tenantC =
          dat.get("SALES_" + CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_C)
                  && dat.get("PRODUCT_" + CUSTOMER_CODE).asText().equalsIgnoreCase(TENANT_C)
              ? ++tenantC
              : tenantC;
    }
    Assert.assertTrue(tenantA > 0);
    Assert.assertTrue(tenantB > 0);
    Assert.assertTrue(tenantC > 0);
  }

  @Test
  public void testDskWithCustCodeFilterDl() throws IOException {
    JsonObject sipDsl = testDataForDl;
    sipDsl.addProperty(CUSTOMER_CODE, TENANT_A);

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", CUSTOMER_CODE);
    field1.addProperty("table", "sales");
    field1.addProperty("alias", CUSTOMER_CODE);
    field1.addProperty("columnName", CUSTOMER_CODE);
    field1.addProperty("displayName", CUSTOMER_CODE);
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);

    JsonElement js = new JsonArray();

    sipDsl
        .get("sipQuery")
        .getAsJsonObject()
        .get("artifacts")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("fields")
        .getAsJsonArray()
        .set(1, field1);

    sipDsl.get("sipQuery").getAsJsonObject().add("filters", js);
    sipDsl.get("sipQuery").getAsJsonObject().add("sorts", js);

    JsonObject artifacts =
        sipDsl
            .get("sipQuery")
            .getAsJsonObject()
            .get("artifacts")
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject();

    JsonArray artifactsList = new JsonArray();
    artifactsList.add(artifacts);

    sipDsl.get("sipQuery").getAsJsonObject().add("artifacts", artifactsList);

    sipDsl.get("sipQuery").getAsJsonObject().add("joins", new JsonArray());

    // Add security group for TenantA customer.
    ObjectNode secGroup = mapper.createObjectNode();
    secGroup.put("description", "TestDesc2");
    secGroup.put("securityGroupName", "TestGroup2");
    Response secGroupRes =
        given(spec)
            .header("Authorization", "Bearer " + customToken)
            .contentType(ContentType.JSON)
            .body(secGroup)
            .when()
            .post("/security/auth/admin/security-groups")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    JsonNode secGroups = secGroupRes.as(JsonNode.class);
    Long groupSysId = secGroups.get("groupId").asLong();
    logger.debug("security groupId : {}",groupSysId);

    ObjectNode dskAttValues = mapper.createObjectNode();
    dskAttValues.put("booleanCriteria","AND");
    ObjectNode dskValues = mapper.createObjectNode();
    dskValues.put("columnName","string");
    ArrayNode values = mapper.createArrayNode();
    values.add("string 1");
    ObjectNode model = mapper.createObjectNode();
    model.put("operator","ISIN");
    model.put("values",values);
    dskValues.put("model",model);
    ArrayNode booleanQuery = mapper.createArrayNode();
    booleanQuery.add(dskValues);
    dskAttValues.put("booleanQuery",booleanQuery);
    given(spec)
        .header("Authorization", "Bearer " + customToken)
        .contentType(ContentType.JSON)
        .body(dskAttValues)
        .when()
        .put("/security/auth/admin/dsk-security-groups/" + groupSysId)
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));

    given(spec)
        .header("Authorization", "Bearer " + customToken)
        .body("TestGroup2")
        .when()
        .put("/security/auth/admin/users/" + 5 + "/security-group")
        .then()
        .assertThat()
        .statusCode(200)
        .body("valid", equalTo(true));

    // Update token after applying DSK.
    String customTok = authenticate("sawadmin@" + TENANT_A + ".com", "Sawsyncnewuser1!");

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(sipDsl.toString());
    Response response = execute(customTok, jsonNode);
    Assert.assertNotNull(response);
    ObjectNode attUserRes = response.getBody().as(ObjectNode.class);
    ArrayNode data = attUserRes.withArray("data");
    Assert.assertTrue(data.size() == 1);
    String validateCustCode = TENANT_A;
    Assert.assertEquals(data.get(0).get(CUSTOMER_CODE).asText(), validateCustCode);
    Assert.assertEquals(data.get(0).get("string").asText(), "string 1");

    given(authSpec)
        .body("TestGroup2")
        .when()
        .delete("/security/auth/admin/security-groups/" + groupSysId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testScheduleForMultiTenancy() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "46");
    analysisId = createAnalysis(customToken, data);

    ObjectNode scheduleObj = scheduleData();
    scheduleObj.put("activeRadio", "currenttime");
    scheduleObj.put("activeTab", "immediate");
    scheduleObj.put("analysisID", analysisId);
    scheduleObj.put("cronExpression", "");
    scheduleObj.put("jobName", analysisId + "-p96a99");
    scheduleObj.put("type", "esReport");
    scheduleObj.put("jobGroup", TENANT_A);
    scheduleObj.put("scheduleState", "new");
    scheduleObj.put("zip", false);
    scheduleObj.put("categoryID", "46");
    String json = mapper.writeValueAsString(scheduleObj);
    createSchedule(json, customToken);

    Response executionResultForScheduled =
        given(spec)
            .header("Authorization", "Bearer " + customToken)
            .get("/sip/services/internal/proxy/storage/" + analysisId + "/lastExecutions/data")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(executionResultForScheduled);
    deleteAnalysis(analysisId, customToken);
  }

  @Test
  public void testCreateAnalysis() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "5");
    analysisId = createAnalysis(token, data);
    deleteAnalysis(analysisId, token);
  }

  @Test
  public void testUpdateAnalysis() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonObject data = testData;
    data.addProperty("category", "5");
    JsonNode jsonNode = objectMapper.readTree(data.toString());
    given(spec)
        .header("Authorization", "Bearer " + token)
        .body(jsonNode)
        .when()
        .put("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);

    given(spec)
        .header("Authorization", "Bearer " + token)
        .delete("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testGetAnalysis() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "5");
    analysisId = createAnalysis(token, data);
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
    deleteAnalysis(analysisId, token);
  }

  @Test
  public void testGetAnalysisByCategory() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "5");
    analysisId = createAnalysis(token, data);
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/dslanalysis/" + analysisId + "?category=5")
        .then()
        .assertThat()
        .statusCode(200);
    deleteAnalysis(analysisId, token);
  }

  @Test
  public void testDeleteAnalysis() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "5");
    analysisId = createAnalysis(token, data);
    given(spec)
        .header("Authorization", "Bearer " + token)
        .delete("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testListExecutions() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "5");
    analysisId = createAnalysis(token, data);
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/internal/proxy/storage/" + analysisId + "/executions")
        .then()
        .assertThat()
        .statusCode(200);
    deleteAnalysis(analysisId, token);
  }

  @Test
  public void testLastExecutionsData() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "5");
    analysisId = createAnalysis(token, testData);
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/internal/proxy/storage/" + analysisId + "/lastExecutions/data")
        .then()
        .assertThat()
        .statusCode(200);
    deleteAnalysis(analysisId, token);
  }

  @Test
  public void testDerivedMetricSimpleArithmetic() throws IOException {
    String fieldName = "simpleArithmetic";
    String formula = "1 + 2";
    String expression =
        "{\"operator\":\"+\",\"operand1\":{\"value\":1},\"operand2\":{\"value\":2}}";

    JsonNode payloadData = prepareDerivedMetricPayload(fieldName, formula, expression);

    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(payloadData)
            .post(
                "/sip/services/internal/proxy/storage/execute?id="
                    + analysisId
                    + "&executionType=preview&page=1&pageSize=100")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();

    Assert.assertNotNull(response);
    ObjectNode responseData = response.getBody().as(ObjectNode.class);

    Assert.assertNotNull(responseData.get("data"));
  }

  @Test
  public void testDerivedMetricWithAggregation() throws IOException {
    String formula = "sum(integer) + 2";
    String expression =
        "{\"operator\":\"+\",\"operand1\":{\"aggregate\":\"sum\",\"column\":\"integer\"},"
            + "\"operand2\":{\"value\":2}}";
    String fieldName = "arithmeticWithAgg";

    JsonNode payloadData = prepareDerivedMetricPayload(fieldName, formula, expression);

    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(payloadData)
            .post(
                "/sip/services/internal/proxy/storage/execute?id="
                    + analysisId
                    + "&executionType=preview&page=1&pageSize=100")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();

    Assert.assertNotNull(response);
    ObjectNode responseData = response.getBody().as(ObjectNode.class);

    Assert.assertNotNull(responseData.get("data"));
  }

  @Test
  public void testDerivedMetricWithAggregation2() throws IOException {
    String formula = "sum(integer) + avg(integer)";
    String fieldName = "arithmeticWithAgg";
    String expression =
        "{\"operator\":\"+\",\"operand1\":{\"aggregate\":\"sum\",\"column\":\"integer\"},"
            + "\"operand2\":{\"aggregate\":\"avg\",\"column\":\"integer\"}}";

    JsonNode payloadData = prepareDerivedMetricPayload(fieldName, formula, expression);

    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(payloadData)
            .post(
                "/sip/services/internal/proxy/storage/execute?id="
                    + analysisId
                    + "&executionType=preview&page=1&pageSize=100")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();

    Assert.assertNotNull(response);
    ObjectNode responseData = response.getBody().as(ObjectNode.class);

    Assert.assertNotNull(responseData.get("data"));
  }

  @Test
  public void testDerivedMetricWithMultipleOperators() throws IOException {

    String fieldName = "multipleOperations";

    String formula = "sum(double) - (avg(integer) + avg(double))";
    String expression =
        "{\"operator\":\"-\",\"operand1\":{\"aggregate\":\"sum\",\"column\":\"double\"},"
            + "\"operand2\":{\"operator\":\"+\","
            + "\"operand1\":{\"aggregate\":\"avg\",\"column\":\"integer\"},"
            + "\"operand2\":{\"aggregate\":\"avg\",\"column\":\"double\"}}}";

    JsonNode payloadData = prepareDerivedMetricPayload(fieldName, formula, expression);

    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(payloadData)
            .post(
                "/sip/services/internal/proxy/storage/execute?id="
                    + analysisId
                    + "&executionType=preview&page=1&pageSize=100")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();

    Assert.assertNotNull(response);
    ObjectNode responseData = response.getBody().as(ObjectNode.class);

    Assert.assertNotNull(responseData.get("data"));
  }

  /**
   * This is a test for negative scenario. The formula used here is <code>integer / sum(integer)
   * </code>, which is not supported. In this case, the API will throw 500 Internal Server Error.
   *
   * @throws IOException - In case of invalid json
   */
  @Test
  public void testderivedMetricWithSubAggregation() throws IOException {
    String fieldName = "percentage";
    String formula = "integer / sum(integer)";

    String expression =
        "{\"operator\":\"/\",\"operand1\":{\"column\":\"integer\"},"
            + "\"operand2\":{\"aggregate\":\"sum\",\"column\":\"integer\"}}";

    JsonNode payloadData = prepareDerivedMetricPayload(fieldName, formula, expression);

    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(payloadData)
            .post(
                "/sip/services/internal/proxy/storage/execute?id="
                    + analysisId
                    + "&executionType=preview&page=1&pageSize=100")
            .then()
            .assertThat()
            .statusCode(500)
            .extract()
            .response();
  }

  @Test
  public void testExecutionWithGroupIntervalMinute() throws IOException {
    String groupInterval = null;
    Response reponseAtSecondLevel = getExecutedDataWithGroupInterval(groupInterval);
    Assert.assertNotNull(reponseAtSecondLevel);
    ArrayNode data = reponseAtSecondLevel.getBody().as(ObjectNode.class).withArray("data");
    Assert.assertNotNull(data);
    Long expectedValue = 0L;
    for (JsonNode jsonNode : data) {
      Long value = jsonNode.get("long").asLong();
      expectedValue = expectedValue + value;
    }
    String groupIntervalMinute = "minute";
    Response reponseAtMinuteLevel = getExecutedDataWithGroupInterval(groupIntervalMinute);
    Assert.assertNotNull(reponseAtMinuteLevel);
    ArrayNode dataForMinuteGrouping =
        reponseAtMinuteLevel.getBody().as(ObjectNode.class).withArray("data");
    Assert.assertNotNull(dataForMinuteGrouping);
    Assert.assertNotNull(dataForMinuteGrouping);
    Long actualValue = dataForMinuteGrouping.get(0).get("long").asLong();
    Assert.assertEquals(expectedValue, actualValue);
  }

  @Test
  public void testExecutionWithGroupIntervalSecond() throws IOException {
    String minuteGroupInterval = "minute";
    Response reponseAtMinuteLevel = getExecutedDataWithGroupInterval(minuteGroupInterval);
    ArrayNode dataForMinuteGrouping =
        reponseAtMinuteLevel.getBody().as(ObjectNode.class).withArray("data");
    Integer responseSizeForMinute = dataForMinuteGrouping.size();
    String groupInterval = "second";
    Response reponseAtSecondLevel = getExecutedDataWithGroupInterval(groupInterval);
    Assert.assertNotNull(reponseAtSecondLevel);
    ArrayNode dataForSecondGrouping =
        reponseAtSecondLevel.getBody().as(ObjectNode.class).withArray("data");
    Integer responseSizeForSecond = dataForSecondGrouping.size();
    Assert.assertTrue(responseSizeForSecond > responseSizeForMinute);
  }

  private Response getExecutedDataWithGroupInterval(String groupInterval) throws IOException {
    JsonNode payload = preparePayloadForGroupInterval(groupInterval);
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(payload)
            .post(
                "/sip/services/internal/proxy/storage/execute?id="
                    + analysisId
                    + "&executionType=preview&page=1&pageSize=100")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    return response;
  }

  /**
   * This Method prepares the payload with group interval specified.
   *
   * @return created payload.
   */
  private JsonNode preparePayloadForGroupInterval(String groupInterval) throws IOException {
    JsonObject payload = testData;
    JsonObject fieldWithGroupIntervalMinute = prepareFieldForGroupInterval(groupInterval);
    JsonArray fields =
        payload
            .getAsJsonObject("sipQuery")
            .getAsJsonArray("artifacts")
            .get(0)
            .getAsJsonObject()
            .getAsJsonArray("fields");
    for (int i = fields.size(); i > 0; i--) {
      fields.remove(i - 1);
    }
    fields.add(prepareFieldForGroupInterval(groupInterval));

    JsonObject field3 = new JsonObject();
    field3.addProperty("dataField", "long");
    field3.addProperty("columnName", "long");
    field3.addProperty("displayName", "Long");
    field3.addProperty("type", "long");
    field3.addProperty("aggregate", "sum");
    fields.add(field3);
    JsonArray filters = payload.getAsJsonObject("sipQuery").getAsJsonArray("filters");
    for (int j = filters.size(); j > 0; j--) {
      filters.remove(j - 1);
    }

    JsonObject filter = new JsonObject();
    filter.addProperty("type", "date");
    filter.addProperty("tableName", "sample");
    filter.addProperty("isOptional", false);
    filter.addProperty("columnName", "date");
    filter.addProperty("isRuntimeFilter", false);
    filter.addProperty("isGlobalFilter", false);
    JsonObject model = new JsonObject();
    model.addProperty("format", "yyyy-MM-dd HH:mm:ss");
    model.addProperty("operator", "NEQ");
    model.addProperty("gte", "2019-10-16 11:05:00");
    model.addProperty("lte", "2019-10-16 11:05:59");
    filter.add("model", model);
    filters.add(filter);

    return mapper.readTree(payload.toString());
  }

  /**
   * This Method prepares the field with specified group interval..
   *
   * @return field wit group interval.
   */
  private JsonObject prepareFieldForGroupInterval(String groupInterval) {
    JsonObject field = new JsonObject();
    field.addProperty("dataField", "date");
    field.addProperty("area", "g-axis");
    field.addProperty("columnName", "date");
    field.addProperty("displayName", "Date");
    field.addProperty("type", "date");
    field.addProperty("dateFormat", "MM/dd/yyyy HH:mm:ss");
    field.addProperty("groupInterval", groupInterval);
    return field;
  }

  @Test
  public void exportData() throws IOException {
    ObjectNode analysis = testCreateDlAnalysis();
    String analysisId = analysis.get("analysisId").asText();
    executeAnalysis(analysisId);
    ObjectNode node = scheduleData();
    node.put("analysisID", analysisId);
    String json = mapper.writeValueAsString(node);
    createSchedule(json, token);
    List<Map<String, String>> data = getLastExecutionsData(analysisId);

    given(spec)
        .header("Authorization", "Bearer " + token)
        .delete("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
    // base setup for ftp server
    String username = "user";
    String password = "password";
    String homeDirectory = "/";
    String filename = "report.csv";
    // Write the data to csv
    FakeFtpServer f = createFileOnFakeFtp(username, password, homeDirectory, filename, data);

    // check if the file has been uploaded and read the contents
    String dataResult =
        readFile("/data/" + filename, "localhost", f.getServerControlPort(), username, password);
    // check if the file actually has data
    // this has been done just to avoid rewriting integration test case when data changes.
    // the goal here is to just check if the data that we got from scheduled analysis execution
    // is present in the file.
    assertTrue(dataResult.length() > 0);
  }

  private ObjectNode scheduleData() {
    ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("activeRadio", "everyDay");
    objectNode.put("activeTab", "daily");
    objectNode.put("analysisID", "123");
    objectNode.put("analysisName", "Untitled Analysis");
    objectNode.put("cronExpression", "0 31 20 1/1 * ? *");
    objectNode.put("fileType", "csv");
    objectNode.put("jobName", "123");
    objectNode.put("metricName", "Sample (report) - new");
    objectNode.put("type", "report");
    objectNode.put("userFullName", "System");
    ArrayNode email = objectNode.putArray("emailList");
    email.add("abc@synchronoss.com");
    email.add("xyz@synchronoss.com");
    ArrayNode ftp = objectNode.putArray("ftp");
    ftp.add("ftp");
    objectNode.put("jobScheduleTime", "2019-07-01T16:24:28+05:30");
    objectNode.put("categoryID", "4");
    objectNode.put("jobGroup", "SYNCHRONOSS");
    objectNode.put("endDate", "2099-03-01T16:24:28+05:30");
    objectNode.put("timezone", "UTC");
    return objectNode;
  }

  /**
   * This Method is create analysis.
   *
   * @return created analysis
   */
  public ObjectNode testCreateDlAnalysis() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonObject data = testData;
    data.addProperty("category", "5");
    JsonNode jsonNode = objectMapper.readTree(data.toString());
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(jsonNode)
            .when()
            .post("/sip/services/dslanalysis/")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    return response.getBody().as(ObjectNode.class);
  }

  /**
   * This Method is used to last execution for analysis.
   *
   * @param analysisId analysis id.
   * @return data for the execution
   */
  public List<Map<String, String>> getLastExecutionsData(String analysisId) throws IOException {
    return given(spec)
        .header("Authorization", "Bearer " + token)
        .get(
            "/sip/services/internal/proxy/storage/"
                + analysisId
                + "/lastExecutions/data?analysisType=report")
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response()
        .path("data");
  }

  private void createSchedule(String json, String token) {
    given(spec)
        .filter(document("create-schedule", preprocessResponse(prettyPrint())))
        .header("Authorization", "Bearer " + token)
        .body(json)
        .when()
        .post("/sip/services/scheduler/schedule")
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();
  }

  /**
   * This Method is create ftp server.
   *
   * @param username username.
   * @param password password.
   * @param homeDirectory homeDirectory.
   * @param filename filename.
   * @param data execution data.
   * @return ftpserver
   */
  public FakeFtpServer createFileOnFakeFtp(
      String username,
      String password,
      String homeDirectory,
      String filename,
      List<Map<String, String>> data) {
    FakeFtpServer fakeFtpServer = new FakeFtpServer();
    fakeFtpServer.setServerControlPort(0);
    fakeFtpServer.addUserAccount(new UserAccount(username, password, homeDirectory));

    FileSystem fileSystem = new UnixFakeFileSystem();
    fileSystem.add(new DirectoryEntry("/data"));
    fileSystem.add(new FileEntry("/data/" + filename, data.toString()));
    fakeFtpServer.setFileSystem(fileSystem);

    fakeFtpServer.start();
    return fakeFtpServer;
  }

  /**
   * This Method is used to execute the analysis.
   *
   * @param analysisId analysis id
   */
  void executeAnalysis(String analysisId) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testDataForDl.toString());
    given(spec)
        .header("Authorization", "Bearer " + token)
        .body(jsonNode)
        .when()
        .post("/sip/services/internal/proxy/storage/execute?executionType=publish&id=" + analysisId)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();
  }

  /**
   * This Method is read file.
   *
   * @param filename filename.
   * @param server server.
   * @param port port .
   * @param username username.
   * @param password password.
   * @return content of file as string
   */
  public String readFile(String filename, String server, int port, String username, String password)
      throws IOException {

    FTPClient ftpClient = new FTPClient();
    ftpClient.connect(server, port);
    ftpClient.login(username, password);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    boolean success = ftpClient.retrieveFile(filename, outputStream);
    ftpClient.disconnect();

    if (!success) {
      throw new IOException("Retrieve file failed: " + filename);
    }
    return outputStream.toString();
  }

  /**
   * Prepares the payload for derived metric field.
   *
   * @param fieldName Name of the field
   * @param formula Formula for the derived metrics
   * @param expression Formula in json format
   * @return JSON payload for the derived metric
   */
  private JsonNode prepareDerivedMetricPayload(String fieldName, String formula, String expression)
      throws IOException {
    JsonObject formulaField = new JsonObject();
    formulaField.addProperty("area", "y");
    formulaField.addProperty("dataField", fieldName);
    formulaField.addProperty("columnName", fieldName);
    formulaField.addProperty("displayName", "Derived Metric");

    formulaField.addProperty("formula", formula);
    formulaField.addProperty("expression", expression);
    formulaField.addProperty("type", "double");
    formulaField.addProperty("areaIndex", 0);

    JsonObject dateField = new JsonObject();
    dateField.addProperty("area", "x");
    dateField.addProperty("columnName", "date");
    dateField.addProperty("dataField", "date");
    dateField.addProperty("displayName", "Date");
    dateField.addProperty("groupInterval", "year");
    dateField.addProperty("min_doc_count", 0);
    dateField.addProperty("name", "date");
    dateField.addProperty("type", "date");
    dateField.addProperty("dateFormat", "MMM d YYYY");

    JsonArray artifactFields = new JsonArray();
    artifactFields.add(formulaField);
    artifactFields.add(dateField);

    JsonObject payload = testData;
    JsonArray fields =
        payload
            .getAsJsonObject("sipQuery")
            .getAsJsonArray("artifacts")
            .get(0)
            .getAsJsonObject()
            .getAsJsonArray("fields");
    for (int i = fields.size(); i > 0; i--) {
      fields.remove(i - 1);
    }

    fields.add(formulaField);

    fields.add(dateField);
    JsonArray sorts = payload.getAsJsonObject("sipQuery").getAsJsonArray("sorts");
    for (int i = sorts.size(); i > 0; i--) {
      sorts.remove(i - 1);
    }

    JsonArray filters = payload.getAsJsonObject("sipQuery").getAsJsonArray("filters");
    for (int i = filters.size(); i > 0; i--) {
      filters.remove(i - 1);
    }

    JsonObject sortField = new JsonObject();
    sortField.addProperty("order", "asc");
    sortField.addProperty("columnName", "date");
    sortField.addProperty("type", "date");
    sorts.add(sortField);

    return mapper.readTree(payload.toString());
  }

  /**
   * Dsl execute method which takes up a token and request body to return back the response.
   *
   * @param custToken custom token for each customer login
   * @param body Request Body
   * @return Response Object
   */
  public Response execute(String custToken, JsonNode body) {
    if (body.has("category")) {
      ((ObjectNode) body).remove("category");
    }
    return given(spec)
        .header("Authorization", "Bearer " + custToken)
        .body(body)
        .when()
        .post("/sip/services/internal/proxy/storage/execute?id=" + analysisId)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();
  }

  /**
   * Method used to create new analysis report.
   *
   * @param token auth token
   * @return Analysis id
   * @throws IOException IoException
   */
  public String createAnalysis(String token, JsonObject data) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(data.toString());
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(jsonNode)
            .when()
            .post("/sip/services/dslanalysis/")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    ObjectNode root = response.getBody().as(ObjectNode.class);
    analysisId = root.get("analysisId").asText();

    return analysisId;
  }

  /**
   * Deletes analysis from maprDB.
   *
   * @param analysisId Analysis id
   */
  public void deleteAnalysis(String analysisId, String token) {
    given(spec)
        .header("Authorization", "Bearer " + token)
        .delete("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testCreateAnalysisForUnauthorizedPermissions() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonObject data = testData;
    data.addProperty("category", "5");
    data.addProperty("semanticId", "workbench::sample-elasticsearch");
    JsonNode jsonNode = objectMapper.readTree(data.toString());
    given(spec)
        .header("Authorization", "Bearer " + tokenForNegativeCases)
        .body(jsonNode)
        .when()
        .post("/sip/services/dslanalysis/")
        .then()
        .assertThat()
        .statusCode(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  public void testUpdateAnalysisForUnauthorizedPermissions() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonObject data = testData;
    data.addProperty("category", "5");
    data.addProperty("semanticId", "workbench::sample-elasticsearch");
    JsonNode jsonNode = objectMapper.readTree(testData.toString());

    jsonNode = objectMapper.readTree(data.toString());
    given(spec)
        .header("Authorization", "Bearer " + tokenForNegativeCases)
        .body(jsonNode)
        .when()
        .put("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  public void testDeleteAnalysisForUnauthorizedPermissions() {
    given(spec)
        .header("Authorization", "Bearer " + tokenForNegativeCases)
        .delete("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  public void testExecuteAnalysisForUnauthorizedPermissions() throws IOException {
    JsonObject data = testData;
    data.addProperty("category", "5");
    data.addProperty("semanticId", "workbench::sample-elasticsearch");

    ObjectNode upsertPrivilege = prepareDataForModifyingPrivilege();

    // Remove Execute Privilege and test whether api responds with UNAUTHORIZED.
    modifyPrivilege(upsertPrivilege);

    tokenForNegativeCases = authenticate("reviewer@synchronoss.com", "Sawsyncnewuser1!");

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", CUSTOMER_CODE);
    field1.addProperty("area", "x-axis");
    field1.addProperty("alias", CUSTOMER_CODE);
    field1.addProperty("columnName", CUSTOMER_CODE + ".keyword");
    field1.addProperty("displayName", CUSTOMER_CODE);
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);

    JsonObject sipDsl = data;
    JsonElement js = new JsonArray();
    sipDsl
        .get("sipQuery")
        .getAsJsonObject()
        .get("artifacts")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("fields")
        .getAsJsonArray()
        .set(1, field1);
    sipDsl.get("sipQuery").getAsJsonObject().add("filters", js);
    sipDsl.get("sipQuery").getAsJsonObject().add("sorts", js);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode body = mapper.readTree(sipDsl.toString());

    given(spec)
        .header("Authorization", "Bearer " + tokenForNegativeCases)
        .body(body)
        .when()
        .post("/sip/services/internal/proxy/storage/execute?id=" + analysisId)
        .then()
        .assertThat()
        .statusCode(HttpStatus.UNAUTHORIZED.value());

    upsertPrivilege.put("privilegeCode", 37376);
    upsertPrivilege.put("privilegeDesc", "View, Publish, Export");
    ObjectNode subCatPriv = mapper.createObjectNode();
    subCatPriv.put("privilegeCode", 45568);
    subCatPriv.put("privilegeDesc", "View, Execute, Publish, Export");
    subCatPriv.put("privilegeId", 8);
    subCatPriv.put("subCategoryId", 4);
    ArrayNode subCatNew = mapper.createArrayNode();
    subCatNew.add(subCatPriv);
    upsertPrivilege.put("subCategoriesPrivilege", subCatNew);

    // Modify the privileges as original.
    modifyPrivilege(upsertPrivilege);
  }

  /**
   * Prepare dataset for modifying privileges to test different scenario's.
   *
   * @return ObjectNode
   */
  public ObjectNode prepareDataForModifyingPrivilege() {
    ObjectNode upsertPrivilege = mapper.createObjectNode();
    upsertPrivilege.put("categoryCode", "F0000000001");
    upsertPrivilege.put("categoryId", 2);
    upsertPrivilege.put("categoryName", "CANNED ANALYSIS");
    upsertPrivilege.put("categoryType", "PARENT_F0000000001");
    upsertPrivilege.put("customerId", 1);
    upsertPrivilege.put("masterLoginId", "sawadmin@synchronoss.com");
    upsertPrivilege.put("moduleId", 1);
    upsertPrivilege.put("moduleName", "ANALYZE");
    upsertPrivilege.put("privilegeCode", 45568);
    upsertPrivilege.put("privilegeDesc", "View, Execute, Publish, Export");
    upsertPrivilege.put("privilegeId", 8);
    upsertPrivilege.put("productId", 1);
    upsertPrivilege.put("productName", "SAW Demo");
    upsertPrivilege.put("roleId", 4);
    upsertPrivilege.put("roleName", "REVIEWER");
    upsertPrivilege.put("subCategoryId", 4);
    upsertPrivilege.put("subCategoryName", "OPTIMIZATION");

    ObjectNode subCatPriv = mapper.createObjectNode();
    subCatPriv.put("privilegeCode", 37376);
    subCatPriv.put("privilegeDesc", "View, Publish, Export");
    subCatPriv.put("privilegeId", 8);
    subCatPriv.put("subCategoryId", 4);

    ArrayNode subCat = mapper.createArrayNode();
    subCat.add(subCatPriv);
    upsertPrivilege.put("subCategoriesPrivilege", subCat);

    return upsertPrivilege;
  }

  /**
   * Call upsert api to modify the privileges.
   *
   * @param body Request body
   */
  public void modifyPrivilege(ObjectNode body) {
    given(spec)
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .body(body)
        .when()
        .post("/sip/security/auth/admin/cust/manage/privileges/upsert")
        .then()
        .assertThat()
        .statusCode(200);
  }
}

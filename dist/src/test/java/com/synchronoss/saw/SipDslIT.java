package com.synchronoss.saw;

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

public class SipDslIT extends BaseIT {
  String analysisId = "f37cde24-b833-46ba-ae2d-42e286c3fc39";
  protected JsonObject testData = null;
  protected JsonObject sipQuery = null;
  protected JsonObject testDataForDl = null;
  protected JsonObject sipQueryDl = null;
  protected JsonObject sipDslForCustomerCodeFilter = null;
  protected String customToken;

  @Before
  public void setUpData() throws JsonProcessingException {
    customToken = authenticate("sawadmin@ATT.com", "Sawsyncnewuser1!");
    testData = new JsonObject();
    testData.addProperty("type", "esReport");
    testData.addProperty("semanticId", "workbench::sample-elasticsearch-att");
    testData.addProperty("id", analysisId);
    testData.addProperty("customerCode", "SYNCHRONOSS");
    testData.addProperty("projectCode", "workbench");
    testData.addProperty("module", "ANALYZE");
    testData.addProperty("createdTime", 1543921879);
    testData.addProperty("createdBy", "sipadmin@synchronoss.com");
    testData.addProperty("modifiedTime", 1543921879);
    testData.addProperty("modifiedBy", "sipadmin@synchronoss.com");
    testData.addProperty("category", "5");
    testData.addProperty("userId", 1);

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
    sipQuery.addProperty("semanticId", "workbench::sample-elasticsearch-att");

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
    testDataForDl.addProperty("customerCode", "SYNCHRONOSS");
    testDataForDl.addProperty("projectCode", "sip-sample");
    testDataForDl.addProperty("module", "productSpecific/ANALYZE");
    testDataForDl.addProperty("createdTime", 1543921879);
    testDataForDl.addProperty("createdBy", "sipadmin@synchronoss.com");
    testDataForDl.addProperty("modifiedTime", 1543921879);
    testDataForDl.addProperty("modifiedBy", "sipadmin@synchronoss.com");
    testDataForDl.addProperty("category", "5");
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
    sipDsl.addProperty("customerCode", "ATT");
    String validateCustCode = "ATT";

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", "customerCode");
    field1.addProperty("area", "x-axis");
    field1.addProperty("alias", "customerCode");
    field1.addProperty("columnName", "customerCode.keyword");
    field1.addProperty("displayName", "customerCode");
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

    // Below code uses customToken (i,e. uname = "sawadmin@ATT.com", this customer is added
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
      Assert.assertEquals(dataNode.get("customerCode").asText(), validateCustCode);
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
    int att = 0;
    int snt = 0;
    int mct = 0;
    while (itr.hasNext()) {
      JsonNode dat = itr.next();
      att = dat.get("customerCode").asText().equalsIgnoreCase("ATT") ? ++att : att;
      snt = dat.get("customerCode").asText().equalsIgnoreCase("SNT") ? ++snt : snt;
      mct = dat.get("customerCode").asText().equalsIgnoreCase("MCT") ? ++mct : mct;
    }
    Assert.assertTrue(att > 0);
    Assert.assertTrue(snt > 0);
    Assert.assertTrue(mct > 0);
  }

  @Test
  public void testCustomerCodeFilterWithDsk() throws IOException {
    // Add security group for ATT customer.
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

    ObjectNode root = mapper.createObjectNode();
    root.put("attributeName", "string");
    root.put("value", "string 1");
    given(spec)
        .header("Authorization", "Bearer " + customToken)
        .contentType(ContentType.JSON)
        .body(root)
        .when()
        .post("/security/auth/admin/security-groups/" + groupSysId + "/dsk-attribute-values")
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

    JsonObject sipDsl = testData;
    sipDsl.addProperty("customerCode", "ATT");

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", "customerCode");
    field1.addProperty("area", "x-axis");
    field1.addProperty("alias", "customerCode");
    field1.addProperty("columnName", "customerCode.keyword");
    field1.addProperty("displayName", "customerCode");
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
    String customTok = authenticate("sawadmin@ATT.com", "Sawsyncnewuser1!");
    Response response = execute(customTok, jsonNode);
    Assert.assertNotNull(response);
    ObjectNode attUserRes = response.getBody().as(ObjectNode.class);
    ArrayNode data = attUserRes.withArray("data");
    Assert.assertTrue(data.size() == 1);
    String validateCustCode = "ATT";
    Assert.assertEquals(data.get(0).get("customerCode").asText(), validateCustCode);
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
  public void testSipDslDataLakeExecute() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testDataForDl.toString());
    String testStringFilter = "string 201";
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
    sipDsl.addProperty("customerCode", "ATT");

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", "customerCode");
    field1.addProperty("table", "sales");
    field1.addProperty("alias", "customerCode");
    field1.addProperty("columnName", "customerCode");
    field1.addProperty("displayName", "customerCode");
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
    String validateCustCode = "ATT";
    while (iterator.hasNext()) {
      JsonNode dataNode = iterator.next();
      Assert.assertEquals(dataNode.get("SALES_customerCode").asText(), validateCustCode);
      Assert.assertEquals(dataNode.get("PRODUCT_customerCode").asText(), validateCustCode);
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

    int att = 0;
    int snt = 0;
    int mct = 0;

    Iterator<JsonNode> itr = syncData.iterator();
    while (itr.hasNext()) {
      JsonNode dat = itr.next();
      att =
          dat.get("SALES_customerCode").asText().equalsIgnoreCase("ATT")
                  && dat.get("PRODUCT_customerCode").asText().equalsIgnoreCase("ATT")
              ? ++att
              : att;
      snt =
          dat.get("SALES_customerCode").asText().equalsIgnoreCase("SNT")
                  && dat.get("PRODUCT_customerCode").asText().equalsIgnoreCase("SNT")
              ? ++snt
              : snt;
      mct =
          dat.get("SALES_customerCode").asText().equalsIgnoreCase("MCT")
                  && dat.get("PRODUCT_customerCode").asText().equalsIgnoreCase("MCT")
              ? ++mct
              : mct;
    }
    Assert.assertTrue(att > 0);
    Assert.assertTrue(snt > 0);
    Assert.assertTrue(mct > 0);
  }

  @Test
  public void testDskWithCustCodeFilterDl() throws IOException {
    JsonObject sipDsl = testDataForDl;
    sipDsl.addProperty("customerCode", "ATT");

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", "customerCode");
    field1.addProperty("table", "sales");
    field1.addProperty("alias", "customerCode");
    field1.addProperty("columnName", "customerCode");
    field1.addProperty("displayName", "customerCode");
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

    // Add security group for ATT customer.
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

    ObjectNode root = mapper.createObjectNode();
    root.put("attributeName", "string");
    root.put("value", "string 1");
    given(spec)
        .header("Authorization", "Bearer " + customToken)
        .contentType(ContentType.JSON)
        .body(root)
        .when()
        .post("/security/auth/admin/security-groups/" + groupSysId + "/dsk-attribute-values")
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
    String customTok = authenticate("sawadmin@ATT.com", "Sawsyncnewuser1!");

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(sipDsl.toString());
    Response response = execute(customTok, jsonNode);
    Assert.assertNotNull(response);
    ObjectNode attUserRes = response.getBody().as(ObjectNode.class);
    ArrayNode data = attUserRes.withArray("data");
    Assert.assertTrue(data.size() == 1);
    String validateCustCode = "ATT";
    Assert.assertEquals(data.get(0).get("SALES_customerCode").asText(), validateCustCode);
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
  public void testCreateAnalysis() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testData.toString());
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

    given(spec)
        .header("Authorization", "Bearer " + token)
        .delete("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testUpdateAnalysis() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testData.toString());
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
    ObjectMapper objectMapper = new ObjectMapper();
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testGetAnalysisByCategory() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/dslanalysis/" + analysisId + "?category=5")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testDeleteAnalysis() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    given(spec)
        .header("Authorization", "Bearer " + token)
        .delete("/sip/services/dslanalysis/" + analysisId)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testListExecutions() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/internal/proxy/storage/" + analysisId + "/executions")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void testLastExecutionsData() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    given(spec)
        .header("Authorization", "Bearer " + token)
        .get("/sip/services/internal/proxy/storage/" + analysisId + "/lastExecutions/data")
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  public void exportData() throws IOException {
    ObjectNode analysis = testCreateDlAnalysis();
    String analysisId = analysis.get("analysisId").asText();
    executeAnalysis(analysisId);
    ObjectNode node = scheduleData();
    node.put("analysisID", analysisId);
    String json = mapper.writeValueAsString(node);
    createSchedule(json);
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
    JsonNode jsonNode = objectMapper.readTree(testDataForDl.toString());
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

  private void createSchedule(String json) {
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
   * Dsl execute method which takes up a token and request body to return back the response.
   *
   * @param custToken custom token for each customer login
   * @param body Request Body
   * @return Response Object
   */
  public Response execute(String custToken, JsonNode body) {
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
}

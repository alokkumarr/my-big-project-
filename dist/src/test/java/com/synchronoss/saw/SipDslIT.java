package com.synchronoss.saw;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SipDslIT extends BaseIT {
  String analysisId = "f37cde24-b833-46ba-ae2d-42e286c3fc39";
  protected JsonObject testData = null;
  protected JsonObject sipQuery = null;
  protected JsonObject testDataForDl = null;
  protected JsonObject sipQueryDl = null;

  @Before
  public void setUpData() {
    testData = new JsonObject();
    testData.addProperty("type", "chart");
    testData.addProperty("type", "chart");
    testData.addProperty("semanticId", "d23c6142-2c10-459e-b1f6-29edd1b2ccfe");
    testData.addProperty("id", analysisId);
    testData.addProperty("customerCode", "SYNCHRONOSS");
    testData.addProperty("projectCode", "sip-sample");
    testData.addProperty("module", "productSpecific/ANALYZE");
    testData.addProperty("createdTime", 1543921879);
    testData.addProperty("createdBy", "sipadmin@synchronoss.com");
    testData.addProperty("modifiedTime", 1543921879);
    testData.addProperty("modifiedBy", "sipadmin@synchronoss.com");
    testData.addProperty("category", "5");
    testData.addProperty("userId", 1);

    sipQuery = new JsonObject();

    JsonObject artifact1 = new JsonObject();
    artifact1.addProperty("artifactName", "sample");

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
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);
    JsonObject field2 = new JsonObject();
    field2.addProperty("dataField", "long");
    field2.addProperty("columnName", "long");
    field2.addProperty("displayName", "long");
    field2.addProperty("type", "long");
    artifactFields.add(field2);
    JsonObject field3 = new JsonObject();
    field3.addProperty("dataField", "float");
    field3.addProperty("columnName", "float");
    field3.addProperty("displayName", "Float");
    field3.addProperty("type", "float");
    artifactFields.add(field3);
    JsonObject field4 = new JsonObject();
    field4.addProperty("dataField", "date");
    field4.addProperty("columnName", "date");
    field4.addProperty("displayName", "Date");
    field4.addProperty("type", "date");
    artifactFields.add(field4);
    JsonObject field5 = new JsonObject();
    field5.addProperty("dataField", "double");
    field5.addProperty("columnName", "double");
    field5.addProperty("displayName", "Double");
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
    field6.addProperty("type", "string");
    JsonArray artifactFields2 = new JsonArray();
    artifactFields2.add(field6);
    JsonObject field7 = new JsonObject();
    field7.addProperty("dataField", "long_2");
    field7.addProperty("columnName", "long_2");
    field7.addProperty("displayName", "long_2");
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
    ResponseBody responseBody = response.getBody();
    List<Map<String, String>> dataNode = responseBody.path("data");
    Assert.assertEquals(dataNode.get(0).get("string"), testStringFilter);
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
    ResponseBody responseBody = response.getBody();
    List<Map<String, String>> dataNode = responseBody.path("data");
    Assert.assertEquals(dataNode.get(0).get("string"), testStringFilter);
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
}

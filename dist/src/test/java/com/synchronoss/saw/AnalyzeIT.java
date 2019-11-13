package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration test that lists metrics, creates an analysis, saves it, executes it and lists the
 * execution results.
 */
public class AnalyzeIT extends BaseIT {

  @Test
  public void testSSOAuthentication() {
    Response response =
        given(spec)
            .header("Cache-Control", "no-store")
            .filter(document("sso-authentication", preprocessResponse(prettyPrint())))
            .when()
            .get("/saw/security/authentication?jwt=" + getJWTToken())
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    assertNotNull("Valid access Token not found, Authentication failed ", response.path("aToken"));
    assertNotNull("Valid refresh Token not found, Authentication failed", response.path("rToken"));
  }

  /**
   * List and find the metric ID
   *
   * @param token
   * @return
   * @throws JsonProcessingException
   */
  private String listMetrics(String token, String metricName) throws JsonProcessingException {
    String path = "contents[0]['ANALYZE'].find " + "{it.metricName == '" + metricName + "'}.id";
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .filter(document("list-metrics", preprocessResponse(prettyPrint())))
            .when()
            .get("/saw/services/internal/semantic/md?projectId=workbench")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    try {
      String metricId = response.path(path);
      if (metricId == null) {
        return retryListMetrics(token, metricName);
      }
      return metricId;
    } catch (IllegalArgumentException e) {
      return retryListMetrics(token, metricName);
    }
  }

  private String retryListMetrics(String token, String metricName) throws JsonProcessingException {
    /* Path was not found, so wait and retry.  The sample metrics
     * are loaded asynchronously, so wait until the loading
     * finishes before proceeding.  */
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
    }
    return listMetrics(token, metricName);
  }

  private ObjectNode createAnalysis(String token, String metricId, String analysisType)
      throws JsonProcessingException {
    ObjectNode node = mapper.createObjectNode();
    ObjectNode contents = node.putObject("contents");
    contents.put("action", "create");
    ArrayNode keys = contents.putArray("keys");
    ObjectNode key = keys.addObject();
    key.put("customerCode", "SYNCHRONOSS");
    key.put("module", "ANALYZE");
    key.put("id", metricId);
    key.put("analysisType", analysisType);
    String json = mapper.writeValueAsString(node);
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when()
            .post("/saw/services/analysis")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    ObjectNode root = response.as(ObjectNode.class);
    return (ObjectNode) root.get("contents").get("analyze").get(0);
  }

  private void savePivotAnalysis(
      String token, String analysisId, String analysisName, ObjectNode analysis)
      throws JsonProcessingException {
    analysis.put("saved", true);
    analysis.put("categoryId", 4);
    analysis.put("name", analysisName);
    analysis.set("sqlBuilder", sqlBuilderPivot());
    ArrayNode artifacts = (ArrayNode) analysis.get("artifacts");
    for (JsonNode artifactNode : artifacts) {
      ObjectNode artifact = (ObjectNode) artifactNode;
      ArrayNode columns = (ArrayNode) artifact.get("columns");
      for (JsonNode columnNode : columns) {
        ObjectNode column = (ObjectNode) columnNode;
        column.put("checked", true);
        String name = column.get("name").asText();
        String area = null;
        String dataType = null;
        if (name.equals("string.keyword")) {
          area = "row";
          dataType = "string";
        } else if (name.equals("date")) {
          area = "column";
          dataType = "date";
        } else if (name.equals("integer")) {
          area = "data";
          dataType = "number";
          column.put("aggregate", "sum");
          ObjectNode format = column.putObject("format");
          format.put("type", "fixedpoint");
          format.put("precision", 2);
        }
        if (area != null) {
          column.put("area", area);
          column.put("areaIndex", 0);
        }
        if (dataType != null) {
          column.put("dataType", dataType);
        }
      }
    }
    ObjectNode node = mapper.createObjectNode();
    ObjectNode contents = node.putObject("contents");
    contents.put("action", "update");
    ArrayNode keys = contents.putArray("keys");
    ObjectNode key = keys.addObject();
    key.put("id", analysisId);
    ArrayNode analyze = contents.putArray("analyze");
    analyze.add(analysis);
    String json = mapper.writeValueAsString(node);
    given(spec)
        .header("Authorization", "Bearer " + token)
        .body(json)
        .when()
        .post("/saw/services/analysis")
        .then()
        .assertThat()
        .statusCode(200);
  }

  private void saveDLReportAnalysis(
      String token, String analysisId, String analysisName, ObjectNode analysis)
      throws JsonProcessingException {
    analysis.put("saved", true);
    analysis.put("categoryId", 4);
    analysis.put("name", analysisName);
    analysis.set("sqlBuilder", sqlBuilderDLReport());
    ArrayNode artifacts = (ArrayNode) analysis.get("artifacts");
    for (JsonNode artifactNode : artifacts) {
      ObjectNode artifact = (ObjectNode) artifactNode;
      ArrayNode columns = (ArrayNode) artifact.get("columns");
      for (JsonNode columnNode : columns) {
        ObjectNode column = (ObjectNode) columnNode;
        column.put("checked", true);
      }
    }
    ObjectNode node = mapper.createObjectNode();
    ObjectNode contents = node.putObject("contents");
    contents.put("action", "update");
    ArrayNode keys = contents.putArray("keys");
    ObjectNode key = keys.addObject();
    key.put("id", analysisId);
    ArrayNode analyze = contents.putArray("analyze");
    analyze.add(analysis);
    String json = mapper.writeValueAsString(node);
    given(spec)
        .header("Authorization", "Bearer " + token)
        .body(json)
        .when()
        .post("/saw/services/analysis")
        .then()
        .assertThat()
        .statusCode(200);
  }

  private ObjectNode sqlBuilderPivot() {
    ObjectNode sqlBuilder = mapper.createObjectNode();
    sqlBuilder.put("booleanCriteria", "AND");
    sqlBuilder.putArray("filters");
    sqlBuilder.putArray("sorts");
    ArrayNode rowFields = sqlBuilder.putArray("rowFields");
    ObjectNode rowField = rowFields.addObject();
    rowField.put("type", "string");
    rowField.put("columnName", "string.keyword");
    ArrayNode columnFields = sqlBuilder.putArray("columnFields");
    ObjectNode columnField = columnFields.addObject();
    columnField.put("type", "date");
    columnField.put("columnName", "date");
    columnField.put("dateInterval", "day");
    ArrayNode dataFields = sqlBuilder.putArray("dataFields");
    ObjectNode dataField = dataFields.addObject();
    dataField.put("type", "integer");
    dataField.put("columnName", "integer");
    dataField.put("aggregate", "sum");
    dataField.put("name", "integer");
    return sqlBuilder;
  }

  private ObjectNode sqlBuilderDLReport() {
    ObjectNode sqlBuilder = mapper.createObjectNode();
    sqlBuilder.put("booleanCriteria", "AND");
    sqlBuilder.putArray("filters");
    sqlBuilder.putArray("orderByColumns");
    ArrayNode joins = sqlBuilder.putArray("joins");
    ObjectNode join = joins.addObject();
    join.put("type", "inner");
    ArrayNode criteria = join.putArray("criteria");
    ObjectNode criteria1 = criteria.addObject();
    criteria1.put("columnName", "string");
    criteria1.put("side", "right");
    criteria1.put("tableName", "SALES");
    ObjectNode criteria2 = criteria.addObject();
    criteria2.put("columnName", "string_2");
    criteria2.put("side", "left");
    criteria2.put("tableName", "PRODUCT");
    sqlBuilder.putArray("orderByColumns");
    return sqlBuilder;
  }

  private void listAnalyses(String token, String analysisName) throws JsonProcessingException {
    ObjectNode node = mapper.createObjectNode();
    ObjectNode contents = node.putObject("contents");
    contents.put("action", "search");
    ArrayNode keys = contents.putArray("keys");
    ObjectNode key = keys.addObject();
    key.put("categoryId", "4");
    String json = mapper.writeValueAsString(node);
    String path = "contents.analyze.find { it.name == '" + analysisName + "' }.metricName";
    given(spec)
        .header("Authorization", "Bearer " + token)
        .body(json)
        .when()
        .post("/saw/services/analysis")
        .then()
        .assertThat()
        .statusCode(200)
        .body(path, equalTo("sample-elasticsearch"));
  }

  private void executeAnalysis(String token, String analysisId) throws JsonProcessingException {
    ObjectNode node = mapper.createObjectNode();
    ObjectNode contents = node.putObject("contents");
    contents.put("action", "execute");
    ArrayNode keys = contents.putArray("keys");
    ObjectNode key = keys.addObject();
    key.put("id", analysisId);
    String json = mapper.writeValueAsString(node);
    String buckets = "contents.analyze[0].data.row_level_1.buckets";
    given(spec)
        .header("Authorization", "Bearer " + token)
        .body(json)
        .when()
        .post("/saw/services/analysis")
        .then()
        .assertThat()
        .statusCode(200)
        .body(buckets + ".find { it.key == 'string 1' }.doc_count", equalTo(1));
  }

  private void executeDLAnalysis(String token, String analysisId) throws JsonProcessingException {
    ObjectNode node = mapper.createObjectNode();
    ObjectNode contents = node.putObject("contents");
    contents.put("action", "execute");
    ArrayNode keys = contents.putArray("keys");
    ObjectNode key = keys.addObject();
    key.put("id", analysisId);
    String json = mapper.writeValueAsString(node);
    String buckets = "contents.analyze[0]";
    given(spec)
        .header("Authorization", "Bearer " + token)
        .body(json)
        .when()
        .post("/saw/services/analysis")
        .then()
        .assertThat()
        .statusCode(200)
        .body(buckets + ".totalRows", equalTo(247));
  }

  private String listSingleExecution(String token, String analysisId) {
    Response response =
        request(token)
            .when()
            .get("/saw/services/analysis/" + analysisId + "/executions")
            .then()
            .assertThat()
            .statusCode(200)
            .body("executions.size()", greaterThan(0))
            .extract()
            .response();
    return response.path("executions[0].id");
  }

  private List<Map<String, String>> getExecution(
      String token, String analysisId, String executionId) {
    String path = "/saw/services/analysis/" + analysisId + "/executions/" + executionId + "/data";
    return request(token)
        .when()
        .get(path)
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response()
        .path("data");
  }

  private ObjectNode globalFilters() {
    // ObjectMapper mapper = new ObjectMapper();
    ObjectNode objectNode = mapper.createObjectNode();
    ArrayNode globalFilters = objectNode.putArray("globalFilters");
    ObjectNode globalFilter = globalFilters.addObject();
    globalFilter.put("tableName", "sample");
    globalFilter.put("semanticId", "123");
    ArrayNode filters = globalFilter.putArray("filters");
    ObjectNode filter = filters.addObject();
    filter.put("columnName", "long");
    filter.put("type", "long");
    filter.put("size", "10");
    filter.put("order", "asc");
    ObjectNode filter1 = filters.addObject();
    filter1.put("columnName", "string.keyword");
    filter1.put("type", "string");
    filter1.put("size", "10");
    filter1.put("order", "asc");
    ObjectNode es = mapper.createObjectNode();
    es.put("storageType", "ES");
    es.put("indexName", "sample");
    es.put("type", "sample");
    globalFilter.putPOJO("esRepository", es);
    return objectNode;
  }

  @Test(timeout = 300000)
  public void testGlobalFilter() throws JsonProcessingException {
    /* Use list metrics method, which waits for sample metrics to
     * be loaded before returning, to ensure sample metrics are
     * available before creating filters */
    listMetrics(token, "sample-elasticsearch");
    /* Proceed to creating filters */
    ObjectNode node = globalFilters();
    String json = mapper.writeValueAsString(node);
    String field = "string.keyword";
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when()
            .post("/saw/services/filters")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    ObjectNode root = response.as(ObjectNode.class);
    JsonNode jsonNode= root.get("long");
    Assert.assertTrue("Range filter max value ",jsonNode.get("_max").asLong()==1553);
    Assert.assertTrue("Range filter max value ",jsonNode.get("_min").asLong()==1000);
  }

  private RequestSpecification request(String token) {
    return given(spec).header("Authorization", "Bearer " + token);
  }

  private String getJWTToken() {
    Long tokenValid = 150l;
    String secretKey = "Dgus5PoaEHm2tKEjy0cUGnzQlx86qiutmBZjPbI4y0U=";
    Map<String, Object> map = new HashMap<>();
    map.put("valid", true);
    map.put("validUpto", System.currentTimeMillis() + tokenValid * 60 * 1000);
    map.put("validityReason", "");
    map.put("masterLoginId", "sawadmin@synchronoss.com");
    return Jwts.builder()
        .setSubject("sawadmin@synchronoss.com")
        .claim("ticket", map)
        .setIssuedAt(new Date())
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  @Test
  public void schedulerTest() throws JsonProcessingException {
    ObjectNode node = scheduleData();
    String json = mapper.writeValueAsString(node);
    createSchedule(json);
    updateSchedule(json);
    String categoryID = node.get("categoryID").asText();
    String jobGroup = node.get("jobGroup").asText();
    listSchedule(categoryID, jobGroup);
  }

  private void createSchedule(String json) {
    Response response =
        given(spec)
            .filter(document("create-schedule", preprocessResponse(prettyPrint())))
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when()
            .post("/saw/services/scheduler/schedule")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
  }

  private void updateSchedule(String json) {
    Response response =
        given(spec)
            .filter(document("update-schedule", preprocessResponse(prettyPrint())))
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when()
            .post("/saw/services/scheduler/update")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
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
        objectNode.put("jobScheduleTime", "2018-03-01T16:24:28+05:30");
        objectNode.put("categoryID", "4");
        objectNode.put("jobGroup", "SYNCHRONOSS");
        objectNode.put("endDate", "2099-03-01T16:24:28+05:30");
        objectNode.put("timezone", "UTC");
        return objectNode;
    }

  private void listSchedule(String categoryID, String groupName) throws JsonProcessingException {
    ObjectNode node = mapper.createObjectNode();
    node.put("categoryId", categoryID);
    node.put("groupkey", groupName);
    String json = mapper.writeValueAsString(node);
    Response response =
        given(spec)
            .filter(document("list-schedule", preprocessResponse(prettyPrint())))
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when()
            .post("/saw/services/scheduler/jobs")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
  }

  @Test
  public void kpiExecuteTest() throws JsonProcessingException {
    ObjectNode node = kpiData();
    String json = mapper.writeValueAsString(node);
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when()
            .post("/saw/services/kpi")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
  }

  /**
   * prepare data to execute KPI.
   *
   * @return
   */
  private ObjectNode kpiData() {
    ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("action", "execute");
    ArrayNode keys = objectNode.putArray("keys");
    ObjectNode key = keys.addObject();
    key.put("customerCode", "SYNCHRONOSS");
    key.put("module", "observe");
    key.put("semanticId", "workbench::sample-elasticsearch");
    key.put("analysisType", "kpi");
    ObjectNode kpi = mapper.createObjectNode();
    kpi.put("id", "abc-123");
    kpi.put("name", "Integer");
    kpi.put("tableName", "sample");
    kpi.put("semanticId", "workbench::sample-elasticsearch");
    ArrayNode dataFields = kpi.putArray("dataFields");
    ObjectNode fields = mapper.createObjectNode();
    fields.put("columnName", "integer");
    fields.put("name", "integer");
    ArrayNode aggregate = fields.putArray("aggregate");
    aggregate.add("sum");
    aggregate.add("avg");
    aggregate.add("min");
    aggregate.add("max");
    aggregate.add("count");
    dataFields.add(fields);
    ArrayNode filters = kpi.putArray("filters");
    ObjectNode filtersObject = mapper.createObjectNode();
    filtersObject.put("type", "date");
    filtersObject.put("columnName", "date");
    ObjectNode modal = mapper.createObjectNode();
    modal.put("preset", "LSM");
    filtersObject.putPOJO("model", modal);
    filters.add(filtersObject);
    ObjectNode esRepository = mapper.createObjectNode();
    esRepository.put("storageType", "ES");
    esRepository.put("indexName", "sample");
    esRepository.put("type", "sample");
    kpi.putPOJO("esRepository", esRepository);
    objectNode.putPOJO("kpi", kpi);
    return objectNode;
  }

  @Test
  public void userPreferenceTest() throws JsonProcessingException {
    String json = mapper.writeValueAsString(PreferenceData());
    Response create =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .body(json)
            .when()
            .post("/saw/security/auth/user/preferences/upsert")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    ObjectNode createNode = create.as(ObjectNode.class);
    Assert.assertEquals(1, createNode.get("userID").asLong());
    Assert.assertEquals(1, createNode.get("customerID").asLong());
    Assert.assertEquals(4, createNode.get("preferences").size());
    String json1 = mapper.writeValueAsString(deletePreferenceData());
    given(spec)
        .header("Authorization", "Bearer " + token)
        .header("Content-Type", "application/json")
        .body(json1)
        .when()
        .post("/saw/security/auth/user/preferences/delete")
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();

    Response fetch =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .when()
            .get("/saw/security/auth/user/preferences/fetch")
            .then()
            .defaultParser(Parser.JSON)
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    ObjectNode fetchNode = fetch.as(ObjectNode.class);
    Assert.assertEquals(1, fetchNode.get("userID").asLong());
    Assert.assertEquals(1, fetchNode.get("customerID").asLong());
    Assert.assertEquals(2, fetchNode.get("preferences").size());
  }

  private ArrayNode PreferenceData() {
    ArrayNode arrayNode = mapper.createArrayNode();
    ObjectNode objectNode1 = arrayNode.addObject();
    objectNode1.put("preferenceName", "defaultURL1");
    objectNode1.put("preferenceValue", "http://localhost/saw/observe/1");
    ObjectNode objectNode2 = arrayNode.addObject();
    objectNode2.put("preferenceName", "defaultURL2");
    objectNode2.put("preferenceValue", "http://localhost/saw/observe/2");
    ObjectNode objectNode3 = arrayNode.addObject();
    objectNode3.put("preferenceName", "defaultURL3");
    objectNode3.put("preferenceValue", "http://localhost/saw/observe/3");
    ObjectNode objectNode4 = arrayNode.addObject();
    objectNode4.put("preferenceName", "defaultURL4");
    objectNode4.put("preferenceValue", "http://localhost/saw/observe/4");
    return arrayNode;
  }

  private ArrayNode deletePreferenceData() {
    ArrayNode arrayNode = mapper.createArrayNode();
    ObjectNode objectNode1 = arrayNode.addObject();
    objectNode1.put("preferenceName", "defaultURL1");
    objectNode1.put("preferenceValue", "http://localhost/saw/observe/1");
    ObjectNode objectNode2 = arrayNode.addObject();
    objectNode2.put("preferenceName", "defaultURL3");
    objectNode2.put("preferenceValue", "http://localhost/saw/observe/3");
    return arrayNode;
  }
}

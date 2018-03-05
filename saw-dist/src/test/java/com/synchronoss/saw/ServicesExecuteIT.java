package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.util.*;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.*;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;

/**
 * Integration test that lists metrics, creates an analysis, saves it,
 * executes it and lists the execution results.
 */
public class ServicesExecuteIT {
    private RequestSpecification spec;

    private ObjectMapper mapper;
    private String token;

    @BeforeClass
    public static void setUpClass() {
        String port = System.getProperty("saw.docker.port");
        if (port == null) {
            throw new RuntimeException("Property saw.docker.port unset");
        }
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Rule
    public final JUnitRestDocumentation restDocumentation =
        new JUnitRestDocumentation();

    @Before
    public void setUp() throws JsonProcessingException {
        this.spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation)).build();
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        /* Token is required for all the test cases.
         Initialize the token before test case run.  */
        token = authenticate();
    }

    @Test
    public void testExecuteAnalysis() throws JsonProcessingException {
        String metricId = listMetrics(token);
        ObjectNode analysis = createAnalysis(token, metricId);
        String analysisId = analysis.get("id").asText();
        String analysisName = "Test (" + System.currentTimeMillis() + ")";
        saveAnalysis(token, analysisId, analysisName, analysis);
        listAnalyses(token, analysisName);
        executeAnalysis(token, analysisId);
        String executionId = listSingleExecution(token, analysisId);
        List<Map<String, String>> data = getExecution(
            token, analysisId, executionId);
        /* Note: For now the execution results are empty, so expect
         * zero rows.  Update to expected count when implementation
         * changes.  */
        assertThat(data.size(), equalTo(0));
    }

    @Test
    public void testSSOAuthentication()
    {
         Response response = given(spec)
                 .header("Cache-Control", "no-store").
                 filter(document("sso-authentication",
                 preprocessResponse(prettyPrint())))
            .when().get("/security/authentication?jwt=" +getJWTToken())
            .then().assertThat().statusCode(200)
            .extract().response();
        assertNotNull("Valid access Token not found, Authentication failed ",response.path("aToken"));
        assertNotNull("Valid refresh Token not found, Authentication failed",response.path("rToken"));
    }

    private static final String TEST_USERNAME = "sawadmin@synchronoss.com";
    private static final String TEST_PASSWORD = "Sawsyncnewuser1!";

    private String authenticate() throws JsonProcessingException {
        ObjectNode node = mapper.createObjectNode();
        node.put("masterLoginId", TEST_USERNAME);
        node.put("password", TEST_PASSWORD);
        String json = mapper.writeValueAsString(node);
        Response response = given(spec)
            .accept("application/json")
            .header("Content-Type", "application/json")
            .body(json)
            .filter(document(
                        "authenticate",
                        preprocessRequest(
                            preprocessReplace(TEST_USERNAME, "user@example.com"),
                            preprocessReplace(TEST_PASSWORD, "password123"))))
            .when().post("/security/doAuthenticate")
            .then().assertThat().statusCode(200)
            .body("aToken", startsWith(""))
            .extract().response();
        return response.path("aToken");
    }

    private OperationPreprocessor preprocessReplace(String from, String to) {
        return replacePattern(Pattern.compile(Pattern.quote(from)), to);
    }

    private String listMetrics(String token) throws JsonProcessingException {
        ObjectNode node = mapper.createObjectNode();
        ObjectNode contents = node.putObject("contents");
        contents.put("action", "search");
        contents.put("context", "Semantic");
        contents.put("select", "headers");
        ArrayNode keys = contents.putArray("keys");
        ObjectNode key = keys.addObject();
        key.put("customerCode", "SYNCHRONOSS");
        key.put("module", "ANALYZE");
        String json = mapper.writeValueAsString(node);
        String path = "contents[0]['ANALYZE'].find "
            + "{it.metric == 'sample-elasticsearch'}.id";
        Response response = given(spec)
            .header("Authorization", "Bearer " + token)
            .filter(document("list-metrics",
                             preprocessResponse(prettyPrint())))
            .body(json)
            .when().post("/services/md")
            .then().assertThat().statusCode(200)
            .body(path, instanceOf(String.class))
            .extract().response();
        return response.path(path);
    }

    private ObjectNode createAnalysis(String token, String metricId)
        throws JsonProcessingException {
        ObjectNode node = mapper.createObjectNode();
        ObjectNode contents = node.putObject("contents");
        contents.put("action", "create");
        ArrayNode keys = contents.putArray("keys");
        ObjectNode key = keys.addObject();
        key.put("customerCode", "SYNCHRONOSS");
        key.put("module", "ANALYZE");
        key.put("id", metricId);
        key.put("analysisType", "pivot");
        String json = mapper.writeValueAsString(node);
        Response response = given(spec)
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when().post("/services/analysis")
            .then().assertThat().statusCode(200)
            .extract().response();
        ObjectNode root = response.as(ObjectNode.class);
        return (ObjectNode) root.get("contents").get("analyze").get(0);
    }

    private void saveAnalysis(String token, String analysisId,
                              String analysisName, ObjectNode analysis)
        throws JsonProcessingException {
        analysis.put("saved", true);
        analysis.put("categoryId", 4);
        analysis.put("name", analysisName);
        analysis.set("sqlBuilder", sqlBuilder());
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
            .when().post("/services/analysis")
            .then().assertThat().statusCode(200);
    }

    private ObjectNode sqlBuilder() {
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

    private void listAnalyses(String token, String analysisName)
        throws JsonProcessingException {
        ObjectNode node = mapper.createObjectNode();
        ObjectNode contents = node.putObject("contents");
        contents.put("action", "search");
        ArrayNode keys = contents.putArray("keys");
        ObjectNode key = keys.addObject();
        key.put("categoryId", "4");
        String json = mapper.writeValueAsString(node);
        String path = "contents.analyze.find { it.name == '"
            + analysisName + "' }.metric";
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when().post("/services/analysis")
            .then().assertThat().statusCode(200)
            .body(path, equalTo("sample-elasticsearch"));
    }

    private void executeAnalysis(String token, String analysisId)
        throws JsonProcessingException {
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
            .when().post("/services/analysis")
            .then().assertThat().statusCode(200)
            .body(buckets + ".find { it.key == 'string 1' }.doc_count", equalTo(1));
    }

    private String listSingleExecution(String token, String analysisId) {
        Response response = request(token)
            .when().get("/services/analysis/" + analysisId + "/executions")
            .then().assertThat().statusCode(200)
            .body("executions", hasSize(1))
            .extract().response();
        return response.path("executions[0].id");
    }

    private List<Map<String, String>> getExecution(
        String token, String analysisId, String executionId) {
        String path = "/services/analysis/" + analysisId + "/executions/"
            + executionId + "/data";
        return request(token).when().get(path)
            .then().assertThat().statusCode(200)
            .extract().response().path("data");
    }

    private ObjectNode globalFilters() {
       // ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode globalFilters =  objectNode.putArray("globalFilters");
        ObjectNode globalFilter = globalFilters.addObject();
        globalFilter.put("tableName", "sample");
        globalFilter.put("semanticId", "123");
        ArrayNode filters = globalFilter.putArray("filters");
        ObjectNode filter = filters.addObject();
        filter.put("columnName","long");
        filter.put("type","long");
        filter.put("size","10");
        filter.put("order","asc");
        ObjectNode filter1 = filters.addObject();
        filter1.put("columnName","string.keyword");
        filter1.put("type","string");
        filter1.put("size","10");
        filter1.put("order","asc");
        ObjectNode es = mapper.createObjectNode();
        es.put("storageType","es");
        es.put("indexName","sample");
        es.put("type","sample");
        globalFilter.putPOJO("esRepository",
                es);
        return objectNode;
    }

    @Test
    public void globalFilterTest()  throws JsonProcessingException {
        ObjectNode node = globalFilters();
        String json = mapper.writeValueAsString(node);
        String field = "string.keyword";
        Response response = given(spec)
                .header("Authorization", "Bearer " + token)
                .body(json)
                .when().post("/services/filters")
                .then().assertThat().statusCode(200)
                .extract().response();
        ObjectNode root = response.as(ObjectNode.class);
       JsonNode jsonNode= root.get("long");
       Assert.assertTrue("Range filter max value ",jsonNode.get("_max").asLong()==1498);
       Assert.assertTrue("Range filter max value ",jsonNode.get("_min").asLong()==1000);
    }

    private RequestSpecification request(String token) {
        return given(spec).header("Authorization", "Bearer " + token);
    }

   private String getJWTToken() {
       Long tokenValid = 150l;
       String secretKey = "Dgus5PoaEHm2tKEjy0cUGnzQlx86qiutmBZjPbI4y0U=";
       Map<String, Object> map = new HashMap();
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
    public void schedulerTest() throws JsonProcessingException
    {
        ObjectNode node = scheduleData();
        String json = mapper.writeValueAsString(node);
        createSchedule(json);
        updateSchedule(json);
        String categoryID = node.get("categoryID").asText();
        String jobGroup = node.get("jobGroup").asText();
        listSchedule(categoryID,jobGroup);
    }

    private void createSchedule(String json) {
        Response response = given(spec).filter(document("create-schedule",
                preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + token)
                .body(json)
                .when().post("/services/scheduler/schedule")
                .then().assertThat().statusCode(200)
                .extract().response();
    }

    private void updateSchedule(String json) {
        Response response = given(spec).filter(document("update-schedule",
                preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + token)
                .body(json)
                .when().post("/services/scheduler/update")
                .then().assertThat().statusCode(200)
                .extract().response();
    }

    private void listSchedule(String categoryID,String groupName) throws JsonProcessingException {
        ObjectNode node = mapper.createObjectNode();
        node.put("categoryId",categoryID);
        node.put("groupkey",groupName);
        String json = mapper.writeValueAsString(node);
        Response response = given(spec).filter(document("list-schedule",
                preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + token)
                .body(json)
                .when().post("/services/scheduler/jobs")
                .then().assertThat().statusCode(200)
                .extract().response();
    }

    private ObjectNode scheduleData()
    {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("activeRadio","everyDay");
        objectNode.put("activeTab","daily");
        objectNode.put("analysisID","123");
        objectNode.put("analysisName","Untitled Analysis");
        objectNode.put("cronExpression","0 31 20 1/1 * ? *");
        objectNode.put("fileType","csv");
        objectNode.put("jobName","123");
        objectNode.put("metricName","Sample (report) - new");
        objectNode.put("type","report");
        objectNode.put("userFullName","System");
        ArrayNode email = objectNode.putArray("emailList");
        email.add("abc@synchronoss.com");
        email.add("xyz@synchronoss.com");
        objectNode.put("jobScheduleTime","2018-03-01T16:24:28+05:30");
        objectNode.put("categoryID","4");
        objectNode.put("jobGroup","SYNCHRONOSS");
     return objectNode;
    }

}

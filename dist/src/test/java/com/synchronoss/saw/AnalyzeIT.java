package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockftpserver.core.session.SessionKeys.USERNAME;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.apache.commons.net.ftp.FTPClient;
import org.junit.*;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;

/**
 * Integration test that lists metrics, creates an analysis, saves it,
 * executes it and lists the execution results.
 */
public class AnalyzeIT extends BaseIT {
    @Test(timeout=300000)
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

    @Test(timeout = 300000)
    public void exportData() throws JsonProcessingException, IOException {

        // create and save analysis
        String metricId = listMetrics(token);
        ObjectNode analysis = createAnalysis(token, metricId);
        String analysisId = analysis.get("id").asText();
        String analysisName = "Test (" + System.currentTimeMillis() + ")";
        saveAnalysis(token, analysisId, analysisName, analysis);

        // create schedule
        ObjectNode node = scheduleData();
        // udpate the analysis ID
        node.put("analysisID", analysisId);
        String json = mapper.writeValueAsString(node);
        createSchedule(json);

        // execute the analysis and retrieve results
        executeAnalysis(token, analysisId);
        String executionId = listSingleExecution(token, analysisId);
        List<Map<String, String>> data = getExecution(
            token, analysisId, executionId);
        // base setup for ftp server
        String username = "user";
        String password = "password";
        String homeDirectory = "/";
        String filename = "report.csv";
        // Write the data to csv
        FakeFtpServer f = createFileOnFakeFTP(username, password, homeDirectory, filename, data);

        // check if the file has been uploaded and read the contents
        String dataResult = readFile("/data/" + filename, "localhost", f.getServerControlPort(),
            username, password);
        // check if the file actually has data
        // this has been done just to avoid rewriting integration test case when data changes.
        // the goal here is to just check if the data that we got from scheduled analysis execution
        // is present in the file.
        assertTrue(dataResult.length() > 0);
    }

    public FakeFtpServer createFileOnFakeFTP(String username, String password, String homeDirectory, String filename, List<Map<String, String>> data) {
        FakeFtpServer aFakeFtpServer = new FakeFtpServer();
        aFakeFtpServer.setServerControlPort(0);
        aFakeFtpServer.addUserAccount(new UserAccount(username, password, homeDirectory));

        FileSystem aFileSystem = new UnixFakeFileSystem();
        aFileSystem.add(new DirectoryEntry("/data"));
        // using tostring because data is empty
        aFileSystem.add(new FileEntry("/data/" + filename, data.toString()));
        aFakeFtpServer.setFileSystem(aFileSystem);

        aFakeFtpServer.start();
        return aFakeFtpServer;
    }

    public String readFile(String filename, String server, int port, String username, String password) throws IOException {

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
            .extract().response();
        try {
            String metricId = response.path(path);
            if (metricId == null) {
                return retryListMetrics(token);
            }
            return metricId;
        } catch (IllegalArgumentException e) {
            return retryListMetrics(token);
        }
    }

    private String retryListMetrics(String token)
        throws JsonProcessingException {
        /* Path was not found, so wait and retry.  The sample metrics
         * are loaded asynchronously, so wait until the loading
         * finishes before proceeding.  */
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {}
        return listMetrics(token);
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

    @Test(timeout=300000)
    public void testGlobalFilter()  throws JsonProcessingException {
        /* Use list metrics method, which waits for sample metrics to
         * be loaded before returning, to ensure sample metrics are
         * available before creating filters */
        listMetrics(token);
        /* Proceed to creating filters */
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

    private ObjectNode  scheduleData()
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
        ArrayNode ftp = objectNode.putArray("ftp");
        ftp.add("ftp");
        objectNode.put("jobScheduleTime","2018-03-01T16:24:28+05:30");
        objectNode.put("categoryID","4");
        objectNode.put("jobGroup","SYNCHRONOSS");
        objectNode.put("endDate", "2099-03-01T16:24:28+05:30");
     return objectNode;
    }

    @Test
    public void kpiExecuteTest() throws JsonProcessingException
    {
        ObjectNode node = kpiData();
        String json = mapper.writeValueAsString(node);
        Response response = given(spec)
            .header("Authorization", "Bearer " + token)
            .body(json)
            .when().post("/services/kpi")
            .then().assertThat().statusCode(200)
            .extract().response();
    }

    /**
     * prepare data to execute KPI.
     * @return
     */
    private ObjectNode kpiData()
    {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("action", "execute");
        ArrayNode keys = objectNode.putArray("keys");
        ObjectNode key = keys.addObject();
        key.put("customerCode", "SYNCHRONOSS");
        key.put("module", "observe");
        key.put("semanticId", "dd2335a1-fa77-4db2-b50b-5391ac7117de");
        key.put("analysisType", "kpi");
        ObjectNode kpi = mapper.createObjectNode();
        kpi.put("id","abc-123");
        kpi.put("name","Integer");
        kpi.put("tableName","sample");
        kpi.put("semanticId","dd2335a1-fa77-4db2-b50b-5391ac7117de");
        ArrayNode dataFields = kpi.putArray("dataFields");
        ObjectNode fields = mapper.createObjectNode();
        fields.put("columnName","integer");
        fields.put("name","integer");
        ArrayNode aggregate = fields.putArray("aggregate");
        aggregate.add("sum");
        aggregate.add("avg");
        aggregate.add("min");
        aggregate.add("max");
        aggregate.add("count");
        dataFields.add(fields);
        ArrayNode filters = kpi.putArray("filters");
        ObjectNode filtersObject = mapper.createObjectNode();
        filtersObject.put("type","date");
        filtersObject.put("columnName","date");
        ObjectNode modal = mapper.createObjectNode();
        modal.put("preset","LSM");
        filtersObject.putPOJO("model",modal);
        filters.add(filtersObject);
        ObjectNode esRepository = mapper.createObjectNode();
        esRepository.put("storageType", "ES");
        esRepository.put("indexName", "sample");
        esRepository.put("type", "sample");
        kpi.putPOJO("esRepository",esRepository);
        objectNode.putPOJO("kpi",kpi);
        return objectNode;
    }
}

package com.synchronoss.saw;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.io.IOException;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.response.Response;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Workbench Service integration tests.  Tests parsing, viewing and
 * executing components.
 */
public class WorkbenchIT extends com.synchronoss.saw.BaseIT {
    private static final String WORKBENCH_PROJECT = "workbench";
    private static final String WORKBENCH_PATH =
        "/services/internal/workbench/projects/" + WORKBENCH_PROJECT;
    private static final int WAIT_RETRIES = 30;
    private static final int WAIT_SLEEP_SECONDS = 5;
    private final Logger log = LoggerFactory.getLogger(getClass().getName());


    /**
     * Parse a CSV file into dataset with given name using Workbench
     * Services.
     */
    private String parseDataset2() throws IOException {

        ObjectNode root = mapper.createObjectNode();
        root.put("name", "WBAPARSER01");
        root.put("component", "parser");
        ObjectNode config = root.putObject("configuration");
        ArrayNode fields = config.putArray("fields");

        ObjectNode field1 = fields.addObject();
        field1.put("name", "State");
        field1.put("type", "string");

        ObjectNode field2 = fields.addObject();
        field2.put("name", "Name");
        field2.put("type", "string");

        ObjectNode field3 = fields.addObject();
        field3.put("name", "NTDID");
        field3.put("type", "string");

        ObjectNode field4 = fields.addObject();
        field4.put("name", "LegacyNTDID");
        field4.put("type", "string");

        ObjectNode field5 = fields.addObject();
        field5.put("name", "OrgType");
        field5.put("type", "string");

        ObjectNode field6 = fields.addObject();
        field6.put("name", "ReporterType");
        field6.put("type", "string");

        ObjectNode field7 = fields.addObject();
        field7.put("name", "UrbanizedArea");
        field7.put("type", "string");

        ObjectNode field8 = fields.addObject();
        field8.put("name", "UZAPopulation");
        field8.put("type", "string");

        ObjectNode field9 = fields.addObject();
        field9.put("name", "UZASize");
        field9.put("type", "string");

        ObjectNode field10 = fields.addObject();
        field10.put("name", "WholeAgencyVOMSSize");
        field10.put("type", "string");

        ObjectNode field11 = fields.addObject();
        field11.put("name", "Mode");
        field11.put("type", "string");

        ObjectNode field12 = fields.addObject();
        field12.put("name", "TOS");
        field12.put("type", "string");

        ObjectNode field13 = fields.addObject();
        field13.put("name", "VOMS");
        field13.put("type", "integer");

        ObjectNode field14 = fields.addObject();
        field14.put("name", "MajorMechanicalFailure");
        field14.put("type", "integer");

        ObjectNode field15 = fields.addObject();
        field15.put("name", "OtherMechanicalFailure");
        field15.put("type", "integer");

        ObjectNode field16 = fields.addObject();
        field16.put("name", "TotalRevenueSystemMechanical");
        field16.put("type", "integer");

        ObjectNode field17 = fields.addObject();
        field17.put("name", "C4");
        field17.put("type", "string");

        config.put("file", "RevenueVehicleMaintPerf2.csv");
        config.put("lineSeparator", "\n");
        config.put("delimiter", ",");
        config.put("quoteChar", "\"");
        config.put("quoteEscape", "\\");
        config.put("headerSize", "4");

        ObjectNode outputs = config.putObject("output");
        outputs.put("dataSet", "WBAPARSER01");
        outputs.put("mode", "replace");
        outputs.put("format", "parquet");
        outputs.put("catalog", "data");

        ArrayNode parameters = config.putArray("parameters");
        ObjectNode p1 = parameters.addObject();
        p1.put("name", "spark.master");
        p1.put("value", "local[*]");

        String json = mapper.writeValueAsString(root);

        log.debug("request: " + json);

        Response response = given(authSpec)
            .body(json)
            .when()
            .post(WORKBENCH_PATH + "/datasets")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
        String resp = response.getBody().asString();

        assert (resp != null);
        log.debug("Response: " + resp);
        JsonNode node = mapper.reader().readTree(resp);
        assert (node != null);
        String id = node.get("id").asText();
        return id;
    }



    /**
     * Parse a CSV file into dataset with given name using Workbench
     * Services.
     */
    private void parseDataset(String name) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.put("name", name);
        root.put("component", "parser");
        ObjectNode config = root.putObject("configuration");
        config.put("action", "create");
        ArrayNode fields = config.putArray("fields");
        ObjectNode field1 = fields.addObject();
        field1.put("name", "field1");
        field1.put("type", "string");
        ObjectNode field2 = fields.addObject();
        field2.put("name", "field2");
        field2.put("type", "long");
        ObjectNode field3 = fields.addObject();
        field3.put("name", "field3");
        field3.put("type", "string");
        config.put("file", "test.csv");
        config.put("lineSeparator", "\n");
        config.put("delimiter", ",");
        config.put("quoteChar", "\"");
        config.put("quoteEscape", "\\");
        config.put("headerSize", "0");
        ObjectNode outputs = config.putObject("output");
        outputs.put("dataSet", name);
        outputs.put("mode", "replace");
        outputs.put("format", "parquet");
        outputs.put("catalog", "data");

        ArrayNode parameters = config.putArray("parameters");
        ObjectNode p1 = parameters.addObject();
        p1.put("name", "spark.master");
        p1.put("value", "local[*]");

        String json = mapper.writeValueAsString(root);
        log.debug("request: " + json);

        Response response = given(authSpec)
            .body(json)
            .when().post(WORKBENCH_PATH + "/datasets")
            .then().assertThat().statusCode(200)
            .extract()
            .response();
        String resp = response.getBody().asString();

        assert (resp != null);
        log.debug("Response: " + resp);
        JsonNode node = mapper.reader().readTree(resp);
        assert (node != null);


    }

    /**
     * Wait until dataset becomes visible in Workbench Services, using
     * the given number of retries before timing out.
     */
    private void waitForDataset(String id, int retries)
        throws JsonProcessingException {
        String status = getDatasetStatus(id);
        if (status == null
            || status.equals("INIT")
            || status.equals("IN-PROGRESS")
            || status.equals("STARTED")) {
            if (retries == 0) {
                throw new RuntimeException(
                    "Timed out waiting while waiting for dataset");
            }
            log.debug("Waiting for dataset: id = {}, retries = {}",
                      id, retries);
            try {
                Thread.sleep(WAIT_SLEEP_SECONDS * 1000);
            } catch (InterruptedException e) {
                log.debug("Interrupted");
            }
            waitForDataset(id, retries - 1);
        } else if (!status.equals("SUCCESS")
                    && !status.equals("FAILED")
                    && !status.equals("PARTIAL")) {
            throw new RuntimeException(
                "Unknown dataset status: " + status);
        } else if (status.equals("SUCCESS")) {
            log.info("XDF successfully completed.");
        } else if (status.equals("FAILED")) {
            log.info("XDF failed.");
        } else {
            log.info("XDF partially succeeded.");
        }
        /* Dataset is in SUCCESS state, so return */
    }

    /**
     * Get the status of a dataset in the Workbench Service.
     */
    private String getDatasetStatus(String id)
        throws JsonProcessingException {
        String datasetPath = "find { it._id == '" + id + "' }";
        String statusPath = datasetPath + ".asOfNow.status";
        Response response = given(authSpec)
            .when().get(WORKBENCH_PATH + "/datasets")
            .then().assertThat().statusCode(200)
            /* Note: Assertion below commented out until datasets are
             * preregistered */
            //.body(datasetPath, isA(Map.class))
            .extract().response();

        String resp = response.getBody().asString();
        assert (resp != null);
        return response.path(statusPath);
    }


    @Test
    public void testListPreregDatasets() throws IOException {
        // id = parseDataset("test_list")
        String id = parseDataset2();
        assert (id.equalsIgnoreCase("workbench::WBAPARSER01"));
        log.debug("ID: " + id);
        waitForDataset(id, WAIT_RETRIES);
    }

    @Test
    public void testParseDataset() throws IOException {
        String name = "test_parse";
        parseDataset(name);
        /* Workaround: Until the dataset creation API provides the
         * dataset ID, construct it manually here. */
        String id = "workbench::" + name;
        waitForDataset(id, WAIT_RETRIES);
    }




    @Test
    public void testSQLDataset() throws JsonProcessingException {
        String name = "test-sql-" + testId();
        /* Use only characters suitable for a SQL table name */
        name = name.replace("-", "_");
        /* Execute SQL */
        executeSQLDataset(name);
        /* Workaround: Until the dataset creation API provides the
         * dataset ID, construct it manually here. */
        String id = "workbench::" + name;
        waitForDataset(id, WAIT_RETRIES);
    }

    /**
     * Execute SQL on a dataset with given name using Workbench
     * Services.
     */
    private void executeSQLDataset(String name)
        throws JsonProcessingException {
        String inputName = "test-parse-" + testId();
        /* Use only characters suitable for a SQL table name */
        inputName = inputName.replace("-", "_");
        /* Create dataset to be used for testing viewing dataset */
        parseDataset(inputName);
        /* Workaround: Until the dataset creation API provides the
         * dataset ID, construct it manually here. */
        String inputId = "workbench::" + inputName;
        waitForDataset(inputId, WAIT_RETRIES);
        /* Execute SQL on dataset */
        ObjectNode root = mapper.createObjectNode();
        root.put("name", name);
        root.put("input", inputName);
        root.put("component", "sql");
        ObjectNode config = root.putObject("configuration");
        String sql = "CREATE TABLE " + name + " AS SELECT * FROM "
            + inputName;
        config.put("script", sql);
        given(authSpec)
            .body(root)
            .when().post(WORKBENCH_PATH + "/datasets")
            .then().assertThat().statusCode(200);
        /* Wait for dataset */
        String outputId = "workbench::" + name;
        waitForDataset(outputId, WAIT_RETRIES);
        /* View dataset results */
        viewDataset(name);
    }

    @Test
    public void testListDatasets() {
        /* Note: Placeholder for Workbench list datasets integration
         * test.  To be done: Create a dataset and then make
         * assertions on it when listing available datasets. */
        given(authSpec)
            .when().get(WORKBENCH_PATH + "/datasets")
            .then().assertThat().statusCode(200)
            .body(containsString(""));
    }

    @Test
    public void testPreviewDataset() throws IOException {
        String name = "test-preview-" + testId();
        /* Create dataset to be used for testing viewing dataset */
        parseDataset(name);
        /* Workaround: Until the dataset creation API provides the
         * dataset ID, construct it manually here. */
        String id = "workbench::" + name;
        waitForDataset(id, WAIT_RETRIES);
        /* View dataset */
        viewDataset(name);
    }

    private void viewDataset(String name) throws JsonProcessingException {
        ObjectNode root = mapper.createObjectNode();
        root.put("name", name);
        Response response = given(authSpec)
            .body(root)
            .when().post(WORKBENCH_PATH + "/previews")
            .then().assertThat().statusCode(200)
            .extract().response();
        String previewId = response.path("id");
        /* Wait for preview to become available */
        waitForPreview(previewId, WAIT_RETRIES);
        /* Assert preview rows exist */
        given(authSpec)
            .when().get(WORKBENCH_PATH + "/previews/" + previewId)
            .then().assertThat().statusCode(200)
            .body("rows", hasSize(3))
            .body("rows[0].field1", equalTo("foo"));
    }

    /**
     * Wait until preview becomes visible in Workbench Services, using
     * the given number of retries before timing out.
     */
    private void waitForPreview(String id, int retries)
        throws JsonProcessingException {
        Response response = given(authSpec)
            .when().get(WORKBENCH_PATH + "/previews/" + id)
            .then().assertThat().statusCode(200)
            .extract().response();
        String status = response.path("status");
        if (status.equals("success")) {
            return;
        } else if (!status.equals("queued")) {
            throw new RuntimeException("Unknown preview status: " + status);
        }
        /* Preview not found yet, so wait more */
        if (retries == 0) {
            throw new RuntimeException(
                "Timed out waiting while waiting for preview");
        }
        log.debug("Waiting for preview: id = {}, retries = {}",
                 id, retries);
        try {
            Thread.sleep(WAIT_SLEEP_SECONDS * 1000);
        } catch (InterruptedException e) {
            log.debug("Interrupted");
        }
        waitForPreview(id, retries - 1);
    }

    /**
     * Generate ID suitable for use as suffix in dataset names to
     * ensure each test gets a unique dataset name
     */
    private String testId() {
        return UUID.randomUUID().toString();
    }

}

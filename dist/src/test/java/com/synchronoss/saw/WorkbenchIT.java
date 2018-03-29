package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class WorkbenchIT extends BaseIT {
    private static final String WORKBENCH_PROJECT = "workbench";
    private static final String WORKBENCH_PATH =
        "/services/internal/workbench/projects/" + WORKBENCH_PROJECT;
    private static final int WAIT_RETRIES = 20;
    private static final int WAIT_SLEEP_SECONDS = 5;
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * Parse a CSV file into dataset with given name using Workbench
     * Services.
     */
    private void parseDataset(String name) throws JsonProcessingException {
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
        given(authSpec)
            .body(root)
            .when().post(WORKBENCH_PATH + "/datasets")
            .then().assertThat().statusCode(200);
    }

    /**
     * Wait until dataset becomes visible in Workbench Services, using
     * the given number of retries before timing out.
     */
    private void waitForDataset(String id, int retries)
        throws JsonProcessingException {
        String status = getDatasetStatus(id);
        if (status == null || status.equals("INIT")) {
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
        } else if (!status.equals("SUCCESS")) {
            throw new RuntimeException(
                "Unknown dataset status: " + status);
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
        return response.path(statusPath);
    }

    @Test
    public void testParseDataset() throws JsonProcessingException {
        String name = "test-parse-" + testId();
        parseDataset(name);
        /* Workaround: Until the dataset creation API provides the
         * dataset ID, construct it manually here. */
        String id = "workbench::" + name;
        waitForDataset(id, WAIT_RETRIES);
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
    public void testPreviewDataset() throws JsonProcessingException {
        String name = "test-preview-" + testId();
        /* Create dataset to be used for testing viewing dataset */
        parseDataset(name);
        /* Workaround: Until the dataset creation API provides the
         * dataset ID, construct it manually here. */
        String id = "workbench::" + name;
        waitForDataset(id, WAIT_RETRIES);
        /* View dataset */
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
            .body("rows[0][0]", equalTo("foo"));
    }

    /**
     * Wait until preview becomes visible in Workbench Services, using
     * the given number of retries before timing out.
     */
    private void waitForPreview(String id, int retries)
        throws JsonProcessingException {
        Response response = given(authSpec)
            .when().get(WORKBENCH_PATH + "/previews/" + id);
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return;
        } else if (statusCode != 404) {
            throw new RuntimeException(
                "Unhandled status code: " + statusCode);
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

package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

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
    private static final int DATASET_WAIT_RETRIES = 10;
    private static final int DATASET_WAIT_SLEEP_SECONDS = 5;
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
        String json = mapper.writeValueAsString(root);
        given(authSpec)
            .body(json)
            .when().post(WORKBENCH_PATH + "/datasets")
            .then().assertThat().statusCode(200);
    }

    /**
     * Wait until dataset becomes visible in Workbench Services.
     */
    private void waitForDataset(String id)
        throws JsonProcessingException {
        waitForDatasetRetry(id, DATASET_WAIT_RETRIES);
    }

    /**
     * Wait until dataset becomes visible in Workbench Services, using
     * the given number of retries before timing out.
     */
    private void waitForDatasetRetry(String id, int retries)
        throws JsonProcessingException {
        String status = getDatasetStatus(id);
        if (status == null || status.equals("INIT")) {
            if (retries == 0) {
                throw new RuntimeException(
                    "Timed out waiting while waiting for dataset");
            }
            log.info("Waiting for dataset: id = {}, retries = {}",
                     id, retries);
            try {
                Thread.sleep(DATASET_WAIT_SLEEP_SECONDS * 1000);
            } catch (InterruptedException e) {
                log.debug("Interrupted");
            }
            waitForDatasetRetry(id, retries - 1);
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
        String name = "test_parse";
        parseDataset(name);
        /* Workaround: Until the dataset creation API provides the
         * dataset ID, construct it manually here. */
        String id = "workbench::" + name;
        waitForDataset(id);
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
}

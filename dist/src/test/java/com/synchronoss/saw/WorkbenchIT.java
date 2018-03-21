package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

public class WorkbenchIT extends BaseIT {
    @Test
    public void testWorkbenchListDatasets() {
        /* Note: Placeholder for Workbench list datasets integration
         * test.  To be done: Create a dataset and then make
         * assertions on it when listing available datasets. */
        given(spec)
            .header("Authorization", "Bearer " + token)
            .when().get(
                "/services/internal/workbench/projects/workbench/datasets")
            .then().assertThat().statusCode(200)
            .body(containsString(""));
    }
}

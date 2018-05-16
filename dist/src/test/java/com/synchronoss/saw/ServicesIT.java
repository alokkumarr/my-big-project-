package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

/**
 * Services integration tests.  Tests health checks.
 */
public class ServicesIT extends BaseIT {
    private RequestSpecification spec;

    @Before
    public void setUp() {
        spec = new RequestSpecBuilder().build();
    }

    @Test
    public void testHealth() {
        given(spec).accept("application/json")
            .when().get("/services/actuator/health")
            .then().statusCode(200).body("status", equalTo("UP"));
    }
}

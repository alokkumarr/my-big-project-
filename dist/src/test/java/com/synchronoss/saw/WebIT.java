package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

public class WebIT extends BaseIT {
    private RequestSpecification spec;

    @Before
    public void setUp() {
        this.spec = new RequestSpecBuilder().build();
    }

    @Test
    public void testWebRoot() {
        given(spec).accept("text/html")
            .when().get("/web/")
            .then().statusCode(200).body(
                "html.head.script", containsString("document.write"));
    }
}

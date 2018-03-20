package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WebIT {
    private RequestSpecification spec;

    @BeforeClass
    public static void setUpClass() {
        String port = System.getProperty("saw.docker.port");
        if (port == null) {
            throw new RuntimeException("Property saw.docker.port unset");
        }
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

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

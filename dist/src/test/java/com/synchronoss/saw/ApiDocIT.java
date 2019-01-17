package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

/**
 * Generate static REST API definitions for use in SIP Developer
 * Guide.
 */
public class ApiDocIT extends BaseIT {
  private RequestSpecification spec;

  @Before
  public void setUp() {
    this.spec = new RequestSpecBuilder().build();
  }

  @Test
  public void testApiDoc() throws IOException {
    /* Request REST API documentation for services, for use in later
     * stages of build while rendering SIP Developer Guide
     * documentation */
    writeApiDoc("observe", "/services/observe");
    writeApiDoc("scheduler", "/services/scheduler");
    writeApiDoc("security", "/security");
    writeApiDoc("semantic", "/services/internal/semantic");
    writeApiDoc("proxy", "/services/internal/proxy");
    writeApiDoc("workbench", "/services/internal/workbench");
  }

  /**
   * Write the REST API definition of the given service to a JSON
   * file.
   */
  private void writeApiDoc(String name, String pathPrefix)
      throws IOException {
    Response response = given(spec).accept("application/json")
        .when().get(pathPrefix + "/v2/api-docs")
        .then().statusCode(200)
        .extract().response();
    String spec = response.getBody().asString();
    String outputDir = "target/apidoc-input";
    Files.createDirectories(Paths.get(outputDir));
    try (BufferedWriter writer = Files.newBufferedWriter(
        Paths.get(outputDir, "apidoc-" + name + ".json"),
        StandardCharsets.UTF_8)) {
      writer.write(spec);
    }
  }
}

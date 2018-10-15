package com.synchronoss.saw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

public class BaseIT {
  @Rule
  public final JUnitRestDocumentation restDocumentation =
      new JUnitRestDocumentation();

  protected RequestSpecification spec;
  protected RequestSpecification authSpec;
  protected ObjectMapper mapper;
  protected String token;

  @BeforeClass
  public static void setUpClass() {
    String host = System.getProperty("saw.docker.host");
    String port = System.getProperty("saw.docker.port");
    if (host == null) {
      throw new RuntimeException("Property saw.docker.host unset");
    }
    if (port == null) {
      throw new RuntimeException("Property saw.docker.port unset");
    }
    RestAssured.baseURI = "http://" + host + ":" + port + "/saw";
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }
    

  @Before
  public void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    spec = new RequestSpecBuilder()
           .addFilter(documentationConfiguration(restDocumentation))
           .build();
    token = authenticate();
    authSpec = new RequestSpecBuilder()
               .addFilter(documentationConfiguration(restDocumentation))
               .build().header("Authorization", "Bearer " + token);
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
                                    preprocessReplace(
                                      TEST_USERNAME, "user@example.com"),
                                    preprocessReplace(
                                      TEST_PASSWORD, "password123"))))
                        .when().post("/security/doAuthenticate")
                        .then().assertThat().statusCode(200)
                        .body("aToken", startsWith(""))
                        .extract().response();
    return response.path("aToken");
  }

  private OperationPreprocessor preprocessReplace(String from, String to) {
    return replacePattern(Pattern.compile(Pattern.quote(from)), to);
  }
}

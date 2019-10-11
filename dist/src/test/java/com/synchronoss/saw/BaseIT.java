package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.UUID;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;

public class BaseIT {
  private static final String TEST_USERNAME = "sawadmin@synchronoss.com";
  private static final String TEST_PASSWORD = "Sawsyncnewuser1!";
  @Rule public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
  protected final Logger log = LoggerFactory.getLogger(getClass().getName());

  @Rule
  public TestWatcher watcher =
      new TestWatcher() {
        @Override
        public void starting(final Description method) {
          log.debug("Test: {}", method.getMethodName());
        }
      };

  protected RequestSpecification spec;
  protected RequestSpecification authSpec;
  protected ObjectMapper mapper;
  protected String token;

  @BeforeClass
  public static void setUpClass() {
    String host = System.getProperty("saw.docker.host");
    String port;
    String secure = System.getProperty("sip.cloud.secure");
    if (secure.equalsIgnoreCase("True")) {
      port = System.getProperty("sip.docker.secure.port");
      RestAssured.useRelaxedHTTPSValidation();
      RestAssured.baseURI = "https://" + host + ":" + port + "/";
    } else {
      port = System.getProperty("saw.docker.port");
      RestAssured.baseURI = "http://" + host + ":" + port + "/";
    }

    if (host == null) {
      throw new RuntimeException("Property saw.docker.host unset");
    }
    if (port == null) {
      throw new RuntimeException("Property saw.docker.port unset");
    }
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @Before
  public void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    spec =
        new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation)).build();
    token = authenticate();
    authSpec =
        new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build()
            .header("Authorization", "Bearer " + token);
  }

  private String authenticate() throws JsonProcessingException {
    ObjectNode node = mapper.createObjectNode();
    node.put("masterLoginId", TEST_USERNAME);
    node.put("password", TEST_PASSWORD);
    String json = mapper.writeValueAsString(node);
    Response response =
        given(spec)
            .accept("application/json")
            .header("Content-Type", "application/json")
            .body(json)
            .filter(
                document(
                    "authenticate",
                    preprocessRequest(
                        preprocessReplace(TEST_USERNAME, "user@example.com"),
                        preprocessReplace(TEST_PASSWORD, "password123"))))
            .when()
            .post("/security/doAuthenticate")
            .then()
            .assertThat()
            .statusCode(200)
            .body("aToken", startsWith(""))
            .extract()
            .response();
    return response.path("aToken");
  }

  protected String authenticate(String userName, String password) throws JsonProcessingException {
    ObjectNode node = mapper.createObjectNode();
    node.put("masterLoginId", userName);
    node.put("password", password);
    String json = mapper.writeValueAsString(node);
    Response response =
        given(spec)
            .accept("application/json")
            .header("Content-Type", "application/json")
            .body(json)
            .filter(
                document(
                    "authenticate",
                    preprocessRequest(
                        preprocessReplace(userName, "user@example.com"),
                        preprocessReplace(password, "password123"))))
            .when()
            .post("/security/doAuthenticate")
            .then()
            .assertThat()
            .statusCode(200)
            .body("aToken", startsWith(""))
            .extract()
            .response();
    return response.path("aToken");
  }

  private OperationPreprocessor preprocessReplace(String from, String to) {
    return replacePattern(Pattern.compile(Pattern.quote(from)), to);
  }

  /**
   * Generate ID suitable for use as suffix in dataset names to ensure each test gets a unique
   * dataset name.
   */
  protected String testId() {
    return UUID.randomUUID().toString();
  }
}

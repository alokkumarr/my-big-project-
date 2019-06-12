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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
  @Rule
  public TestWatcher watcher =
      new TestWatcher() {
        @Override
        public void starting(final Description method) {
          log.debug("Test: {}", method.getMethodName());
        }
      };

  @Rule public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  protected final Logger log = LoggerFactory.getLogger(getClass().getName());

  protected RequestSpecification spec;
  protected RequestSpecification authSpec;
  protected ObjectMapper mapper;
  protected String token;
  protected JsonObject testData;
  protected JsonObject sipQuery = null;

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
    RestAssured.baseURI = "http://" + host + ":" + port + "/";
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  /** Set up data for All DSL queries. */
  public void setUpData() {
    testData = new JsonObject();
    testData.addProperty("type", "chart");
    testData.addProperty("type", "chart");
    testData.addProperty("semanticId", "d23c6142-2c10-459e-b1f6-29edd1b2ccfe");
    testData.addProperty("id", "f37cde24-b833-46ba-ae2d-42e286c3fc39");
    testData.addProperty("customerCode", "SYNCHRONOSS");
    testData.addProperty("projectCode", "sip-sample");
    testData.addProperty("module", "productSpecific/ANALYZE");
    testData.addProperty("createdTime", 1543921879);
    testData.addProperty("createdBy", "sipadmin@synchronoss.com");
    testData.addProperty("modifiedTime", 1543921879);
    testData.addProperty("modifiedBy", "sipadmin@synchronoss.com");
    testData.addProperty("category","5");
    testData.addProperty("userId",1);

    sipQuery = new JsonObject();

    JsonObject artifact1 = new JsonObject();
    artifact1.addProperty("artifactName", "sample");

    JsonObject field1 = new JsonObject();
    field1.addProperty("dataField", "string");
    field1.addProperty("area", "x-axis");
    field1.addProperty("alias", "String");
    field1.addProperty("columnName", "string.keyword");
    field1.addProperty("displayName", "String");
    field1.addProperty("type", "string");
    JsonArray artifactFields = new JsonArray();
    artifactFields.add(field1);

    JsonObject field2 = new JsonObject();
    field2.addProperty("dataField", "integer");
    field2.addProperty("area", "y-axis");
    field2.addProperty("columnName", "integer");
    field2.addProperty("displayName", "integer");
    field2.addProperty("type", "integer");
    field2.addProperty("aggregate", "avg");
    artifactFields.add(field2);

    JsonObject field3 = new JsonObject();
    field3.addProperty("dataField", "long");
    field3.addProperty("area", "z-axis");
    field3.addProperty("columnName", "long");
    field3.addProperty("displayName", "Long");
    field3.addProperty("type", "long");
    field3.addProperty("aggregate", "avg");
    artifactFields.add(field3);

    JsonObject field4 = new JsonObject();
    field4.addProperty("dataField", "date");
    field4.addProperty("area", "g-axis");
    field4.addProperty("columnName", "date");
    field4.addProperty("displayName", "Date");
    field4.addProperty("type", "date");
    field4.addProperty("groupInterval", "year");
    artifactFields.add(field4);

    JsonObject field5 = new JsonObject();
    field5.addProperty("dataField", "double");
    field5.addProperty("area", "m-axis");
    field5.addProperty("columnName", "double");
    field5.addProperty("displayName", "Double");
    field5.addProperty("type", "double");
    field5.addProperty("aggregate", "avg");
    artifactFields.add(field5);

    JsonObject field6 = new JsonObject();
    field6.addProperty("dataField", "float");
    field6.addProperty("area", "m-axis");
    field6.addProperty("columnName", "float");
    field6.addProperty("displayName", "Float");
    field6.addProperty("type", "double");
    field6.addProperty("aggregate", "avg");
    artifactFields.add(field6);

    artifact1.add("fields", artifactFields);
    JsonArray artifacts = new JsonArray();
    artifacts.add(artifact1);
    sipQuery.add("artifacts", artifacts);
    sipQuery.addProperty("booleanCriteria", "AND");

    JsonObject filter1 = new JsonObject();
    filter1.addProperty("type", "long");
    filter1.addProperty("artifactsName", "sample");
    filter1.addProperty("isOptional", false);
    filter1.addProperty("columnName", "long");
    filter1.addProperty("isRuntimeFilter", false);
    filter1.addProperty("isGlobalFilter", false);
    JsonObject model = new JsonObject();
    model.addProperty("operator", "EQ");
    model.addProperty("value", 1000);
    filter1.add("model", model);
    JsonArray filters = new JsonArray();
    filters.add(filter1);

    JsonObject filter2 = new JsonObject();
    filter2.addProperty("type", "date");
    filter2.addProperty("tableName", "sample");
    filter2.addProperty("isOptional", false);
    filter2.addProperty("columnName", "date");
    filter2.addProperty("isRuntimeFilter", false);
    filter2.addProperty("isGlobalFilter", false);
    model = new JsonObject();
    model.addProperty("format", "epoch_second");
    model.addProperty("operator", "NEQ");
    model.addProperty("value", 1483228800000L);
    model.addProperty("otherValue", 1483228800000L);
    filter2.add("model", model);
    filters.add(filter2);

    JsonObject filter3 = new JsonObject();
    filter3.addProperty("type", "date");
    filter3.addProperty("tableName", "sample");
    filter3.addProperty("isOptional", false);
    filter3.addProperty("columnName", "date");
    filter3.addProperty("isRuntimeFilter", false);
    filter3.addProperty("isGlobalFilter", false);
    model = new JsonObject();
    model.addProperty("format", "epoch_millis");
    model.addProperty("operator", "BTW");
    model.addProperty("value", 1451606400000L);
    model.addProperty("otherValue", 1551606400000L);
    filter3.add("model", model);
    filters.add(filter3);

    JsonObject filter4 = new JsonObject();
    filter4.addProperty("type", "date");
    filter4.addProperty("tableName", "sample");
    filter4.addProperty("isOptional", false);
    filter4.addProperty("columnName", "date");
    filter4.addProperty("isRuntimeFilter", false);
    filter4.addProperty("isGlobalFilter", false);
    model = new JsonObject();
    model.addProperty("format", "YYYY");
    model.addProperty("operator", "GTE");
    model.addProperty("value", 2017);
    filter4.add("model", model);
    filters.add(filter4);

    sipQuery.add("filters", filters);

    JsonObject sort1 = new JsonObject();
    sort1.addProperty("artifacts", "sample");
    sort1.addProperty("aggregate", "sum");
    sort1.addProperty("columnName", "long");
    sort1.addProperty("type", "long");
    sort1.addProperty("order", "asc");
    JsonArray sorts = new JsonArray();
    sorts.add(sort1);

    sipQuery.add("sorts", sorts);

    JsonObject store = new JsonObject();
    store.addProperty("dataStore", "sampleAlias/sample");
    store.addProperty("storageType", "ES");

    sipQuery.add("store", store);
    testData.add("sipQuery", sipQuery);
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
    setUpData();
  }

  private static final String TEST_USERNAME = "sawadmin@synchronoss.com";
  private static final String TEST_PASSWORD = "Sawsyncnewuser1!";

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

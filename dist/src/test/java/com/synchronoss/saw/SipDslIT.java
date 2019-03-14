package com.synchronoss.saw;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class SipDslIT extends BaseIT {
  @Ignore("Placeholder integration test, dependency on SIP-5433")
  @Test
  public void testSipDsl() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("sample-DSL.json").getPath());
    int testData = 643; //To assert Filter on es-data.ndjson
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(file);
    Response response = given(spec)
        .header("Authorization", "Bearer " + token)
        .body(jsonNode)
        .when().post("/saw/services/internal/proxy/storage/fetch")
        .then().assertThat().statusCode(200)
        .extract().response();
    ObjectNode root = response.as(ObjectNode.class);
    JsonNode node = root.get("integer");
    Assert.assertEquals(node.get("integer"),testData);
  }

}

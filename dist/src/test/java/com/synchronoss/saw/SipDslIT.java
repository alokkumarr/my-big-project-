package com.synchronoss.saw;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class SipDslIT extends BaseIT {

  @Test
  public void testSipDsl() throws IOException {
    int testFilterData = 100; // To assert Filter condition.
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(testData.toString());
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(jsonNode)
            .when()
            .post(
                "/saw/services/internal/proxy/storage/fetch?id=f37cde24-b833-46ba-ae2d-42e286c3fc39&ExecutionType=preview")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    ArrayNode root = response.getBody().as(ArrayNode.class);
    Assert.assertEquals(root.get(0).get("integer").asInt(), testFilterData);
  }

  @Test
  public void testSipDslExecute() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(sipQuery.toString());
    System.out.println("json Node : " + sipQuery.toString());
    String testStringFilter = "string 1";
    Response response =
        given(spec)
            .header("Authorization", "Bearer " + token)
            .body(jsonNode)
            .when()
            .post("/saw/services/internal/proxy/storage/execute")
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .response();
    Assert.assertNotNull(response);
    ArrayNode root = response.getBody().as(ArrayNode.class);
    Assert.assertEquals(root.get(0).get("string").asText(), testStringFilter);
  }
}

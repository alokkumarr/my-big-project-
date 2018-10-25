package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test that test the Security Service.
 */
public class SecurityIT extends BaseIT {
  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  @Test
  public void testAddCategory() throws JsonProcessingException {
    ObjectNode root = mapper.createObjectNode();
    root.put("moduleId", 1);
    root.put("categoryName", "Test");
    root.put("categoryDesc", "Test");
    root.put("productId", 1);
    root.put("customerId", 1);
    root.put("activeStatusInd", 1);
    root.put("masterLoginId", "test@example.com");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root)
      .when().post("/security/auth/admin/cust/manage/categories/add")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));
  }

  @Test
  public void testSecurityGroup() {
    ObjectNode secGroup = mapper.createObjectNode();
    secGroup.put("description", "TestDesc");
    secGroup.put("securityGroupName", "TestGroup");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(secGroup)
      .when().post("/security/auth/admin/security-groups")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    given(authSpec)
      .when().get("/auth/admin/security-groups")
      .then().assertThat().statusCode(200);
  }

  @Test
  public void testDskAttributeValues() {
    ObjectNode secGroup = mapper.createObjectNode();
    secGroup.put("description", "TestDesc1");
    secGroup.put("securityGroupName", "TestGroup1");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(secGroup)
      .when().post("/security/auth/admin/security-groups")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    Response response = given(authSpec)
        .when().get("/auth/admin/security-groups")
        .then().statusCode(200).extract().response();
    ArrayNode node = response.as(ArrayNode.class);
    JsonNode jsonNode = node.get(0);
    Long groupSysId = jsonNode.get("secGroupSysId").asLong();

    ObjectNode root = mapper.createObjectNode();
    root.put("attributeName", "TestAttr1");
    root.put("value", "TestValue1");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(root)
      .when().post("/auth/admin/security-groups/" + groupSysId + "/dsk-attribute-values")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    given(authSpec)
      .when().get("/auth/admin/security-groups/" + groupSysId + "/dsk-attribute-values")
      .then().assertThat().statusCode(200);
  }

  @Test
  public void testAssignGroupUser() {
    ObjectNode secGroup = mapper.createObjectNode();
    secGroup.put("description", "TestDesc2");
    secGroup.put("securityGroupName", "TestGroup2");
    given(authSpec)
      .contentType(ContentType.JSON)
      .body(secGroup)
      .when().post("/security/auth/admin/security-groups")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));

    Response response = given(authSpec)
        .when().get("/auth/admin/user-assignments")
        .then().statusCode(200).extract().response();
    ArrayNode node = response.as(ArrayNode.class);
    JsonNode jsonNode = node.get(0);
    Long userSysId = jsonNode.get("userSysId").asLong();

    given(authSpec)
      .when()
      .put("/security/auth/admin/users/" + userSysId 
      + "/security-group?securityGroupName=TestGroup2")
      .then().assertThat().statusCode(200)
      .body("valid", equalTo(true));
  }

}

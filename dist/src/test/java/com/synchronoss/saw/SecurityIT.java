package com.synchronoss.saw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.core.JsonProcessingException;
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
}

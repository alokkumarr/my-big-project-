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

    @Test
    public void testSecurityGroup() {
        ObjectNode secGroup = mapper.createObjectNode();
        secGroup.put("description", "TestDesc");
        secGroup.put("securityGroupName", "TestGroup");
        given(authSpec)
            .contentType(ContentType.JSON)
            .body(secGroup)
            .when().post("/security/auth/admin/security-group")
            .then().assertThat().statusCode(200)
            .body("valid", equalTo(true));

        given(authSpec)
            .when().get("/auth/admin/security-groups")
            .then().assertThat().statusCode(200);

    }

    @Test
    public void testDskAttributeValues() {
        ObjectNode root = mapper.createObjectNode();
        root.put("attributeName","TestAttr");
        root.put("securityGroupName","TestGroup");
        root.put("value","TestValue");
        given(authSpec)
            .contentType(ContentType.JSON)
            .body(root)
            .when().post("/security/auth/admin/dsk-attribute-value")
            .then().assertThat().statusCode(200)
            .body("valid", equalTo(true));

        given(authSpec)
            .when().get("/auth/admin/dsk-attribute-values/TestGroup")
            .then().assertThat().statusCode(200);

    }

    @Test
    public void testAssignGroupUser() {
        ObjectNode secGroup = mapper.createObjectNode();
        secGroup.put("description", "TestDesc");
        secGroup.put("securityGroupName", "TestGroup1");
        given(authSpec)
            .contentType(ContentType.JSON)
            .body(secGroup)
            .when().post("/security/auth/admin/security-group")
            .then().assertThat().statusCode(200)
            .body("valid", equalTo(true));

        ObjectNode root = mapper.createObjectNode();
        root.put("groupName","TestGroup1");
        root.put("userId","sawadmin@synchronoss.com");
        given(authSpec)
            .contentType(ContentType.JSON)
            .body(root)
            .when().post("/security/auth/admin/assign-group-user")
            .then().assertThat().statusCode(200)
            .body("valid", equalTo(true));
    }


}

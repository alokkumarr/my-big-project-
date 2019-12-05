package com.synchronoss.saw;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;

/**
 * Integration test for external security CRUD APIs.
 *
 * @author alok.kumarr
 * @since 3.5.0
 */
public class ExternalSecurityIT extends BaseIT {

  @Test
  public void createRolePrivilegesCategory() {

    Response response = given(authSpec)
        .contentType(ContentType.JSON)
        .body(roleCategoryPrivilege())
        .when()
        .post("/security/external/createRoleCategoryPrivilege")
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();

    Assert.assertNotNull(response);
    ObjectNode apiResponse = response.getBody().as(ObjectNode.class);
    String moduleName = apiResponse.get("moduleName").asText();
    Assert.assertEquals("OBSERVE", moduleName);
    String productName = apiResponse.get("productName").asText();
    Assert.assertEquals("SAW Demo", productName);
  }

  @Test
  public void fetchRolePrivilegesCategory() {

    Response response = given(authSpec)
        .contentType(ContentType.JSON)
        .body(roleCategoryPrivilege())
        .when()
        .get("/security/external/fetchRoleCategoryPrivilege")
        .then()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();

    Assert.assertNotNull(response);
    ObjectNode apiResponse = response.getBody().as(ObjectNode.class);
    String moduleName = apiResponse.get("moduleName").asText();
    Assert.assertEquals("OBSERVE", moduleName);
    String productName = apiResponse.get("productName").asText();
    Assert.assertEquals("SAW Demo", productName);
  }

  private ObjectNode roleCategoryPrivilege() {
    ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("moduleName", "OBSERVE");
    objectNode.put("productName", "SAW Demo");

    ObjectNode roleDetails = mapper.createObjectNode();
    roleDetails.put("activeStatusInd", false);
    roleDetails.put("autoCreate", true);
    roleDetails.put("customerCode", "SYNCHRONOSS");
    roleDetails.put("roleDesc", "Admin User");
    roleDetails.put("roleName", "Test Role Name");
    roleDetails.put("roleType", "ADMIN");
    objectNode.set("role", roleDetails);

    ArrayNode category = objectNode.putArray("category");
    ObjectNode categoryDetail = mapper.createObjectNode();
    categoryDetail.put("autoCreate", true);
    categoryDetail.put("categoryDesc", "Category Description");
    categoryDetail.put("categoryName", "New Category 01");
    categoryDetail.put("categoryType", "000121");

    ArrayNode subCategory = categoryDetail.putArray("subCategory");
    ObjectNode subCategoryDetail = mapper.createObjectNode();
    subCategoryDetail.put("autoCreate", true);

    ArrayNode privileges = subCategoryDetail.putArray("subCategory");
    privileges.add("CREATE");
    privileges.add("PUBLISH");
    privileges.add("DELETE");

    subCategoryDetail.put("subCategoryDesc", "Sub Category Description");
    subCategoryDetail.put("subCategoryName", "New Sub Category 01");

    subCategory.add(subCategoryDetail);
    category.add(categoryDetail);
    return objectNode;
  }
}

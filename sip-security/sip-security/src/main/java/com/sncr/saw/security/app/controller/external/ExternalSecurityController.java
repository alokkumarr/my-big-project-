package com.sncr.saw.security.app.controller.external;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.ProductModuleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.app.service.ExternalSecurityService;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.external.response.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.utils.PrivilegeUtils;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
@Api(value = "The controller provides to perform external admin security operation in synchronoss insight platform ")
@RestController
@RequestMapping("/sip-security/external")
public class ExternalSecurityController {

  @Autowired
  private ExternalSecurityService securityService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProductModuleRepository productModuleRepository;

  @ApiOperation(value = "Create all the Role-Category-Privileges list", nickname = "createRoleCategoryPrivilege", notes = "",
      response = RoleCatPrivilegeResponse.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System administrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. Representation not supported for the resource")})
  @RequestMapping(value = "/createRoleCategoryPrivilege", method = RequestMethod.POST)
  public RoleCatPrivilegeResponse createRoleCategoryPrivilege(HttpServletRequest httpRequest,
                                                              HttpServletResponse httpResponse,
                                                              @RequestBody RoleCategoryPrivilege request) {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    if (request == null) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setValid(false);
      response.setMessage("Request body can't be blank or empty.");
      return response;
    }

    Ticket ticket = SipCommonUtils.getTicket(httpRequest);
    RoleType roleType = ticket.getRoleType();
    String masterLoginId = ticket.getMasterLoginId();
    if ((masterLoginId != null && !userRepository.validateUser(masterLoginId)) || !RoleType.ADMIN.equals(roleType)) {
      httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setValid(false);
      response.setMessage("You are not authorized to perform this operation.");
      return response;
    }

    Role role = request.getRole();
    if (role.getCustomerCode() == null || role.getCustomerCode().isEmpty()) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Customer Code can't be blank or empty.");
      response.setValid(false);
      return response;
    }

    if (role.getRoleType() == null || role.getRoleType().isEmpty()) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Role Type can't be blank or empty.");
      response.setValid(false);
      return response;
    }

    if (!RoleType.validRoleType(role.getRoleType())) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Only ADMIN|USER Role Type are allowed.");
      response.setValid(false);
      return response;
    }

    if (role.getRoleName() == null || role.getRoleName().isEmpty()) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Role Name can't be blank or empty.");
      response.setValid(false);
      return response;
    }

    ProductModuleDetails moduleDetails = productModuleRepository.fetchModuleProductDetail(masterLoginId,
        request.getProductName(),
        request.getModuleName());
    final Long customerSysId = moduleDetails != null ? moduleDetails.getCustomerSysId() : null;
    if (customerSysId == null || customerSysId == 0) {
      httpResponse.setStatus(HttpStatus.OK.value());
      response.setValid(false);
      response.setMessage("Product and Module does not exist for this user.");
      return response;
    }

    // validate role/category/subcategory name
    List<CategoryDetails> categoryList = request.getCategories();
    List<SubCategoryDetails> subCategoryList = null;
    boolean validateRoleName = role != null && role.isAutoCreate() && role.getRoleName() != null && securityService.validateName(role.getRoleName().trim());
    if (validateRoleName) {
      httpResponse.setStatus(HttpStatus.OK.value());
      response.setValid(false);
      response.setMessage("Special symbol not allowed except underscore(_) and hyphen(-) for role name.");
      return response;
    } else if (categoryList != null && !categoryList.isEmpty()) {
      boolean emptyCategoryName = categoryList.stream().anyMatch(category -> category.getCategoryName() == null || category.getCategoryName().isEmpty());
      if (emptyCategoryName) {
        httpResponse.setStatus(HttpStatus.OK.value());
        response.setValid(false);
        response.setMessage("Category name can't be blank or empty.");
        return response;
      }

      boolean invalidCategoryName = categoryList.stream().anyMatch(category -> category.isAutoCreate() && securityService.validateName(category.getCategoryName().trim()));
      if (invalidCategoryName) {
        httpResponse.setStatus(HttpStatus.OK.value());
        response.setValid(false);
        response.setMessage("Special symbol not allowed except underscore(_) and hyphen(-) for category name.");
        return response;
      } else {
        boolean[] invalidSubCatName = {false};
        for (CategoryDetails categoryDetails : categoryList) {
          subCategoryList = categoryDetails.getSubCategories();
          boolean emptySubCategoryName = subCategoryList.stream().anyMatch(category -> category.getSubCategoryName() == null || category.getSubCategoryName().isEmpty());
          if (emptySubCategoryName) {
            httpResponse.setStatus(HttpStatus.OK.value());
            response.setValid(false);
            response.setMessage("Sub Category name can't be blank or empty.");
            return response;
          }
          invalidSubCatName[0] = subCategoryList.stream().anyMatch(subCategory -> subCategory.isAutoCreate() && securityService.validateName(subCategory.getSubCategoryName().trim()));
        }
        if (invalidSubCatName[0]) {
          httpResponse.setStatus(HttpStatus.OK.value());
          response.setValid(false);
          response.setMessage("Special symbol not allowed except underscore(_) and hyphen(-) for sub category name.");
          return response;
        }
      }
    }

    // validate privileges names
    if (subCategoryList != null && !subCategoryList.isEmpty() && subCategoryList.size() > 0) {
      boolean[] validPrivileges = {false};
      subCategoryList.stream().forEach(subCat -> {
        validPrivileges[0] = securityService.validPrivileges(subCat.getPrivilege(), request.getModuleName());
      });

      if (!validPrivileges[0]) {
        httpResponse.setStatus(HttpStatus.OK.value());
        response.setValid(false);
        response.setMessage("Please provide the valid module privileges for category/subcategory.");
        return response;
      }
    }

    if (!role.getCustomerCode().equalsIgnoreCase(moduleDetails.getCustomerCode())) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Customer Code not matched with the user ticket.");
      response.setValid(false);
      return response;
    }

    response = securityService.createRoleCategoryPrivilege(httpResponse, request, masterLoginId, moduleDetails);
    return response;
  }


  @ApiOperation(value = "Fetch all the Role/Category/Privileges details", nickname = "createRoleCategoryPrivilege", notes = "",
      response = RoleCatPrivilegeResponse.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System administrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. Representation not supported for the resource")})
  @RequestMapping(value = "/fetchRoleCategoryPrivilege", method = RequestMethod.GET)
  public RoleCatPrivilegeResponse fetchRoleCategoryPrivilege(HttpServletRequest httpRequest,
                                                             HttpServletResponse httpResponse,
                                                             @RequestBody RoleCategoryPrivilege request) {

    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    if (request == null) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setValid(false);
      response.setMessage("Request body can't be blank or empty.");
      return response;
    }

    Ticket ticket = SipCommonUtils.getTicket(httpRequest);
    RoleType roleType = ticket.getRoleType();
    String masterLoginId = ticket.getMasterLoginId();
    if ((masterLoginId != null && !userRepository.validateUser(masterLoginId)) || !RoleType.ADMIN.equals(roleType)) {
      httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setValid(false);
      response.setMessage("You are not authorized to perform this operation.");
      return response;
    }

    String productName = request.getProductName();
    String moduleName = request.getModuleName();
    ProductModuleDetails moduleDetails = productModuleRepository.fetchModuleProductDetail(masterLoginId, productName, moduleName);
    final Long customerSysId = moduleDetails != null ? moduleDetails.getCustomerSysId() : null;
    if (customerSysId == null || customerSysId == 0) {
      httpResponse.setStatus(HttpStatus.OK.value());
      response.setValid(false);
      response.setMessage("Product and Module does not exist for this user.");
      response.setProductName(productName);
      response.setModuleName(moduleName);
      return response;
    }

    Role role = request.getRole();
    if (role == null || role.getRoleName() == null || role.getRoleName().isEmpty()) {
      httpResponse.setStatus(HttpStatus.OK.value());
      response.setMessage("Role Name can't be blank or empty.");
      response.setProductName(productName);
      response.setModuleName(moduleName);
      return response;
    }

    response = securityService.fetchRoleCategoryPrivilege(request, productName, moduleName, moduleDetails, customerSysId);
    return response;
  }
}

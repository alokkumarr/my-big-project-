package com.sncr.saw.security.app.controller.external;

import com.sncr.saw.security.app.controller.ServerResponseMessages;
import com.sncr.saw.security.app.repository.DataSecurityKeyRepository;
import com.sncr.saw.security.app.repository.ProductModuleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.app.service.ExternalSecurityService;
import com.sncr.saw.security.app.sso.SSORequestHandler;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.UserDetails;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.external.response.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.sncr.saw.security.common.bean.repo.admin.UserDetailsResponse;
import com.sncr.saw.security.common.bean.repo.admin.UsersDetailsList;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.dsk.DskValidity;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.synchronoss.bda.sip.dsk.DskGroupPayload;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.bda.sip.dsk.SipDskAttributeModel;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
@Api(
    value =
        "The controller provides to perform external admin security operation in synchronoss insight platform ")
@RestController
@RequestMapping("/sip-security/auth/admin/v1")
public class ExternalSecurityController {

  @Autowired private ExternalSecurityService securityService;
  @Autowired private UserRepository userRepository;
  @Autowired private ProductModuleRepository productModuleRepository;
  @Autowired DataSecurityKeyRepository dataSecurityKeyRepository;
  @Autowired SSORequestHandler ssoRequestHandler;

  private static final Logger logger = LoggerFactory.getLogger(ExternalSecurityController.class);

  @ApiOperation(
      value = "Create all the Role-Category-Privileges list",
      nickname = "createRoleCategoryPrivilege",
      notes = "",
      response = RoleCatPrivilegeResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Request has been succeeded without any error"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(
            code = 415,
            message = "Unsupported Type. Representation not supported for the resource")
      })
  @PostMapping(value = "/roleCategoryPrivilege")
  public RoleCatPrivilegeResponse createRoleCategoryPrivilege(
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse,
      @RequestBody RoleCategoryPrivilege request) {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    if (request == null) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setValid(false);
      response.setMessage("Request body can't be blank or empty.");
      return response;
    }

    if ("ALERTS".equalsIgnoreCase(request.getModuleName())
        || "WORKBENCH".equalsIgnoreCase(request.getModuleName())) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setValid(false);
      response.setMessage("ALERTS and WORKBENCH module are not allowed.");
      return response;
    }

    Ticket ticket = SipCommonUtils.getTicket(httpRequest);
    RoleType roleType = ticket.getRoleType();
    String masterLoginId = ticket.getMasterLoginId();
    if ((masterLoginId != null && !userRepository.validateUser(masterLoginId))
        || !RoleType.ADMIN.equals(roleType)) {
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

    if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Role Name can't be blank or empty.");
      response.setValid(false);
      return response;
    }

    ProductModuleDetails moduleDetails =
        productModuleRepository.fetchModuleProductDetail(
            masterLoginId, request.getProductName(), request.getModuleName());
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
    boolean validateRoleName =
        role != null
            && role.isAutoCreate()
            && role.getRoleName() != null
            && securityService.validateName(role.getRoleName().trim());
    if (validateRoleName || role.getRoleName().trim().contains(" ")) {
      httpResponse.setStatus(HttpStatus.OK.value());
      response.setValid(false);
      response.setMessage(
          "Special symbol and numeric not allowed except underscore(_) and hyphen(-) for role name.");
      return response;
    } else if (categoryList != null && !categoryList.isEmpty()) {
      boolean emptyCategoryName =
          categoryList.stream()
              .anyMatch(
                  category ->
                      category.getCategoryName() == null
                          || category.getCategoryName().trim().isEmpty());
      if (emptyCategoryName) {
        httpResponse.setStatus(HttpStatus.OK.value());
        response.setValid(false);
        response.setMessage("Category name can't be blank or empty.");
        return response;
      }

      boolean invalidCategoryName =
          categoryList.stream()
              .anyMatch(
                  category ->
                      category.isAutoCreate()
                          && securityService.validateName(category.getCategoryName().trim()));
      if (invalidCategoryName) {
        httpResponse.setStatus(HttpStatus.OK.value());
        response.setValid(false);
        response.setMessage(
            "Special symbol not allowed except underscore(_) and hyphen(-) for category name.");
        return response;
      } else {
        boolean[] invalidSubCatName = {false};
        for (CategoryDetails categoryDetails : categoryList) {
          subCategoryList =
              categoryDetails.isAutoCreate() && categoryDetails.getSubCategories() != null
                  ? categoryDetails.getSubCategories()
                  : null;
          if (subCategoryList != null && !subCategoryList.isEmpty()) {
            boolean emptySubCategoryName =
                subCategoryList.stream()
                    .anyMatch(
                        category ->
                            category.getSubCategoryName() == null
                                || category.getSubCategoryName().trim().isEmpty());
            if (emptySubCategoryName) {
              httpResponse.setStatus(HttpStatus.OK.value());
              response.setValid(false);
              response.setMessage("Sub Category name can't be blank or empty.");
              return response;
            }
            invalidSubCatName[0] =
                subCategoryList.stream()
                    .anyMatch(
                        subCategory ->
                            subCategory.isAutoCreate()
                                && securityService.validateName(
                                    subCategory.getSubCategoryName().trim()));
          }
        }
        if (invalidSubCatName[0]) {
          httpResponse.setStatus(HttpStatus.OK.value());
          response.setValid(false);
          response.setMessage(
              "Special symbol not allowed except underscore(_) and hyphen(-) for sub category name.");
          return response;
        }
      }
    }

    // validate privileges names
    if (subCategoryList != null && !subCategoryList.isEmpty() && subCategoryList.size() > 0) {
      for (SubCategoryDetails details : subCategoryList) {
        if (request.getModuleName() != null
            && !request.getModuleName().isEmpty()
            && details.isAutoCreate()
            && !securityService.validPrivileges(details.getPrivilege(), request.getModuleName())) {
          httpResponse.setStatus(HttpStatus.OK.value());
          response.setValid(false);
          response.setMessage(
              "Please provide the valid module privileges for category/subcategory.");
          return response;
        }
      }
    }

    if (!role.getCustomerCode().equalsIgnoreCase(moduleDetails.getCustomerCode())) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Customer Code not matched with the user ticket.");
      response.setValid(false);
      return response;
    }

    response =
        securityService.createRoleCategoryPrivilege(
            httpResponse, request, masterLoginId, moduleDetails);
    return response;
  }

  @ApiOperation(
      value = "Fetch all the Role/Category/Privileges details",
      nickname = "createRoleCategoryPrivilege",
      notes = "",
      response = RoleCatPrivilegeResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Request has been succeeded without any error"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(
            code = 415,
            message = "Unsupported Type. Representation not supported for the resource")
      })
  @GetMapping(value = "/roleCategoryPrivilege")
  public RoleCatPrivilegeResponse fetchRoleCategoryPrivilege(
      HttpServletRequest httpRequest,
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
    if ((masterLoginId != null && !userRepository.validateUser(masterLoginId))
        || !RoleType.ADMIN.equals(roleType)) {
      httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setValid(false);
      response.setMessage("You are not authorized to perform this operation.");
      return response;
    }

    String productName = request.getProductName();
    String moduleName = request.getModuleName();
    if ("ALERTS".equalsIgnoreCase(moduleName) || "WORKBENCH".equalsIgnoreCase(moduleName)) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setValid(false);
      response.setMessage("ALERTS and WORKBENCH module are not allowed.");
      return response;
    }

    ProductModuleDetails moduleDetails =
        productModuleRepository.fetchModuleProductDetail(masterLoginId, productName, moduleName);
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

    response =
        securityService.fetchRoleCategoryPrivilege(
            request, productName, moduleName, moduleDetails, customerSysId);
    return response;
  }

  /**
   * Create user with dsk.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param userDetails UserDetails
   * @return Returns UserDetailsResponse
   */
  @ApiOperation(
      value = " create User API ",
      nickname = "CreateUserWithDsk",
      notes = "Admin can only use this API to create the user",
      response = UserDetailsResponse.class)
  @PostMapping(value = "/users")
  @ResponseBody
  public UserDetailsResponse createUser(
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "User details to store", required = true) @RequestBody
          UserDetails userDetails) throws IOException {
    Ticket ticket = SipCommonUtils.getTicket(request);
    RoleType roleType = ticket.getRoleType();
    String masterLoginId = ticket.getMasterLoginId();
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    if (roleType != RoleType.ADMIN) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      userDetailsResponse.setValid(false);
      logger.error("user is not admin");
      userDetailsResponse.setValidityMessage("You are not authorized to perform this operation");
      return userDetailsResponse;
    }
    Long customerId = Long.valueOf(ticket.getCustID());
    return securityService.addUserDetails(userDetails, masterLoginId, customerId, response);
  }

    /**
     * Update user with dsk.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param userDetails UserDetails
     * @return Returns UserDetailsResponse
     */
    @ApiOperation(
        value = " update User API ",
        nickname = "UpdateUserWithDsk",
        notes = "Admin can only use this API to update the user",
        response = UserDetailsResponse.class)
    @PutMapping(value = "/users/{id}")
    @ResponseBody
    public UserDetailsResponse updateUser(
        HttpServletRequest request,
        HttpServletResponse response,
        @ApiParam(value = "User details to store", required = true) @RequestBody
            UserDetails userDetails ,  @PathVariable(name = "id") Long userSysId) throws IOException {
        Ticket ticket = SipCommonUtils.getTicket(request);
        RoleType roleType = ticket.getRoleType();
        String masterLoginId = ticket.getMasterLoginId();
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        if (roleType != RoleType.ADMIN) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            userDetailsResponse.setValid(false);
            logger.error("user is not admin");
            userDetailsResponse.setValidityMessage("You are not authorized to perform this operation");
            return userDetailsResponse;
        }
        userDetails.setUserId(userSysId);
        Long customerId = Long.valueOf(ticket.getCustID());
        return securityService.updateUserDetails(userDetails, masterLoginId, customerId,userSysId, response);
    }

    /**
     * fetch user with dsk.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Returns UserDetailsResponse
    */
    @ApiOperation(
        value = " Fetch User API ",
        nickname = "fetchUserWithDsk",
        notes = "Admin can only use this API to fetch the user",
        response = UserDetailsResponse.class)
    @GetMapping(value = "/users/{id}")
    public UserDetailsResponse getUser(
        HttpServletRequest request,
        HttpServletResponse response, @PathVariable(name = "id") Long userSysId) {
        Ticket ticket = SipCommonUtils.getTicket(request);
        RoleType roleType = ticket.getRoleType();
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        if (roleType != RoleType.ADMIN) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            userDetailsResponse.setValid(false);
            logger.error("user is not admin");
            userDetailsResponse.setValidityMessage("You are not authorized to perform this operation");
            return userDetailsResponse;
        }
        Long customerId = Long.valueOf(ticket.getCustID());
        UserDetails userDetails = userRepository.getUserbyId(userSysId,customerId);
        if (userDetails!=null) {
            userDetailsResponse.setUser(userDetails);
            userDetailsResponse.setValid(true);
            userDetailsResponse.setValidityMessage("User details fetched successfully");
            userDetailsResponse.setError("");
        }
        else {
            userDetailsResponse.setValid(false);
            userDetailsResponse.setValidityMessage("Unable to fetch the user Details");
            userDetailsResponse.setError("Error occurred while fetching user details ");
        }
        return userDetailsResponse;
    }


    /**
     * delete user with dsk.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Returns UserDetailsResponse
     */
    @ApiOperation(
        value = " delete User API ",
        nickname = "deleteUserWithDsk",
        notes = "Admin can only use this API to delete the user",
        response = UserDetailsResponse.class)
    @DeleteMapping(value = "/users/{id}")
    public UserDetailsResponse deleteUser(
        HttpServletRequest request,
        HttpServletResponse response, @PathVariable(name = "id") Long userSysId) {
        Ticket ticket = SipCommonUtils.getTicket(request);
        RoleType roleType = ticket.getRoleType();
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        if (roleType != RoleType.ADMIN) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            userDetailsResponse.setValid(false);
            logger.error("user is not admin");
            userDetailsResponse.setValidityMessage("You are not authorized to perform this operation");
            return userDetailsResponse;
        }
        Long customerId = Long.valueOf(ticket.getCustID());
        UserDetails userDetails = userRepository.getUserbyId(userSysId,customerId);
        if (userDetails!=null) {
            boolean flag = userRepository.deleteUser(userSysId,
                userDetails.getMasterLoginId(),customerId);
            if (flag) {
                userDetailsResponse.setUser(userDetails);
                userDetailsResponse.setValid(true);
                userDetailsResponse.setValidityMessage("User details deleted successfully");
                userDetailsResponse.setError("");
            }
            else {
                userDetailsResponse.setValid(false);
                userDetailsResponse.setValidityMessage("Unable to delete the user Details");
                userDetailsResponse.setError("Error occurred while deleting user details ");
            }
        }
        else {
            userDetailsResponse.setValid(false);
            userDetailsResponse.setValidityMessage("Unable to delete the user Details");
            userDetailsResponse.setError("Error occurred while deleting user details ");
        }
        return userDetailsResponse;
    }

  /**
   * gets all users.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Returns UsersDetailsList
   */
  @ApiOperation(
      value = " Fetch Users API ",
      nickname = "FetchUsers",
      notes = "Admin can only use this API to fetch the users",
      response = UsersDetailsList.class)
  @GetMapping(value = "/users")
  public UsersDetailsList getUserList(HttpServletRequest request, HttpServletResponse response) {
    Ticket ticket = SipCommonUtils.getTicket(request);
    RoleType roleType = ticket.getRoleType();
    UsersDetailsList usersDetailsListResponse = new UsersDetailsList();
    if (roleType != RoleType.ADMIN) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      usersDetailsListResponse.setValid(false);
      usersDetailsListResponse.setValidityMessage(
          "You are not authorized to perform this operation");
      logger.error("user is not admin");
      return usersDetailsListResponse;
    }
    Long customerId = Long.valueOf(ticket.getCustID());
    return securityService.getUsersDetailList(customerId);
  }

  @PostMapping(value = "/dsk-security-groups")
  @ApiOperation(value = "Add DSK security group")
  @ResponseBody
  public DskGroupPayload addDskGroup(
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "DSK group details") @RequestBody DskGroupPayload dskGroupPayload) {
      Ticket ticket = SipCommonUtils.getTicket(request);

      Long customerId = Long.valueOf(ticket.getCustID());
      String createdBy = ticket.getUserFullName();
      RoleType roleType = ticket.getRoleType();

      if (roleType != RoleType.ADMIN) {
          DskGroupPayload payload = new DskGroupPayload();
          logger.error("Invalid user");
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          payload.setValid(false);
          payload.setMessage(ServerResponseMessages.ADD_GROUPS_WITH_NON_ADMIN_ROLE);

          return payload;
      }

      DskGroupPayload responsePayload = null;
      Long securityGroupSysId = null;
      try {
          String securityGroupName = dskGroupPayload.getGroupName();
          String securityGroupDescription = dskGroupPayload.getGroupDescription();
          SipDskAttribute dskAttribute = dskGroupPayload.getDskAttributes();

          SecurityGroups securityGroup = new SecurityGroups();

          if (securityGroupName == null || securityGroupName.length() == 0) {
              responsePayload = new DskGroupPayload();

              responsePayload.setValid(false);
              responsePayload.setMessage("Group name is mandatory");
              response.setStatus(HttpStatus.BAD_REQUEST.value());

              return responsePayload;
          }

          if (dskAttribute == null) {
              responsePayload = new DskGroupPayload();

              responsePayload.setValid(false);
              responsePayload.setMessage("DSK attributes are mandatory");

              response.setStatus(HttpStatus.BAD_REQUEST.value());

              return responsePayload;
          }
          securityGroup.setSecurityGroupName(securityGroupName);
          securityGroup.setDescription(securityGroupDescription);

          DskValidity dskValidity =
              dataSecurityKeyRepository.addSecurityGroups(securityGroup,createdBy,customerId);

          securityGroupSysId = dskValidity.getGroupId();

          if (securityGroupSysId == null) {
              responsePayload = new DskGroupPayload();
              logger.error("Error occurred: {}", dskValidity.getError());
              response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
              responsePayload.setValid(false);
              responsePayload.setMessage(dskValidity.getError());

              return responsePayload;
          }

          // Prepare the list og attribute models. This will also validate for missing attributes.
          List<SipDskAttributeModel> attributeModelList = dataSecurityKeyRepository.
              prepareDskAttributeModelList(securityGroupSysId, dskAttribute, Optional.empty());
          Valid valid = dataSecurityKeyRepository
              .addDskGroupAttributeModelAndValues(securityGroupSysId, attributeModelList);

          if (valid.getValid() == true) {
              responsePayload =
                  dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);

              responsePayload.setValid(true);
          } else {
              responsePayload = new DskGroupPayload();

              responsePayload.setValid(false);
              responsePayload.setMessage(valid.getError());

              if (securityGroupSysId != null) {
                  dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
              }

              response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
          }

      } catch (Exception ex) {

          if (securityGroupSysId != null) {
              dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
          }
          responsePayload = new DskGroupPayload();
          responsePayload.setValid(false);
          response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
          responsePayload.setMessage("Error occurred: " + ex.getMessage());
      }

      return responsePayload;
  }

  @GetMapping(value = "/dsk-security-groups")
  @ApiOperation(value = "Fetch security group details for a customer")
  public Object getAllSecGroupDetailsForCustomer(
      HttpServletRequest request, HttpServletResponse response) {
      List<DskGroupPayload> payload = null;

      Ticket ticket = SipCommonUtils.getTicket(request);

      Long customerId = Long.valueOf(ticket.getCustID());
      RoleType roleType = ticket.getRoleType();

      if (roleType != RoleType.ADMIN) {
          Valid valid = new Valid();
          logger.error("Invalid user");
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          valid.setValid(false);
          valid.setValidityMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);
          return valid;
      }

      try {
          payload = dataSecurityKeyRepository.fetchAllDskGroupForCustomer(customerId);
      } catch (Exception ex) {
          logger.error("Error occurred while fetching security group details: "
              + ex.getMessage(), ex);
          response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      }

      return payload;
  }

  @GetMapping(value = "/dsk-security-groups/{securityGroupId}")
  @ApiOperation(value = "Fetch security group details for a given security group id")
  @ResponseBody
  public DskGroupPayload getSecurityGroupDetails(
      @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
      HttpServletRequest request,
      HttpServletResponse response) {
    DskGroupPayload payload;

    Ticket ticket = SipCommonUtils.getTicket(request);

    RoleType roleType = ticket.getRoleType();

    if (roleType != RoleType.ADMIN) {
      payload = new DskGroupPayload();
      logger.error("Invalid user");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      payload.setValid(false);
      payload.setMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);

      return payload;
    }

    try {
      Long customerId = Long.valueOf(ticket.getCustID());
      payload =
          dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);
      return payload;
    } catch (Exception ex) {
      payload = new DskGroupPayload();
      payload.setValid(false);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      payload.setMessage("Error occurred: " + ex.getMessage());

      return payload;
    }
  }

  @DeleteMapping(value = "/dsk-security-groups/{securityGroupId}")
  @ApiOperation(value = "Delete security group attributes")
  public Valid deleteSecurityGroupAttributeModel(
      @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
      HttpServletRequest request,
      HttpServletResponse response) {

      Ticket ticket = SipCommonUtils.getTicket(request);
      RoleType roleType = ticket.getRoleType();

      Valid valid;
      if (roleType != RoleType.ADMIN) {
          valid = new Valid();
          logger.error("Invalid user");
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          valid.setValid(false);
          valid.setValidityMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);

          return valid;
      }

      try {

          Long customerId = Long.valueOf(ticket.getCustID());

          valid = dataSecurityKeyRepository.deleteDskGroupAttributeModel(securityGroupSysId, customerId);

          if (valid.getValid()) {
              // If the deletion of security group attributes is successful, delete the security group
              // also
              valid = dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
          }
      } catch (Exception ex) {
          valid = new Valid();
          valid.setValid(false);
          valid.setError(ex.getMessage());
          valid.setValidityMessage("Error occurred while deleting security group");
      }

      return valid;
  }

  @PutMapping(value = "/dsk-security-groups/{securityGroupId}")
  @ApiOperation(value = "Update security group attributes")
  @ResponseBody
  public DskGroupPayload updateSecurityGroupAttributeModel (
      @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value="New attributes for the security group")
      @RequestBody SipDskAttribute sipDskAttributes) {
      DskGroupPayload payload = null;

      Ticket ticket = SipCommonUtils.getTicket(request);
      Long customerId = Long.valueOf(ticket.getCustID());

      RoleType roleType = ticket.getRoleType();

      if (roleType != RoleType.ADMIN) {
          payload = new DskGroupPayload();
          logger.error("Invalid user");
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          payload.setValid(false);
          payload.setMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);

          return payload;
      }

      try {

          if (sipDskAttributes == null) {
              payload = new DskGroupPayload();

              payload.setValid(false);
              payload.setMessage("Invalid request");
              response.setStatus(HttpStatus.BAD_REQUEST.value());

              return payload;
          }

          Valid valid = dataSecurityKeyRepository
              .validateCustomerForSecGroup(securityGroupSysId, customerId);


          if (!valid.getValid()) {
              payload = new DskGroupPayload();

              payload.setValid(false);
              payload.setMessage(valid.getValidityMessage());

              return payload;
          }

          List<SipDskAttributeModel> dskAttributeModelList =
              dataSecurityKeyRepository.prepareDskAttributeModelList(
                  securityGroupSysId, sipDskAttributes, Optional.empty());
          // Delete the existing security group attributes
          valid = dataSecurityKeyRepository.deleteDskGroupAttributeModel(securityGroupSysId, customerId);

          if (valid.getValid()) {
              // Add the new security group attributes
              logger.info("Deleted existing DSK attributes");
              valid = dataSecurityKeyRepository
                  .addDskGroupAttributeModelAndValues(securityGroupSysId, dskAttributeModelList);

              if (valid.getValid()) {
                  logger.info("DSK attributes updated successfully");

                  payload = dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);
                  payload.setValid(true);
              } else {
                  logger.error("Error occurred: {}", valid.getError());
                  payload = new DskGroupPayload();

                  payload.setValid(false);
                  payload.setMessage("Unable to update the dsk attributes");
              }

          } else {
              logger.error("Error occurred: {}", valid.getError());
              payload = new DskGroupPayload();

              payload.setValid(false);
              payload.setMessage("Unable to update the dsk attributes");
          }
      // Fetch the attributes and return
    } catch (Exception ex) {
      logger.error(
          "Error occurred while updating the security group attributes: " + ex.getMessage(), ex);

      payload = new DskGroupPayload();
      payload.setValid(false);
      payload.setMessage(ex.getMessage());
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }

      return payload;
  }
}

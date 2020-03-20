package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.id3.model.Id3Claims;
import com.sncr.saw.security.app.id3.service.ValidateId3IdentityToken;
import com.sncr.saw.security.app.repository.DataSecurityKeyRepository;
import com.sncr.saw.security.app.repository.Id3Repository;
import com.sncr.saw.security.app.repository.ProductModuleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.app.repository.impl.Id3RepositoryImpl;
import com.sncr.saw.security.app.service.ExternalSecurityService;
import com.sncr.saw.security.app.sso.SSORequestHandler;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.UserDetails;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.external.response.Id3User;
import com.sncr.saw.security.common.bean.external.response.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.sncr.saw.security.common.bean.repo.admin.UserDetailsResponse;
import com.sncr.saw.security.common.bean.repo.admin.UsersDetailsList;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.dsk.DskValidity;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.constants.ErrorMessages;
import com.synchronoss.bda.sip.dsk.DskGroupPayload;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.bda.sip.dsk.SipDskAttributeModel;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/sip-security/id3/auth/admin/v1")
public class Id3SecurityController {

  private ExternalSecurityService securityService;
  private Id3Repository id3Repository;
  private ProductModuleRepository productModuleRepository;
  private DataSecurityKeyRepository dataSecurityKeyRepository;
  private ValidateId3IdentityToken validateId3IdentityToken;
  private UserRepository userRepository;

  @Autowired
  public Id3SecurityController(
      ExternalSecurityService securityService,
      Id3Repository id3Repository,
      ProductModuleRepository productModuleRepository,
      DataSecurityKeyRepository dataSecurityKeyRepository,
      SSORequestHandler ssoRequestHandler,
      ValidateId3IdentityToken validateId3IdentityToken,
      UserRepository userRepository) {
    this.securityService = securityService;
    this.id3Repository = id3Repository;
    this.productModuleRepository = productModuleRepository;
    this.dataSecurityKeyRepository = dataSecurityKeyRepository;
    this.validateId3IdentityToken = validateId3IdentityToken;
    this.userRepository = userRepository;
  }

  private static final Logger logger = LoggerFactory.getLogger(Id3SecurityController.class);

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
      @RequestBody RoleCategoryPrivilege request,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    if (request == null) {
      logger.warn("Request body can't be blank or empty.");
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(), "Request body can't be blank or empty.");
      return null;
    }

    if ("ALERTS".equalsIgnoreCase(request.getModuleName())
        || "WORKBENCH".equalsIgnoreCase(request.getModuleName())) {
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(), "ALERTS and WORKBENCH module are not allowed.");
      return null;
    }
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    if (id3Claims == null) {
      logger.info("Invalid Token / unable to fetch the id3 details");
      httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid Token");
      return null;
    }
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    String roleType = id3User.getRoleType();
    String masterLoginId = id3User.getUserId();
    if (masterLoginId != null || !roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.info("You are not authorized to perform this operation.");
      httpResponse.sendError(
          HttpStatus.UNAUTHORIZED.value(), "You are not authorized to perform this operation.");
      return null;
    }

    Role role = request.getRole();
    if (role.getCustomerCode() == null || role.getCustomerCode().isEmpty()) {
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(), "Customer Code can't be blank or empty.");
      return null;
    }

    if (role.getRoleType() == null || role.getRoleType().isEmpty()) {
      httpResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Role Type can't be blank or empty.");
      return null;
    }

    if (!RoleType.validRoleType(role.getRoleType())) {
      logger.error("Only ADMIN|USER Role Type are allowed.");
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(), "Only ADMIN|USER Role Type are allowed.");
      return null;
    }

    if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
      logger.error("Role Name can't be blank or empty.");
      httpResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Role Name can't be blank or empty.");
      return null;
    }

    ProductModuleDetails moduleDetails =
        productModuleRepository.fetchModuleProductDetail(
            masterLoginId, request.getProductName(), request.getModuleName());
    final Long customerSysId = moduleDetails != null ? moduleDetails.getCustomerSysId() : null;
    if (customerSysId == null || customerSysId == 0) {
      logger.error("Product and Module does not exist for this user.");
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(), "Product and Module does not exist for this user.");
      return null;
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
      logger.error(
          "Special symbol and numeric not allowed except"
              + " underscore(_) and hyphen(-) for role name.");
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(),
          "Special symbol and numeric not allowed except "
              + "underscore(_) and hyphen(-) for role name.");
      return null;
    } else if (categoryList != null && !categoryList.isEmpty()) {
      boolean emptyCategoryName =
          categoryList.stream()
              .anyMatch(
                  category ->
                      category.getCategoryName() == null
                          || category.getCategoryName().trim().isEmpty());
      if (emptyCategoryName) {
        logger.error("Category name can't be blank or empty.");
        httpResponse.sendError(
            HttpStatus.BAD_REQUEST.value(), "Category name can't be blank or empty.");
        return null;
      }

      boolean invalidCategoryName =
          categoryList.stream()
              .anyMatch(
                  category ->
                      category.isAutoCreate()
                          && securityService.validateName(category.getCategoryName().trim()));
      if (invalidCategoryName) {
        logger.error(
            "Special symbol not allowed except "
                + "underscore(_) and hyphen(-) for category name.");
        httpResponse.sendError(
            HttpStatus.BAD_REQUEST.value(),
            "Special symbol not allowed except"
                + " underscore(_) and hyphen(-) for category name.");
        return null;
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
              logger.error("Sub Category name can't be blank or empty.");
              httpResponse.sendError(
                  HttpStatus.BAD_REQUEST.value(), "Sub Category name can't be blank or empty");
              return null;
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
          logger.error(
              "Special symbol not allowed except "
                  + "underscore(_) and hyphen(-) for sub category name.");
          httpResponse.sendError(
              HttpStatus.BAD_REQUEST.value(),
              "Special symbol not allowed except "
                  + "underscore(_) and hyphen(-) for sub category name.");
          return null;
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
          logger.error("Please provide the valid module " + "privileges for category/subcategory.");
          httpResponse.sendError(
              HttpStatus.BAD_REQUEST.value(),
              "Please provide the valid module " + "privileges for category/subcategory.");
          return null;
        }
      }
    }

    if (!role.getCustomerCode().equalsIgnoreCase(moduleDetails.getCustomerCode())) {
      logger.error("Customer Code not matched with the user ticket.");
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(), "Customer Code not matched with the user ticket.");
      return null;
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
      @RequestBody RoleCategoryPrivilege request,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    if (request == null) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setValid(false);
      response.setMessage("Request body can't be blank or empty.");
      return response;
    }

    // Ticket ticket = SipCommonUtils.getTicket(httpRequest);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    if (id3Claims == null) {
      httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    String roleType = id3User.getRoleType();
    String masterLoginId = id3User.getUserId();
    if (masterLoginId != null || !roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      httpResponse.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.unAuthorizedMessage);
      return null;
    }

    String productName = request.getProductName();
    String moduleName = request.getModuleName();
    if ("ALERTS".equalsIgnoreCase(moduleName) || "WORKBENCH".equalsIgnoreCase(moduleName)) {
      httpResponse.sendError(
          HttpStatus.BAD_REQUEST.value(), "ALERTS and WORKBENCH module are not allowed.");
      return null;
    }

    ProductModuleDetails moduleDetails =
        productModuleRepository.fetchModuleProductDetail(masterLoginId, productName, moduleName);
    final Long customerSysId = moduleDetails != null ? moduleDetails.getCustomerSysId() : null;
    if (customerSysId == null || customerSysId == 0) {
      httpResponse.sendError(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), "Role Name can't be blank or empty.");
      response.setValid(false);
      response.setMessage("Product and Module does not exist for this user.");
      response.setProductName(productName);
      response.setModuleName(moduleName);
      return response;
    }

    Role role = request.getRole();
    if (role == null || role.getRoleName() == null || role.getRoleName().isEmpty()) {
      httpResponse.sendError(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), "Role Name can't be blank or empty.");
      response.setMessage("Role Name can't be blank or empty.");
      response.setProductName(productName);
      response.setModuleName(moduleName);
      return response;
    }

    response =
        securityService.fetchRoleCategoryPrivilege(
            request, productName, moduleName, moduleDetails, customerSysId);
    if (!response.getValid()) {
      httpResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getMessage());
      return null;
    }
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
  public UserDetails createUser(
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "User details to store", required = true) @RequestBody
          UserDetails userDetails,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    // Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3user = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    String roleType = id3user.getRoleType();
    String masterLoginId = id3Claims.getMasterLoginId();

    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("user is not admin");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.unAuthorizedMessage);
      return null;
    }
    Long customerId = id3user.getCustomerSysId();
    userDetailsResponse =
        securityService.addUserDetails(userDetails, masterLoginId, customerId, response);
    if (userDetailsResponse == null) {
      return null;
    } else if (!userDetailsResponse.getValid()) {
      response.sendError(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error occurred while creating user ");
      return null;
    }
    return userDetailsResponse.getUser();
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
  public UserDetails updateUser(
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "User details to store", required = true) @RequestBody
          UserDetails userDetails,
      @PathVariable(name = "id") Long userSysId,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    //  Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    String roleType = id3User.getRoleType();
    String masterLoginId = id3Claims.getMasterLoginId();

    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("User Type isn't Admin, can't perform this operation");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.unAuthorizedMessage);
      return null;
    }
    userDetails.setUserId(userSysId);
    Long customerId = id3User.getCustomerSysId();
    userDetailsResponse =
        securityService.updateUserDetails(
            userDetails, masterLoginId, customerId, userSysId, response);
    if (userDetailsResponse == null) {
      return null;
    } else if (!userDetailsResponse.getValid()) {
      response.sendError(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error occurred while updating user ");
      return null;
    }
    return userDetailsResponse.getUser();
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
      HttpServletResponse response,
      @PathVariable(name = "id") Long userSysId,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    // Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    String roleType = id3User.getRoleType();
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("User Type isn't Admin, can't perform this operation");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.unAuthorizedMessage);
      return null;
    }
    Long customerId = id3User.getCustomerSysId();
    UserDetails userDetails = userRepository.getUserbyId(userSysId, customerId);
    if (userDetails != null) {
      userDetailsResponse.setUser(userDetails);
      userDetailsResponse.setValid(true);
      userDetailsResponse.setValidityMessage("User details fetched successfully");
      userDetailsResponse.setError("");
    } else {
      logger.error("User Type isn't Admin, can't perform this operation");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.unAuthorizedMessage);
      return null;
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
      HttpServletResponse response,
      @PathVariable(name = "id") Long userSysId,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    // Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    String roleType = id3User.getRoleType();
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("User Type isn't Admin, can't perform this operation");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.unAuthorizedMessage);
      return null;
    }
    Long customerId = id3User.getCustomerSysId();
    UserDetails userDetails = userRepository.getUserbyId(userSysId, customerId);
    if (userDetails != null) {
      boolean flag =
          userRepository.deleteUser(userSysId, userDetails.getMasterLoginId(), customerId);
      if (flag) {
        userDetailsResponse.setUser(userDetails);
        userDetailsResponse.setValid(true);
        userDetailsResponse.setValidityMessage("User details deleted successfully");
        userDetailsResponse.setError("");
      } else {
        logger.error("Error occurred while deleting user details");
        response.sendError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error occurred while deleting user details");
        return null;
      }
    } else {
      logger.error("Error occurred while deleting user details");
      response.sendError(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error occurred while deleting user details");
      return null;
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
  public UsersDetailsList getUserList(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    // Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    String roleType = id3User.getRoleType();
    UsersDetailsList usersDetailsListResponse = new UsersDetailsList();
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("User Type isn't Admin, can't perform this operation");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.unAuthorizedMessage);
      return null;
    }
    Long customerId = id3User.getCustomerSysId();
    UsersDetailsList usersDetailsList = securityService.getUsersDetailList(customerId);
    if (!usersDetailsList.getValid()) {
      response.sendError(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), usersDetailsList.getValidityMessage());
      return null;
    }
    return securityService.getUsersDetailList(customerId);
  }

  @PostMapping(value = "/dsk-security-groups")
  @ApiOperation(value = "Add DSK security group")
  @ResponseBody
  public DskGroupPayload addDskGroup(
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "DSK group details") @RequestBody DskGroupPayload dskGroupPayload,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    // Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    Long customerId = id3User.getCustomerSysId();
    String createdBy = "System";
    String roleType = id3User.getRoleType();

    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("Invalid user");
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), ServerResponseMessages.ADD_GROUPS_WITH_NON_ADMIN_ROLE);
      return null;
    }

    DskGroupPayload responsePayload = null;
    Long securityGroupSysId = null;
    try {
      String securityGroupName = dskGroupPayload.getGroupName();
      String securityGroupDescription = dskGroupPayload.getGroupDescription();
      SipDskAttribute dskAttribute = dskGroupPayload.getDskAttributes();
      SecurityGroups securityGroup = new SecurityGroups();

      if (securityGroupName == null || securityGroupName.length() == 0) {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Group name is mandatory");
        return null;
      }

      if (dskAttribute == null) {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "DSK attributes are mandatory");
        return null;
      }
      securityGroup.setSecurityGroupName(securityGroupName);
      securityGroup.setDescription(securityGroupDescription);

      DskValidity dskValidity =
          dataSecurityKeyRepository.addSecurityGroups(securityGroup, createdBy, customerId);

      securityGroupSysId = dskValidity.getGroupId();

      if (securityGroupSysId == null) {
        logger.error("Error occurred: {}", dskValidity.getError());
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), dskValidity.getError());
        return null;
      }

      // Prepare the list og attribute models. This will also validate for missing attributes.
      List<SipDskAttributeModel> attributeModelList =
          dataSecurityKeyRepository.prepareDskAttributeModelList(
              securityGroupSysId, dskAttribute, Optional.empty());
      Valid valid =
          dataSecurityKeyRepository.addDskGroupAttributeModelAndValues(
              securityGroupSysId, attributeModelList);

      if (valid.getValid() == true) {
        responsePayload =
            dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);
        responsePayload.setValid(true);
      } else {
        if (securityGroupSysId != null) {
          dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
        }
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), valid.getError());
      }
    } catch (Exception ex) {

      if (securityGroupSysId != null) {
        dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
      }
      response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
      return null;
    }

    return responsePayload;
  }

  @GetMapping(value = "/dsk-security-groups")
  @ApiOperation(value = "Fetch security group details for a customer")
  public Object getAllSecGroupDetailsForCustomer(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    List<DskGroupPayload> payload = null;

    // Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }

    String roleType = id3User.getRoleType();

    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("Invalid user");
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), ServerResponseMessages.WITH_NON_ADMIN_ROLE);
      return null;
    }
    Long customerId = id3User.getCustomerSysId();

    try {
      payload = dataSecurityKeyRepository.fetchAllDskGroupForCustomer(customerId);
    } catch (Exception ex) {
      logger.error("Error occurred while fetching security group details: " + ex.getMessage(), ex);
      response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
      return null;
    }

    return payload;
  }

  @GetMapping(value = "/dsk-security-groups/{securityGroupId}")
  @ApiOperation(value = "Fetch security group details for a given security group id")
  @ResponseBody
  public DskGroupPayload getSecurityGroupDetails(
      @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    DskGroupPayload payload;

    // Ticket ticket = SipCommonUtils.getTicket(request);

    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }

    String roleType = id3User.getRoleType();

    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("Invalid user");
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), ServerResponseMessages.WITH_NON_ADMIN_ROLE);
      return null;
    }

    try {
      Long customerId = id3User.getCustomerSysId();
      payload =
          dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);
      if (!payload.getValid()) {
        logger.error("Error occurred: {}", payload.getMessage());
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), payload.getMessage());
        return null;
      }
      return payload;
    } catch (Exception ex) {
      logger.error("Error occurred: {}", ex.getMessage());
      response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
      return null;
    }
  }

  @DeleteMapping(value = "/dsk-security-groups/{securityGroupId}")
  @ApiOperation(value = "Delete security group attributes")
  public Valid deleteSecurityGroupAttributeModel(
      @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestHeader("Authorization") String id3token)
      throws IOException {

    //  Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    String roleType = id3User.getRoleType();
    Valid valid;
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("Invalid user");
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), ServerResponseMessages.WITH_NON_ADMIN_ROLE);
      return null;
    }

    try {

      Long customerId = id3User.getCustomerSysId();

      valid =
          dataSecurityKeyRepository.deleteDskGroupAttributeModel(securityGroupSysId, customerId);

      if (valid.getValid()) {
        // If the deletion of security group attributes is successful, delete the security group
        // also
        valid = dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
      }
    } catch (Exception ex) {
      logger.error("Error occurred: {}", ex.getMessage());
      response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
      return null;
    }

    if (!valid.getValid()) {
      logger.error("Error occurred: {}", "Error occurred while deleting the DSK");
      response.sendError(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error occurred while deleting the DSK");
      return null;
    }

    return valid;
  }

  @PutMapping(value = "/dsk-security-groups/{securityGroupId}")
  @ApiOperation(value = "Update security group attributes")
  @ResponseBody
  public DskGroupPayload updateSecurityGroupAttributeModel(
      @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "New attributes for the security group") @RequestBody
          SipDskAttribute sipDskAttributes,
      @RequestHeader("Authorization") String id3token)
      throws IOException {
    DskGroupPayload payload = null;

    // Ticket ticket = SipCommonUtils.getTicket(request);
    Id3Claims id3Claims = validateId3IdentityToken.validateToken(id3token, Id3Claims.Type.ID);
    Id3User id3User = id3Repository.getId3Userdetails(id3Claims.getMasterLoginId());
    if (id3Claims == null) {
      logger.error("Invalid Token");
      response.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorMessages.INVALID_TOKEN);
      return null;
    }
    Long customerId = id3User.getCustomerSysId();

    String roleType = id3User.getRoleType();

    if (!roleType.equalsIgnoreCase(RoleType.ADMIN.name())) {
      logger.error("Invalid user");
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), ServerResponseMessages.WITH_NON_ADMIN_ROLE);
      return null;
    }

    try {
      if (sipDskAttributes == null) {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid request");
        return null;
      }

      Valid valid =
          dataSecurityKeyRepository.validateCustomerForSecGroup(securityGroupSysId, customerId);

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
      valid =
          dataSecurityKeyRepository.deleteDskGroupAttributeModel(securityGroupSysId, customerId);

      if (valid.getValid()) {
        // Add the new security group attributes
        logger.info("Deleted existing DSK attributes");
        valid =
            dataSecurityKeyRepository.addDskGroupAttributeModelAndValues(
                securityGroupSysId, dskAttributeModelList);

        if (valid.getValid()) {
          logger.info("DSK attributes updated successfully");

          payload =
              dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);
          payload.setValid(true);
        } else {
          logger.error("Error occurred: {}", valid.getError());
          response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), valid.getError());
          return null;
        }

      } else {
        logger.error("Error occurred: {}", valid.getError());
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), valid.getError());
        return null;
      }
      // Fetch the attributes and return
    } catch (Exception ex) {
      logger.error(
          "Error occurred while updating the security group attributes: " + ex.getMessage(), ex);
      response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
      return null;
    }
    if (!payload.getValid()) {
      logger.error("Error occurred: {}", payload.getMessage());
      response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), payload.getMessage());
      return null;
    }
    return payload;
  }
}

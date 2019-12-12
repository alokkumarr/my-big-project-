package com.sncr.saw.security.app.controller.external;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.ProductModuleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.app.service.ExternalSecurityService;
import com.sncr.saw.security.common.bean.external.response.RoleCatPrivilegeResponse;
import com.sncr.saw.security.common.bean.external.request.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.Ticket;
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
  private NSSOProperties nSSOProperties;
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
  public RoleCatPrivilegeResponse createRoleCategoryPrivilege(HttpServletRequest request,
                                                              HttpServletResponse httpResponse,
                                                              @RequestBody RoleCategoryPrivilege roleCategoryPrivilege) {
    RoleCatPrivilegeResponse response = new RoleCatPrivilegeResponse();
    if (roleCategoryPrivilege == null) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setValid(false);
      response.setMessage("Request body can't be blank or empty.");
      return response;
    }

    Ticket ticket = SipCommonUtils.getTicket(request);
    RoleType roleType = ticket.getRoleType();
    String masterLoginId = ticket.getMasterLoginId();
    if ((masterLoginId != null && !userRepository.validateUser(masterLoginId)) || !RoleType.ADMIN.equals(roleType)) {
      httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setValid(false);
      response.setMessage("You are not authorized to perform this operation.");
      return response;
    }

    ProductModuleDetails moduleDetails = productModuleRepository.fetchModuleProductDetail(masterLoginId,
        roleCategoryPrivilege.getProductName(),
        roleCategoryPrivilege.getModuleName());
    final Long customerSysId = moduleDetails != null ? moduleDetails.getCustomerSysId() : null;
    if (customerSysId == null || customerSysId == 0) {
      httpResponse.setStatus(HttpStatus.OK.value());
      response.setValid(false);
      response.setMessage("Product and Module does not exist for this user.");
      return response;
    }
    response = securityService.createRoleCategoryPrivilege(httpResponse, roleCategoryPrivilege, masterLoginId, moduleDetails, customerSysId);
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

    if (request.getRole() == null || request.getRole().getRoleName() == null || request.getRole().getRoleName().isEmpty()) {
      httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("Role Name can't be blank or empty.");
      response.setProductName(productName);
      response.setModuleName(moduleName);
      return response;
    }
    response = securityService.fetchRoleCategoryPrivilege(request, productName, moduleName, moduleDetails, customerSysId);

    return response;
  }
}

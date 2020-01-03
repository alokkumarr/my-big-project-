package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.model.DskEligibleFields;
import com.sncr.saw.security.app.model.DskField;
import com.sncr.saw.security.app.model.DskFieldsInfo;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.service.DskEligibleFieldService;
import com.sncr.saw.security.common.bean.Valid;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(
    value =
        "The controller provides operations to DskEligible Fields of"
            + "synchronoss insights platform ")
@RequestMapping("/sip-security/auth/admin/dsk")
public class SipDskFieldsController {

  private static final Logger logger =
      LoggerFactory.getLogger(SipDskFieldsController.class);

  @Autowired NSSOProperties nssoProperties;

  @Autowired DskEligibleFieldService dskEligibleFieldService;

  @ApiOperation(
      value = " Add DSK Eligible Fields ",
      nickname = "AddDskFields",
      notes = "Admin can only use this API to add DSK Eligible Fields",
      response = Valid.class)
  @RequestMapping(value = "/fields", method = RequestMethod.POST)
  @ResponseBody
  public Valid addDskEligibleFields(
      @RequestParam(name = "semanticId") String semanticId,
      @RequestBody List<DskField> dskFields,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    Ticket ticket = SipCommonUtils.getTicket(request);

    Long customerSysId = Long.valueOf(ticket.getCustID());
    Long defaultProdID = Long.valueOf(ticket.getDefaultProdID());
    String createdBy = ticket.getUserFullName();

    DskEligibleFields dskEligibleFields = new DskEligibleFields();
    dskEligibleFields.setCustomerSysId(customerSysId);
    dskEligibleFields.setProductSysId(defaultProdID);
    dskEligibleFields.setSemanticId(semanticId);
    dskEligibleFields.setCreatedBy(createdBy);
    dskEligibleFields.setFields(dskFields);

    return dskEligibleFieldService.addDskEligibleFields(dskEligibleFields, request, response);
  }

  @ApiOperation(
      value = " Delete DSK Eligible Fields ",
      nickname = "DeleteDskFields",
      notes = "Admin can only use this API to add DSK Eligible Fields",
      response = Valid.class)
  @RequestMapping(value = "/fields", method = RequestMethod.DELETE)
  @ResponseBody
  public Valid deleteDskEligibleFields(
      @ApiParam(value = "semantic id", required = true)
          @RequestParam(name = "semanticId", required = true)
          String semanticId,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    return dskEligibleFieldService.deleteDskEligibleFields(semanticId, request, response);
  }

  @ApiOperation(
      value = " Update DSK Eligible Fields ",
      nickname = "UpdateDskFields",
      notes = "Admin can only use this API to update DSK Eligible Fields",
      response = Valid.class)
  @RequestMapping(value = "/fields", method = RequestMethod.PUT)
  @ResponseBody
  public Valid updateDskEligibleFields(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "semanticId") String semanticId,
      @RequestBody List<DskField> dskFields)
      throws IOException {
    Ticket ticket = SipCommonUtils.getTicket(request);

    Long customerSysId = Long.valueOf(ticket.getCustID());
    Long defaultProdID = Long.valueOf(ticket.getDefaultProdID());
    String createdBy = ticket.getUserFullName();

    DskEligibleFields dskEligibleFields = new DskEligibleFields();
    dskEligibleFields.setCustomerSysId(customerSysId);
    dskEligibleFields.setProductSysId(defaultProdID);
    dskEligibleFields.setSemanticId(semanticId);
    dskEligibleFields.setCreatedBy(createdBy);
    dskEligibleFields.setFields(dskFields);


    return dskEligibleFieldService.updateDskEligibleFields(dskEligibleFields, request, response);
  }

    @ApiOperation(
        value = " Fetch DSK Eligible Fields ",
        nickname = "FetchDskFields",
        notes = "Admin can only use this API to fetch DSK Eligible Fields",
        response = Valid.class)
    @RequestMapping(value = "/fields", method = RequestMethod.GET)
    public Object getDskEligibleKeys(HttpServletRequest request, HttpServletResponse response) {
        Ticket ticket = SipCommonUtils.getTicket(request);

        Long customerSysId = Long.valueOf(ticket.getCustID());
        Long defaultProdID = Long.valueOf(ticket.getDefaultProdID());
        RoleType roleType = ticket.getRoleType();
        if (roleType != RoleType.ADMIN) {
            Valid valid = new Valid();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.MODIFY_USER_GROUPS_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.MODIFY_USER_GROUPS_WITH_NON_ADMIN_ROLE);
            return valid;
        }

        DskFieldsInfo dskEligibleFields = dskEligibleFieldService
            .fetchAllDskEligibleFields(customerSysId, defaultProdID);

        return dskEligibleFields;
    }
}

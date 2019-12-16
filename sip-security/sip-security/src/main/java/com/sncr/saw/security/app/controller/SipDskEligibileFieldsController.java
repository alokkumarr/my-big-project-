package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.model.DskEligibleFields;
import com.sncr.saw.security.app.model.DskField;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.service.DskEligibleFieldService;
import com.sncr.saw.security.common.bean.Valid;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "The controller provides operations to DskEligible Fields of"
    + "synchronoss insights platform ")
@RequestMapping("/sip-security/auth/admin/dsk")
public class SipDskEligibileFieldsController {

  private static final Logger logger = LoggerFactory
      .getLogger(SipDskEligibileFieldsController.class);

  @Autowired
  NSSOProperties nssoProperties;

  @Autowired
  DskEligibleFieldService dskEligibleFieldService;

  @ApiOperation(
      value = " Add DSK Eligible Fields ",
      nickname = "AddDskFields",
      notes = "Admin can only use this API to add DSK Eligible Fields",
      response = Valid.class)
  @RequestMapping(
      value = "/fields",
      method = RequestMethod.POST)
  @ResponseBody
  public Valid addDskEligibleFields(@RequestBody DskEligibleFields dskEligibleFields,
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    return dskEligibleFieldService
        .addDskEligibleFields(dskEligibleFields, request, response);
  }

  @ApiOperation(
      value = " Delete DSK Eligible Fields ",
      nickname = "DeleteDskFields",
      notes = "Admin can only use this API to add DSK Eligible Fields",
      response = Valid.class)
  @RequestMapping(
      value = "/fields",
      method = RequestMethod.DELETE)
  @ResponseBody
  public Valid deleteDskEligibleFields(
      @ApiParam(value = "semantic id", required = true)
      @RequestParam(name = "semanticId", required = true) String semanticId,
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    return dskEligibleFieldService
        .deleteDskEligibleFields(semanticId, request, response);
  }

    @ApiOperation(
        value = " Update DSK Eligible Fields ",
        nickname = "UpdateDskFields",
        notes = "Admin can only use this API to update DSK Eligible Fields",
        response = Valid.class)
    @RequestMapping(
        value = "/fields/{semanticId}",
        method = RequestMethod.PUT)
    @ResponseBody
    public Valid updateDskEligibleFields(HttpServletRequest request, HttpServletResponse response,
        @PathVariable(name = "semanticId") String semanticId,
        @RequestBody List<DskField> dskEligibleFields) throws IOException {
        return dskEligibleFieldService
            .updateDskEligibleFields(request, response, semanticId, dskEligibleFields);
    }
}

package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.model.DskEligibleFields;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.service.DskEligibleFieldService;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.util.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
      value = " Fetch Users API ",
      nickname = "FetchUsers",
      notes = "Admin can only use this API to fetch the users",
      response = Valid.class)
  @RequestMapping(
      value = "/fields",
      method = RequestMethod.POST)
  public Valid getUserList(@RequestBody DskEligibleFields dskEligibleFields,
      HttpServletRequest request, HttpServletResponse response) {
    return dskEligibleFieldService
        .AddDskEligibleFields(dskEligibleFields,request, response);
  }


}

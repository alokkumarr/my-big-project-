package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.properties.NSSOProperties;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "The controller provides operations related Module privileges "
    + "synchronoss analytics platform ")
@RequestMapping("/sip-security/auth/admin/modules")
public class SipDskEligibileFieldsController {

  private static final Logger logger = LoggerFactory
      .getLogger(SipDskEligibileFieldsController.class);

  @Autowired
  NSSOProperties nssoProperties;

  private final String AdminRole = "ADMIN";


}

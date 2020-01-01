package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.controller.ServerResponseMessages;
import com.sncr.saw.security.app.model.DskEligibleFields;
import com.sncr.saw.security.app.model.DskField;
import com.sncr.saw.security.app.model.DskFieldsInfo;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.DskEligibleFieldsRepository;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.util.JWTUtils;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.utils.SipCommonUtils;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DskEligibleFieldService {

  private static final Logger logger = LoggerFactory.getLogger(DskEligibleFieldService.class);

  @Autowired private DskEligibleFieldsRepository dskEligibleFieldsRepository;

  @Autowired NSSOProperties nssoProperties;

  private final String AdminRole = "ADMIN";

  public Valid addDskEligibleFields(
      DskEligibleFields dskEligibleFields, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Valid valid = new Valid();
    String jwtToken = JWTUtils.getToken(request);
    String[] valuesFromToken = JWTUtils.parseToken(jwtToken, nssoProperties.getJwtSecretKey());
    Long custId = Long.valueOf(valuesFromToken[1]);
    if (dskEligibleFields.getCustomerSysId() == null || dskEligibleFields.getCustomerSysId() == 0) {
      response.sendError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
      valid.setValid(Boolean.FALSE);
      valid.setError("Customer Id can't be null or 0");
      logger.error("Customer Sys Id can't be null or empty :");
      return valid;
    }
    if (custId != dskEligibleFields.getCustomerSysId()) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      valid.setValid(Boolean.FALSE);
      valid.setError("UnAuthorized Operation!! Contact Admin!");
      logger.error("UnAuthorized Operation!. Invalid customerId.");
      return valid;
    }
    if (!valuesFromToken[3].equalsIgnoreCase(AdminRole)) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      valid.setValid(Boolean.FALSE);
      valid.setError("You are not authorized to perform this operation, contact ADMIN!");
      logger.error("UnAuthorized operation!. Not a Admin Role.");
      return valid;
    }

    if (dskEligibleFields.getProductSysId() == null || dskEligibleFields.getProductSysId() == 0) {
      response.sendError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
      valid.setValid(Boolean.FALSE);
      valid.setError("Product Id can't be null or 0");
      logger.error("Product Id can't be null or 0");
      return valid;
    }

    if (StringUtils.isEmpty(dskEligibleFields.getSemanticId())) {
      response.sendError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
      valid.setValid(Boolean.FALSE);
      valid.setError("semantic Id can't be null or empty");
      logger.error("semantic Id can't be null or empty");
      return valid;
    }

    return dskEligibleFieldsRepository.createDskEligibleFields(dskEligibleFields);
  }

  public Valid deleteDskEligibleFields(
      String semanticId, HttpServletRequest request, HttpServletResponse response) {
    Valid valid = new Valid();
      Ticket ticket = SipCommonUtils.getTicket(request);

    Long customerSysId = Long.valueOf(ticket.getCustID());
    Long defaultProdID = Long.valueOf(ticket.getDefaultProdID());
    if (StringUtils.isEmpty(semanticId)) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      valid.setValid(Boolean.FALSE);
      valid.setError("semantic Id can't be null or empty");
      logger.error("semantic Id can't be null or empty");
      return valid;
    }
    return dskEligibleFieldsRepository
        .deleteDskEligibleFields(customerSysId, defaultProdID, semanticId);
  }

  public Valid updateDskEligibleFields(
      DskEligibleFields dskEligibleFields, HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    return dskEligibleFieldsRepository.updateDskFields(dskEligibleFields);
  }

  public DskFieldsInfo fetchAllDskEligibleFields(Long customerSysId, Long defaultProdID) {
    DskFieldsInfo dskEligibleFields =
        dskEligibleFieldsRepository.fetchAllDskEligibleFields(customerSysId, defaultProdID);

    return dskEligibleFields;
  }
}

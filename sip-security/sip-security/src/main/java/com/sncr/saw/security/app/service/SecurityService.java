package com.sncr.saw.security.app.service;

import com.google.common.base.Preconditions;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.User;
import com.sncr.saw.security.common.bean.repo.admin.UsersList;
import com.sncr.saw.security.common.constants.ErrorMessages;
import com.sncr.saw.security.common.util.SecurityUtils;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

  @Autowired private UserRepository userRepository;

  /**
   * Validate customerId to avoid direct reference.
   *
   * @param ticket ticket from customer
   * @param customerId customer id from body
   * @return true if id matched.
   */
  public boolean haveValidCustomerId(Ticket ticket, Long customerId) {
    return !ticket.getCustID().isEmpty() && Long.valueOf(ticket.getCustID()).equals(customerId);
  }

  public UsersList validateUserDetails(User userDetails) {

    UsersList userDetailsResponse = new UsersList();

    if (userDetails == null) {
      return buildInvalidResponse(String.format(ErrorMessages.nullErrorMessage, "Request Body"));
    }
    if (StringUtils.isBlank(userDetails.getMasterLoginId())) {
      return buildInvalidResponse(
          String.format(ErrorMessages.nullOrEmptyErrorMessage, "masterLoginId"));
    }
    if (StringUtils.isBlank(userDetails.getEmail())) {
      return buildInvalidResponse(String.format(ErrorMessages.nullOrEmptyErrorMessage, "email"));
    }
    if (StringUtils.isBlank(userDetails.getFirstName())) {
      return buildInvalidResponse(
          String.format(ErrorMessages.nullOrEmptyErrorMessage, "firstName"));
    }
    if (StringUtils.isBlank(userDetails.getLastName())) {
      return buildInvalidResponse(String.format(ErrorMessages.nullOrEmptyErrorMessage, "lastName"));
    }
    if (userDetails.getActiveStatusInd() == null) {
      return buildInvalidResponse(String.format(ErrorMessages.nullErrorMessage, "activeStatusInd"));
    }
    if (userDetails.getRoleId() == null) {
      return buildInvalidResponse(String.format(ErrorMessages.nullOrEmptyErrorMessage, "roleId"));
    }
    if (!SecurityUtils.isValidMasterLoginId(userDetails.getMasterLoginId())) {
      return buildInvalidResponse(String.format(ErrorMessages.invalidMessage, "MasterLoginId"));
    }

    if (!SecurityUtils.isEmailValid(userDetails.getEmail())) {
      return buildInvalidResponse(String.format(ErrorMessages.invalidMessage, "Email"));
    }
    return userDetailsResponse;
  }

  private UsersList buildInvalidResponse(String message) {
    UsersList userDetailsResponse = new UsersList();
    userDetailsResponse.setValid(false);
    userDetailsResponse.setValidityMessage(message);
    logger.error("Invalid User input:{}", message);
    return userDetailsResponse;
  }
}

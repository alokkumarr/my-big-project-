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
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.nullErrorMessage, "Request Body"));
      return userDetailsResponse;
    }
    if (StringUtils.isBlank(userDetails.getMasterLoginId())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.nullOrEmptyErrorMessage, "masterLoginId"));
      return userDetailsResponse;
    }
    if (StringUtils.isBlank(userDetails.getEmail())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.nullOrEmptyErrorMessage, "email"));
      return userDetailsResponse;
    }
    if (StringUtils.isBlank(userDetails.getFirstName())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.nullOrEmptyErrorMessage, "firstName"));
      return userDetailsResponse;
    }
    if (StringUtils.isBlank(userDetails.getLastName())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.nullOrEmptyErrorMessage, "lastName"));
      return userDetailsResponse;
    }
    if (userDetails.getActiveStatusInd() == null) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.nullErrorMessage, "activeStatusInd"));
      return userDetailsResponse;
    }

    if (!SecurityUtils.isValidName(userDetails.getFirstName())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.invalidMessage, "FirstName"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "FirstName"));
      return userDetailsResponse;
    }
    if (!SecurityUtils.isValidName(userDetails.getLastName())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.invalidMessage, "LastName"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "LastName"));
      return userDetailsResponse;
    }
    String middleName = userDetails.getMiddleName();
    if (middleName != null) {
      if (!SecurityUtils.isValidName(middleName)) {
        userDetailsResponse.setValid(false);
        userDetailsResponse.setValidityMessage(
            String.format(ErrorMessages.invalidMessage, "MiddleName"));
        logger.debug(String.format(ErrorMessages.invalidMessage, "MiddleName"));
        return userDetailsResponse;
      }
    }
    if (!SecurityUtils.isValidMasterLoginId(userDetails.getMasterLoginId())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.invalidMessage, "MasterLoginId"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "MasterLoginId"));
      return userDetailsResponse;
    }

    if (!SecurityUtils.isEmailValid(userDetails.getEmail())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(String.format(ErrorMessages.invalidMessage, "Email"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "Email"));
      return userDetailsResponse;
    }
    return userDetailsResponse;
  }
}

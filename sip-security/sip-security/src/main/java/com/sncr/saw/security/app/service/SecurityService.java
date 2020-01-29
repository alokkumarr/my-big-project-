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
    validateUser(userDetails);

    UsersList userDetailsResponse = new UsersList();
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

    if (!!SecurityUtils.isEmailValid(userDetails.getEmail())) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(String.format(ErrorMessages.invalidMessage, "Email"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "Email"));
      return userDetailsResponse;
    }
    return userDetailsResponse;
  }

  private void validateUser(User userDetails) {

    Preconditions.checkNotNull(
        userDetails, String.format(ErrorMessages.nullErrorMessage, "Request Body"));
    Preconditions.checkState(
        StringUtils.isNotBlank(userDetails.getMasterLoginId()),
        String.format(ErrorMessages.nullOrEmptyErrorMessage, "masterLoginId"));
    Preconditions.checkState(
        StringUtils.isNotBlank(userDetails.getEmail()),
        String.format(ErrorMessages.nullOrEmptyErrorMessage, "email"));
    Preconditions.checkState(
        StringUtils.isNotBlank(userDetails.getFirstName()),
        String.format(ErrorMessages.nullOrEmptyErrorMessage, "firstName"));
    Preconditions.checkState(
        StringUtils.isNotBlank(userDetails.getLastName()),
        String.format(ErrorMessages.nullOrEmptyErrorMessage, "lastName"));
    Preconditions.checkState(
        StringUtils.isNotBlank(userDetails.getRoleName()),
        String.format(ErrorMessages.nullOrEmptyErrorMessage, "roleName"));
    Preconditions.checkNotNull(
        userDetails.getActiveStatusInd(),
        String.format(ErrorMessages.nullErrorMessage, "activeStatusInd"));
  }
}

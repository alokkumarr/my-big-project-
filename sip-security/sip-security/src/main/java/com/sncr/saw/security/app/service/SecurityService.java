package com.sncr.saw.security.app.service;

import com.google.common.base.Preconditions;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.UserDetails;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.admin.UserDetailsResponse;
import com.sncr.saw.security.common.bean.repo.admin.UsersDetailsList;
import com.sncr.saw.security.common.constants.ErrorMessages;
import com.sncr.saw.security.common.util.PasswordValidation;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
  String namePattern = "^[a-zA-Z]*$";
  String loginIdPattern = "^[A-z\\d_@.#$=!%^)(\\]:\\*;\\?\\/\\,}{'\\|<>\\[&\\+-`~]*$";
  String emailPattern = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

  @Autowired private UserRepository userRepository;

  /**
   * Create user with dsk.
   *
   * @param userDetails UserDetails
   * @param masterLoginId masterLoginId
   * @param loginCustomerId
   * @param response HttpServletResponse
   * @return Returns UserDetailsResponse
   */
  public UserDetailsResponse addUserDetails(UserDetails userDetails, String masterLoginId,
      Long loginCustomerId, HttpServletResponse response) {
    logger.trace("User details body :{}", userDetails);
    validateUserDetails(userDetails);
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    if (!userDetails.getFirstName().matches(namePattern)) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.invalidMessage, "FirstName"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "FirstName"));
      return userDetailsResponse;
    }
    if (!userDetails.getLastName().matches(namePattern)) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.invalidMessage, "LastName"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "LastName"));
      return userDetailsResponse;
    }
    String middleName=userDetails.getMiddleName();
    if (middleName != null) {
      if (!middleName.matches(namePattern)) {
        userDetailsResponse.setValid(false);
        userDetailsResponse.setValidityMessage(
            String.format(ErrorMessages.invalidMessage, "MiddleName"));
        logger.debug(String.format(ErrorMessages.invalidMessage, "MiddleName"));
        return userDetailsResponse;
      }
    }
    if (!userDetails.getMasterLoginId().matches(loginIdPattern)) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.invalidMessage, "MasterLoginId"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "MasterLoginId"));
      return userDetailsResponse;
    }

    if (!userDetails.getEmail().matches(emailPattern)) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(String.format(ErrorMessages.invalidMessage, "Email"));
      logger.debug(String.format(ErrorMessages.invalidMessage, "Email"));
      return userDetailsResponse;
    }
    Long customerSysId = userRepository.getCustomerSysid(userDetails.getCustomerCode());
    if (customerSysId != loginCustomerId) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(ErrorMessages.unAuthorizedMessage);
      logger.debug(ErrorMessages.unAuthorizedMessage, "Email");
      return userDetailsResponse;
    }
    if (customerSysId == null) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.notExistErrorMessage, "customerCode"));
      logger.debug(String.format(ErrorMessages.notExistErrorMessage, "customerCode"));
      return userDetailsResponse;
    }
    String securityGroupName = userDetails.getSecurityGroupName();
    Long groupSysId = null;
    if (securityGroupName != null) {
      groupSysId = userRepository.getSecurityGroupSysid(securityGroupName);
      if (groupSysId == null) {
        userDetailsResponse.setValid(false);
        userDetailsResponse.setValidityMessage(
            String.format(ErrorMessages.notExistErrorMessage, "securityGroupName"));
        logger.debug(String.format(ErrorMessages.notExistErrorMessage, "securityGroupName"));
        return userDetailsResponse;
      }
    }
    Long roleSysId = userRepository.getRoleSysId(userDetails.getRoleName());
    if (roleSysId == null) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.notExistErrorMessage, "roleName"));
      logger.debug(String.format(ErrorMessages.notExistErrorMessage, "roleName"));
      return userDetailsResponse;
    }
    if (StringUtils.isBlank(userDetails.getPassword())) {
      logger.debug("setting random password");
      userDetails.setPassword(generateRandomPassowrd());
    }
    Valid valid = null;

    try {
      Valid validity =
          PasswordValidation.validatePassword(
              userDetails.getPassword(), userDetails.getMasterLoginId());
      userDetailsResponse.setValid(validity.getValid());
      userDetailsResponse.setValidityMessage(validity.getValidityMessage());
      userDetails.setCustomerId(customerSysId);
      userDetails.setRoleId(roleSysId);
      userDetails.setSecGroupSysId(groupSysId);
      if (userDetailsResponse.getValid()) {
        valid = userRepository.addUserDetails(userDetails, masterLoginId);
        if (valid.getValid()) {
          userDetailsResponse.setUser(
              userRepository.getUser(userDetails.getMasterLoginId(), userDetails.getCustomerId()));
          userDetailsResponse.setValid(true);
        } else {
          logger.debug("Error occurred while getting user details:{}", valid.getError());
          userDetailsResponse.setValid(false);
          userDetailsResponse.setValidityMessage(valid.getError());
        }
      }
    } catch (Exception e) {
      userDetailsResponse.setValid(false);
      logger.debug("Error occurred while adding user details:{}", e);
      String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
      userDetailsResponse.setValidityMessage(message + " Please contact server Administrator");
      userDetailsResponse.setError(e.getMessage());
      return userDetailsResponse;
    }
    return userDetailsResponse;
  }

  /**
   * Fetches all users.
   *
   * @param customerId customerId
   * @return Returns UserDetailsResponse
   */
  public UsersDetailsList getUsersDetailList(Long customerId) {
    UsersDetailsList usersDetailsListResponse = new UsersDetailsList();
    logger.trace("Getting users for customer id :{}", customerId);
    try {
      List<UserDetails> userDetailsList = userRepository.getUsersDetailList(customerId);
      usersDetailsListResponse.setUsers(userDetailsList);
      usersDetailsListResponse.setRecordCount(userDetailsList.size());
      usersDetailsListResponse.setValid(true);
    } catch (Exception e) {
      usersDetailsListResponse.setValid(false);
      logger.debug("Error occurred while getting user details:{}", e);
      String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
      usersDetailsListResponse.setValidityMessage(message + " Please contact server Administrator");
      usersDetailsListResponse.setError(e.getMessage());
      return usersDetailsListResponse;
    }
    return usersDetailsListResponse;
  }

  private void validateUserDetails(UserDetails userDetails) {
    Preconditions.checkNotNull(
        userDetails, String.format(ErrorMessages.nullErrorMessage, "Request Body"));
    Preconditions.checkState(
        StringUtils.isNotBlank(userDetails.getCustomerCode()),
        String.format(ErrorMessages.nullOrEmptyErrorMessage, "customercode"));
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

  private String generateRandomPassowrd() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
    String totalChars = RandomStringUtils.randomAlphanumeric(2);
    return new StringBuilder(upperCaseLetters)
        .append(lowerCaseLetters)
        .append(numbers)
        .append(specialChar)
        .append(totalChars)
        .toString();
  }
}

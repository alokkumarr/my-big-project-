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
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
  @Autowired private UserRepository userRepository;

  /**
   * Create user with dsk.
   *
   * @param masterLoginId masterLoginId
   * @param userDetails UserDetails
   * @return Returns UserDetailsResponse
   */
  public UserDetailsResponse addUserDetails(UserDetails userDetails, String masterLoginId) {
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    validateUserDetails(userDetails);
    Long customerSysId = userRepository.getCustomerSysid(userDetails.getCustomerCode());
    if (customerSysId == null) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.notExistErrorMessage, "customerCode"));
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
        return userDetailsResponse;
      }
    }
    Long roleSysId = userRepository.getRoleSysId(userDetails.getRoleName());
    if (roleSysId == null) {
      userDetailsResponse.setValid(false);
      userDetailsResponse.setValidityMessage(
          String.format(ErrorMessages.notExistErrorMessage, "roleName"));
      return userDetailsResponse;
    }
    if (StringUtils.isBlank(userDetails.getPassword())) {
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
          userDetailsResponse.setValid(false);
          userDetailsResponse.setValidityMessage(valid.getError());
        }
      }
    } catch (Exception e) {
      userDetailsResponse.setValid(false);
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
    try {
      List<UserDetails> userDetailsList = userRepository.getUsersDetailList(customerId);
      usersDetailsListResponse.setUsers(userDetailsList);
      usersDetailsListResponse.setRecordCount(userDetailsList.size());
      usersDetailsListResponse.setValid(true);
    } catch (Exception e) {
      usersDetailsListResponse.setValid(false);
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

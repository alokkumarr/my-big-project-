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

import com.synchronoss.bda.sip.jwt.token.Ticket;
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
}

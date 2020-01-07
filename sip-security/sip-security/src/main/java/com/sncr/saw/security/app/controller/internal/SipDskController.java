package com.sncr.saw.security.app.controller.internal;

import com.sncr.saw.security.app.repository.DataSecurityKeyRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.UserDetails;
import com.synchronoss.bda.sip.dsk.DskGroupPayload;
import com.synchronoss.bda.sip.dsk.DskDetails;
import com.synchronoss.bda.sip.jwt.token.DataSecurityKeys;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/internal/sip-security/dsk")
public class SipDskController {
  private static final Logger logger = LoggerFactory.getLogger(SipDskController.class);

  @Autowired private UserRepository userRepository;

  @Autowired DataSecurityKeyRepository dataSecurityKeyRepository;

  /**
   * Fetch the data security details by the master login id
   *
   * @param userId
   * @return details of dsk
   */
  @RequestMapping(value = "/fetch", method = RequestMethod.GET)
  @ApiOperation(
      value = "get dsk details for a user",
      nickname = "dskDetailsByUser",
      notes = "",
      response = DskDetails.class)
  public DskDetails dskDetailsByUser(
      @ApiParam(value = "master login id", required = true) @RequestParam(value = "userId")
          String userId,
      HttpServletResponse response) {
    DskDetails dskGroupResponse = new DskDetails();
    try {
      DskGroupPayload dskGroupPayload;
      if (userId == null || userId.isEmpty()) {
        response.setStatus(400);
        dskGroupResponse.setMessage("User Id can't be null or blank");
        return dskGroupResponse;
      }
      UserDetails userDetails = userRepository.getUserById(userId);
      if (userDetails == null) {
        dskGroupResponse.setMessage("User not found");
        return dskGroupResponse;
      }
      Map<String, String> customerDetails = userRepository.getCustomerDetails(userId);
      dskGroupResponse.setCustomerCode(customerDetails.get("customerCode"));
      dskGroupResponse.setIsJvCustomer(Integer.parseInt(customerDetails.get("isJVCustomer")));
      dskGroupResponse.setFilterByCustomerCode(
          Integer.parseInt(customerDetails.get("filterByCustomerCode")));
      Long secGroupSysId = userDetails.getSecGroupSysId();
      if (secGroupSysId != null) {
        dskGroupPayload =
            dataSecurityKeyRepository.fetchDskGroupAttributeModel(
                secGroupSysId, userDetails.getCustomerId());
        dskGroupResponse.setDskGroupPayload(dskGroupPayload);
        dskGroupResponse.setMessage("success");
        logger.trace("DSK for user is :{}", dskGroupResponse);
        return dskGroupResponse;
      } else {
        dskGroupResponse.setMessage("DSK doesn't exist for the user");
        return dskGroupResponse;
      }
    } catch (Exception e) {
      logger.error("Exception occured while fetching dsk for user:{}", e);
      dskGroupResponse.setMessage("Exception occured while fetching dsk for user" + e);
      return dskGroupResponse;
    }
  }
}

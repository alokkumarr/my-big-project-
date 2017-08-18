package com.synchronoss.saw.composite.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import com.synchronoss.saw.composite.model.ChangePasswordDetails;
import com.synchronoss.saw.composite.model.LoginDetails;
import com.synchronoss.saw.composite.model.LoginResponse;
import com.synchronoss.saw.composite.model.ResetPwdDtls;
import com.synchronoss.saw.composite.model.Valid;

/**
 * This interface will define the specification<br/>
 * for saw security layer mediation API for the sub-systems.<br>
 * This Feign client is client for the Security layer
 * 
 * @author saurav.paul
 *
 */

// TODO: verify about the Fall back implementation
// @FeignClient(name="saw-security-service")
public interface SAWSecurityServiceInterface {


  @RequestMapping(method = RequestMethod.POST, value = "/saw-security/doAuthenticate",
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public LoginResponse login(@RequestBody LoginDetails loginDetails);

  @RequestMapping(method = RequestMethod.POST, value = "/saw-security/auth/doLogout",
      produces = "application/json")
  public String logout(@RequestBody String ticketID) throws HttpClientErrorException;

  @RequestMapping(method = RequestMethod.POST, value = "/saw-security/auth/changePassword",
      produces = "application/json")
  public Valid change(@RequestBody ChangePasswordDetails changePasswordDetails)
      throws HttpClientErrorException;

  @RequestMapping(method = RequestMethod.POST, value = "/saw-security/resetPassword",
      produces = "application/json")
  public Valid reset(@RequestBody ResetPwdDtls resetPwdDtls) throws HttpClientErrorException;

  @RequestMapping(method = RequestMethod.POST, value = "/saw-security/auth/validateToken",
      produces = "application/json")
  public Valid validate(@RequestBody LoginDetails loginDetails) throws HttpClientErrorException;

  @RequestMapping(method = RequestMethod.POST, value = "/saw-security/getDefaults",
      produces = "application/json")
  public LoginResponse defaults(@RequestBody LoginDetails loginDetails)
      throws HttpClientErrorException;

}

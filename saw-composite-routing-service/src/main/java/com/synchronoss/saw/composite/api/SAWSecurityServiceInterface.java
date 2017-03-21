package com.synchronoss.saw.composite.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.synchronoss.saw.composite.exceptions.SecurityModuleSAWException;
import com.synchronoss.saw.composite.exceptions.TokenMissingSAWException;
import com.synchronoss.saw.composite.fallback.SAWSecurityServiceFallbackImpl;
import com.synchronoss.saw.composite.model.ChangePasswordDetails;
import com.synchronoss.saw.composite.model.LoginDetails;
import com.synchronoss.saw.composite.model.LoginResponse;
import com.synchronoss.saw.composite.model.ResetPwdDtls;
import com.synchronoss.saw.composite.model.Valid;

/**
 * This interface will define the specification<br/>
 * for saw security layer mediation API for the sub-systems.<br>
 * This Feign client is client for the Security layer
 * @author saurav.paul
 *
 */

 // TODO: verify about the Fall back implementation
@FeignClient(value= "saw-security", fallback = SAWSecurityServiceFallbackImpl.class)
public interface SAWSecurityServiceInterface {
	
	
	@RequestMapping (method = RequestMethod.POST, value = "/doAuthenticate", produces = "application/json")
	public ResponseEntity<LoginResponse> login (@RequestBody LoginDetails loginDetails) throws SecurityModuleSAWException, TokenMissingSAWException;
	
	@RequestMapping (method = RequestMethod.POST, value = "/auth/doLogout", produces = "application/json")
	public ResponseEntity<String> logout (@RequestBody String ticketID) throws SecurityModuleSAWException,TokenMissingSAWException;
	
	@RequestMapping (method = RequestMethod.POST, value = "/auth/changePassword", produces = "application/json")
	public ResponseEntity<Valid> change (@RequestBody ChangePasswordDetails changePasswordDetails)throws SecurityModuleSAWException, TokenMissingSAWException;

	@RequestMapping (method = RequestMethod.POST, value = "/resetPassword", produces = "application/json")
	public ResponseEntity<Valid> reset (@RequestBody ResetPwdDtls resetPwdDtls)throws SecurityModuleSAWException, TokenMissingSAWException;

	@RequestMapping (method = RequestMethod.POST, value = "/auth/validateToken", produces = "application/json")
	public ResponseEntity<Valid> validate (@RequestBody LoginDetails loginDetails)throws SecurityModuleSAWException, TokenMissingSAWException;


}

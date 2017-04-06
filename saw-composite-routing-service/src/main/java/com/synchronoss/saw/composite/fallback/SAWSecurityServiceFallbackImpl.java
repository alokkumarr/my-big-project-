package com.synchronoss.saw.composite.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.synchronoss.saw.composite.api.SAWSecurityServiceInterface;
import com.synchronoss.saw.composite.exceptions.SecurityModuleSAWException;
import com.synchronoss.saw.composite.exceptions.TokenMissingSAWException;
import com.synchronoss.saw.composite.model.ChangePasswordDetails;
import com.synchronoss.saw.composite.model.LoginDetails;
import com.synchronoss.saw.composite.model.LoginResponse;
import com.synchronoss.saw.composite.model.ResetPwdDtls;
import com.synchronoss.saw.composite.model.Valid;

public class SAWSecurityServiceFallbackImpl {

	private static final Logger LOG = LoggerFactory.getLogger(SAWSecurityServiceFallbackImpl.class);

	
	public LoginResponse login(LoginDetails loginDetails) throws HttpClientErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	



}

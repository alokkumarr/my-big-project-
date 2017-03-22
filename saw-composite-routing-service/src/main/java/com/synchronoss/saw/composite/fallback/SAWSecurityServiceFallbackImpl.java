package com.synchronoss.saw.composite.fallback;

import org.springframework.http.ResponseEntity;

import com.synchronoss.saw.composite.api.SAWSecurityServiceInterface;
import com.synchronoss.saw.composite.exceptions.SecurityModuleSAWException;
import com.synchronoss.saw.composite.exceptions.TokenMissingSAWException;
import com.synchronoss.saw.composite.model.ChangePasswordDetails;
import com.synchronoss.saw.composite.model.LoginDetails;
import com.synchronoss.saw.composite.model.LoginResponse;
import com.synchronoss.saw.composite.model.ResetPwdDtls;
import com.synchronoss.saw.composite.model.Valid;

public class SAWSecurityServiceFallbackImpl implements SAWSecurityServiceInterface{

	@Override
	public ResponseEntity<LoginResponse> login(LoginDetails loginDetails)
			throws SecurityModuleSAWException, TokenMissingSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> logout(String ticketID) throws SecurityModuleSAWException, TokenMissingSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Valid> change(ChangePasswordDetails changePasswordDetails)
			throws SecurityModuleSAWException, TokenMissingSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Valid> reset(ResetPwdDtls resetPwdDtls)
			throws SecurityModuleSAWException, TokenMissingSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Valid> validate(LoginDetails loginDetails)
			throws SecurityModuleSAWException, TokenMissingSAWException {
		// TODO Auto-generated method stub
		return null;
	}


}

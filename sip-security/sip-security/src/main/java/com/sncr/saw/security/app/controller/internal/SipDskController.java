package com.sncr.saw.security.app.controller.internal;

import com.sncr.saw.security.app.repository.UserRepository;
import com.synchronoss.bda.sip.jwt.token.DataSecurityKeys;
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

	@Autowired
	private UserRepository userRepository;

	/**
	 * Fetch the data security details by the master login id
	 *
	 * @param userId
	 * @return details of dsk
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public DataSecurityKeys dskDetailsByUserId(@RequestParam(value = "userId") String userId, HttpServletResponse response) {
		DataSecurityKeys securityKeys = new DataSecurityKeys();
		if (userId == null || userId.isEmpty()) {
			response.setStatus(400);
			securityKeys.setMessage("User Id can't be null or blank");
			return securityKeys;
		}
		return userRepository.fetchDSKDetailByUserId(userId);
	}
}

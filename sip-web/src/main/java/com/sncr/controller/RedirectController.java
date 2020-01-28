package com.sncr.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan
@Configuration
public class RedirectController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RedirectController.class);
	
	private final static String LOGIN_PAGE="login.html";
		
	@RequestMapping(value = {"/","/observe","/alerts","/analyze"}, method = RequestMethod.GET)
	public void redirectToLogin(HttpServletRequest request, HttpServletResponse response){
		LOGGER.debug("RedirectController - RedirectController - START");
	
		try {
			request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
		} catch (Exception e2) {
			// error
			LOGGER.error("Error while login page {}.", e2.getMessage());
		}
		
   }	
}

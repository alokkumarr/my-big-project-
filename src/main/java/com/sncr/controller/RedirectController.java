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
	private static final Logger logger = LoggerFactory
			.getLogger(RedirectController.class);
	
	private final String sawLoginPage="login.html";
		
	@RequestMapping(value = {"/","/observe","/alerts","/analyze"}, method = RequestMethod.GET)
	public void redirectToLogin(HttpServletRequest request, HttpServletResponse response){
		logger.debug("RedirectController - RedirectController - START");
	
		try {
			request.getRequestDispatcher(sawLoginPage).forward(request, response);
		} catch (Exception e2) {
			// error 
			e2.printStackTrace();
		}
		
   }	
}

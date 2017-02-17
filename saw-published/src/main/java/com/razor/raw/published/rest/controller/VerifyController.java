package com.razor.raw.published.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sunil.belakeri
 *
 * 
 */
@RestController
public class VerifyController {

	@RequestMapping(value = "/verifyAppStatus")
	public void verifyAppStatus()
	{
		return;
	}
	
}

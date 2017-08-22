package com.synchronoss.saw.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.synchronoss.saw.gateway.SAWGatewayProperties;
import com.synchronoss.saw.gateway.ServiceUtils;
import com.synchronoss.saw.gateway.aspects.RoutingLogging;
import com.synchronoss.saw.gateway.exceptions.SecurityModuleSAWException;

/**
 * This class is the integration & universal response object<br>
 * to the user interface. This class is the implementation of actual<br>
 * integrated API<br>
 * 
 * @author saurav.paul
 * @version 1.0
 */

@RestController
public class GatewayServiceController {

	private static final Logger LOG = LoggerFactory.getLogger(GatewayServiceController.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUtils serviceUtils;

	
	@Autowired
	private SAWGatewayProperties sawCompositeProperties;
	
	@RoutingLogging
		public ResponseEntity<?> menu(@RequestBody String payload, @RequestHeader HttpHeaders headers) 
	{

		// TODO : For time being it's true
		boolean validity_token = true;
		
		String default_uri = "/analysis";
		String token;
		ResponseEntity<?> response = null;
		ResponseEntity<?> responseSecurity = null;
		String security_url = null;
				//serviceUtils.getSAWAddress(this.loadBalancerClient, sawCompositeProperties.getSecurityContext()) + "/auth/validateToken";
		LOG.info("security_url():" + security_url);
		
		try 
		{
			//Need to send request for validity with token on headers
			// If status code 500 then token expired i.e not valid
			// message is "Token has expired. Please re-login".
			
			HttpEntity<String> request = new HttpEntity<String>("token", headers);
			responseSecurity = restTemplate.exchange(security_url, HttpMethod.POST, request, String.class);
			int statusCode = responseSecurity.getStatusCodeValue();
			LOG.info(String.valueOf(statusCode));
			//token = headers.get
			
			
			
			// check for the validity l
			// get the defaults
			// Pay load set the keys module, customer_code, role_type,
			// data_security_key as part of
			// content, type=menu

		} catch (HttpClientErrorException ex) {
			throw new SecurityModuleSAWException(ex.getMessage());
		}

		return null;
	}

	@RoutingLogging
	public ResponseEntity<?> menu(@RequestBody String payload) 
	{

		// TODO : For time being it's true
		boolean validity_token = true;
		String default_uri = "/analyze";
		String token;
		ResponseEntity<?> response = null;
		String security_url = null;
		//serviceUtils.getSAWAddress(this.loadBalancerClient, sawCompositeProperties.getSecurityContext()) + "/doAuthenticate";
		LOG.info("security_url():" + security_url);

		try 
		{
			
			// check for the validity l
			// get the defaults
			// Pay load set the keys module, customer_code, role_type,
			// data_security_key as part of
			// content, type=menu

		} catch (HttpClientErrorException ex) {
			throw new SecurityModuleSAWException(ex.getMessage());
		}

		return null;
	}


	@RoutingLogging
	@RequestMapping(method = RequestMethod.POST, value = "/menu/categories")
	public ResponseEntity<?> menuCategories(@RequestBody String payload) 
	{

		boolean validity_token = true;
		String default_uri = "/analyze";
		String token;
		ResponseEntity<?> response = null;
		String security_url = null;
				//serviceUtils.getSAWAddress(this.loadBalancerClient, sawCompositeProperties.getSecurityContext()) + "/doAuthenticate";
		LOG.info("security_url():" + security_url);

		try 
		{
			
			// check for the validity l
			// get the defaults
			// Pay load set the keys module, customer_code, role_type,
			// data_security_key as part of
			// content, type=menu

		} catch (HttpClientErrorException ex) {
			throw new SecurityModuleSAWException(ex.getMessage());
		}

		return null;
	}

	
}

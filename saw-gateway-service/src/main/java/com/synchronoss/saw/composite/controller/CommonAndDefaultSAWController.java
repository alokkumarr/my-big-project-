package com.synchronoss.saw.composite.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.synchronoss.saw.composite.SAWCompositeProperties;
import com.synchronoss.saw.composite.ServiceUtils;
import com.synchronoss.saw.composite.api.SAWCompositeServiceInterface;
import com.synchronoss.saw.composite.aspects.RoutingLogging;
import com.synchronoss.saw.composite.exceptions.SecurityModuleSAWException;
import com.synchronoss.saw.composite.exceptions.TokenValidationSAWException;
import com.synchronoss.saw.composite.model.LoginDetails;
import com.synchronoss.saw.composite.model.LoginResponse;
import com.synchronoss.saw.composite.model.RoutingPayload;
import com.synchronoss.saw.composite.model.Valid;

/**
 * This class is the integration & universal response object<br>
 * to the user interface. This class is the implementation of actual<br>
 * integrated API<br>
 * 
 * @author saurav.paul
 * @version 1.0
 */

// TODO : Non Blocking asynchronous implementation
// TODO : Feign Client & Zuul needs to tested for time being it has been deactivated


@RestController
public class CommonAndDefaultSAWController {

	private static final Logger LOG = LoggerFactory.getLogger(CommonAndDefaultSAWController.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUtils serviceUtils;

	@Autowired
	@Qualifier("saw-composite-api")
	private SAWCompositeServiceInterface sawCompositeServiceInterface;

	@Autowired
	private SAWCompositeProperties sawCompositeProperties;
	
	//@Autowired
	//private final LoadBalancerClient loadBalancerClient;

	

	//@HystrixCommand(fallbackMethod = "defaultMenu")
	@RoutingLogging
	@RequestMapping(method = RequestMethod.POST, value = "/menu")
	public RoutingPayload menu(@RequestBody RoutingPayload payload, @RequestHeader HttpHeaders headers) 
	{

		// TODO : For time being it's true
		boolean validity_token = true;
		
		String default_uri = "/analysis";
		String token;
		ResponseEntity<RoutingPayload> response = null;
		ResponseEntity<Valid> responseSecurity = null;
		String security_url = null;
				//serviceUtils.getSAWAddress(this.loadBalancerClient, sawCompositeProperties.getSecurityContext()) + "/auth/validateToken";
		LOG.info("security_url():" + security_url);
		
		try 
		{
			//Need to send request for validity with token on headers
			// If status code 500 then token expired i.e not valid
			// message is "Token has expired. Please re-login".
			
			HttpEntity<String> request = new HttpEntity<String>("token", headers);
			responseSecurity = restTemplate.exchange(security_url, HttpMethod.POST, request, Valid.class);
			int statusCode = responseSecurity.getStatusCodeValue();
			LOG.info(String.valueOf(statusCode));
			//token = headers.get
			
			if (responseSecurity.getBody().getValid())
			{
				
			}
			else 
			{
				throw new TokenValidationSAWException("Username & Password is invalid");
			}
			
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

	/**
	 * This method is fallback implementation of /menu endpoints
	 * @param loginDetails
	 * @return
	 */
	public RoutingPayload defaultMenu(@RequestBody LoginDetails loginDetails) {
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken("Here I am your Token");
		return null;
	}
	
	
	//@HystrixCommand(fallbackMethod = "defaultMenuItems")
	@RoutingLogging
	@RequestMapping(method = RequestMethod.POST, value = "/menuItems")
	public LoginResponse menu(@RequestBody RoutingPayload payload) 
	{

		// TODO : For time being it's true
		boolean validity_token = true;
		String default_uri = "/analyze";
		String token;
		ResponseEntity<LoginResponse> response = null;
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

	/**
	 * This method is fallback implementation of /menu endpoints
	 * @param loginDetails
	 * @return
	 */
	public RoutingPayload defaultMenuItems(@RequestBody RoutingPayload payload) {
		return null;
	}

	//@HystrixCommand(fallbackMethod = "defaultMenuCategories")
	@RoutingLogging
	@RequestMapping(method = RequestMethod.POST, value = "/menu/categories")
	public LoginResponse menuCategories(@RequestBody RoutingPayload payload) 
	{

		// TODO : For time being it's true
		boolean validity_token = true;
		String default_uri = "/analyze";
		String token;
		ResponseEntity<LoginResponse> response = null;
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

	/**
	 * This method is fallback implementation of /menu/categories endpoints
	 * @param loginDetails
	 * @return
	 */
	public RoutingPayload defaultMenuCategories(@RequestBody RoutingPayload payload) {
		return null;
	}
	
}

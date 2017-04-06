package com.synchronoss.saw.composite.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.synchronoss.saw.composite.SAWCompositeProperties;
import com.synchronoss.saw.composite.ServiceUtils;
import com.synchronoss.saw.composite.api.SAWCompositeServiceInterface;
import com.synchronoss.saw.composite.aspects.RoutingLogging;
import com.synchronoss.saw.composite.exceptions.SecurityModuleSAWException;
import com.synchronoss.saw.composite.model.LoginDetails;
import com.synchronoss.saw.composite.model.LoginResponse;

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
public class CompositeSAWController {

	private static final Logger LOG = LoggerFactory.getLogger(CompositeSAWController.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceUtils serviceUtils;

	@Autowired
	@Qualifier("saw-composite-api")
	private SAWCompositeServiceInterface sawCompositeServiceInterface;

	@Autowired
	private SAWCompositeProperties sawCompositeProperties;
	
	private final LoadBalancerClient loadBalancerClient;

	@Autowired
	public CompositeSAWController(LoadBalancerClient loadBalancerClient) {
		this.loadBalancerClient = loadBalancerClient;
	}

	@HystrixCommand(fallbackMethod = "defaultlogin")
	@RoutingLogging
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public LoginResponse login(@RequestBody LoginDetails loginDetails) 
	{

		ResponseEntity<LoginResponse> response = null;
		String security_url = serviceUtils.getSAWAddress(this.loadBalancerClient, sawCompositeProperties.getSecurityContext()) + "/doAuthenticate";
		LOG.info("security_url():" + security_url);

		try 
		{
			HttpEntity<LoginDetails> request = new HttpEntity<>(loginDetails);
			response = restTemplate.exchange(security_url, HttpMethod.POST, request, LoginResponse.class);
			LOG.info(response.getStatusCode().getReasonPhrase());

			// check for the validity
			// get the defaults
			// Pay load set the keys module, customer_code, role_type,
			// data_security_key as part of
			// content, type=menu

		} catch (HttpClientErrorException ex) {
			throw new SecurityModuleSAWException(ex.getMessage());
		}

		return response.getBody();
	}

	/**
	 * This method is fallback implementation of /login endpoints
	 * @param loginDetails
	 * @return
	 */
	public LoginResponse defaultlogin(@RequestBody LoginDetails loginDetails) {
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken("Here I am your Token");
		return loginResponse;
	}

}

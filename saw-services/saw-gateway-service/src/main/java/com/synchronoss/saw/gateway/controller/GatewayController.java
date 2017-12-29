package com.synchronoss.saw.gateway.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import com.synchronoss.saw.gateway.ApiGatewayProperties;
import com.synchronoss.saw.gateway.exceptions.TokenMissingSAWException;
import com.synchronoss.saw.gateway.utils.ContentRequestTransformer;
import com.synchronoss.saw.gateway.utils.HeadersRequestTransformer;
import com.synchronoss.saw.gateway.utils.URLRequestTransformer;
import com.synchronoss.saw.gateway.utils.Valid;

/**
 * @author spau0004
 *
 */
@RestController
public class GatewayController {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApiGatewayProperties apiGatewayProperties;
  
  @Value("${security.service.host}")
  private String apiGatewayOtherProperties;

  private HttpClient httpClient;

  @PostConstruct
  public void init() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    httpClient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();
  }

  
  /**
 * @param request
 * @return
 * @throws NoSuchRequestHandlingMethodException
 * @throws IOException
 * @throws URISyntaxException
 */
@RequestMapping(value = "/**", method = {GET, POST, DELETE, OPTIONS, PUT})
  @ResponseBody
  public ResponseEntity<String> proxyRequest(HttpServletRequest request) throws  IOException, URISyntaxException {
    HttpUriRequest proxiedRequest = createHttpUriRequest(request);
    logger.info("request: {}", proxiedRequest);
    logger.trace(request.getHeader("Accept"));
    logger.trace(request.getHeader("Authorization"));
    logger.trace(request.getHeader("Content-type"));	
    logger.trace(request.getHeader("Host"));
    logger.trace(request.getHeader("Origin"));
    logger.trace(request.getHeader("Referer"));
    logger.trace(request.getHeader("User-Agent"));
    HttpResponse proxiedResponse = null;
    ResponseEntity<String> responseEntity = null;
    Header header = null;
    try {
    	header = proxiedRequest.getHeaders("Authorization")[0];
    }
    catch (ArrayIndexOutOfBoundsException ex) {
     throw new 	TokenMissingSAWException("Token is missing on the request header.");
    }
    if (header!=null && header.getValue()!=null){
    	HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    	RestTemplate restTemplate = new RestTemplate();
    	String url = apiGatewayOtherProperties+"/auth/validateToken";
    	logger.info("security server URL", url);
    	try {
        ResponseEntity<?> securityResponse = restTemplate.exchange(url, HttpMethod.POST,
            requestEntity, Valid.class);
        logger.info(securityResponse.getStatusCode().getReasonPhrase());
        logger.info(securityResponse.toString());
        Valid validate =(Valid) securityResponse.getBody();
        if (securityResponse.getStatusCode().equals(HttpStatus.OK)){
          proxiedResponse = httpClient.execute(proxiedRequest);
          responseEntity = new ResponseEntity<>(read(proxiedResponse.getEntity().getContent()),
              makeResponseHeaders(proxiedResponse),
              HttpStatus.valueOf(proxiedResponse.getStatusLine().getStatusCode()));
        } else {
          responseEntity = new ResponseEntity<>(validate.getValidityMessage(),
              makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
        }
      } catch(HttpClientErrorException e) {
    	  //SAW-1374: Just keep the message hardcoded itself.
        responseEntity = new ResponseEntity<>("{\"message\":\"Invalid Token\"}",
            makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
        logger.info("Invalid Token: "+ responseEntity.toString());
        return responseEntity;
      }
    }
    else {
    	responseEntity = new ResponseEntity<>("Token is not present & it is invalid request", makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
    }
    logger.info("Response {}", proxiedResponse.getStatusLine().getStatusCode());
    return responseEntity;
  }

  private HttpHeaders makeResponseHeaders(HttpResponse response) {
    HttpHeaders result = new HttpHeaders();
    Header h = response.getFirstHeader("Content-Type");
    result.set(h.getName(), h.getValue());
    return result;
  }
  
  private HttpHeaders makeResponseHeadersInvalid() {
	    HttpHeaders result = new HttpHeaders();
	    result.setAccessControlAllowOrigin("*");
	    List<String> allowedHeaders = new ArrayList<String>();
	    allowedHeaders.add("Origin");
	    allowedHeaders.add("X-Requested-With");
	    allowedHeaders.add("Content-Type");
	    allowedHeaders.add("Accept");
	    allowedHeaders.add("Authorization");
	    result.setAccessControlAllowHeaders(allowedHeaders);
	    List<HttpMethod> allowedMethods = new ArrayList<HttpMethod>();
	    allowedMethods.add(HttpMethod.DELETE);
	    allowedMethods.add(HttpMethod.POST);
	    allowedMethods.add(HttpMethod.GET);
	    allowedMethods.add(HttpMethod.OPTIONS);
	    result.setAccessControlAllowMethods(allowedMethods);
	    result.setCacheControl("no-cache, no-store, max-age=0, must-revalidate");
	    result.setConnection("keep-alive");
	    result.setContentType(MediaType.APPLICATION_JSON_UTF8);
	    result.setAccessControlMaxAge(0);
	    result.setExpires(0);
	    result.setPragma("no-cache");
	    result.set("Server", "nginx");
	    result.set("X-Content-Type-Options", "nosniff");
	    result.set("X-Frame-Options", "DENY");
	    return result;
	  }

  private HttpHeaders setRequestHeader(HttpServletRequest request){
	HttpHeaders  requestHeaders = new HttpHeaders();
  	requestHeaders.set("Host", request.getHeader("Host"));
    requestHeaders.set("Accept", request.getHeader("Accept"));
  	requestHeaders.set("Authorization", request.getHeader("Authorization"));
 	requestHeaders.set("Origin", request.getHeader("Origin"));
  	requestHeaders.set("Referer", request.getHeader("Referer"));
  	requestHeaders.set("User-Agent", request.getHeader("User-Agent"));
  	requestHeaders.set("Content-type", request.getHeader("Content-type"));
	return requestHeaders;  
  }
  private HttpUriRequest createHttpUriRequest(HttpServletRequest request) throws URISyntaxException, IOException {
    URLRequestTransformer urlRequestTransformer = new URLRequestTransformer(apiGatewayProperties);
    ContentRequestTransformer contentRequestTransformer = new ContentRequestTransformer();
    HeadersRequestTransformer headersRequestTransformer = new HeadersRequestTransformer();
    headersRequestTransformer.setPredecessor(contentRequestTransformer);
    contentRequestTransformer.setPredecessor(urlRequestTransformer);
    return headersRequestTransformer.transform(request).build();
  }

  private String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }
}

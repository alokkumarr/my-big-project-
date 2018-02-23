package com.synchronoss.saw.gateway.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.synchronoss.saw.gateway.ApiGatewayProperties;
import com.synchronoss.saw.gateway.ApiGatewayProperties.Endpoint;
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
 * @throws IOException
 * @throws URISyntaxException
   * @throws ServletException 
   * @throws FileUploadException 
 */
@RequestMapping(value = "/**", method = {GET, POST, DELETE, OPTIONS, PUT})
  @ResponseBody
  public ResponseEntity<?> proxyRequest(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(name ="files", required = false) MultipartFile[] uploadfiles) throws  IOException, URISyntaxException, ServletException, FileUploadException {
  HttpUriRequest proxiedRequest = null;
    HttpResponse proxiedResponse = null;
    ResponseEntity<String> responseEntity = null;
    String header = null;
    logger.trace("Accept {}",request.getHeader("Accept"));
    logger.trace("Authorization {}",request.getHeader("Authorization"));
    logger.trace("Content-type {}",request.getHeader("Content-type"));    
    logger.trace("Host {}",request.getHeader("Host"));
    logger.trace("Origin {}",request.getHeader("Origin"));
    logger.trace("Referer {}",request.getHeader("Referer"));
    logger.trace("User-Agent {}",request.getHeader("User-Agent"));
    try {
    	header = request.getHeader("Authorization");
    }
    catch (ArrayIndexOutOfBoundsException ex) {
     throw new 	TokenMissingSAWException("Token is missing on the request header.");
    }
    if (header!=null){
    	HttpEntity<?> requestEntity = new HttpEntity<Object>(setRequestHeader(request));
    	RestTemplate restTemplate = new RestTemplate();
    	String url = apiGatewayOtherProperties+"/auth/validateToken";
    	logger.info("security server URL {}", url);
    	try {
        ResponseEntity<?> securityResponse = restTemplate.exchange(url, HttpMethod.POST,
            requestEntity, Valid.class);
        logger.info(securityResponse.getStatusCode().getReasonPhrase());
        logger.info(securityResponse.toString());
        Valid validate =(Valid) securityResponse.getBody();
          
    	  if (securityResponse.getStatusCode().equals(HttpStatus.OK)){
    	  if (!ServletFileUpload.isMultipartContent(request)) {
          proxiedRequest = createHttpUriRequest(request);  
          proxiedResponse = httpClient.execute(proxiedRequest);
          responseEntity = new ResponseEntity<>(read(proxiedResponse.getEntity().getContent()), 
              makeResponseHeaders(proxiedResponse),HttpStatus.valueOf(proxiedResponse.getStatusLine().getStatusCode()));
          }
          else {
            if(uploadfiles!=null && uploadfiles.length==0){throw new FileUploadException("There are no files to upload");}
            String uploadURI = request.getRequestURI();
            if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
              uploadURI =getServiceUrl(uploadURI, request) + "?" + request.getQueryString();
            } else {
              uploadURI = getServiceUrl(uploadURI, request);
            }
            String tmpDir = System.getProperty("java.io.tmpdir");
            Map<String, String> map = new HashMap<>();
            FileSystemResource requestfile = null;
            List<FileSystemResource> files = new ArrayList<>();
            ResponseEntity<String> uploadResponseEntity = null;
            try {
              for (MultipartFile fileItem : uploadfiles){
                String fileName = fileItem.getOriginalFilename();
                File incomingTargetFile = new File(tmpDir + File.separator + fileName);
                java.nio.file.Files.copy(fileItem.getInputStream(), incomingTargetFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
                IOUtils.closeQuietly(fileItem.getInputStream());
                requestfile = new FileSystemResource(incomingTargetFile);
                files.add(requestfile);
                map.put(fileName, requestfile.getPath());
              }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.set("Authorization", request.getHeader("Authorization"));
            HttpEntity<Object> uploadHttptEntity = new HttpEntity<Object>(map, headers);
            RestTemplate uploadrestTemplate = new RestTemplate();
            uploadResponseEntity = uploadrestTemplate.exchange(uploadURI, HttpMethod.POST, uploadHttptEntity, String.class);
            logger.info("uploadResponseEntity {} ", uploadResponseEntity.toString());
            } catch (Exception e) {
            logger.error("Exception thrown during file upload ", e);
          }
            for (FileSystemResource file : files){
            logger.trace("Filename :" + file.getFilename() + " has been deleted from temp folder after uploading to the destination");
            file.getFile().delete();}
            responseEntity = new ResponseEntity<>(uploadResponseEntity.getBody(), uploadResponseEntity.getHeaders(), uploadResponseEntity.getStatusCode());
            logger.trace("uploadResponseEntity response structure {}",  uploadResponseEntity.getBody() + ":" + uploadResponseEntity.getHeaders() + ":" + uploadResponseEntity.getStatusCodeValue());
            logger.trace("responseEntity response structure {}", requestEntity.getBody() + ":"+ responseEntity.getStatusCodeValue());
            return responseEntity;
          }
        } else {responseEntity = new ResponseEntity<>(validate.getValidityMessage(),makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);}
      } catch(HttpClientErrorException e) {
    	  //SAW-1374: Just keep the message hardcoded itself.
        responseEntity = new ResponseEntity<>("{\"message\":\"Invalid Token\"}", makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
        logger.info("Invalid Token: "+ responseEntity.toString());
        return responseEntity;
      }
    }
    else {
    	responseEntity = new ResponseEntity<>("Token is not present & it is invalid request", makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
    }
    logger.info("Response {}", responseEntity.getStatusCode());
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
  private HttpUriRequest createHttpUriRequest(HttpServletRequest request) throws URISyntaxException, IOException, UnsupportedCharsetException, ServletException {
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
  
  @ExceptionHandler(value=NoHandlerFoundException.class)
  private String getServiceUrl(String requestURI, HttpServletRequest httpServletRequest)  {
    Optional<Endpoint> endpoint =
            apiGatewayProperties.getEndpoints().stream()
                    .filter(e ->requestURI.matches(e.getPath()) && e.getMethod() == RequestMethod.valueOf(httpServletRequest.getMethod())
                    ).findFirst();
    return endpoint.get().getLocation() + requestURI;
  }
}

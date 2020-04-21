package com.synchronoss.saw.gateway.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.synchronoss.saw.gateway.utils.UserCustomerMetaData;
import com.synchronoss.sip.utils.RestUtil;

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
  
  @Value("${sip.ssl.enable}")
  private Boolean sipSslEnable;

  @Value("${gateway.fileupload.path}")
  private String tmpDir;
  
  @Autowired
  private RestUtil restUtil;

  private HttpClient httpClient;

  @PostConstruct
  public void init() throws Exception {
    httpClient = restUtil.getHttpClient();
  }

  private static final String CONTENT_TYPE = HttpHeaders.CONTENT_TYPE;
  private static final String USER_AGENT = HttpHeaders.USER_AGENT;
  private static final String HOST = HttpHeaders.HOST;
  private static final String ACCEPT = HttpHeaders.ACCEPT;
  private static final String ORIGIN = HttpHeaders.ORIGIN;
  private static final String REFERER = HttpHeaders.ORIGIN;
  private static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;

  /**
   *
   */
  @CrossOrigin (origins = "*")
  @RequestMapping (value = "/{path:^(?!actuator).*$}/**", method = {GET, POST, DELETE, OPTIONS, PUT})
  @ResponseBody
  /* Note: Spring Boot Actuator paths are excluded from proxying above
   * to allow downstream health check requests to pass without an
   * authentication token */
  public ResponseEntity<?> proxyRequest(
    HttpServletRequest request, HttpServletResponse response,
    @RequestParam (name = "files", required = false) MultipartFile[] uploadfiles,
    @RequestParam (name = "path", required = false) String filePath) throws Exception {

    HttpUriRequest proxiedRequest;
    HttpResponse proxiedResponse;
    ResponseEntity<String> responseEntity;
    String header;
    logger.trace("Request info : {}", request.getRequestURI());
    logger.trace("filePath info : {}", filePath);
    logger.trace("Accept {}", request.getHeader(ACCEPT));
    logger.trace("Authorization {}", request.getHeader(AUTHORIZATION));
    logger.trace("Content-type {}", request.getHeader(CONTENT_TYPE));
    logger.trace("Host {}", request.getHeader(HOST));
    logger.trace("Origin {}", request.getHeader(ORIGIN));
    logger.trace("Referer {}", request.getHeader(REFERER));
    logger.trace("User-Agent {}", request.getHeader(USER_AGENT));
    try {
      header = request.getHeader(AUTHORIZATION);
    } catch (ArrayIndexOutOfBoundsException ex) {
      throw new TokenMissingSAWException("Token is missing on the request header.");
    }
    if (header != null) {
      HttpEntity<?> requestEntity = new HttpEntity<>(setRequestHeader(request));
      RestTemplate restTemplate = restUtil.restTemplate();
      String url = apiGatewayOtherProperties + "/auth/customer/details";
      logger.trace("security server URL {}", url);
      try {
        ResponseEntity<?> securityResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity, UserCustomerMetaData.class);
        logger.trace(securityResponse.getStatusCode().getReasonPhrase());
        logger.trace(securityResponse.toString());
        UserCustomerMetaData userMetadata = (UserCustomerMetaData)securityResponse.getBody();
        if (securityResponse.getStatusCode().equals(HttpStatus.OK)) {
          if (!ServletFileUpload.isMultipartContent(request)) {
            proxiedRequest = createHttpUriRequest(request, userMetadata);
            proxiedResponse = httpClient.execute(proxiedRequest);
            responseEntity = new ResponseEntity<>(read(proxiedResponse.getEntity()), makeResponseHeaders(proxiedResponse),
                                                  HttpStatus.valueOf(proxiedResponse.getStatusLine().getStatusCode()));
          } else {
            logger.trace("Inside the file upload section....");
            if (uploadfiles == null || uploadfiles.length == 0) {
              throw new FileUploadException("There are no files to upload");
            }
            String uploadURI = request.getRequestURI();
            uploadURI = getServiceUrl(uploadURI, request);
            logger.debug("temp dir : {}", tmpDir);
            Map<String, String> map = new HashMap<>();
            FileSystemResource requestfile;
            List<FileSystemResource> files = new ArrayList<>();
            ResponseEntity<String> uploadResponseEntity = null;
            try {
              for (MultipartFile fileItem : uploadfiles) {
                String fileName = fileItem.getOriginalFilename();
                Path pathToFile = Paths.get(tmpDir + File.separator + fileName);
                Files.createDirectories(pathToFile.getParent());
                File incomingTargetFile = new File(tmpDir + File.separator + fileName);
                java.nio.file.Files.copy(fileItem.getInputStream(), incomingTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                IOUtils.closeQuietly(fileItem.getInputStream());
                requestfile = new FileSystemResource(incomingTargetFile);
                files.add(requestfile);
                map.put(fileName, requestfile.getPath());
              }
              logger.trace("Map contains : {}", map);
              logger.trace("uploadURI : {}", uploadURI);
              HttpHeaders headers = new HttpHeaders();
              headers.setContentType(MediaType.APPLICATION_JSON);
              headers.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
              headers.set("directoryPath", filePath);
              HttpEntity<Object> uploadHttptEntity = new HttpEntity<>(map, headers);
              uploadResponseEntity = restTemplate.exchange(uploadURI, HttpMethod.POST, uploadHttptEntity, String.class);
              logger.debug("uploadResponseEntity {} ", uploadResponseEntity);
            } catch (Exception e) {
              logger.error("Exception thrown during file upload ", e);
            }
            for (FileSystemResource file : files) {
              logger.trace(String.format("Filename : %s has been deleted from temp folder after uploading to the destination", file.getFilename()));
              file.getFile().delete();
            }
            String uploadResponse = "";
            if (uploadResponseEntity != null) {
              uploadResponse = uploadResponseEntity.getBody();
              logger.trace("uploadResponseEntity response structure {}", uploadResponse + ":" + uploadResponseEntity.getHeaders() + ":" + uploadResponseEntity.getStatusCodeValue());
            }
            responseEntity = new ResponseEntity<>(uploadResponse, makeResponseHeadersUpload(), HttpStatus.OK);
            logger.trace("responseEntity response structure {}", responseEntity.getStatusCodeValue());
            return responseEntity;
          }
        } else {
          responseEntity = new ResponseEntity<>("{\"message\":\"Invalid User Credential\"}", makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
        }
      } catch (HttpClientErrorException e) {
        //SAW-1374: Just keep the message hardcoded itself.
        responseEntity = new ResponseEntity<>("{\"message\":\"Invalid Token\"}", makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
        logger.info("Invalid Token: {}", responseEntity);
        return responseEntity;
      }
    } else {
      /* If accessing REST API definition, pass the request through */
      if (request.getRequestURI().endsWith(URLRequestTransformer.API_DOCS_PATH)) {
        proxiedRequest = createHttpUriRequest(request, new UserCustomerMetaData());
        proxiedResponse = httpClient.execute(proxiedRequest);
        return new ResponseEntity<>(read(proxiedResponse.getEntity()), makeResponseHeaders(proxiedResponse), HttpStatus.valueOf(proxiedResponse.getStatusLine().getStatusCode()));
      }
      responseEntity = new ResponseEntity<>("Token is not present & it is invalid request", makeResponseHeadersInvalid(), HttpStatus.UNAUTHORIZED);
    }
    logger.trace("Response {}", responseEntity.getStatusCode());
    return responseEntity;
  }

  private HttpHeaders makeResponseHeaders(HttpResponse response) {
    HttpHeaders result = new HttpHeaders();
    Header contentTypeHeader = response.getFirstHeader(CONTENT_TYPE);
    if(contentTypeHeader != null){
        result.set(contentTypeHeader.getName(), contentTypeHeader.getValue());
    }
    return result;
  }

  private HttpHeaders makeResponseHeadersUpload() {
    HttpHeaders result = new HttpHeaders();
    result.set(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return result;
  }

  
  private HttpHeaders makeResponseHeadersInvalid() {
        HttpHeaders result = new HttpHeaders();
        List<String> allowedHeaders = new ArrayList<>();
        allowedHeaders.add(ORIGIN);
        allowedHeaders.add("X-Requested-With");
        allowedHeaders.add(CONTENT_TYPE);
        allowedHeaders.add(ACCEPT);
        allowedHeaders.add(AUTHORIZATION);
        result.setAccessControlAllowHeaders(allowedHeaders);
        List<HttpMethod> allowedMethods = new ArrayList<>();
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
    requestHeaders.set(HOST, request.getHeader(HOST));
    requestHeaders.set(ACCEPT, request.getHeader(ACCEPT));
    requestHeaders.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
    requestHeaders.set(ORIGIN, request.getHeader(ORIGIN));
    requestHeaders.set(REFERER, request.getHeader(REFERER));
    requestHeaders.set(USER_AGENT, request.getHeader(USER_AGENT));
    requestHeaders.set(CONTENT_TYPE, request.getHeader(CONTENT_TYPE));
    return requestHeaders;  
  }
  

  private HttpUriRequest createHttpUriRequest(HttpServletRequest request, UserCustomerMetaData userRelatedMetaData) throws URISyntaxException, IOException, UnsupportedCharsetException, ServletException {
    logger.trace("createHttpUriRequest starts here");
    URLRequestTransformer urlRequestTransformer = new URLRequestTransformer(apiGatewayProperties);
    ContentRequestTransformer contentRequestTransformer = new ContentRequestTransformer();
    HeadersRequestTransformer headersRequestTransformer = new HeadersRequestTransformer();
    headersRequestTransformer.setUserRelatedMetaData(userRelatedMetaData);
    headersRequestTransformer.setPredecessor(contentRequestTransformer);
    contentRequestTransformer.setPredecessor(urlRequestTransformer);
    logger.trace("createHttpUriRequest ends here");
    return headersRequestTransformer.transform(request).build();
  }

  private String read(org.apache.http.HttpEntity entity) throws IOException {
        if(entity == null){
            return "";
        }else{
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                return buffer.lines().collect(Collectors.joining("\n"));
            }
        }
  }
  
  @ExceptionHandler(value=NoHandlerFoundException.class)
  private String getServiceUrl(String requestURI, HttpServletRequest httpServletRequest)  {
    logger.trace("Request Url: {}", requestURI);
    Optional<Endpoint> endpoint =
            apiGatewayProperties.getEndpoints().stream()
                    .filter(e ->requestURI.matches(e.getPath()) && e.getMethod() == RequestMethod.valueOf(httpServletRequest.getMethod())
                    ).findFirst();
    String endPoint = endpoint.get().getLocation() + requestURI;
    logger.trace("Destination Url: {}", endPoint);
    return endPoint;
  }
  
}
